---
layout: post
title: Stack kata. TL;DR
tags: rust tdd
---

I look at the tests in two ways. On one hand, they are experiments. If I write a test for my program that use some third-party libraries or frameworks, I expect to see some results. They can be valid or not. These kind of tests are experiments. I write the tests to gain some empirical experience of how things work inside.
On the other hand, they are formal proofs of my program. If I write a test for the data structure or an algorithm I assert that when you do this and that the outcome will be that. In this article, I will cover writing tests for the `Stack` data structure.

## Project layout

Add a new module to your kata crate with `stack_kata` name such that your project should be as follows:

```
code-katas/
    |
    +-src/
    |   |
    |   +-stack_kata/
    |   |   |
    |   |   +-mod.rs
    |   |   +-day_1.rs
    |   |
    |   +- .
    |   +- .    //other katas
    |   +- .
    |   +- lib.rs
    |
    +-Cargo.toml
```

## Nothing test

Do not forget to perform a preparation step before doing code kata. Add `pub mod stack_kata;` to `src/lib.rs`, `pub mod day_1;` to `src/stack_kata/mod.rs` file and the following empty test to `src/stack_kata/day_1.rs` file:

```rust
#[cfg(test)]
mod tests {
    #[test]
    fn nothing() {
    }
}
```

Run the test:

```sh
$ cargo test stack_kata::day_1
    Finished dev [unoptimized + debuginfo] target(s) in 0.0 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 1 test
test stack_kata::day_1::tests::nothing ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured
```

We are done here. Can move on with our tests.

> If you run `cargo test` in your terminal you will see results of all tests of all your katas in the project. You may notice that I run `cargo test stack_kata::day_1` to filter out only needed tests results.

## Create an empty stack

Simplicity on the first place. `Stack` creation is the simplest test. Remove `nothing` test and type the following lines into our `tests` module.

```rust
#[test]
fn creates_an_empty_stack() {
    let stack = Stack;
}
```

Run the test:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0425]: cannot find value `Stack` in this scope
 --> src/stack_kata/day_1.rs:5:21
  |
5 |         let stack = Stack;
  |                     ^^^^^ not found in this scope

error: aborting due to previous error

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

Great, we have got a compilation error that means a failing test. Let's create `Stack` `struct` by writing `pub struct Stack;` in the `day_1` module and import it into `tests` module with the `use` statement.

Run the test:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
warning: unused variable: `stack`, #[warn(unused_variables)] on by default
 --> src/stack_kata/day_1.rs:9:13
  |
9 |         let stack = Stack;
  |             ^^^^^

    Finished dev [unoptimized + debuginfo] target(s) in 1.6 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 1 test
test stack_kata::day_1::tests::creates_an_empty_stack ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured
```

> Creation is the first step to the great future of our `Stack` data structure.

It is `GREEN`, cool, now we can think about the further development of our data structure. However, we haven't made any assertion. That means we don't specify what the state of a newly created `Stack`. Tests have to be specific while implementation has to become more general. Update `creates_an_empty_stack` to the test above.

```rust
#[test]
fn creates_an_empty_stack() {
    let mut stack = Stack;

    assert_eq!(stack.pop(), None);
}
```

Run the test:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error: no method named `pop` found for type `stack_kata::day_1::Stack` in the current scope
  --> src/stack_kata/day_1.rs:11:26
   |
11 |         assert_eq!(stack.pop(), None);
   |                          ^^^

error: aborting due to previous error

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

Let's fix the test by adding `pop` function into `Stack`'s `impl` block as in the following snippet:

```rust
impl Stack {
    pub fn pop(&mut self) -> Option<i32> {
    }
}
```

It won't compile because `Rust` compiler expect returning value of type `Option` rather then `()`. Here we have only one way to fix this - return `None`. By this time your code should be as follows:

```rust
pub struct Stack;

impl Stack {

    pub fn pop(&mut self) -> Option<i32> {
        None
    }
}

#[cfg(test)]
mod tests {
    use super::Stack;

    #[test]
    fn creates_an_empty_stack() {
        let mut stack = Stack;

        assert_eq!(stack.pop(), None);
    }
}
```

> Everything in `Rust` is an expression. Our empty function is an expression too, that returns `()` type.
>
> By the way, you may try this example (It shows that assignment is an expression):
>
>```rust
> let mut b = false;
> if b = true { //error[E0308]: mismatched types
>   //^^^^^^^^ expected bool, found ()
>   //do something
>}
>```
>
>Note that I typed `=` instead of `==` in the `if` condition.

> `Rust` does not have `null` pointers, therefore to handle a situation when the value is absent use `Option<T>`. `Option<T>` is a `Rust` `enum` that has two variants either `Some(T)`, if there is a value, or `None` if there is not. It is so common type that it is imported into `Rust` modules by default.

## Push into

At this stage, we are ready to implement `push` function for our `Stack`. But before we create a collection of elements from our `Stack` let's make it hold at least one element. The test for this case is:

```rust
#[test]
fn pushes_one_element_onto_stack() {
    let mut stack = Stack;

    stack.push(1);

    assert_eq!(stack.pop(), Some(1));
    assert_eq!(stack.pop(), None);
}
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error: no method named `push` found for type `stack_kata::day_1::Stack` in the current scope
  --> src/stack_kata/day_1.rs:25:15
   |
