---
layout: page
title: Reading List
---

# Papers

**Data Structures**

 * [Modern B-Tree Techniques](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.219.7269&rep=rep1&type=pdf)
 * [The Log-Structured Merge-Tree (LSM-Tree)](http://www.cs.umb.edu/~poneil/lsmtree.pdf)

**Distributed Systems**

 * [In Search of an Understandable Consensus Algorithm (Extended Version)](https://raft.github.io/raft.pdf) :heavy_check_mark:
 * [ARC: Analysis of Raft Consensus](https://www.cl.cam.ac.uk/techreports/UCAM-CL-TR-857.pdf)
 * [Viewstamped Replication Revisited](http://pmg.csail.mit.edu/papers/vr-revisited.pdf) :heavy_check_mark:
 * [Providing High Availability Using Lazy Replication](https://www.cs.princeton.edu/courses/archive/fall19/cos418/papers/lazy.pdf) :open_book:
 * [Raft Refloated: Do We Have Consensus?](https://www.cl.cam.ac.uk/~ms705/pub/papers/2015-osr-raft.pdf)
 * [ARIES: A Transaction Recovery Method Supporting Fine-Granularity Locking and Partial Rollbacks Using Write-Ahead Logging](http://db.csail.mit.edu/madden/html/aries.pdf)
 * [Tango: Distributed Data Structures over a Shared Log](http://research.microsoft.com/pubs/199947/Tango.pdf)
 * [Wormhole: Reliable Pub-Sub to Support Geo-replicated Internet Services](https://www.usenix.org/system/files/conference/nsdi15/nsdi15-paper-sharma.pdf)
 * [All Aboard the Databus!](http://www.socc2012.org/s18-das.pdf)
 * [Unreliable Failure Detectors for Reliable Distributed Systems](http://courses.csail.mit.edu/6.852/08/papers/CT96-JACM.pdf)
 * [Life beyond Distributed Transactions: an Apostate’s Opinion](http://adrianmarriott.net/logosroot/papers/LifeBeyondTxns.pdf)

**Hardware**

 * [What Every Programmer Should Know About Memory](https://people.freebsd.org/~lstewart/articles/cpumemory.pdf) :heavy_check_mark:
 * [Meltdown: Reading Kernel Memory from User Space](https://www.usenix.org/system/files/conference/usenixsecurity18/sec18-lipp.pdf) :heavy_check_mark:
 * [Spectre Attacks: Exploiting Speculative Execution](https://spectreattack.com/spectre.pdf) :heavy_check_mark:

**Linux**

 * [Interrupt Handling in Linux](https://opus4.kobv.de/opus4-fau/frontdoor/deliver/index/docId/6722/file/report.pdf)

**Network Protocols**

 * [Is it Still Possible to Extend TCP?](https://conferences.sigcomm.org/imc/2011/docs/p181.pdf)
 * [Fitting Square Pegs Through Round Pipes Unordered Delivery Wire-Compatible with TCP and TLS](https://www.researchgate.net/publication/50235622_Fitting_Square_Pegs_Through_Round_Pipes_Unordered_Delivery_Wire-Compatible_with_TCP_and_TLS)
 * [The QUIC Transport Protocol: Design and Internet-Scale Deployment](https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/46403.pdf)

# Blogs

**Data Structures**

 * [The Log: What every software engineer should know about real-time data’s unifying abstraction](https://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying)

**Concurency**

 * [Writing a seqlock in Rust](https://pitdicker.github.io/Writing-a-seqlock-in-Rust/)
 * [Spinlocks Considered Harmful](https://matklad.github.io/2020/01/02/spinlocks-considered-harmful.html)
 * [Mutexes Are Faster Than Spinlocks](https://matklad.github.io/2020/01/04/mutexes-are-faster-than-spinlocks.html)
 * [Measuring Mutexes, Spinlocks and how Bad the Linux Scheduler Really is](https://probablydance.com/2019/12/30/measuring-mutexes-spinlocks-and-how-bad-the-linux-scheduler-really-is/)
 * [I'm not feeling the async pressure](https://lucumr.pocoo.org/2020/1/1/async-pressure/)
 * [Bounded Concurrent Time-Stamp Systems Are Constructible](https://groups.csail.mit.edu/tds/papers/Shavit/TM-393.pdf)

**Distributed Systems**

 * [Turning the database inside-out with Apache Samza](https://martin.kleppmann.com/2015/03/04/turning-the-database-inside-out.html)
 * [Change Data Capture: The Magic Wand We Forgot](http://martin.kleppmann.com/2015/06/02/change-capture-at-berlin-buzzwords.html)
 * [Bottled Water: Real-time integration of PostgreSQL and Kafka](https://www.confluent.io/blog/bottled-water-real-time-integration-of-postgresql-and-kafka/)
 * [Using logs to build a solid data infrastructure (or: why dual writes are a bad idea)](https://www.confluent.io/blog/using-logs-to-build-a-solid-data-infrastructure-or-why-dual-writes-are-a-bad-idea/) :heavy_check_mark:
 * [Eventually Consistent](https://www.allthingsdistributed.com/2007/12/eventually_consistent.html)
 * [Strong consistency models](https://aphyr.com/posts/313-strong-consistency-models)
 * [Jepsen: MongoDB](https://aphyr.com/posts/284-call-me-maybe-mongodb)
 * [Consensus Protocols: Two-Phase Commit](https://www.the-paper-trail.org/post/2008-11-27-consensus-protocols-two-phase-commit/)
 * [Exactly-once or not, atomic broadcast is still impossible in Kafka - or anywhere](https://www.the-paper-trail.org/post/2017-07-28-exactly-not-atomic-broadcast-still-impossible-kafka/)
 * [Exactly-once Semantics are Possible: Here’s How Kafka Does it](https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/)
 * [Transactional Messaging in Kafka](https://www.confluent.io/blog/transactions-apache-kafka/)
 * [The Case Against Queues](http://widgetsandshit.com/teddziuba/2011/02/the-case-against-queues.html)
 * [DBMS Musings](http://dbmsmusings.blogspot.com/2019/08/an-explanation-of-difference-between.html)
 * [Gray Failures](https://www.the-paper-trail.org/post/2020-04-19-gray-failures/) :heavy_check_mark:

**Linux**

 * [How are Unix pipes implemented?](https://toroid.org/unix-pipe-implementation)

**Software Development**

 * [Revenge of the Nerds](http://www.paulgraham.com/icad.html?viewfullsite=1)
 * [A Senior Engineer's CheckList](https://littleblah.com/post/2019-09-01-senior-engineer-checklist/)

**Programming Languages**

 * [No, dynamic type systems are not inherently more open](https://lexi-lambda.github.io/blog/2020/01/19/no-dynamic-type-systems-are-not-inherently-more-open/)
 * [Parse, don’t validate](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/)


# Books :books:

**Distributed Systems**

 * [Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems](https://www.amazon.com/Designing-Data-Intensive-Applications-Reliable-Maintainable/dp/1449373321/)
 * [Database Internals: A Deep Dive into How Distributed Data Systems Work](https://www.amazon.com/Database-Internals-Deep-Distributed-Systems-dp-1492040347/dp/1492040347)
