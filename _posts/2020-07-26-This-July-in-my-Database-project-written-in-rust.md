---
layout: post
title: This July in my Database project written in Rust
tags: [Rust, DBMS]
---

This month went by with a lot of changes, ideas, and questions from contributors that hugely influencing overall database design. The most significant ones are: we start working on `QueryProcessor`, PostgreSQL wire protocol module has got TLS support, and gone through refactorings and API changes. Let's get started :smile:

### Expressions Evaluation

This is one of the changes the could be seen from a user perspective. We land the functionality that could evaluate string concatenation and a couple of mathematical operations in `INSERT` and `UPDATE` queries. The simplest example of the query is:
```sql
INSERT INTO schema_name.table_name VALUES (1 + 4 / 2 - 3, 'abc' || 'def');
```

Database will evaluate `1 + 4 / 2 - 3` into `-2` and `'abc' || 'def'` into `'abcdef'` before writing on disk, well yeh, instead of panicking :laughing:. Also, it does runtime check not to concatenate numbers and multiply strings.

Currently, the database supports only constant evaluation. Query preprocessor, that I'll write further about, is going to help us to evaluate dynamic expressions like:
```sql
UPDATE schema_name.table_name SET column_1 = column_1 + 10;
```

Also, there is a plan to add other operations supported by `PostgreSQL` like square root, cube root, factorial, and bitwise operations. Before that [sqlparser](https://github.com/ballista-compute/sqlparser-rs) crate should be extended as it can't parse these operations.

### Query Preprocessor

The idea is that the SQL engine has to first analyze the SQL abstract syntax tree and be sure that a query could be executed. After that SQL engine goes through runtime checks like if schema, table(s), and column(s) exist in storage and then perform read, write or return an error(s) to a client. [Andrew Bregger](https://github.com/AndrewBregger) did [a prototype](https://github.com/alex-dukhno/isomorphicdb/pull/183) and it is still work in progress to land it into mainline. Currently, we have migrated `(CREATE|DROP) SCHEMA`, `(CREATE|DROP) TABLE`. I postpone the migration of `INSERT` queries due to changes to on-disk representation which influenced all other types of queries, and also I didn't have a clear vision of how to handle already implemented expression evaluation inside queries. Hopefully, new on-disk representation was landed in the mainline and unblocked migration of other queries to be preprocessed.

### On-Disk Representation

As we use [sled](https://github.com/spacejam/sled) key-value storage instead of working directly with a file system that allows us to use a very simple approach. Rows were just strings converted into bytes that separated by `|`. As all incoming data were represented as strings whenever the database had to insert a row it validated type constraints, converted into bytes, and join them with `|` as a separator. Right now, database use two abstraction `Datum` to abstract away types (e.g. `boolean`, `smallint`, and so on) and `Binary` to abstract away how `Datum`s packed into `Vec<u8>`. `Binary` packs incoming data into `Vec` by putting type tag first, then size if it is a dynamically sized type like `varchar`, and then push data onto `Vec`.
As work on this was done in the scope of Query Preprocessor prototype I still not sure about its current final version and I'd like to have a look at how other relational (and not) databases represent data on disk.

### PostgreSQL protocol module supports TLS

This month [Steven](https://github.com/silathdiir) has submitted a couple of PR into the PostgreSQL wire protocol module. One of them is TLS support. PostgreSQL wire protocol defines message flow between client and server on SSL/TLS negotiation and these changes are crucial to extract module into a separate crate.

### Insert query with named columns

One more addition from Steven, is that database can handle `INSERT` queries if user specify column names. For example:
```sql
INSERT INTO schema_name.table_name (column_2, column_4, column_1, column_3) 
VALUES (42, 44, 41, 43);
```

### Support for Serial and Boolean SQL Types

Two more contributions from the community were adding support for serial (`smallserial`, `serial` and `bigserial`) (by [Suhaib Affan](https://github.com/suhaibaffan)) and `boolean` types (by [Lukasz Piepiora](https://github.com/lpiepiora)).

### Automatic functional Tests on CI

No more manually running SQL queries! Files with SQL queries were migrated to `python` `pytest` that uses `psycopg2` package to make a query to the database. Migration work was done by [Alex](https://github.com/Aleks0010V). He also provides help with maintaining it and extends it with more test cases.

### Event-driven architecture

One more big change to the overall design  the database was landed to mainline that allows to redesign/rearchitect system to send a response to a client as soon as we get something either runtime error or data that read from disk. This also influenced the `protocol` module. Previously, it has `Listener` API that returns `Connection` that abstracted away communication between a database and a client. Right now, the `protocol` provides a `hand_shake` function that needs asynchronous media (e.g. `TcpStream`) and return `Receiver` and `Sender` which are used to receive commands (e.g. `Termination` or `Query`) and send errors or data back to the client.

### Releasing docker image

As I notice experiments generate many ideas of where and how the project could grow and reach certain milestones I decided to experiment with its distribution. As the developers are the main group of users/experimenters in the databases field and most of them should have installed docker on their working machine I went ahead with docker image distribution. If you are interested to play around with what database can do now you can follow [the documentation](https://github.com/alex-dukhno/isomorphicdb/tree/master/docs).

### Discord Server

Last but not least, [discord server](https://discord.gg/PUcTcfU) has been created to discuss issues, ideas, and current development areas. You are more than welcome to come and join us.


There were a lot of things that went by this month, some of them I missed or forget about. However, at the end of this article, I'd like to share some, I hope the near future, plans on what we will be working in the following weeks or months.

1. Extending Query Preprocessor to handle `INSERT`s, `UPDATE`s, `DELETE`s, and `SELECT`s queries
1. Changing the `storage` module API to better support `sql_engine` to check runtime errors
1. Implementing early ideas on supporting transactions (I have couple ideas based on `Rust` `RWLocks` or `sled` `Tree.transaction` functionality)
1. Changing how system information about schemas, tables, columns, and types is handled right now
1. Further evolution of query evaluation with Query Processor to allow database execute predicative logic. That opens up a door to work on API design to work with indexes and query cost computation.