25 |         stack.push(1);
   |               ^^^^

error: aborting due to previous error

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

Oh, that was unexpected :trollface:. Let's add `push` function to our `Stack`.

```rust
    pub fn push(&mut self, item: i32) {
    }
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)

// warnings are omitted

    Finished dev [unoptimized + debuginfo] target(s) in 1.24 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 2 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_one_element_onto_stack ... FAILED

failures:

---- stack_kata::day_1::tests::pushes_one_element_onto_stack stdout ----
	thread 'stack_kata::day_1::tests::pushes_one_element_onto_stack' panicked at 'assertion failed: `(left == right)` (left: `None`, right: `Some(1)`)', src/stack_kata/day_1.rs:31
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    stack_kata::day_1::tests::pushes_one_element_onto_stack

test result: FAILED. 1 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

So what we can do to fix this failing test?... Our `Stack` has two variants: it has a value or not. Oh, that our `Option`. Let's add `item` field to `Stack` `struct`:

```rust
pub struct Stack {
    item: Option<i32>
}

impl Stack {

    pub fn pop(&mut self) -> Option<i32> {
        let ret = self.item;
        self.item = None;
        ret
    }

    pub fn push(&mut self, item: i32) {
        self.item = Some(item);
    }
}
```

Run our tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0423]: expected value, found struct `Stack`
  --> src/stack_kata/day_1.rs:24:25
   |
24 |         let mut stack = Stack;
   |                         ^^^^^ did you mean `Stack { /* fields */ }`?

error[E0423]: expected value, found struct `Stack`
  --> src/stack_kata/day_1.rs:31:25
   |
31 |         let mut stack = Stack;
   |                         ^^^^^ did you mean `Stack { /* fields */ }`?

error: aborting due to 2 previous errors

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

Ouch! When we add fields to a `struct` we need to specify their value when instantiating the `struct`'s object. Quickly fix tests by assigning `None` to `item` field.

```rust
#[test]
fn creates_an_empty_stack() {
    let mut stack = Stack { item: None };

    assert_eq!(stack.pop(), None);
}

#[test]
fn pushes_one_element_onto_stack() {
    let mut stack = Stack { item: None };

    stack.push(1);

    assert_eq!(stack.pop(), Some(1));
    assert_eq!(stack.pop(), None);
}
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.37 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 2 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_one_element_onto_stack ... ok

test result: ok. 2 passed; 0 failed; 0 ignored; 0 measured
```

Hooray! We are almost done here. This line `Stack { item: None }` shows that we can instanciate a `Stack` with any value (e.g. `Stack { item: Some(3) }`). It breaks encapsulation and is not good for our tests. Define static function `empty` to create a new empty `Stack` and replace `Stack { item: None }` with its invocations.

```rust
pub struct Stack {
    item: Option<i32>
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            item: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        let ret = self.item;
        self.item = None;
        ret
    }

    pub fn push(&mut self, item: i32) {
        self.item = Some(item);
    }
}

#[cfg(test)]
mod tests {
    use super::Stack;

    #[test]
    fn creates_an_empty_stack() {
        let mut stack = Stack::empty();

        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_one_element_onto_stack() {
        let mut stack = Stack::empty();

        stack.push(1);

        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);
    }
}
```

> If you are going to practice this kata you may define `empty` function during `BLUE` (refactoring) stage after the first test. I haven't done it deliberately to show the way of creating a `struct` that does not have a static factory function.

If you are new to `Rust` you may be OK to 

```rust
pub fn pop(&mut self) -> Option<i32> {
    let ret = self.item;
    self.item = None;
    ret
}
```

However, if you are not, you might be saying: "Common man, it is ugly! You'd better use `Option`'s `take` function". Wow, what is that `take` function doing? If you have a look at the documentation of the standard library it says:

```
fn take(&mut self) -> Option<T>

Takes the value out of the option, leaving a None in its place.
```

In short, it swaps `None` with current `Option` value. Replace our code with `take` function invocation.

```rust
pub struct Stack {
    item: Option<i32>
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            item: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        self.item.take()
    }

    pub fn push(&mut self, item: i32) {
        self.item = Some(item);
    }
}

#[cfg(test)]
mod tests {
    use super::Stack;

    #[test]
    fn creates_an_empty_stack() {
        let mut stack = Stack::empty();

        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_one_element_onto_stack() {
        let mut stack = Stack::empty();

        stack.push(1);

        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);
    }
}
```

Check that we break nothing.

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.36 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 2 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_one_element_onto_stack ... ok

test result: ok. 2 passed; 0 failed; 0 ignored; 0 measured
```

Cool. Let's move on.

