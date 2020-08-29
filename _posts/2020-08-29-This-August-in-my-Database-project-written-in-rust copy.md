---
layout: post
title: This August in my Database project written in Rust
tags: [Rust, DBMS]
---

This month besides adventures like moving into another apartment and having a release on my daily job, I have got time to work on the project, listen to [Andy Pavlo's intro to Database System course](https://www.youtube.com/watch?v=oeYBdghaIjc&list=PLSE8ODhjZXjbohkNBWQs_otTrBTrjyohi), and think through ideas that floated in my mind. I will start with what happened to [the database project](https://github.com/alex-dukhno/database) in the code/development perspective and then dump my ideas on what is next.

### PostgreSQL Protocol Extended Query

[Steven](https://github.com/silathdiir) continues to contribute to the PostgreSQL wire protocol. This month he laid the foundation of [Extended Query part](https://www.postgresql.org/docs/current/protocol-flow.html#PROTOCOL-FLOW-EXT-QUERY) of PostgreSQL wire Protocol. The database can parse and save prepared statements. Two phases that have left are parameter bindings and query execution. In terms of PG Protocol to be a separate crate, this work on Extended Query and Authentication flow should be finished before finalizing the final shape of the crate.

### Dynamic expression evaluation

Last month, we introduced the evaluation of simple expressions. The database can add, subtract numbers or concatenate strings. This month [Andrew](https://github.com/AndrewBregger) started to work on extending capabilities to evaluate dynamic expressions that could contain column names. He submitted [a PR for updated queries](https://github.com/alex-dukhno/database/pull/258) that I hope we merge soon. I think this work lays out a direction to evaluating predicates in `where` clause and executing more complex `select` queries.

### Definition Schema

Before this month, saving and handling metadata was messy. It did not allow us to implement the `SQL` interface over it. So this month, we reworked it to align with described in the `SQL` standard `DEFINITION SCHEMA`. For now, it can save `CATALOG`s, `SCHEMA`s, and `TABLE`s. This restructure helps to introduce users for authentication by adding the appropriate table to the `DEFINITION SCHEMA` and a couple of methods :smile:

### No more Frontend and Backend storage

Working through materials of [Intro to Database Systems course](https://15445.courses.cs.cmu.edu/fall2019/schedule.html), I started to realize that the thing that I called `FrontendStorage` is something that belongs to the `SQL engine`. Because it was something that had known how to write, read, and delete from underling [sled](https://github.com/spacejam/sled) key-value storage. Also, it had known how to store metadata about existing `schema`s and `table`s and how to map sled's Database and Tree structures to schemas and tables. It was reworked into `CatalogManager` that managed how to load and store data after DDL and DML queries execution. `CatalogManager` seeks the help of `DataDefinition` structure that manages everything that happens to `DEFINITION SCHEMA`. These structures will help work toward making database transactional. They are going to provide a bridge between in-memory datasets that transactions can manipulate and data that is stored on disk.

### Persistence

Finally, the last improvement to the storage system in the database was that it does not use a temp folder anymore to store data. `CatalogManager` and `DataDefinition` struct were developed with the possibility to save data on-disk. By default, the database will start in in-memory mode, it simplifies `SQL engine` testing a lot, but if you pass the `PERSISTENCE` env variable to the starting command the database will use a disk.

More information about project development on [GitHub](https://github.com/alex-dukhno/database)

## Thoughts on the Project future

And now I would like to write about my thoughts on where this project is going. Since the beginning of the project, I was thinking of how to make it bigger than just a pet project that I am building in my garage. I started to notice how happier I am when I saw the number of stars, forks and, watchers increasing. How differently I am looking at the project when people contributing code in it. This month was full of thoughts on how I can establish a commercial open source company. Working through that I found out that I am missing: a team, a prototype, and a product/company name. My feeling is that these three things, in combination, will unlock what goal, mission my future company, is going to achieve - in simple words produce a solid business plan of how to get scalable, high performant, NewSQL database written in Rust.

Thanks for the reading.
