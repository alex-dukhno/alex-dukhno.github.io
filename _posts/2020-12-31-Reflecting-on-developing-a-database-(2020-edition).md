---
layout: post
title: Reflecting on developing a database (2020 edition)
tags: [Rust, DBMS]
---

I thought I would publish a post each month about what is happening with my [database](https://github.com/alex-dukhno/isomorphicdb) project, but it turns out it requires more time and energy to do so :smile: Thus I will try to summarise my experience of developing a relational database and how it went in 2020 in this post.

## TL;DR or brief history of my journey

I realize that I want to work on databases and SQL engines somewhere around the end of 2018. I didn't know what to do except that to come up with a list of existing technologies with SQL capabilities and send my CV to companies that support and develop them. Most of them are open-source projects. At the end of 2019, I decided to try to contribute a code to some of them. One of the projects was [Hazelcast](https://github.com/hazelcast/hazelcast). I was invited to hiring interviews after a couple of months of contribution and joined the company on 3rd February of 2020. I have joined the Management Center team that develops a web-based product that manages Hazelcast clusters, reports metrics, and other functionality. Git history of [database](https://github.com/alex-dukhno/isomorphicdb) project says that the first commit was on 20th April 2020. I have learned a lot about the internals of SQL engines since that time. There were many obstacles and problems I had to analyze and solve. That helped me gain invaluable experience. At some point, I realized how much I love developing these complicated "engineering beasts" and how web-based applications don't motivate me to work. At the end of October, I decided to talk about this with my manager. That resulted in me joining the Hazelcast core subteam that works on a distributed SQL engine starting from 1st January 2021. That doesn’t mean I will stop working on my database project - it is the opposite. Now I have more time to gain experience in developing SQL engines.

For the rest of this post, I describe how my perception of what the SQL engine is and how it works changes over the year.

## SQL over B-Tree

To get somehow started working on the database, I decided that it will be [just](https://alex-dukhno.github.io/2017-05-17-There-is-just-no-just-in-software-development/) a [sql parser](https://github.com/ballista-compute/sqlparser-rs) and a [BTreeMap](https://doc.rust-lang.org/std/collections/struct.BTreeMap.html). If the database receives an insert statement, it invokes `BTreeMap#insert`. If it receives an update statement, it invokes `BTreeMap#get` and then `BTreeMap#insert`. If it receives a delete statement, it invokes `BTreeMap#remove`. If it receives a select statement, it invokes `BTreeMap#values`. What could be easier than that, right?

After I typed in `BTreeMap` I realize that it had to be over some keys and values and couldn't be generic of some `K` and `V` because I couldn't know what tables users could create at runtime. So I decided to support only `i32` keys and values. It didn't take me much to write code to see my idea works. Something like:

```rust
let stmt = sqlparser::Parser::parse("insert into t values(1)");
process(stmt);
let stmt = sqlparser::Parser::parse("select * from t");
print_selected_data(stmt);
```

## Server and other parts

The next was ... client. I knew that it would be hard to develop two applications: client and server, and even harder to develop a communication protocol between them, so I decided: "Ok, let database implements `PostgreSQL` wire protocol, and I could use `psql` for testing" Sounds easier than done... I spent a couple of days googling a simple example of server-side implementation of a `PostgreSQL` wire protocol. All databases that I knew implement protocol incorporated it so much in their query execution flow that I couldn’t understand what is what and why it all was needed. That is why I decided to build a rust crate for easier server-side implementations of the protocol. First, it lived as a cargo module, but after six months and a lot of contribution from [Steven](https://github.com/silathdiir), I decided to give it a go and extract it into a separate [pg_wire](https://github.com/alex-dukhno/pg_wire) crate.

## Types

`PostgreSQL` wire protocol supports two modes of passing queries between client and server: `Simple` and `Extended`. In the `Simple` mode, a client sends a query as a string, and the server processes it. In the `Extended` mode, the client and server exchange messages. They do so to figure out what variable parameters the client uses, what their types are, and so on. The database should have a type system to support the `Extended` mode. And I have to tell you it is much interesting to watch how your database can handle not only `insert into table_name values (1);` but also `insert into table_name values ('here a string');`. After that `BTreeMap` was transformed from `BTreeMap<i32, i32>` into `BTreeMap<Vec<u8>, Vec<u8>>`. I used a `|` symbol to separate column values. Map values were split by `|` and transformed into plain `String`s - thank God `PostgreSQL` wire protocol supports text and binary communication between client and server :smile:

## What is a query execution pipeline?

If you are interested in the topic of databases you probably read from books, articles, or watched presentations from conferences then you probably know that before executing a query a database does parsing, analysis, optimizing, building a plan for the query, and only then executes it. As I moving along in my database development these phases emerged. In the beginning, it was parsing and executing phases. Then I wanted to do more interesting `select`s with `where` clause, I realized that the database has to build some sort of query plan: what columns to select from which table and how to filter data that is not needed. At that time the database had intertwined plan and execution phases to support the `Extended` mode of `PostgreSQL` protocol. To overcome that I started the development of a query analyzer. At some point, I realized that there is no much difference in `sqlparaser::ast` and `plan` structure that are passed and transformed from one into another. Developing that idea I realized that there are at least three different expression trees in the SQL query. The first one is each of the `values` in an `insert` statement. The second one is each assignment inside `update` queries and projection items in `select` queries. The difference between them very small to notice, `insert`'s values do not support column name in an expression but `update`'s assignments and `select`'s projection items do. The third one is the predicates in `select`'s `where`, `having` clauses and data definition's `check` constraints. In the third variant, a predicate tree to be valid has to have the resulting `bool` type after an evaluation. These details seem insignificant but by realizing them I could see how much simpler query validation could be. Each type of query could have a separate validation code with some generality e.g. `select`s, `delete`s and `update`s have `where` clause. It triggered a massive refactoring and redesign. After a month of work, I merged [the first PR](https://github.com/alex-dukhno/isomorphicdb/pull/438) that contains 8k lines of changes a week ago and [the second one](https://github.com/alex-dukhno/isomorphicdb/pull/454) is coming.

To summaries my post somehow, I would like to wish everyone to find a domain where you want to build software in so much that if one day you realize that you have take a completely different approach (like throwing away everything and start over) you could no matter what accept it and continue your journey full of discovery and fun.

Happy New Year everyone!