Now when we are ready to make a collection from `Stack` `struct` I realize that our `pushes_one_element_onto_stack` is a partial case of pushing into and popping out a single item from a stack. Let's add one more `push` `pop` invocations on our `Stack` object and rename `pushes_one_element_onto_stack` to `pushes_into_pops_out_one`.

```rust
pub struct Stack {
    item: Option<i32>
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            item: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        self.item.take()
    }

    pub fn push(&mut self, item: i32) {
        self.item = Some(item);
    }
}

#[cfg(test)]
mod tests {
    use super::Stack;

    #[test]
    fn creates_an_empty_stack() {
        let mut stack = Stack::empty();

        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_into_pops_out_one() {
        let mut stack = Stack::empty();

        stack.push(1);

        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);

        stack.push(2);

        assert_eq!(stack.pop(), Some(2));
        assert_eq!(stack.pop(), None);
    }
}
```

Do not forget to rerun the tests. That's it for now.

## Push many

After proving that our `Stack` can hold one element let's write a test that helps us to check if `Stack` can hold a bunch of elements.

```rust
#[test]
fn pushes_thee_elements_into_stack() {
    let mut stack = Stack::empty();

    stack.push(1);
    stack.push(2);
    stack.push(3);

    assert_eq!(stack.pop(), Some(3));
    assert_eq!(stack.pop(), Some(2));
    assert_eq!(stack.pop(), Some(1));
    assert_eq!(stack.pop(), None);
}
```

Run the tests.

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.64 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... FAILED

failures:

---- stack_kata::day_1::tests::pushes_thee_elements_into_stack stdout ----
	thread 'stack_kata::day_1::tests::pushes_thee_elements_into_stack' panicked at 'assertion failed: `(left == right)` (left: `None`, right: `Some(2)`)', src/stack_kata/day_1.rs:56
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    stack_kata::day_1::tests::pushes_thee_elements_into_stack

test result: FAILED. 2 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

Now we are allowed to implement this functionality. Let's use linked list under the hood of `Stack` to make it hold many elements. Define private `Node` `struct` with fields `item`, with type `i32`, and `next` with type ... Here we have to handle heap allocation. For this purpose `Rust` has `Box` `struct`. Also `next` filed either have a reference to another `Node` or to nothing, hey it is `Option` again. Combining this, the `next` field of our `Node` `struct` has `Option<Box<Node>>` type. `Stack` has to have reference to head `Node` too. Add the `head` field to `Stack` with the same type as `Node`'s `next`. Having that, to fix our failing test is quite easy.

```rust
struct Node {
    item: i32,
    next: Option<Box<Node>>
}

pub struct Stack {
    head: Option<Box<Node>>,
    item: Option<i32>
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            head: None,
            item: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        match self.head.take() {
            Some(old_head) => {
                self.head = old_head.next;
                Some(old_head.item)
            }
            None => None
        }
    }

    pub fn push(&mut self, item: i32) {
        let new_head = Box::new(Node {
            item: item,
            next: self.head.take()
        });

        self.head = Some(new_head);
    }
}
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0382]: use of moved value: `old_head`
  --> src/stack_kata/day_1.rs:25:22
   |
24 |                 self.head = old_head.next;
   |                             ------------- value moved here
25 |                 Some(old_head.item)
   |                      ^^^^^^^^^^^^^ value used here after move
   |
   = note: move occurs because `old_head.next` has type `std::option::Option<std::boxed::Box<stack_kata::day_1::Node>>`, which does not implement the `Copy` trait

error: aborting due to previous error

error: Could not compile `code-katas`.
Build failed, waiting for other jobs to finish...
```

Oh man! What the hell? For those who are new to `Rust` it is not obvious what is going here. However, it is nothing special :trollface:. When we use `.` operation on `old_head` we actually moved and dereferenced it simultaneously. It can be fixed easily by introducing dereferencing `old_head` to a temporary variable.

```rust
pub fn pop(&mut self) -> Option<i32> {
    match self.head.take() {
        Some(old_head) => {
            let node = *old_head;
            self.head = node.next;
            Some(node.item)
        }
        None => None
    }
}
```

Run the tests

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
warning: field is never used: `item`, #[warn(dead_code)] on by default
  --> src/stack_kata/day_1.rs:10:5
   |
10 |     item: Option<i32>
   |     ^^^^^^^^^^^^^^^^^

warning: field is never used: `item`, #[warn(dead_code)] on by default
  --> src/stack_kata/day_1.rs:10:5
   |
10 |     item: Option<i32>
   |     ^^^^^^^^^^^^^^^^^

    Finished dev [unoptimized + debuginfo] target(s) in 1.46 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok
test stack_kata::day_1::tests::creates_an_empty_stack ... ok

test result: ok. 3 passed; 0 failed; 0 ignored; 0 measured
```

Tests are `GREEN`. It is time to refactor. The compiler warned us about useless `item` field, let's remove it. `Option<Box<Node>>` looks too verbose, let's use `Rust` type aliasing feature to make it shorter. A type alias is defined by `type` keyword. Introduce `Link` alias for `Option<Box<Node>>`.

```rust
type Link = Option<Box<Node>>;

