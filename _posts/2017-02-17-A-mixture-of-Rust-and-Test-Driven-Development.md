---
layout: post
title: A mixture of Rust and Test Driven Development
---

## Disclaimer
In my blog, I intend to outline how to practice [Test Driver Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development) in [Rust](https://www.rust-lang.org/en-US/). Future posts will contain step by step guidance of process how to write tests first and make teeny-tiny steps to implement missed functionality with simple exercises.

## My path

I started learning Rust back to 1.0 version days. I am still learning it (yes, I am sure that it is a continuous process, and it never ends unless I am bored with Rust). More than a year ago I was told about TDD and practicing it by doing [code katas](http://codekata.com/) every day. I tried to apply this approach to [learning Rust](https://github.com/Alex-Diez/rust-tdd-katas). It has changed my way of learning new programming languages forever. I have tried this with other languages and I have got some knowledge and understanding of [Scala](https://github.com/Alex-Diez/scala-tdd-katas), [Haskell](https://github.com/Alex-Diez/haskell-tdd-kata) and [Python](https://github.com/Alex-Diez/python-tdd-katas) programming languages since that time. Now, I am learning [JavaScript](https://github.com/Alex-Diez/javascript-tdd-katas) in the same way.

## What is Test Driven Development (in short)

[Robert Martin](https://en.wikipedia.org/wiki/Robert_Cecil_Martin), aka [Uncle Bob](http://blog.cleancoder.com/), describes it in three simple rules:

* You are not allowed to write any production code unless it is to make a failing unit test pass.
* You are not allowed to write any more of a unit test than is sufficient to fail; and compilation failures are failures.
* You are not allowed to write any more production code than is sufficient to pass the one failing unit test.

Basically, you are switching between writing tests and production code. That's what I am going to write about in future posts; the process of doing TDD with code exercises.

## Code kata (in short)

Here the kata has the same meaning as in Karate kata; the repetition of the same exercise many times. However, in the case of the code kata, you have to write a coding exercise. If you are asking: "why should I repeat myself?". I would like to answer this question by quoting [Dave Thomas](https://en.wikipedia.org/wiki/Dave_Thomas_(programmer)), one of the authors of [The pragmatic programmer](https://en.wikipedia.org/wiki/The_Pragmatic_Programmer) and author of `code kata` blog, "Because experience is the only teacher". If you've done something once you will remember it few days, but if you are continuously practicing it, it will stick to you and will never leave. Repetition creates habits, and habits are what enable mastery.

## Prepare your oven before cooking

The most convenient way to install Rust and manage its updates is using [rustup](https://www.rustup.rs). When you download and complete installation procedure you will have Rust compiler, standard library and a package manager - [Cargo](https://crates.io/), installed on your machine. To make sure that you have Rust on board run the following command in terminal:

```sh
$ rustc -V
```

Now you are ready to create your katas project (or crate in terms of Rust). To do so, you need to go back to your terminal and enter the following command:

```sh
$ cargo new --lib code-katas
```

The `code-katas` will have the following content:

```
code-katas/
    |
    +-src/
    |   |
    |   +-lib.rs
    |
    +-Cargo.toml
```

If you run

```sh
$ cargo test
```

the output will be similar to

```sh
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 0.75 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 1 test
test it_works ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured

   Doc-tests code-katas

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured
```

As you can notice, Cargo generates configuration file `Cargo.toml` and `src/lib.rs` with an empty test for you.

That's it for the project set up. Future posts will contain what you need to add to you katas project.

## Why I am writing this blog

As the saying goes, you learn better when you are teaching. One day, writing another code kata in Rust I was thinking "How can I better understand TDD?". The immediate answer was "Write blog posts of your process of writing katas!". So here we are. I hope articles will help you master your TDD and Rust skills.
