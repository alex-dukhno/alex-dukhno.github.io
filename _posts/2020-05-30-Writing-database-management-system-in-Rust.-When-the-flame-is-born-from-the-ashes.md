---
layout: post
title: Writing database management system in Rust. When the flame is born from the ashes
tags: [Rust, DBMS]
---

I've been dreaming about implementing my database management system since the beginning of my software engineer career.

My first DBMS, as far as I can call it so, was a simple client-server java application. It could store tables definition and data in an `XML` file and execute simple `select`, `insert`, `update` and `delete` queries.

Few years passed in pursuit of career achievements but the dream hasn't left my mind. When I heard about `Rust` and played with it after the `1.0` release - the idea of DBMS again spun in my head. I have had a few attempts to write `SQL` engines that are buried in the repo [rust-sql-engine](https://github.com/alex-dukhno/rust-sql-engine).

The dream started growing again after a short chat with [Alex Petrov](https://twitter.com/ifesdjeen) on research groups around DBMS. Shortly, I created a private git repo on `GitHub` and started writing code. Then, suddenly for me, different concepts started popping up in my head. While I was writing code for a storage engine I realized that I have a lot of ideas that I would like to implement in my project. These are some of them:
 * Implement `PostgreSQL` wire protocol and integrate it with `RSocket`,
 * Go full non-blocking and reactive in a client-server communication,
 * Write on-disk storage using `BPF` for Linux OS,
 * Write storage engine using `NVM`,
 * Compiling `SQL` queries into binary using `LLVM`,
 * Go distributed - implement effective conflict resolution algorithms, replication, CRDT.

There is a bunch of work that I couldn't possibly do alone and fun that I would love to share with database community enthusiasts. Today, I am making my `GitHub` repo public with the initial work that I’ve done. Here it is: [alex-dukhno/database](https://github.com/alex-dukhno/database). If you are interested in contributing please don’t hesitate and reach me out.