struct Node {
    item: i32,
    next: Link
}

pub struct Stack {
    head: Link
}
```

We used pattern matching to handle the case when a `Stack` does not contain any value. In our case, `None` branch does nothing and returns another `None`. For such situation `Option` has `map` function. It takes a closure which will be applied to the value of the `Option` only if it is a `Some(T)`.

```rust
pub fn pop(&mut self) -> Option<i32> {
    self.head.take().map(|old_head| {
        let head = *old_head;
        self.head = head.next;
        head.item
    })
}
```

Do not forget to rerun the tests before going further.

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.0 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok

test result: ok. 3 passed; 0 failed; 0 ignored; 0 measured
```

## Iterate over the stack

A collection is not a collection if you are not able to iterate over it. Let us add a function to create `Iterator` over our `Stack`.

```rust
#[test]
fn iterate_over_stack() {
    let mut stack = Stack::empty();

    stack.push(1);
    stack.push(2);
    stack.push(3);

    let mut iter = stack.into_iter();

    assert_eq!(iter.next(), Some(3));
    assert_eq!(iter.next(), Some(2));
    assert_eq!(iter.next(), Some(1));
    assert_eq!(iter.next(), None);
}
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error: no method named `into_iter` found for type `stack_kata::day_1::Stack` in the current scope
  --> src/stack_kata/day_1.rs:85:30
   |
85 |         let mut iter = stack.into_iter();
   |                              ^^^^^^^^^
   |
   = note: the method `into_iter` exists but the following trait bounds were not satisfied: `stack_kata::day_1::Stack : std::iter::Iterator`, `&stack_kata::day_1::Stack : std::iter::Iterator`, `&mut stack_kata::day_1::Stack : std::iter::Iterator`
   = help: items from traits can only be used if the trait is implemented and in scope; the following trait defines an item `into_iter`, perhaps you need to implement it:
   = help: candidate #1: `std::iter::IntoIterator`

error: aborting due to previous error

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

`Rust` is telling about `std::iter::IntoIterator` but let's ignore it for a while and add `into_iter` function to `impl` block.

```rust
    pub fn into_iter(self) ->  {

    }
```

What should it return? Let's define a `struct` - `StackIter` with `next` function in its `impl` block and make `into_iter` return an object of the `StackIter` `struct`.

```rust
pub struct Stack {
    head: Link
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            head: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        self.head.take().map(|old_head| {
            let head = *old_head;
            self.head = head.next;
            head.item
        })
    }

    pub fn push(&mut self, item: i32) {
        let new_head = Box::new(Node {
            item: item,
            next: self.head.take()
        });

        self.head = Some(new_head);
    }

    pub fn into_iter(self) -> StackIter {
        StackIter
    }
}

pub struct StackIter;

impl StackIter {
    pub fn next(&mut self) -> Option<i32> {
        None
    }
}
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
    Finished dev [unoptimized + debuginfo] target(s) in 0.0 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 4 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok
test stack_kata::day_1::tests::iterate_over_stack ... FAILED

failures:

---- stack_kata::day_1::tests::iterate_over_stack stdout ----
	thread 'stack_kata::day_1::tests::iterate_over_stack' panicked at 'assertion failed: `(left == right)` (left: `None`, right: `Some(3)`)', src/stack_kata/day_1.rs:99
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    stack_kata::day_1::tests::iterate_over_stack

test result: FAILED. 3 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

Cool, it compiles. Now we can implement `next` function in `StackIter`. We define that `into_iter` moves `Stack` object and `next` function returns `Option<i32>` so we can add `stack` field in `StackIter` and delegate call to `Stack`'s `pop` function when `next` is invoked.

```rust
//impl Stack is omitted
    pub fn into_iter(self) -> StackIter {
        StackIter { stack: self }
    }

pub struct StackIter {
    stack: Stack
}

impl StackIter {
    pub fn next(&mut self) -> Option<i32> {
        self.stack.pop()
    }
}
```

Run the tests:

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.30 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 4 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::iterate_over_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok

test result: ok. 4 passed; 0 failed; 0 ignored; 0 measured

   Doc-tests code-katas

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured
```

Great. Let's refactor a bit. First of all, `Rust` has `Iterator` `trait` which has lots of functions including `next` which is the only one that should be implemented. To implement `trait` for a `struct` you have to define `impl trait-name for struct-name` block.

```rust
pub struct StackIter {
    stack: Stack
}

impl Iterator for StackIter {
    type Item = i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.stack.pop()
    }
}
```

> `Trait`s have only public functions, therefore, you don't need to specify that `next` has a `pub` access type.

Do you remember that `Rust` said about `std::iter::IntoIterator`? It is a `trait` in the standard library that has `into_iter` function. With the implementation of this `trait` you may write you collections in `for` loop as in the following snippet:

```rust
for item in stack {
    //do stuff with item
}
```


```rust
impl IntoIterator for Stack {
    type Item = i32;
    type IntoIter = StackIter;

