---
layout: post
title: This June in my Database project written in Rust
tags: [Rust, DBMS]
---

It has been a month since I open my database project up. A couple of people wrote to me that it would be interesting to read about the project's progress I decided to write once a month some kind of report. It would be useful for me too, to recap what happened during a month. Let's get started :smile:

### Setting up integration tests

It is strange but I forget that it was done this month and not before opening the project. But anyway, I use a synchronous driver from [postgres](https://crates.io/crates/postgres) crate. The tests are an actual copy of [SQL files](https://github.com/alex-dukhno/database/tree/master/compatibility) but I don't have to check manually that database returns correct values.
Unfortunately, due to that driver [does not send `terminate` signal when it's dropped](https://github.com/sfackler/rust-postgres/issues/613) I had to apply an ugly workaround to stop tests, because that DB could not handle the next client when the first one disconnects. I have plans to remove WA, but I have to try it out with asynchronous `rust-postgres` drive or maybe switch to other languages like `python`, `ruby`, or something else. Anyway, it is in my backlog for now.

### Orginaise project as Cargo workspace

This was mostly code moving task but working on it helps me to see how different parts of the system interdependent. For some reason :shrug: code that handled `SQL` queries held PostgreSQL Protocol connection to receive queries and send responses back. Working on this task helped me to simplify code a bit to continue to work on other parts of DB like [extract PostgreSQL Wire Protocol](#Extracting-PostgreSQL-Wire-Protocol) and [split storage into Frontend and Backend](#Splitting-storage-into-Frontend-and-Backend-parts)

### Extracting PostgreSQL Wire Protocol

Before starting working on the project I did a quick search of server-side implementation of [PostgreSQL Wire Protocol](https://www.postgresql.org/docs/12/protocol.html). I saw people discussed it in several places, but I hadn't found it as a library. Then [@jamii](https://github.com/jamii) pointed me to [Materialize](https://github.com/MaterializeInc/materialize) as they are building PostrgreSQL compatible streaming database. Eploring the source code I realize that rust ecosistem has several async runtimes: [tokio](https://github.com/tokio-rs/tokio), [async-std](https://github.com/async-rs/async-std), emerging [smol](https://github.com/stjepang/smol) and my implementation would be dependent upon one of them. After some thought, I decided to build server side wire protocol API as a library that provide API over client-server communication. I managed to extract a few `trait`s to connect and recieve queries. Currently, I am a bit hasitant to extract more stuff into protocol as I am working on other parts of database and many other concepts still are emerging and more experiments and research has to be done.

### Splitting storage into Frontend and Backend parts

Right now I use [sled](https://github.com/spacejam/sled) as on-disk storage and I was a bit doubtful to split storage in two parts as I didn't see benefits. However, the idea that it would be nice to be able in unit tests inject persistence system failures I decided to give it a try. After a couple of days of work, I realized that if I extract the `BackendStorage` `trait` it also allows me to run all `sql_engine` unit tests in memory without touching the file system.

### First `good first issue` and `help wanted` issues

I don't know about others but at some point I realized that my project is "big" for me :smile:. I’d like to work on many things but some are more interested to work on than others, at least at this point. So I started more freely put `good first issue` and `help wanted` labels on issues. :wide_smile:

### First contributor

Hi [Steven](https://github.com/silathdiir) :wave: :smile:. Steven has contributed code to [handle the situation when a user is trying to select columns that are not present in a table](https://github.com/alex-dukhno/database/pull/74) and [handle the update of a specified column and a nonexistent column](https://github.com/alex-dukhno/database/pull/100). But beyond the code, our communication helps me realize how much should I describe `good first issue` and `help wanted` issues that that contributors could easily pick up and start working on code. Issues descriptions are "Hi, welcome to the project" messages. Thanks, Steven for that realization.

### SQL types

A week ago I started to add `SQL` types before that database silently assume there is only `smallint` type and nothing has to be done with it except store and retrieve. I've merged a few PRs but the task is still in progress. I postponed work on many parts of it because adding integer (`smallint`, `integer`, and `bigint`) and string (`char` and `varchar`) types already influenced `sql_engine` and `storage` parts of the project. Code becomes unnecessarily complex, so I decided to spend some part of the next month to work on code quality improvement.

This is pretty much it of what was happening this June in my database project. See you in a month :smile:
