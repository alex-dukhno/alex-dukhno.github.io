# Introduction or how I study Rust

The first time when I heard about [Rust](https://www.rust-lang.org/en-US/) was at Yandex conference. The guy who presented a topic was a Java/C++ engineer at Yandex. And when he said that Rust was going to solve problems of Java and C++ programming languages (e.g. dangling pointers in C++, GC pauses in Java). I was inspired to learn this programming language.

I am a Java developer and in my career I have rarely thought about memory management unless I was going to fix another memory leak. That's what stops me from learning any system programming languages. But Rust is different. I wrote my first program in Rust as my regular Java program. I did not have to clean up memory after myself. However, it didn't compile at the first and maybe even at the tenth time. That's what Rust is. Since that time I had been waiting for 1.0-version to start studying Rust more deeply.

A year ago, I decided to practice test driven development. The choice of programming language was obvious. Rust has a simple  built-in test framework. This allows you to make a test method just put '`#[test]`' attribute on any method. For instance:

```rust
fn add_two(x: i32) -> i32 {
    x + 2
}

#[test]
fn test_add_two() {
    let x = 2;
    assert_eq!(add_two(2), 4);
}
```
To run this snippet of test I use Rust's package manager [cargo](https://github.com/rust-lang/cargo). Two these things let me concentrate on writing tests immediately. All I needed was put '`#[test]`' attribute on methods and run `cargo test` in my project folder.

Sometimes I need to ignore test for refactoring. To do that I just put '`#[ignore]`' attribute on a test method.

However, 'set up' and 'tear down' methods are essential features of xUnit frameworks, Rust does not provide them. That is why I started using [stainless](https://github.com/reem/stainless). It is an awesome testing framework for Rust. However, it uses unstable Rust features, so you can use it only with nightly version of compiler. Stainless gives you ability to write less boilerplate tests. For instance:

```rust
before_each {
    let x = 2;
}

it "add two" {
    assert_eq!(add_two(x), 4);
}

it "add three" {
    assert_eq!(add_three(x), 5);
}
```

The code inside of '`before_each`' section will be executed before each test. That mean variable '`x`'  will have value of '`2`' in both tests from above example.

Despite of such neat features, I could not mark tests as ignored. This missing feature was a great opportunity for me to contribute into open source.

On my way of studying Rust I found very useful [rust-clippy](https://github.com/Manishearth/rust-clippy) crate. It is a collections of lints. As the stainless rust-clippy also uses unstable compiler features. It analyzes code during compilation and points out where you can use more idiomatic Rust code.

When I started to feel that I knew Rust well enough I decided to write my pet project, a SQL database. I rewrote it a few times moving everything here and there. After another attempt I decided to step back and learn other programming languages such as Scala and Haskell. I knew that some day I would come back to Rust. After more than half a year this day has come. I started my project from scratch. I will write about my progress with it in my further post.