    fn into_iter(self) -> Self::IntoIter {
        StackIter { stack: self }
    }
}
```

Rerun the tests

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.20 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 4 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::iterate_over_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok

test result: ok. 4 passed; 0 failed; 0 ignored; 0 measured

   Doc-tests code-katas

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured
```

## Iterate over stack several times

You should know that after calling `into_iter` an object of our `Stack` will be moved and we could not use it anymore. Let's implement `Iterator` that borrows our `Stack` instead of moving. The test is almost the same as for `StackIter` instead of returning `Option<i32>` borrowing iterator's `next` function will return `Option<&i32>`.

```rust
#[test]
fn ref_iterator_over_stack() {
    let mut stack = Stack::empty();

    stack.push(1);
    stack.push(2);
    stack.push(3);

    let mut iter = stack.into_iter();

    assert_eq!(iter.next(), Some(&3));
    assert_eq!(iter.next(), Some(&2));
    assert_eq!(iter.next(), Some(&1));
    assert_eq!(iter.next(), None);
}
```

Run the tests

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0308]: mismatched types
   --> src/stack_kata/day_1.rs:143:9
    |
143 |         assert_eq!(iter.next(), Some(&3));
    |         ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ expected i32, found &{integer}
    |
    = note: expected type `std::option::Option<i32>`
               found type `std::option::Option<&{integer}>`
    = help: here are some functions which might fulfill your needs:
            - .cloned()
            - .take()
            - .unwrap()
    = note: this error originates in a macro outside of the current crate

error[E0308]: mismatched types
   --> src/stack_kata/day_1.rs:144:9
    |
144 |         assert_eq!(iter.next(), Some(&2));
    |         ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ expected i32, found &{integer}
    |
    = note: expected type `std::option::Option<i32>`
               found type `std::option::Option<&{integer}>`
    = help: here are some functions which might fulfill your needs:
            - .cloned()
            - .take()
            - .unwrap()
    = note: this error originates in a macro outside of the current crate

error[E0308]: mismatched types
   --> src/stack_kata/day_1.rs:145:9
    |
145 |         assert_eq!(iter.next(), Some(&1));
    |         ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ expected i32, found &{integer}
    |
    = note: expected type `std::option::Option<i32>`
               found type `std::option::Option<&{integer}>`
    = help: here are some functions which might fulfill your needs:
            - .cloned()
            - .take()
            - .unwrap()
    = note: this error originates in a macro outside of the current crate

error: aborting due to 3 previous errors

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

Oh no! `Rust` argues on types. Let's try to fix this by writing `(&stack).into_iter()` instead of `stack.into_iter()`. Rerun the tests. Common man! The same stuff. Let's go by another way. In terms of `Rust`, `Stack`, `&Stack` and `&mut Stack` are different types. That allows us to implement the same `trait` by one `struct`. Given that, we can define `impl IntoIterator for &Stack` and define the `RefStackIter` `struct` to be our borrowing iterator.

```rust
impl IntoIterator for &Stack {
    type Item = &i32;
    type IntoIter = RefStackIter;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter
    }
}

pub struct RefStackIter;

impl Iterator for RefStackIter {
    type Item = &i32;

    fn next(&mut self) -> Option<Self::Item> {
        None
    }
}
```

Let's run tests again.

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0106]: missing lifetime specifier
  --> src/stack_kata/day_1.rs:37:23
   |
37 | impl IntoIterator for &Stack {
   |                       ^ expected lifetime parameter

error[E0106]: missing lifetime specifier
  --> src/stack_kata/day_1.rs:38:17
   |
38 |     type Item = &i32;
   |                 ^ expected lifetime parameter

error[E0106]: missing lifetime specifier
  --> src/stack_kata/day_1.rs:49:17
   |
49 |     type Item = &i32;
   |                 ^ expected lifetime parameter

error: aborting due to 3 previous errors

error: Could not compile `code-katas`.
```

Lifetimes. Shit, it may be the hardest part of `Rust` language. I won't spend lots of time to discuss what lifetimes are. There are lots of blog posts about them. You may google it and find more information. However, I say that each reference has its lifetime. Reference's lifetime should not be longer than the lifetime of the variable to which it referring. In our case, references to `i32` should have at most the same lifetime as a `Stack` `struct` because they are references to the `Stack` items.

```rust
impl<'i> IntoIterator for &'i Stack {
    type Item = &'i i32;
    type IntoIter = RefStackIter;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter
    }
}

pub struct RefStackIter;

impl Iterator for RefStackIter {
    type Item = &'i i32;

    fn next(&mut self) -> Option<Self::Item> {
        None
    }
}
```

Rerun the tests

```rust
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0261]: use of undeclared lifetime name `'i`
  --> src/stack_kata/day_1.rs:49:18
   |
49 |     type Item = &'i i32;
   |                  ^^ undeclared lifetime

error: aborting due to previous error

