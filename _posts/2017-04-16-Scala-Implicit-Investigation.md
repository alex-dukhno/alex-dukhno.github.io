---
layout: post
title: Scala Implicit investigation
tags: [Scala, compilers]
---

I have been doing internal [`Scala`](http://www.scala-lang.org) courses for six weeks. One of task on the third week was to write a function that calculates given factorial recursively with and without tail recursion. Playing with `BigInt` in `Scala` and solving this trivial task I faced some interesting compiler behavior with `implicit conversions`. In this article, I want to dive deeper in how particular home assignment showed me what is `implicit`s in `Scala` and how it influences on my programs.

# Recursive algorithm

The most simple algorithm is a recursive one. You need to multiple `n` on `factorial(n-1)`. It has one disadvantage, you can get `StackOverFlowError` if you specify `n` as a enormous number. However, in this article, I am not talking about that. Here is an implementation:

```scala
def factorial(n: Int): BigInt = {
    if (n == 0) 1 else n * factorial(n - 1)
}
```

You may notice that `n` argument has `Int` type. Thus, `scalac` adds `implicit conversion` before multiply `n` on `factorial(n - 1)`. For those who are brave enough, you can ask `scalac` to show which implicit method it applies to convert `Int` into `BigInt`. To do this, you need to compile `Scala` source file with `-Xlog-implicit-conversions` option. It will print similar to:

```sh
$ scalac -Xlog-implicit-conversions Test.scala
Test.scala:14: inferred view from Int(1) to BigInt via math.this.BigInt.int2bigInt: (i: Int)scala.math.BigInt
    if (n == 0) 1 else n * factorialInt(n - 1)
                ^
Test.scala:14: applied implicit conversion from n.type to ?{def *(x$1: ? >: BigInt): BigInt} = implicit def int2bigInt(i: Int): scala.math.BigInt
    if (n == 0) 1 else n * factorialInt(n - 1)
                       ^
```

> By the way, for those who are much braver than that you may use `-Xprint:typer` option of `scalac` to see how `scalac` inserts `implicit conversion` before compiling `Scala` code into `bytecode` instructions.
>
>```sh
>$ scalac -Xprint:typer Test.scala
> [[syntax trees at end of                     typer]] // Test.scala
> package <empty> {
>   object Test extends scala.AnyRef {
>     def <init>(): Test.type = {
>       Test.super.<init>();
>       ()
>     };
>     def factorial(n: Int): BigInt = if (n.==(0))
>       math.this.BigInt.int2bigInt(1)
>     else
>       math.this.BigInt.int2bigInt(n).*(Test.this.factorial(n.-(1)))
>   }
> }
>```

# Iterative algorithm

Before writing a tail recursive `factorial` algorithm, I decided to write a simple iterative algorithm using `Scala`'s `for` comprehension. Here it is:

```scala
def factorial(n: BigInt): BigInt = {
  var factorial: BigInt = 1
  for (i <- 1 to n)
    factorial *= i
  factorial
}
```

However, `scalac` refuses to compile it.

```sh
$ scalac Test.scala
Test.scala:8: error: type mismatch;
 found   : BigInt
 required: Int
    for (i <- 1 to n)
                   ^
one error found
```

> If you want better understand why it happens, you might need to read [Implicit Conversions and Parameters](http://www.artima.com/pins1ed/implicit-conversions-and-parameters.html) from "Programming in Scala" by Martin Odersky, Lex Spoon, and Bill Venners.

Let's use `scalac`'s flag from previous part to figure out what implicit method it is trying to apply.

```sh
$ scalac -Xlog-implicit-conversions Test.scala
Test.scala:7: inferred view from Int(1) to BigInt via math.this.BigInt.int2bigInt: (i: Int)scala.math.BigInt
    var factorial: BigInt = 1
                            ^
Test.scala:8: applied implicit conversion from Int(1) to ?{def to: ?} = implicit def intWrapper(x: Int): scala.runtime.RichInt
    for (i <- 1 to n)
              ^
Test.scala:8: error: type mismatch;
 found   : BigInt
 required: Int
    for (i <- 1 to n)
                   ^
one error found
```

Ok, `scalac` applies `BigInt.int2bigInt` to convert `1` to `BigInt` in `var factorial: BigInt = 1`, but why is it trying to apply `implicit def intWrapper(x: Int): scala.runtime.RichInt` to convert `1` in `for (i <- 1 to n)`? If you are somewhat familiar with `Scala`, you may know that `Int` can be used as primitive and as boxed `java.lang.Integer`; for example when used in collections. However, `Scala`'s standard library has one more wrapper for `Int`; it is `RichInt`. It has a few convenient methods such as `to` and `until` to create `Range` of `Int`s. `implicit def intWrapper(x: Int): RichInt` locates in the `scala.Predef` `object` which `scalac` imports by default as `scala._` and `java.lang._` packages. `Implicit conversion` `Int` into `RichInt` has much higher priority than `Int` into `BigInt`; that is why `scalac` uses `implicit def intWrapper(x: Int): RichInt` instead of `implicit def int2bigInt(i: Int): BigInt`. However, it can be fixed by adding `import BigInt.int2bigInt` to `Scala` source file.

```scala
import BigInt.int2bigInt

object Test {
  def factorialBigInt(n: BigInt): BigInt = {
    var factorial: BigInt = 1
    for (i <- 1 to n)
      factorial *= i
    factorial
  }
}
```

```sh
$ scalac -Xlog-implicit-conversions Test.scala
Test.scala:7: inferred view from Int(1) to BigInt via scala.`package`.BigInt.int2bigInt: (i: Int)scala.math.BigInt
    var factorial: BigInt = 1
                            ^
Test.scala:8: applied implicit conversion from Int(1) to ?{def to(x$1: ? >: BigInt): ?} = implicit def int2bigInt(i: Int): scala.math.BigInt
    for (i <- 1 to n)
              ^
```

# Summary

`Implicit`s is a very powerful tool in `Scala` programming language; however, care must be taken when you use it. Sometimes your code would not compile, and you will spend lots of time figuring out what is going on. That is why you had better know your tools and use it appropriately.
