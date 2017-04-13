---
layout: post
title: Bowling Game Code Kata. TL;DR
---

By doing this exercise we implement functionality to count a score of a [pin bowling game](https://en.wikipedia.org/wiki/Bowling)

## "Hello, world!"

A classical example of the introduction to a new programming language is printing out "Hello, world!" to the standard output. However, I start learning programming languages with code exercise described in this post. The exercise is very simple and shows basic language syntax.

## Project layout

If you start the project as described in the previous post you should have project structure as follows:

```
code-katas/
    |
    +-src/
    |   |
    |   +-lib.rs
    |
    +-Cargo.toml
```

Now we need to create Rust files for bowling game kata; in terms of Rust, each file is a module. To do that you need to create file `src/bowling_game_kata.rs` and add `pub mod bowling_game_kata;` line to `src/lib.rs` (Remove empty `it_works` test from `src/lib.rs` files if you haven't done it yet).
Now your project should look like:

```
code-katas/
    |
    +-src/
    |   |
    |   +-bowling_game_kata.rs
    |   +-lib.rs
    |
    +-Cargo.toml
```

> In our case, `src/lib.rs` is the root module of the crate. The others `*.rs` files will be modules with the same name as the files. You may declare sub-module inside `*.rs` file by typing:
>
>```rust
>mod module_name {
>    //module stuff is here
>}
>```
>
>A folder can be a module as well. But you have to create `mod.rs` file inside it.
>
>```
> crate/
>   |
>   +-src/
>   |   |
>   |   +-folder_mod/
>   |   |   |
>   |   |   +-mod.rs //module name is 'folder_mod'
>   |   |   +-sub_module_of_folder_mod.rs
>   |   +- lib.rs
>   +-Cargo.toml
>```
>

## Set up tests environment

This is a step that I perform before start my daily code kata to make sure that I can run tests and able to check that I haven't broken anything in my code. Type the following code into `src/bowling_game_kata.rs`:

```rust
#[cfg(test)]
mod tests {
    #[test]
    fn nothing() {
    }
}
```

And then run `cargo test` in your project directory. You should see similar to:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 0.67 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 1 test
test bowling_game_kata::tests::nothing ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured

   Doc-tests code-katas

running 0 tests

test result: ok. 0 passed; 0 failed; 0 ignored; 0 measured
```

Cargo runs two types of tests. Tests for code and documentation. Doc-tests you may ignore, we are learning how to write code not, a documentation.

Make sure that our `bowling_game_kata::tests::nothing` was run. This is an indicator that we can continue our exercise.

> Marking module by `#[cfg(test)]` attribute we make it special for the compiler, it won't be in the binary of Rust project. It is idiomatic for Rust to put unit tests inside a sub-module.
> To make any function a test you need to mark it by `#[test]` attribute. That's it.

## The very first test

All who teach TDD say that you need to start with the simplest test case. Sometimes, the simplest thing is a very hard thing. If you are in the beginning of practicing TDD or don't know what the first test should be; think what your first line of production code would be. In our case for counting bowling the game score we definitely need to create a `Game` `struct`, so let's write a test for this.

Remove our `nothing` test and write a test for creating an object of bowling game structure.

```rust
    #[test]
    fn creates_bowling_game() {
        let game = Game;
    }
```

Run our test.

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error[E0425]: cannot find value `Game` in this scope
 --> src/bowling_game_kata.rs:5:20
  |
5 |         let game = Game;
  |                    ^^^^ not found in this scope

error: aborting due to previous error

error: Could not compile `code-katas`.
Build failed, waiting for other jobs to finish...
error: build failed
```

Heh, nothing special. Just a compile time error. Rust compiler tells us that it can't find `Game`. Let's help him. Declare `Game` `struct` in our `bowling_game_kata` module by typing `pub struct Game;` line. Run the test again.

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
warning: struct is never used: `Game`, #[warn(dead_code)] on by default
 --> src/bowling_game_kata.rs:1:1
  |
1 | pub struct Game;
  | ^^^^^^^^^^^^^^^^

error[E0425]: cannot find value `Game` in this scope
 --> src/bowling_game_kata.rs:7:20
  |
7 |         let game = Game;
  |                    ^^^^ not found in this scope
  |
  = help: possible candidate is found in another module, you can import it into scope:
            `use bowling_game_kata::Game;`

error: aborting due to previous error

error: Could not compile `code-katas`.
Build failed, waiting for other jobs to finish...
error: build failed
```

Ah, we actually forget to import `Game` struct into our `tests` module.

> By the way, notice that compiler gives us a hint how to fix error by printing
>
>```sh
> = help: possible candidate is found in another module, you can import it into scope:
>
>           `use bowling_game_kata::Game;`
>```
>

So add `use bowling_game_kata::Game;` to our `tests` module. Your code should look like this by now:

```rust
pub struct Game;

#[cfg(test)]
mod tests {
    use bowling_game_kata::Game;

    #[test]
    fn creates_bowling_game() {
        let game = Game;
    }
}
```

Run the test.

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
warning: unused variable: `game`, #[warn(unused_variables)] on by default
 --> src/bowling_game_kata.rs:9:13
  |
9 |         let game = Game;
  |             ^^^^

    Finished dev [unoptimized + debuginfo] target(s) in 0.56 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 1 test
test bowling_game_kata::tests::creates_bowling_game ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured
```

Hooray, we pass the first test. However, TDD is not just to write tests and pass them. TDD has three stages `RED`, when we write a test that fails, `GREEN`, when we write least production code to make the test pass, and `BLUE`, when we refactor our code and rerun tests to make sure that we haven't screwed up anything. At this point we in the `BLUE` stage, but what can be refactored? Not that much. The `Game;` part of our test is not saying anything useful, for example, that we instantiate a `Game` object. Let's write a static factory function. Functions that have access to a `struct` should be defined in `impl` block.

```rust
pub struct Game;

impl Game {
    pub fn new() -> Game {
        Game
    }
}

#[cfg(test)]
mod tests {
    use bowling_game_kata::Game;

    #[test]
    fn creates_bowling_game() {
        let game = Game::new();
    }
}
```

And check that we haven't broken anything.

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
warning: unused variable: `game`, #[warn(unused_variables)] on by default
  --> src/bowling_game_kata.rs:15:13
   |
15 |         let game = Game::new();
   |             ^^^^

    Finished dev [unoptimized + debuginfo] target(s) in 0.69 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 1 test
test bowling_game_kata::tests::creates_bowling_game ... ok

test result: ok. 1 passed; 0 failed; 0 ignored; 0 measured
```

We got a compiler warning but who cares, we pass the test and we can move on.

> Rust does not have constructors as a language item. Idiomatically static functions in `impl` blocks are used for this purpose.

## Gutter game

When you are doing TDD, tests should drive your development (that's why TDD is called TDD). You need to write tests that test behavior, not API. So what behavior we need to test next? Do not forget a test case should be the simplest as possible. How about a player rolls a ball and has knocked down none of the pins for the entire game? Let's write this test and run.

```rust
#[test]
fn gutter_game() {
    let mut game = Game::new();

    for _ in 0..20 {
        game.roll(0);
    }

    assert_eq!(game.score(), 0);
}
```

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
error: no method named `roll` found for type `bowling_game_kata::Game` in the current scope
  --> src/bowling_game_kata.rs:22:18
   |
22 |             game.roll(0);
   |                  ^^^^

error: no method named `score` found for type `bowling_game_kata::Game` in the current scope
  --> src/bowling_game_kata.rs:25:25
   |
25 |         assert_eq!(game.score(), 0);
   |                         ^^^^^

error: aborting due to 2 previous errors

error: Could not compile `code-katas`.

To learn more, run the command again with --verbose.
```

>By default Rust variables are immutable. To make them mutable we need to specify it by using `mut` keyword in their declaration.

To fix this mess we need define two more functions: `roll` to roll a bowling ball and `score` to count the game score.

```rust
impl Game {
    pub fn new() -> Game {
        Game
    }

    pub fn roll(&mut self, pins: i32) {
    }

    pub fn score(self) -> i32 {
        -1
    }
}
```

```sh
$ cargo test
    Finished dev [unoptimized + debuginfo] target(s) in 1.1 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 2 tests
test bowling_game_kata::tests::creates_bowling_game ... ok
test bowling_game_kata::tests::gutter_game ... FAILED

failures:

---- bowling_game_kata::tests::gutter_game stdout ----
	thread 'bowling_game_kata::tests::gutter_game' panicked at 'assertion failed: `(left == right)` (left: `-1`, right: `0`)', src/bowling_game_kata.rs:32
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    bowling_game_kata::tests::gutter_game

test result: FAILED. 1 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

I deliberately made `score` return `-1`. Why? To make sure that the test does something useful. "If tests test production code. Who tests the tests?" - it is a common question of newbies who start learning TDD. By failing the test we make sure that it covers some piece of our production functionality.
Let's change `-1` to `0` to pass the test.

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)

    //warnings are omitted

    Finished dev [unoptimized + debuginfo] target(s) in 1.8 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 2 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::creates_bowling_game ... ok

test result: ok. 2 passed; 0 failed; 0 ignored; 0 measured
```

> Some notes on writing `mut` keyword near a function arguments.
> One may argue that it is too verbose to write `mut` to define that an argument will be mutated by the function and compiler could figure it out from the context.
> However, by doing TDD you first define API that you would like your program has and than implement it; when I type `mut` I start thinking "Do I really need to mutate this argument? Can I come up with API that doesn't mutate inner state?". The other benefit from this is that when you look at a function signature you immediately see that the function has side effects, which is good to know.

>As you may know, if you pass an argument to a function by value, ownership will be moved to the called function. What is happening when the function takes `self` by value? After returning from the function call compiler will prevent us from using this object and the allocated memory, used by the object, will be freed. For example:
>
>```rust
>fn some_function() {
>   let mut game = Game::new();
>   play_the_game(&mut game);
>   let score = game.score();
>   //game.score() //<- compile time error error[E0382]: use of moved value: `game`
>
>   //do stuff with score
>
>   //here game object will be dropped and memory freed
>}
>```
>

Now we can refactor. `i32` is a signed type, however, the number of `pins` and game `score` can't be negative, so let's change them to `u32` type. Run the tests, it still works. Tests are code, therefore, we need to clean them up too. As you may notice `gutter_game` test creates `Game`, thus, we can remove `creates_bowling_game` test. Now your code should look like:

```rust
pub struct Game;

impl Game {
    pub fn new() -> Game {
        Game
    }

    pub fn roll(&mut self, pins: u32) {
    }

    pub fn score(self) -> u32 {
        0
    }
}

#[cfg(test)]
mod tests {
    use bowling_game_kata::Game;

    #[test]
    fn gutter_game() {
        let mut game = Game::new();
        for _ in 0..20 {
            game.roll(0);
        }

        assert_eq!(game.score(), 0);
    }
}
```

## All ones

The next test case is a little bit more complex than previous. We have a bowling game when only one pin was knocked down on a roll. Here is a test for it:

```rust
#[test]
fn all_ones() {
    let mut game = Game::new();

    for _ in 0..20 {
        game.roll(1);
    }

    assert_eq!(game.score(), 20);
}
```

Run the tests.

```sh
$ cargo test
    Finished dev [unoptimized + debuginfo] target(s) in 1.23 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 2 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::all_ones ... FAILED

failures:

---- bowling_game_kata::tests::all_ones stdout ----
	thread 'bowling_game_kata::tests::all_ones' panicked at 'assertion failed: `(left == right)` (left: `0`, right: `20`)', src/bowling_game_kata.rs:37
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    bowling_game_kata::tests::all_ones

test result: FAILED. 1 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

To make it pass we can introduce a field `points` to our `Game` `struct` and add a number of pins knocked down when `roll`ing a bowling ball (Actually, `score` is better name for the field, but I don't want to mix `score` field and function names here).

```rust
pub struct Game {
    points: u32
}

impl Game {
    pub fn new() -> Game {
        Game { points: 0 }
    }

    pub fn roll(&mut self, pins: u32) {
        self.points += pins;
    }

    pub fn score(self) -> u32 {
        self.points
    }
}
```

Run the tests. We passed them. Making all tests `GREEN` we are allowed to refactor. We have code duplication in tests. These `for` loops can be moved into help function. Let's called it `roll_many`; it will take three arguments: `game`, `times` and `pins`.

```rust
fn roll_many(game: &mut Game, times: u32, pins: u32) {
    for _ in 0..times {
        game.roll(pins);
    }
}

#[test]
fn gutter_game() {
    let mut game = Game::new();

    roll_many(&mut game, 20, 0);

    assert_eq!(game.score(), 0);
}

#[test]
fn all_ones() {
    let mut game = Game::new();

    roll_many(&mut game, 20, 1);

    assert_eq!(game.score(), 20);
}
```

Do not forget to rerun the tests to make sure they are passed.

## Roll one spare

Now we are going to test functionality which takes into account `spare`. When all `10` frame pins knocked down by two rolls, is called `spare`. For each `spare` a player earns bonus points which are the number of pins knocked down by the next roll. So the test is the following:

```rust
#[test]
fn one_spare() {
    let mut game = Game::new();

    game.roll(5);
    game.roll(5);
    game.roll(3);
    roll_many(&mut game, 17, 0);

    assert_eq!(game.score(), 16);
}
```

Run the tests:

```sh
$ cargo test
    Finished dev [unoptimized + debuginfo] target(s) in 0.0 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::one_spare ... FAILED

failures:

---- bowling_game_kata::tests::one_spare stdout ----
	thread 'bowling_game_kata::tests::one_spare' panicked at 'assertion failed: `(left == right)` (left: `13`, right: `16`)', src/bowling_game_kata.rs:56
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    bowling_game_kata::tests::one_spare

test result: FAILED. 2 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

If you look at current implementation you will notice that we need to somehow save previous rolls and a frame index in `roll` function. It is too complicated. Let's refactor our code. But we can't do that because we have the failing test! `#[ignore]` attribute is going to rescue us. `#[ignore]` attribute signals Cargo that test should not be run. Put it on the `one_spare` test as follows:

```rust
#[test]
#[ignore]
fn one_spare() {
    let mut game = Game::new();

    game.roll(5);
    game.roll(5);
    game.roll(3);
    roll_many(&mut game, 17, 0);

    assert_eq!(game.score(), 16);
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.11 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::one_spare ... ignored
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::all_ones ... ok

test result: ok. 2 passed; 0 failed; 1 ignored; 0 measured
```

Great, we've got `GREEN` tests and we can refactor. The problem with current implementation is that we have computation where we are not expecting. We are counting the game score in `roll` function instead of `score`. Let's introduce `rolls` filed which type will be `std::vec::Vec`. It is a simple vector; we can append it by `push`ing items into it. We `push` the number of knocked down pins to `rolls` in each invocation of `roll` function and then looping the vector counting the game score in `score` function. So the code looks like:

```rust
pub struct Game {
    points: u32,
    rolls: Vec<u32>
}

impl Game {
    pub fn new() -> Game {
        Game { points: 0, rolls: Vec::new() }
    }

    pub fn roll(&mut self, pins: u32) {
        self.rolls.push(pins);
    }

    pub fn score(self) -> u32 {
        let mut score = 0;

        for pins in self.rolls {
            score += pins;
        }

        score
    }
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
warning: field is never used: `points`, #[warn(dead_code)] on by default
 --> src/bowling_game_kata.rs:2:5
  |
2 |     points: u32,
  |     ^^^^^^^^^^^

warning: field is never used: `points`, #[warn(dead_code)] on by default
 --> src/bowling_game_kata.rs:2:5
  |
2 |     points: u32,
  |     ^^^^^^^^^^^

    Finished dev [unoptimized + debuginfo] target(s) in 0.95 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::one_spare ... ignored
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::gutter_game ... ok

test result: ok. 2 passed; 0 failed; 1 ignored; 0 measured
```

Hah! Let's remove useless `points` field. However, a bowling game has `10` frames each of them has `2` rolls (if it is not a `strike`). So let rewrite loop to emphasize our business logic.

```rust
pub fn score(self) -> u32 {
    let mut score = 0;
    let mut roll_index = 0;

    for _ in 0..10 {
        score += self.rolls[roll_index] + self.rolls[roll_index + 1];
        roll_index += 2;
    }

    score
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.43 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::one_spare ... ignored
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::gutter_game ... ok

test result: ok. 2 passed; 0 failed; 1 ignored; 0 measured
```

So now we can go further. Let's remove `#[ignore]` attribute and make sure that we have a failing test.

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 0.94 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::one_spare ... FAILED

failures:

---- bowling_game_kata::tests::one_spare stdout ----
	thread 'bowling_game_kata::tests::one_spare' panicked at 'assertion failed: `(left == right)` (left: `13`, right: `16`)', src/bowling_game_kata.rs:64
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    bowling_game_kata::tests::one_spare

test result: FAILED. 2 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

Indeed, a miracle has not happened we need to fix the `one_spare` test. To do so we need to check that two rolls of the same frame knocked down `10` pins and add the bonus - the number of pins knocked down by the next roll:

```rust
for _ in 0..10 {
    if self.rolls[roll_index] + self.rolls[roll_index + 1] == 10 {
        score += 10 + self.rolls[roll_index + 2];
    } else {
        score += self.rolls[roll_index] + self.rolls[roll_index + 1];
    }
    roll_index += 2;
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.18 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::one_spare ... ok
test bowling_game_kata::tests::all_ones ... ok

test result: ok. 3 passed; 0 failed; 0 ignored; 0 measured
```

Hooray, let's refactor a bit. This line `self.rolls[roll_index] + self.rolls[roll_index + 1] == 10` is hard to read and actually has special meaning - it is a `spare`, so let's move it to a separated function called `is_spare`; it needs to take `roll_index` as argument. And `self.rolls[roll_index + 2]` move to `spare_bonus` function to underline what it really is.

```rust
pub fn score(self) -> u32 {
    let mut score = 0;
    let mut roll_index = 0;

    for _ in 0..10 {
        if self.is_spare(roll_index) {
            score += 10 + self.spare_bonus(roll_index);
        } else {
            score += self.rolls[roll_index] + self.rolls[roll_index + 1];
        }
        roll_index += 2;
    }

    score
}

fn is_spare(&self, roll_index: usize) -> bool {
    self.rolls[roll_index] + self.rolls[roll_index + 1] == 10
}

fn spare_bonus(&self, roll_index: usize) -> u32 {
    self.rolls[roll_index + 2]
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.23 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 3 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::one_spare ... ok
test bowling_game_kata::tests::all_ones ... ok

test result: ok. 3 passed; 0 failed; 0 ignored; 0 measured
```

Do not forget to clean up your tests too. These two `game.roll(5);` lines can be moved to `roll_spare` help function.

The code should look like this at the current stage:

```rust
pub struct Game {
    rolls: Vec<u32>
}

impl Game {
    pub fn new() -> Game {
        Game { rolls: Vec::new() }
    }

    pub fn roll(&mut self, pins: u32) {
        self.rolls.push(pins);
    }

    pub fn score(self) -> u32 {
        let mut score = 0;
        let mut roll_index = 0;

        for _ in 0..10 {
            if self.is_spare(roll_index) {
                score += 10 + self.spare_bonus(roll_index);
            } else {
                score += self.rolls[roll_index] + self.rolls[roll_index + 1];
            }
            roll_index += 2;
        }

        score
    }

    fn is_spare(&self, roll_index: usize) -> bool {
        self.rolls[roll_index] + self.rolls[roll_index + 1] == 10
    }

    fn spare_bonus(&self, roll_index: usize) -> u32 {
        self.rolls[roll_index + 2]
    }
}

#[cfg(test)]
mod tests {
    use bowling_game_kata::Game;

    fn roll_many(game: &mut Game, times: u32, pins: u32) {
        for _ in 0..times {
            game.roll(pins);
        }
    }

    fn roll_spare(game: &mut Game) {
        game.roll(5);
        game.roll(5);
    }

    #[test]
    fn gutter_game() {
        let mut game = Game::new();

        roll_many(&mut game, 20, 0);

        assert_eq!(game.score(), 0);
    }

    #[test]
    fn all_ones() {
        let mut game = Game::new();

        roll_many(&mut game, 20, 1);

        assert_eq!(game.score(), 20);
    }

    #[test]
    fn one_spare() {
        let mut game = Game::new();

        roll_spare(&mut game);
        game.roll(3);
        roll_many(&mut game, 17, 0);

        assert_eq!(game.score(), 16);
    }
}
```

## One strike

Now we are ready to roll a `strike`. Bonus for the `strike` is the sum of pins knocked down by the next two rolls. So the test is as follows:

```rust
#[test]
fn one_strike() {
    let mut game = Game::new();

    game.roll(10);
    game.roll(3);
    game.roll(4);
    roll_many(&mut game, 16, 0);

    assert_eq!(game.score(), 24);
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.17 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 4 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::one_spare ... ok
test bowling_game_kata::tests::one_strike ... FAILED

failures:

---- bowling_game_kata::tests::one_strike stdout ----
	thread 'bowling_game_kata::tests::one_strike' panicked at 'index out of bounds: the len is 19 but the index is 19', /Users/rustbuild/src/rust-buildbot/slave/nightly-dist-rustc-mac/build/src/libcollections/vec.rs:1392
note: Run with `RUST_BACKTRACE=1` for a backtrace.


failures:
    bowling_game_kata::tests::one_strike

test result: FAILED. 3 passed; 1 failed; 0 ignored; 0 measured

error: test failed
```

It fails. Progress! Now we can implement the functionality. The strategy is the same as with the `spare`. Check if a roll knocked down `10` pins and sum up the roll with the next two rolls pins.

```rust
pub fn score(self) -> u32 {
    let mut score = 0;
    let mut roll_index = 0;

    for _ in 0..10 {
        if self.rolls[roll_index] == 10 {
            score += 10 + self.rolls[roll_index + 1] + self.rolls[roll_index + 2];
            roll_index += 1;
        } else if self.is_spare(roll_index) {
            score += 10 + self.spare_bonus(roll_index);
            roll_index += 2;
        } else {
            score += self.rolls[roll_index] + self.rolls[roll_index + 1];
            roll_index += 2;
        }
    }

    score
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.41 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 4 tests
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::one_spare ... ok
test bowling_game_kata::tests::one_strike ... ok

test result: ok. 4 passed; 0 failed; 0 ignored; 0 measured
```

`GREEN`! It is about time for refactoring. Again the strategy is the same as with the `spare`. `self.rolls[roll_index] == 10` condition move to a separated function `is_strike` and `self.rolls[roll_index + 1] + self.rolls[roll_index + 2]` move to `strike_bonus` function. Also, move `game.roll(10);` to `roll_strike()` in our tests.

```rust
pub struct Game {
    rolls: Vec<u32>
}

impl Game {
    pub fn new() -> Game {
        Game { rolls: Vec::new() }
    }

    pub fn roll(&mut self, pins: u32) {
        self.rolls.push(pins);
    }

    pub fn score(self) -> u32 {
        let mut score = 0;
        let mut roll_index = 0;

        for _ in 0..10 {
            if self.is_strike(roll_index) {
                score += 10 + self.strike_bonus(roll_index);
                roll_index += 1;
            } else if self.is_spare(roll_index) {
                score += 10 + self.spare_bonus(roll_index);
                roll_index += 2;
            } else {
                score += self.rolls[roll_index] + self.rolls[roll_index + 1];
                roll_index += 2;
            }
        }

        score
    }

    fn is_strike(&self, roll_index: usize) -> bool {
        self.rolls[roll_index] == 10
    }

    fn strike_bonus(&self, roll_index: usize) -> u32 {
        self.rolls[roll_index + 1] + self.rolls[roll_index + 2]
    }

    fn is_spare(&self, roll_index: usize) -> bool {
        self.rolls[roll_index] + self.rolls[roll_index + 1] == 10
    }

    fn spare_bonus(&self, roll_index: usize) -> u32 {
        self.rolls[roll_index + 2]
    }
}

#[cfg(test)]
mod tests {
    use bowling_game_kata::Game;

    fn roll_many(game: &mut Game, times: u32, pins: u32) {
        for _ in 0..times {
            game.roll(pins);
        }
    }

    fn roll_spare(game: &mut Game) {
        game.roll(5);
        game.roll(5);
    }

    fn roll_strike(game: &mut Game) {
        game.roll(10);
    }

    #[test]
    fn gutter_game() {
        let mut game = Game::new();

        roll_many(&mut game, 20, 0);

        assert_eq!(game.score(), 0);
    }

    #[test]
    fn all_ones() {
        let mut game = Game::new();

        roll_many(&mut game, 20, 1);

        assert_eq!(game.score(), 20);
    }

    #[test]
    fn one_spare() {
        let mut game = Game::new();

        roll_spare(&mut game);
        game.roll(3);
        roll_many(&mut game, 17, 0);

        assert_eq!(game.score(), 16);
    }

    #[test]
    fn one_strike() {
        let mut game = Game::new();

        roll_strike(&mut game);
        game.roll(3);
        game.roll(4);
        roll_many(&mut game, 16, 0);

        assert_eq!(game.score(), 24);
    }
}
```

## Perfect game

The last case that should be considered is when a player rolls only the strikes. We need take into account `12` rolls; not `10`. Last two rolls are the bonus for the 10th `strike`. The test is:

```rust
#[test]
fn perfect_game() {
    let mut game = Game::new();

    roll_many(&mut game, 12, 10);

    assert_eq!(game.score(), 300);
}
```

Run the tests:

```sh
$ cargo test
   Compiling code-katas v0.1.0 (file:///Users/alex-diez/Projects/code-katas)
    Finished dev [unoptimized + debuginfo] target(s) in 1.27 secs
     Running target/debug/deps/code_katas-91959ec1e8c184b3

running 5 tests
test bowling_game_kata::tests::gutter_game ... ok
test bowling_game_kata::tests::all_ones ... ok
test bowling_game_kata::tests::one_spare ... ok
test bowling_game_kata::tests::one_strike ... ok
test bowling_game_kata::tests::perfect_game ... ok

test result: ok. 5 passed; 0 failed; 0 ignored; 0 measured
```

Passed? Sometimes you will write tests that pass. It is not bad, and it is not good either; because you would not understand why it had happened. Now we need to find out why it passed. In this particular case when we count the game score `is_strike` condition will be `true` all the time therefore `10` rolls times `10` pins plus `20` pins as the bonus gives us `300`. So we are done.

## Final code

```rust
pub struct Game {
    rolls: Vec<u32>
}

impl Game {
    pub fn new() -> Game {
        Game { rolls: Vec::new() }
    }

    pub fn roll(&mut self, pins: u32) {
        self.rolls.push(pins);
    }

    pub fn score(self) -> u32 {
        let mut score = 0;
        let mut roll_index = 0;

        for _ in 0..10 {
            if self.is_strike(roll_index) {
                score += 10 + self.strike_bonus(roll_index);
                roll_index += 1;
            } else if self.is_spare(roll_index) {
                score += 10 + self.spare_bonus(roll_index);
                roll_index += 2;
            } else {
                score += self.rolls[roll_index] + self.rolls[roll_index + 1];
                roll_index += 2;
            }
        }

        score
    }

    fn is_strike(&self, roll_index: usize) -> bool {
        self.rolls[roll_index] == 10
    }

    fn strike_bonus(&self, roll_index: usize) -> u32 {
        self.rolls[roll_index + 1] + self.rolls[roll_index + 2]
    }

    fn is_spare(&self, roll_index: usize) -> bool {
        self.rolls[roll_index] + self.rolls[roll_index + 1] == 10
    }

    fn spare_bonus(&self, roll_index: usize) -> u32 {
        self.rolls[roll_index + 2]
    }
}

#[cfg(test)]
mod tests {
    use bowling_game_kata::Game;

    fn roll_many(game: &mut Game, times: u32, pins: u32) {
        for _ in 0..times {
            game.roll(pins);
        }
    }

    fn roll_spare(game: &mut Game) {
        game.roll(5);
        game.roll(5);
    }

    fn roll_strike(game: &mut Game) {
        game.roll(10);
    }

    #[test]
    fn gutter_game() {
        let mut game = Game::new();

        roll_many(&mut game, 20, 0);

        assert_eq!(game.score(), 0);
    }

    #[test]
    fn all_ones() {
        let mut game = Game::new();

        roll_many(&mut game, 20, 1);

        assert_eq!(game.score(), 20);
    }

    #[test]
    fn one_spare() {
        let mut game = Game::new();

        roll_spare(&mut game);
        game.roll(3);
        roll_many(&mut game, 17, 0);

        assert_eq!(game.score(), 16);
    }

    #[test]
    fn one_strike() {
        let mut game = Game::new();

        roll_strike(&mut game);
        game.roll(3);
        game.roll(4);
        roll_many(&mut game, 16, 0);

        assert_eq!(game.score(), 24);
    }

    #[test]
    fn perfect_game() {
        let mut game = Game::new();

        roll_many(&mut game, 12, 10);

        assert_eq!(game.score(), 300);
    }
}
```

## Post Script

To master any skills it'd better repeat the exercise few times. When I started practicing TDD I did 14 days cycle for each code kata, now I do it in 10 days cycle (you know human psychology, we like rounded numbers).
I don't like lots of files in one directory that's why my project has the followings layout:

```
code-katas/
    |
    +-src/
    |   |
    |   +-bowling_game_kata/
    |   |   |
    |   |   +- day_1.rs
    |   |   +- day_2.rs
    |   |   | ...
    |   |   +- day_N.rs
    |   |   +- mod.rs
    |   |
    |   +- other_code_kata/
    |   |   |
    |   |   +- ...
    |   |
    |   +-lib.rs
    |
    +-Cargo.toml
```