error: Could not compile `code-katas`.
```

This might be the reason why lifetimes the hardest part. You won't compile you program from the first time because of them. Lifetimes are declared on the `struct`s, so our `RefStackIter` becomes `RefStackIter<'i>`.

```rust
impl<'i> IntoIterator for &'i Stack {
    type Item = &'i i32;
    type IntoIter = RefStackIter<'i>;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter
    }
}

pub struct RefStackIter<'i>;

impl<'i> Iterator for RefStackIter<'i> {
    type Item = &'i i32;

    fn next(&mut self) -> Option<Self::Item> {
        None
    }
}
```

Run tests

```rust
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0392]: parameter `'i` is never used
  --> src/stack_kata/day_1.rs:46:25
   |
46 | pub struct RefStackIter<'i>;
   |                         ^^ unused type parameter
   |
   = help: consider removing `'i` or using a marker such as `std::marker::PhantomData`

error: aborting due to previous error

error: Could not compile `code-katas`.
```

Rust, you must be kidding? `RefStackIter<'i>` doesn't have any fields on which we can apply `'i` lifetime on. However, `Rust` has `std::marker::PhantomData` `struct` that we can use. `PhantomData` wouldn't appear in our `struct` at runtime. We help `Rust`'s type checker to understand that `'i` is used and then the compiler will remove it.

```rust
impl<'i> IntoIterator for &'i Stack {
    type Item = &'i i32;
    type IntoIter = RefStackIter<'i>;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter { marker: PhantomData }
    }
}

use std::marker::PhantomData;

pub struct RefStackIter<'i> {
    marker: PhantomData<&'i i32>
}

impl<'i> Iterator for RefStackIter<'i> {
    type Item = &'i i32;

    fn next(&mut self) -> Option<Self::Item> {
        None
    }
}
```

Run the tests.

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.35 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 5 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::iterate_over_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok
test stack_kata::day_1::tests::ref_iterator_over_stack ... FAILED

failures:

---- stack_kata::day_1::tests::ref_iterator_over_stack stdout ----
	thread 'stack_kata::day_1::tests::ref_iterator_over_stack' panicked at 'assertion failed: `(left == right)` (left: `None`, right: `Some(3)`)', src/stack_kata/day_1.rs:147
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    stack_kata::day_1::tests::ref_iterator_over_stack

test result: FAILED. 4 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

Eventually, we made `Rust` compile our code. Let's fix our test by adding the `current` field to `RefStackIter` which type will be `Option<&'i Node>` (notice the lifetime). The `current` field is a reference to the `Stack` elements and when we call `next` function we make `current` refer to the next element in the `Stack` and return a reference to the `current` `item`. To do that we need to use already known `take` and `map` functions and `as_ref` which is the new one. `Option::as_ref` function borrows internal `Option` `element` and returns `Some(&element)` if there is one and `None` if there is not.

```rust
impl<'i> IntoIterator for &'i Stack {
    type Item = &'i i32;
    type IntoIter = RefStackIter<'i>;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter { current: self.head.as_ref().map(|head| &**head), marker: PhantomData }
    }
}

use std::marker::PhantomData;

pub struct RefStackIter<'i> {
    current: Option<&'i Node>,
    marker: PhantomData<&'i i32>
}

impl<'i> Iterator for RefStackIter<'i> {
    type Item = &'i i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.current.take().map(|node| {
            self.current = node.next.as_ref().map(|next| &**next);
            &node.item
        })
    }
}
```

Run the tests

```sh
$ cargo test stack_kata::day_1
    Finished dev [unoptimized + debuginfo] target(s) in 0.0 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 5 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::iterate_over_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok
test stack_kata::day_1::tests::ref_iterator_over_stack ... ok

test result: ok. 5 passed; 0 failed; 0 ignored; 0 measured

   Doc-tests code-katas

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured
```

> Lets break down the `self.head.as_ref().map(|head| &**head)` line. `self.head` type is `Option<Box<Node>>`, when we call `as_ref` it returns `Option<&Box<Node>>`. We had to write a closure for `map` function that takes a parameter with `&Box<Node>` type. Thus, we need dereference `&Box<Node>` to `Box<Node>` by applying `*` operator and dereference `Box<Node>` to `Node`. However, the closure has to return `&Node` instead of `Node`, that's why we apply `&` on `**head`.

Instead of writing ugly `(&stack).into_iter()` we can implement `as_ref` function for our `Stack`. And guess what? `Rust` has `AsRef` trait that has `as_ref` function, let's implement the trait for the `Stack`. And remove useless `PhantomData` field on `RefStackIter`.

```rust
type Link = Option<Box<Node>>;

struct Node {
    item: i32,
    next: Link
}

pub struct Stack {
    head: Link
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            head: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        self.head.take().map(|old_head| {
            let head = *old_head;
            self.head = head.next;
            head.item
        })
    }

    pub fn push(&mut self, item: i32) {
        let new_head = Box::new(Node {
            item: item,
            next: self.head.take()
        });

        self.head = Some(new_head);
    }
}

impl AsRef<Stack> for Stack {
    fn as_ref(&self) -> &Self {
        self
    }
}

impl<'i> IntoIterator for &'i Stack {
    type Item = &'i i32;
    type IntoIter = RefStackIter<'i>;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter { current: self.head.as_ref().map(|head| &**head) }
    }
}

pub struct RefStackIter<'i> {
    current: Option<&'i Node>
}

impl<'i> Iterator for RefStackIter<'i> {
    type Item = &'i i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.current.take().map(|node| {
            self.current = node.next.as_ref().map(|next| &**next);
            &node.item
        })
    }
}

impl IntoIterator for Stack {
    type Item = i32;
    type IntoIter = StackIter;

    fn into_iter(self) -> Self::IntoIter {
        StackIter { stack: self }
    }
}

pub struct StackIter {
    stack: Stack
}

impl Iterator for StackIter {
    type Item = i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.stack.pop()
    }
}

#[cfg(test)]
mod tests {
    use super::Stack;

    #[test]
    fn creates_an_empty_stack() {
        let mut stack = Stack::empty();

        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_into_pops_out_one() {
        let mut stack = Stack::empty();

        stack.push(1);

        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);

        stack.push(2);

        assert_eq!(stack.pop(), Some(2));
        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_thee_elements_into_stack() {
        let mut stack = Stack::empty();

        stack.push(1);
        stack.push(2);
        stack.push(3);

        assert_eq!(stack.pop(), Some(3));
        assert_eq!(stack.pop(), Some(2));
        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn iterate_over_stack() {
        let mut stack = Stack::empty();

        stack.push(1);
        stack.push(2);
        stack.push(3);

        let mut iter = stack.into_iter();

        assert_eq!(iter.next(), Some(3));
        assert_eq!(iter.next(), Some(2));
        assert_eq!(iter.next(), Some(1));
        assert_eq!(iter.next(), None);
    }

    #[test]
    fn ref_iterator_over_stack() {
        let mut stack = Stack::empty();

        stack.push(1);
        stack.push(2);
        stack.push(3);

        let mut iter = stack.as_ref().into_iter();

        assert_eq!(iter.next(), Some(&3));
        assert_eq!(iter.next(), Some(&2));
        assert_eq!(iter.next(), Some(&1));
        assert_eq!(iter.next(), None);
    }
}
```

## Iterator that uniquely borrows a `Stack` object

By analogy, we can implement an `Iterator` that borrows `Stack` with mutable access to its elements.

```rust
#[test]
fn ref_mut_iterator_over_stack() {
    let mut stack = Stack::empty();

    stack.push(1);
    stack.push(2);
    stack.push(3);

    let mut iter = stack.as_mut().into_iter();

    assert_eq!(iter.next(), Some(&mut 3));
    assert_eq!(iter.next(), Some(&mut 2));
    assert_eq!(iter.next(), Some(&mut 1));
    assert_eq!(iter.next(), None);
}
```

Lets implement `IntoIterator` trait for `&mut Stack`, `AsMut` trait for `Stack` and define `RefMutStackIter` and implement `Iterator` trait for it.

```rust
impl AsMut<Stack> for Stack {
    fn as_mut(&mut self) -> &mut Self {
        self
    }
}

impl<'mi> IntoIterator for &'mi mut Stack {
    type Item = &'mi mut i32;
    type IntoIter = RefMutStackIter<'mi>;

    fn into_iter(self) -> Self::IntoIter {
        RefMutStackIter { current: self.head.as_mut().map(|head| &mut **head)}
    }
}

pub struct RefMutStackIter<'mi> {
    current: Option<&'mi mut Node>
}

impl<'mi> Iterator for RefMutStackIter<'mi> {
    type Item = &'mi mut i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.current.take().map(|node| {
            self.current = node.next.as_mut().map(|next| &mut **next);
            &mut node.item
        })
    }
}
```

Run the tests

```sh
$ cargo test stack_kata::day_1
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.14 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 6 tests
test stack_kata::day_1::tests::creates_an_empty_stack ... ok
test stack_kata::day_1::tests::pushes_into_pops_out_one ... ok
test stack_kata::day_1::tests::pushes_thee_elements_into_stack ... ok
test stack_kata::day_1::tests::iterate_over_stack ... ok
test stack_kata::day_1::tests::ref_iterator_over_stack ... ok
test stack_kata::day_1::tests::ref_mut_iterator_over_stack ... ok

test result: ok. 6 passed; 0 failed; 0 ignored; 0 measured

   Doc-tests code-katas

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured
```

Awesome!

## Last but not least

In all tests with iterators, we write the same three lines:

```rust
stack.push(1);
stack.push(2);
stack.push(3);
```

Looks like we tried to create `Stack` object from another collection. `Rust` has `FromIterator` `trait` that has `from_iter` function with `IntoIterator` `trait` as a parameter. After implementing the `FromIterator` `trait` for `Stack` we will be able to create `Stack`, for example from ranges, and replace

```rust
let mut stack = Stack::empty();

stack.push(1);
stack.push(2);
stack.push(3);
```

with

```rust
let mut stack = Stack::from_iter(1..4);
```

```rust
use std::iter::FromIterator;

type Link = Option<Box<Node>>;

struct Node {
    item: i32,
    next: Link
}

pub struct Stack {
    head: Link
}

impl Stack {
    pub fn empty() -> Self {
        Stack {
            head: None
        }
    }

    pub fn pop(&mut self) -> Option<i32> {
        self.head.take().map(|old_head| {
            let head = *old_head;
            self.head = head.next;
            head.item
        })
    }

    pub fn push(&mut self, item: i32) {
        let new_head = Box::new(Node {
            item: item,
            next: self.head.take()
        });

        self.head = Some(new_head);
    }
}

impl AsMut<Stack> for Stack {
    fn as_mut(&mut self) -> &mut Self {
        self
    }
}

impl<'mi> IntoIterator for &'mi mut Stack {
    type Item = &'mi mut i32;
    type IntoIter = RefMutStackIter<'mi>;

    fn into_iter(self) -> Self::IntoIter {
        RefMutStackIter { current: self.head.as_mut().map(|head| &mut **head)}
    }
}

pub struct RefMutStackIter<'mi> {
    current: Option<&'mi mut Node>
}

impl<'mi> Iterator for RefMutStackIter<'mi> {
    type Item = &'mi mut i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.current.take().map(|node| {
            self.current = node.next.as_mut().map(|next| &mut **next);
            &mut node.item
        })
    }
}

impl AsRef<Stack> for Stack {
    fn as_ref(&self) -> &Self {
        self
    }
}

impl<'i> IntoIterator for &'i Stack {
    type Item = &'i i32;
    type IntoIter = RefStackIter<'i>;

    fn into_iter(self) -> Self::IntoIter {
        RefStackIter { current: self.head.as_ref().map(|head| &**head) }
    }
}

pub struct RefStackIter<'i> {
    current: Option<&'i Node>
}

impl<'i> Iterator for RefStackIter<'i> {
    type Item = &'i i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.current.take().map(|node| {
            self.current = node.next.as_ref().map(|next| &**next);
            &node.item
        })
    }
}

impl IntoIterator for Stack {
    type Item = i32;
    type IntoIter = StackIter;

    fn into_iter(self) -> Self::IntoIter {
        StackIter { stack: self }
    }
}

pub struct StackIter {
    stack: Stack
}

impl Iterator for StackIter {
    type Item = i32;

    fn next(&mut self) -> Option<Self::Item> {
        self.stack.pop()
    }
}

impl FromIterator<i32> for Stack {
    fn from_iter<I: IntoIterator<Item = i32>>(items: I) -> Self {
        let mut stack = Stack::empty();
        for item in items {
            stack.push(item);
        }
        stack
    }
}

#[cfg(test)]
mod tests {
    use super::Stack;
    use std::iter::FromIterator;

    #[test]
    fn creates_an_empty_stack() {
        let mut stack = Stack::empty();

        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_into_pops_out_one() {
        let mut stack = Stack::empty();

        stack.push(1);

        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);

        stack.push(2);

        assert_eq!(stack.pop(), Some(2));
        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn pushes_thee_elements_into_stack() {
        let mut stack = Stack::from_iter(1..4);

        assert_eq!(stack.pop(), Some(3));
        assert_eq!(stack.pop(), Some(2));
        assert_eq!(stack.pop(), Some(1));
        assert_eq!(stack.pop(), None);
    }

    #[test]
    fn iterate_over_stack() {
        let mut stack = Stack::from_iter(1..4);

        let mut iter = stack.into_iter();

        assert_eq!(iter.next(), Some(3));
        assert_eq!(iter.next(), Some(2));
        assert_eq!(iter.next(), Some(1));
        assert_eq!(iter.next(), None);
    }

    #[test]
    fn ref_iterator_over_stack() {
        let stack = Stack::from_iter(1..4);

        let mut iter = stack.as_ref().into_iter();

        assert_eq!(iter.next(), Some(&3));
        assert_eq!(iter.next(), Some(&2));
        assert_eq!(iter.next(), Some(&1));
        assert_eq!(iter.next(), None);
    }

    #[test]
    fn ref_mut_iterator_over_stack() {
        let mut stack = Stack::from_iter(1..4);

        let mut iter = stack.as_mut().into_iter();

        assert_eq!(iter.next(), Some(&mut 3));
        assert_eq!(iter.next(), Some(&mut 2));
        assert_eq!(iter.next(), Some(&mut 1));
        assert_eq!(iter.next(), None);
    }
}
```

## Wrap up

We had written a bunch of tests. Some may argue that they are silly and useless. However, if we change the internal representation of our `Stack` `struct`, for instance replacing linked list with a vector, we can automatically check that we haven't broken anything.
The other argument is that write tests first may be a bad idea. But, in my opinion, when you write tests first you ask yourself "can I prove that 'X'?" and by implementing the correct functionality you say "yes, I can prove 'X'".
