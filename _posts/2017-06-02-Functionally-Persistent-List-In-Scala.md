---
layout: post
title: Functionally Persistent List In Scala. TL;DR
tags: Scala data-structures TDD
---

It has been for more than a month that I wrote any technical article in my blog. However, one thought hit me a few weeks ago to write an article about a data structure - [persistent list](https://en.wikipedia.org/wiki/Persistent_data_structure#Linked_lists). Of course approaching test driven development, could not be the other way. I hope it will not be in too functional style, and people who do not think that way will be comfortable with its content. Let's get started.

# Setup development environment

I will use [scala build tool(sbt for short)](http://www.scala-sbt.org) for running tests. You need to download and install `sbt` for working throughout this article (if you are willing to do so). I configured the following project layout.

```sh
scala-data-structures/
    |
    +-src/
    |   |
    |   +-main/
    |   |   |
    |   |   +-scala/
    |   |       |
    |   |       +-PersistentList.scala
    |   |
    |   +-test/
    |   |   |
    |   |   +-scala/
    |   |       |
    |   |       +-PersistentListTest.scala
    |   |
    |   +-project/
    |       |
    |       +-build.properties
    |
    +-build.sbt
```

If you are `Java` developer you should be familiar with such project structure, it is similar to `maven`. The main configuration file is `build.sbt`. It contains a description of the project structure, a list of dependencies and other declarations how to `build`, `run` and `test` your project. My `build.sbt` has the following content.

```
name := "scala-data-structures"
version := "1.0"

lazy val root = Project("scala-data-structures", file("."))
  .aggregate(persistent_list)

lazy val persistent_list = project.in(file("persistent-list"))
    .settings(
        scalaVersion := "2.12.1",
        libraryDependencies ++= Seq(
            "org.scalatest" %% "scalatest" % "3.0.1" % "test"
        ),

        scalacOptions ++= Seq("-deprecation", "-feature")
)
```

You may see that it has attributes such as `name`, `version` and `root` (where is the source of the project locates). I also include a declaration of `persistent_list` sub-project and add it to the `root` project via `aggregate()`. For testing, I will use [scalatest](http://www.scalatest.org); it is a very reach and robust library for testing `Scala` code. `build.properties` is the other configuration file for `sbt`. It contains the only one line `sbt.version=0.13.13`. This line serves for `sbt` to know that developer is using right version to work with your project. If other developers want to work on your project and `sbt` has already been installed then it checks that its version is the same, if not it will download and use specified version.

The last thing that we need to do is to check that our environment configured correctly. Add the following test to `src/test/scala/PersistentListTest.scala`

```scala
import org.scalatest.FlatSpec

class PersistentListTest extends FlatSpec {

    it should "run an empty test" in {

    }
}
```

Then run `sbt` command in your terminal. `sbt` loads and updates your project after that type `test` sub-command.

```sh
$ sbt
[info] Loading project definition from /Users/alex-diez/Projects/scala-data-structures/project
[info] Set current project to scala-data-structures (in build file:/Users/alex-diez/Projects/scala-data-structures/)
> test
[info] Updating {file:/Users/alex-diez/Projects/scala-data-structures/}persistent_list...
[info] Updating {file:/Users/alex-diez/Projects/scala-data-structures/}scala-data-structures...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/alex-diez/Projects/scala-data-structures/persistent-list/target/scala-2.12/test-classes...
[info] PersistentListTest:
[info] - should run an empty test
[info] Run completed in 316 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 5 s, completed May 16, 2017 9:36:36 PM
```

Now we can move on with development of our functionally persistent list.

# Create an empty list

If you have read my previous articles about test driven development approach, then nothing has changed. The first test must be the simplest one, and nothing is more elementary than the creation of an empty list. Remove `should run an empty test` test and add the following one.

```scala
import org.scalatest.{FlatSpec, Matchers}

class PersistentListTest extends FlatSpec with Matchers {

    it should "create an empty list" in {
        new PersistentList().toString() shouldBe "[]"
    }
}
```

>
> I could introduce `isEmpty` method on the `PersistentList`.
> However, using `toString` is much better for understanding what elements the list contains and in which order
>

`Matchers` is the `Trait` that has lots of methods for more DSL(domain specific language) a-like assertions. Indeed, we can every time run `test` command in `sbt` console. However, `sbt` provides superior functionality that will run tests each time when you change and save your test or source code file. To do so run `~test` command in `sbt` console.

```sh
> ~test
[info] Compiling 1 Scala source to /Users/alex-diez/Projects/scala-data-structures/persistent-list/target/scala-2.12/test-classes...
not found: type PersistentList
[error]         new PersistentList().toString() shouldBe "[]"
[error]             ^
[error] one error found
[error] (persistent_list/test:compileIncremental) Compilation failed
[error] Total time: 0 s, completed May 16, 2017 9:52:41 PM
1. Waiting for source changes... (press enter to interrupt)
```

Yeah, got an error. Let's declare `PersistentList` class in `src/main/scala/PersistentList.scala` file and save it to check that test will be run automatically.

```scala
class PersistentList {
}
```
```sh
[info] Compiling 1 Scala source to /Users/alex-diez/Projects/scala-data-structures/persistent-list/target/scala-2.12/classes...
[info] PersistentListTest:
[info] - should create an empty list *** FAILED ***
[info]   "[PersistentList@640de9f1]" was not equal to "[[]]" (PersistentListTest.scala:6)
[info] Run completed in 425 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 0, failed 1, canceled 0, ignored 0, pending 0
[info] *** 1 TEST FAILED ***
[error] Failed tests:
[error] 	PersistentListTest
[error] (persistent_list/test:test) sbt.TestsFailedException: Tests unsuccessful
[error] Total time: 1 s, completed May 16, 2017 9:54:25 PM
```

Great! Our test failed because `Java` `Object`'s `toString` implementation was used. Fix the test by overriding `toString` method that returns `"[]"` `String`.

```scala
class PersistentList {
    override def toString: String = "[]"
}
```
```sh
[info] PersistentListTest:
[info] - should create an empty list
[info] Run completed in 213 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
```

Cool! May go on.

# Prepend an item to a list

To be able to create an empty list, of course, is very cool. However, we can't take anything useful from this functionality. Let's add `::` method to our list that prepend an item to it.

```scala
it should "prepend an item to a list" in {
    var list = new PersistentList()
    list = 10 :: list

    list.toString() shouldBe "[10]"
}
```
```sh
value :: is not a member of PersistentList
[error]         list = 10 :: list
[error]                   ^
[error] one error found
```

The compiler tells us that it can't find `::` method on our `PersistentList`, let's implement it. It will take `Int` type argument and return `PersistentList`. But why? :thinking:. `Persistent data structures` always should be immutable; they have to save their previous historical view. Thus, `::` should return a list that contains elements of the previous list and one more element. At first, let's make the compiler happy and return `this` from `::`.

```scala
class PersistentList {
    def ::(item: Int): PersistentList = this
    override def toString: String = "[]"
}
```
```sh
[info] PersistentListTest:
[info] - should create an empty list
[info] - should prepend an item to a list *** FAILED ***
[info]   "[[]]" was not equal to "[[10]]" (PersistentListTest.scala:13)
```

To fix this test create new `PersistentList` with `head` field bind to `item` parameter.

```scala
class PersistentList(val head: Int) {
    def this() = {
        this(0)
    }

    def ::(item: Int): PersistentList = new PersistentList(item)

    override def toString: String = {
        if (head == 0) { "[]" } else { s"[$head]" }
    }
}
```
```sh
[info] PersistentListTest:
[info] - should create an empty list
[info] - should prepend an item to a list
```

Green! Pass! Great! This is a little bit imperative. To make it more functional we need to rewrite it using `case class`es, `case object`es and `sealed trait`s.

```scala
sealed trait PersistentList {
    def ::(item: Int): PersistentList
}

object PersistentList {
    def apply(): PersistentList = Empty

    private case object Empty extends PersistentList {
        override def ::(item: Int): PersistentList = Cons(item)
        override def toString(): String = "[]"
    }

    private case class Cons(head: Int) extends PersistentList {
        override def ::(item: Int): PersistentList = Cons(item)
        override def toString(): String = s"[$head]"
    }
}
```

`Empty` is the `case object`; thus it can be the only one empty list in a whole universe. `Cons` is the `case class` represents a list that holds items. Both `Empty` and `Cons` `extends` `PersistentList` `trait` which defines the `::` method. Also, I wrote `companion object` with the same name `PersistentList` and defined `apply` method.

>
> Notice that `::` in `Empty` and `Cons` are overridden
>

Thanks to that `Scala` allows method implementations in `trait`s, therefore, move `::` to `PersistentList` because `Empty` and `Cons` have the same implementation.

```scala
sealed trait PersistentList {
    import PersistentList.Cons

    def ::(item: Int): PersistentList = Cons(item)
}

object PersistentList {
    def apply(): PersistentList = Empty

    private case object Empty extends PersistentList {
        override def toString(): String = "[]"
    }

    private case class Cons(head: Int) extends PersistentList {
        override def toString(): String = s"[$head]"
    }
}
```

Because `PersistentList` is a `trait` now tests must be changed also. Using properties of `persistent data structure`s define `val emptyList` in test class.

```scala
class PersistentListTest extends FlatSpec with Matchers {

    val emptyList = PersistentList();

    it should "create an empty list" in {
        emptyList.toString() shouldBe "[]"
    }

    it should "prepend an item to a list" in {
        (10 :: emptyList).toString() shouldBe "[10]"
    }
}
```

# Prepend many items to a list

The next step is to implement functionality to create a list that can hold more than one item.

```scala
it should "prepend many items to a list" in {
    (30 :: 20 :: 10 :: emptyList).toString() shouldBe "[30, 20, 10]"
}
```
```sh
[info] PersistentListTest:
[info] - should create an empty list
[info] - should prepend an item to a list
[info] - should prepend many items to a list *** FAILED ***
[info]   "[30[]]" was not equal to "[30[, 20, 10]]" (PersistentListTest.scala:16)
```

Since this is a list we need somehow save reference to previous element in the list. Thus add `tail` parameter to `Cons` and change `::` method, also change `toString` in `Cons`.

```scala
sealed trait PersistentList {
    import PersistentList.Cons

    def ::(item: Int): PersistentList = Cons(item, this)
}

object PersistentList {
    def apply(): PersistentList = Empty

    private case object Empty extends PersistentList {
        override def toString(): String = "[]"
    }

    private case class Cons(head: Int, tail: PersistentList) extends PersistentList {
        override def toString(): String = {
            var list = this
            val builder = new StringBuilder()
            builder.append('[')
            while (list.tail != Empty) {
                builder.append(list.head)
                builder.append(", ")
                list = list.tail.asInstanceOf[Cons]
            }
            builder.append(list.head)
            builder.append(']')
            builder.toString()
        }
    }
}
```
```sh
[info] PersistentListTest:
[info] - should create an empty list
[info] - should prepend an item to a list
[info] - should prepend many items to a list
```

Again, `toString` implementation is in imperative style; in addition to this we have an ugly cast using `asInstanceOf`. To make it looks functional we need to use `pattern matching`; it is easier to do with `nested function`.

```scala
sealed trait PersistentList {
    import PersistentList.Cons

    def ::(item: Int): PersistentList = Cons(item, this)
}

object PersistentList {
    def apply(): PersistentList = Empty

    private case object Empty extends PersistentList {
        override def toString(): String = "[]"
    }

    private case class Cons(head: Int, tail: PersistentList) extends PersistentList {
        override def toString(): String = {
            import scala.annotation.tailrec

            @tailrec
            def loop(builder: StringBuilder, list: PersistentList): String = {
                list match {
                    case Cons(head, tail) =>
                        if (builder.nonEmpty) builder.append(", ")
                        loop(builder.append(head), tail)
                    case Empty => builder.toString
                }
            }

            "[" + loop(new StringBuilder(), this) + "]"
        }
    }
}
```

By the way, we can make our code shorter by moving `nested function` from `Cons` `toString` to `PersistentList` `trait`.

```scala
sealed trait PersistentList {
    import PersistentList._
    import scala.annotation.tailrec

    def ::(item: Int): PersistentList = Cons(item, this)

    override def toString(): String = {
        @tailrec
        def loop(builder: StringBuilder, list: PersistentList[E]): String = list match {
            case Cons(head, tail) =>
                if (builder.nonEmpty) builder.append(", ")
                loop(builder.append(head), tail)
            case Empty => builder.toString
        }

        "[" + loop(new StringBuilder(), this) + "]"
    }
}

object PersistentList {
    def apply(): PersistentList = Empty

    private case object Empty extends PersistentList

    private case class Cons(head: Int, tail: PersistentList) extends PersistentList
}
```

>
> I put `@tailrec` annotation on nested `loop` function; I will write about it little bit latter.
>

# Head and tail

`Head` and `tail` are properties of the `persistent list`. `Head` is the first item in the list, on the other side, `tail` is the list that contains all other items (except the first one).

```scala
"None" should "be head of persistent list" in {
    emptyList.head shouldBe None
}
```
```sh
[error]         emptyList.head shouldBe None
[error]                   ^
[error] one error found
```

Let's define `head` property for the `PersistentList` `trait`.

```scala
sealed trait PersistentList {
    //...
    def head: Option[Int]
    //...
}

object PersistentList {
    def apply(): PersistentList = Empty

    private case object Empty extends PersistentList {
        override def head: Option[Int] = None
    }

    private case class Cons(head: Int, tail: PersistentList) extends PersistentList {
        override def head: Option[Int] = ???
    }
}
```
```sh
[error]   the conflicting value head was defined at line 32:29
[error]         override def head: Option[Int] = ???
[error]                      ^
```

To fix this rename `Cons` `head` parameter to `item`.

```sh
[info] PersistentListTest:
[info] - should create an empty list
[info] - should prepend an item to a list
[info] - should prepend many items to a list
[info] None
[info] - should be head of empty list
```
```scala
"Some(30)" should "be head of nonempty list" in {
     (30 :: 20 :: 10 :: emptyList).head shouldBe Some(30)
 }
```
```sh
[info] Some(30)
[info] - should be head of nonempty list *** FAILED ***
[info]   scala.NotImplementedError: an implementation is missing
```

The implementation of `head` for `Cons` is straightforward - just return `Some(item)`

```scala
private case class Cons(item: Int, tail: PersistentList) extends PersistentList {
    override def head: Option[Int] = Some(item)
}
```
```sh
[info] Some(30)
[info] - should be head of nonempty list
```

Let's define filed `nonemptyList` in `PersistentListTest` with value of `30 :: 20 :: 10 :: emptyList` and refactor our tests

```scala
class PersistentListTest extends FlatSpec with Matchers {

    val emptyList = PersistentList();
    val nonemptyList = 30 :: 20 :: 10 :: emptyList

    it should "create an empty list" in {
        emptyList.toString() shouldBe "[]"
    }

    it should "prepend an item to a list" in {
        (10 :: emptyList).toString() shouldBe "[10]"
    }

    it should "prepend many items to a list" in {
        nonemptyList.toString() shouldBe "[30, 20, 10]"
    }

    "None" should "be head of empty list" in {
        emptyList.head shouldBe None
    }

    "Some(30)" should "be head of nonempty list" in {
        nonemptyList.head shouldBe Some(30)
    }
}
```

Check that test of `tail` for nonempty list is passed

```scala
"tail of nonempty list" should "contain all items except the first one" in {
    nonemptyList.tail.toString shouldBe "[20, 10]"
}
```
```sh
value tail is not a member of PersistentList
[error]         nonemptyList.tail.toString shouldBe "[20, 10]"
[error]                      ^
```

agh... need to define the method, don't forget to do it in the `Empty` `object` too.

```scala
sealed trait PersistentList {
    //...
    def tail: PersistentList
    //...
}

//...
    private case object Empty extends PersistentList {
        override def head: Option[Int] = None

        override def tail: PersistentList = ???
    }
//...
}
```
```sh
[info] tail of nonempty list
[info] - should contain all items except the first one
```

Tail of the `Empty` has left to implement. Indeed, the `Empty` list does not have a `tail`; thus, it can be only the other `Empty` list.

```scala
    "tail of empty list" should "be the empty list" in {
        emptyList.tail.toString shouldBe "[]"
    }
```
```scala
    private case object Empty extends PersistentList {
        override def head: Option[Int] = None

        override def tail: PersistentList = Empty
    }
```
```sh
[info] tail of empty list
[info] - should be the empty list
```

# Lists concatenation

If we can prepend an item to a list, then we should be able to prepend another list to the list. Let's start with two empty lists.

```scala
"Result list" should "be an empty list when two empty lists concatenated" in {
    (emptyList ++ emptyList).toString shouldBe "[]"
}
```
```sh
value ++ is not a member of PersistentList
[error]         (emptyList ++ emptyList).toString shouldBe "[]"
[error]                    ^
```

Define `++` method with `Empty` as return value in `PersistentList` `trait`.

```scala
def ++(other: PersistentList): PersistentList = Empty
```
```sh
[info] Result list
[info] - should be an empty list when two empty lists concatenated
```

The next case when one of the lists is nonempty.

```scala
it should "be the nonempty list when nonempty and empty list are concatenated" in {
    (nonemptyList ++ emptyList).toString shouldBe "[30, 20, 10]"
    (emptyList ++ nonemptyList).toString shouldBe "[30, 20, 10]"
}
```
```sh
[info] - should be the nonempty list when nonempty and empty list are concatenated *** FAILED ***
[info]   "[[]]" was not equal to "[[30, 20, 10]]" (PersistentListTest.scala:41)
```

To fix this up we need to check that `this` list is not `Empty` and return it, otherwise `other`.

```scala
def ++(other: PersistentList): PersistentList = {
    if (this != Empty) this
    else other
}
```
```sh
[info] Result list
[info] - should be an empty list when two empty lists are concatenated
[info] - should be the nonempty list when nonempty and empty list are concatenated
```

The last case is when we concatenate two nonempty lists.

```scala
it should "contain items of both nonempty lists" in {
    val listOne = 30 :: 20 :: 10 :: emptyList
    val listTwo = 60 :: 50 :: 40 :: emptyList

    (listOne ++ listTwo).toString shouldBe "[30, 20, 10, 60, 50, 40]"
    (listTwo ++ listOne).toString shouldBe "[60, 50, 40, 30, 20, 10]"
}
```
```sh
[info] Result list
[info] - should be an empty list when two empty lists are concatenated
[info] - should be the nonempty list when nonempty and empty list are concatenated
[info] - should contain items of both nonempty lists *** FAILED ***
[info]   "[30, 20, 10[]]" was not equal to "[30, 20, 10[, 60, 50, 40]]" (PersistentListTest.scala:49)
```

This will be tough for those who are not familiar with functional programming. However, if we have a nonempty list we need to prepend its `head` to a list that is the result of concatenation of its `tail` and the `other` list. Sounds creepy, but if you write it in `Scala` code instead of english you should have similar to:

```scala
def ++(other: PersistentList): PersistentList = this match {
    case Cons(head, tail) => head :: (tail ++ other)
    case Empty => other
}
```
```sh
[info] Result list
[info] - should be an empty list when two empty lists are concatenated
[info] - should be the nonempty list when nonempty and empty list are concatenated
[info] - should contain items of both nonempty lists
```

Fantastic!

# Drop needless things

Sometimes it is useful to drop few first items from the list or have a list that was few `::` operation before. Again, the simplest case is when you `drop` from the `Empty` list - would be cut nothing.

```scala
"Empty list" should "drop nothing" in {
    emptyList.drop(10).toString shouldBe "[]"
}
```
```sh
value drop is not a member of PersistentList
[error]         emptyList.drop(10).toString shouldBe "[]"
```

Implementation is trivial. Just return `Empty`.

```scala
def drop(n: Int): PersistentList = Empty
```
```sh
[info] Empty list
[info] - should drop nothing
```

What if I `drop` 0 items from the list or -1? Of course, it would be the same list.

```scala
"Nonempty list" should "drop nothing" in {
    nonemptyList.drop(0).toString shouldBe "[30, 20, 10]"
    nonemptyList.drop(-1).toString shouldBe "[30, 20, 10]"
}
```
```sh
[info] Nonempty list
[info] - should drop nothing *** FAILED ***
[info]   "[[]]" was not equal to "[[30, 20, 10]]" (PersistentListTest.scala:58)
```

Again, the fix is just trivial - return `this` instead of `Empty`

```scala
def drop(n: Int): PersistentList = this
```
```sh
[info] Nonempty list
[info] - should drop nothing
```

The last case is to drop specified number of items from a nonempty list.

```scala
it should "drop two items" in {
    nonemptyList.drop(2).toString shouldBe "[10]"
}
```
```sh
[info] Nonempty list
[info] - should drop nothing
[info] - should drop two items *** FAILED ***
[info]   "[[30, 20, ]10]" was not equal to "[[]10]" (PersistentListTest.scala:63)
```

Let's have a look how `drop` operation looks like. `[30, 20, 10].drop(2)` is the same as `[20, 10].drop(1)` and `[20, 10].drop(1)` is the same as `[10].drop(0)`. Thus `aList.drop(2)` is the same as `aList.tail.drop(1)` and `aList.tail.drop(1)` is the same as `aList.tail.tail.drop(0)`.

```text
|  head  |           tail          |      |  head  |      tail      |    |  head  |  tail |
+--------+--------+--------+-------+      +--------+--------+-------+    +--------+-------+
|   30   |   20   |   10   | empty |  =>  |   20   |   10   | empty | => |   10   | empty |
+--------+--------+--------+-------+      +--------+--------+-------+    +--------+-------+
  n == 2                                     n == 1                        n == 0
```

In words, we need to call `drop` on the list's `tail` with decremented `n` parameter until `n` is greater than `0`; in `Scala`:

```scala
def drop(n: Int): PersistentList = this match {
    case Cons(_, tail) if n > 0 => tail.drop(n - 1)
    case _ => this
}
```
```sh
[info] Nonempty list
[info] - should drop nothing
[info] - should drop two items
```

>
> `if n > 0` in `case Cons(_, tail) if n > 0 => tail.drop(n - 1)` is so-called `guard`. The `case` will be evaluated only when `this` list will match it and also  when its `head` will meet the condition.
>

The other case when you want to drop items from a list while they met some predicate. For this purpose implement the `dropWhile` method on a list.

```scala
"Empty list" should "drop nothing by predicate" in {
    emptyList.dropWhile(i => i == 10).toString shouldBe "[]"
}
```
```sh
value dropWhile is not a member of PersistentList
[error]         emptyList.dropWhile(i => i == 10).toString shouldBe "[]"
```

The simplest fix for the simplest test.

```scala
def dropWhile(predicate: Int => Boolean): PersistentList = Empty
```

We are going the same way as with `drop` method; first, return `Empty` and then `this`.

>
> To implement `drop` and `dropWhile` methods I am using so called `triangulation` technique.
> * First, I wrote the obvious implementation;
> * Second, I fake an implementation by `this`;
> * Third, write a real implementation.
>

```scala
"Nonempty list" should "drop nothing by predicate" in {
    nonemptyList.dropWhile(i => i > 40).toString shouldBe "[30, 20, 10]"
}
```
```sh
[info] Nonempty list
[info] - should drop nothing by predicate *** FAILED ***
[info]   "[[]]" was not equal to "[[30, 20, 10]]" (PersistentListTest.scala:71)
```
```scala
def dropWhile(predicate: Int => Boolean): PersistentList = this
```

The third case more realistic than two previous.

```scala
it should "drop two items by predicate" in {
    nonemptyList.dropWhile(i => i > 10).toString shouldBe "[10]"
}
```
```sh
[info] Nonempty list
[info] - should drop nothing by predicate
[info] - should drop two items by predicate *** FAILED ***
[info]   "[[30, 20, ]10]" was not equal to "[[]10]" (PersistentListTest.scala:75)
```

The "real" implementation of `dropWhile` is very similar to `drop`. Lets have a look now at how `dropWhile` behaves. We need to call `dropWhile` on the list `tail` until its `head` meets the `predicate`.

```text
|  head  |           tail          |      |  head  |      tail     |    |  head  |  tail |
+--------+--------+--------+-------+     +--------+--------+-------+    +--------+-------+
|   30   |   20   |   10   | empty |  => |   20   |   10   | empty | => |   10   | empty |
+--------+--------+--------+-------+     +--------+--------+-------+    +--------+-------+
 30 > 10                                   20 > 10                        10 > 10
  true                                      true                           false
```

Thus, the implementation is

```scala
def dropWhile(predicate: Int => Boolean): PersistentList = this match {
    case Cons(head, tail) if predicate(head) => tail.dropWhile(predicate)
    case _ => this
}
```
```sh
[info] Nonempty list
[info] - should drop nothing by predicate
[info] - should drop two items by predicate
```

>
> Some of you may notice that both `drop` and `dropWhile` are tail recursive functions.
> Why? Because one of the execution branches, exactly `case Cons(_, _) if <cond>`, has function call of itself in the last execution step.
>
> If you mark those methods with `@tailrec`, do not forget to make it `final` the other way compiler would not be happy.
>

# Take your items to another list

One more useful functionality is when you can `take` the first items from the list by specified number or by a predicate. The strategy will be the same as with `drop` and `dropWhile`, that's why I put the code and standard output without any comments until the "real" implementation.

```scala
"it" should "take nothing from empty list" in {
    emptyList.take(10).toString shouldBe "[]"
}
```
```sh
value take is not a member of PersistentList
[error]         emptyList.take(10).toString shouldBe "[]"
```
```scala
def take(n: Int): PersistentList = Empty
```
```sh
[info] it
[info] - should take nothing from empty list
```
```scala
it should "take nothing from nonempty list" in {
    nonemptyList.take(0).toString shouldBe "[]"
    nonemptyList.take(-1).toString shouldBe "[]"
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
```
```scala
it should "take two first items from nonempty list" in {
    nonemptyList.take(2).toString shouldBe "[30, 20]"
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list *** FAILED ***
[info]   "[[]]" was not equal to "[[30, 20]]" (PersistentListTest.scala:88)
```
```scala
def take(n: Int): PersistentList = this match {
    case Cons(head, tail) if n > 0 => head :: (tail.take(n - 1))
    case _ => Empty
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list
```

`take` is very similar to `drop` except that we return `Empty` when `n` reaches `0` and prepend `head` to a new list

```text
|  head  |           tail          |      |  head  |      tail      |    |  head  |  tail |
+--------+--------+--------+-------+      +--------+--------+-------+    +--------+-------+
|   30   |   20   |   10   | empty |  =>  |   20   |   10   | empty | => |   10   | empty |
+--------+--------+--------+-------+      +--------+--------+-------+    +--------+-------+
    V                                          V
+--------+                       +--------+--------+                     +--------+--------+--------+
|   30   |                       |   30   |   20   |                     |   30   |   20   | empty  |
+--------+                       +--------+--------+                     +--------+--------+--------+
  n == 2                                    n == 1                         n == 0
```

```scala
it should "take nothing from empty list by predicate" in {
    emptyList.takeWhile(i => i > 10).toString shouldBe "[]"
}
```
```sh
alue takeWhile is not a member of PersistentList
[error]         emptyList.takeWhile(i => i > 10).toString shouldBe "[]"
```
```scala
def takeWhile(predicate: Int => Boolean): PersistentList = Empty
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list
[info] - should take nothing from empty list by predicate
```
```scala
it should "take nothing from nonempty list by predicate" in {
    nonemptyList.takeWhile(i => i > 50).toString shouldBe "[]"
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list
[info] - should take nothing from empty list by predicate
[info] - should take nothing from nonempty list by predicate
```
```scala
it should "take two items from nonempty list by predicate" in {
    nonemptyList.takeWhile(i => i > 10).toString shouldBe "[30, 20]"
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list
[info] - should take nothing from empty list by predicate
[info] - should take nothing from nonempty list by predicate
[info] - should take two items from nonempty list by predicate *** FAILED ***
[info]   "[[]]" was not equal to "[[30, 20]]" (PersistentListTest.scala:100)
```
```scala
def takeWhile(predicate: Int => Boolean): PersistentList = this match {
    case Cons(head, tail) if predicate(head) => head :: (tail.takeWhile(predicate))
    case _ => Empty
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list
[info] - should take nothing from empty list by predicate
[info] - should take nothing from nonempty list by predicate
[info] - should take two items from nonempty list by predicate
```

`takeWhile` is very similar to `dropWhile` except that we return `Empty` while `head` meets `predicate` and prepend `head` to a new list

```text
|  head  |           tail          |      |  head  |      tail      |    |  head  |  tail |
+--------+--------+--------+-------+      +--------+--------+-------+    +--------+-------+
|   30   |   20   |   10   | empty |  =>  |   20   |   10   | empty | => |   10   | empty |
+--------+--------+--------+-------+      +--------+--------+-------+    +--------+-------+
    V                                          V
+--------+                       +--------+--------+                     +--------+--------+--------+
|   30   |                       |   30   |   20   |                     |   30   |   20   | empty  |
+--------+                       +--------+--------+                     +--------+--------+--------+
 30 > 10                                    20 > 10                       10 > 10
```

# Map over items

Let's try map content of our `PersistentList` which is `Int`s to `Double`s

```scala
it should "map content to Doubles" in {
    nonemptyList.map(_.toDouble).toString shouldBe "[30.0, 20.0, 10.0]"
}
```
```sh
value map is not a member of PersistentList
[error]         nonemptyList.map(_.toDouble).toString shouldBe "[30.0, 20.0, 10.0]"
```
```scala
def map(mapper: Int => Double): PersistentList = Empty
```

Till now, we are implementing `PersistentList` that can hold only `Int`s, but for implementing `map` method we need to generalize our list.

```scala
sealed trait PersistentList[E] {
    import PersistentList._
    import scala.annotation.tailrec

    def ::(item: E): PersistentList[E] = Cons(item, this)

    def head: Option[E] = this match {
        case Cons(head, _) => Some(head)
        case _ => None
    }

    def tail: PersistentList[E] = this match {
        case Cons(_, tail) => tail
        case _ => Empty()
    }

    def ++(other: PersistentList[E]): PersistentList[E] = this match {
        case Cons(head, tail) => head :: (tail ++ other)
        case Empty() => other
    }

    def drop(n: Int): PersistentList[E] = this match {
        case Cons(_, tail) if n > 0 => tail.drop(n - 1)
        case _ => this
    }

    def dropWhile(predicate: E => Boolean): PersistentList[E] = this match {
        case Cons(head, tail) if predicate(head) => tail.dropWhile(predicate)
        case _ => this
    }

    def take(n: Int): PersistentList[E] = this match {
        case Cons(head, tail) if n > 0 => head :: (tail.take(n - 1))
        case _ => Empty()
    }

    def takeWhile(predicate: E => Boolean): PersistentList[E] = this match {
        case Cons(head, tail) if predicate(head) => head :: (tail.takeWhile(predicate))
        case _ => Empty()
    }

    def map[A](mapper: E => A): PersistentList[A] = Empty()

    override def toString(): String = {
        @tailrec
        def loop(builder: StringBuilder, list: PersistentList[E]): String = {
            list match {
                case Cons(head, tail) =>
                    if (builder.nonEmpty) builder.append(", ")
                    loop(builder.append(head), tail)
                case Empty() => builder.toString
            }
        }

        "[" + loop(new StringBuilder(), this) + "]"
    }
}

object PersistentList {
    def apply[E](): PersistentList[E] = Empty()

    private case class Empty[E]() extends PersistentList[E]

    private case class Cons[E](item: E, next: PersistentList[E]) extends PersistentList[E]
}
```

Now we can implement `map` method. What we need here is that create a new list that will contain mapped items of the current list. Thus, we call `Cons` with `mapper(head)` and `tali.map(mapper)` parameters.

```text
|  head  |           tail          |               |  head  |      tail      |                      |  head  |  tail |
+--------+--------+--------+-------+      +--------+--------+--------+-------+    +--------+--------+--------+-------+    +--------+--------+--------+-------+
|   30   |   20   |   10   | empty |  =>  |   30   |   20   |   10   | empty | => |   30   |   20   |   10   | empty | => |   30   |   20   |   10   | empty |
+--------+--------+--------+-------+      +--------+--------+--------+-------+    +--------+--------+--------+-------+    +--------+--------+--------+-------+
 maps to                                            maps to                                          maps to                                          maps to
+--------+                                +--------+--------+                     +--------+--------+--------+            +--------+--------+--------+-------+
|  30.0  |                                |  30.0  |  20.0  |                     |  30.0  |  20.0  |  10.0  |            |  30.0  |  20.0  |  10.0  | empty |
+--------+                                +--------+--------+                     +--------+--------+--------+            +--------+--------+--------+-------+
```

```scala
def map[A](mapper: E => A): PersistentList[A] = this match {
    case Cons(head, tail) => Cons(mapper(head), tail.map(mapper))
    case _ => Empty()
}
```
```sh
[info] it
[info] - should take nothing from empty list
[info] - should take nothing from nonempty list
[info] - should take two first items from nonempty list
[info] - should take nothing from empty list by predicate
[info] - should take nothing from nonempty list by predicate
[info] - should take two items from nonempty list by predicate
[info] - should map content to Doubles
```

# Better generalization

In previous part we make our list general for all types of item and change `Empty` from `object` to `class` and, therefore, we need instantiate it for each list in our programs. However, we can do better. Let have an example what we can improve:

```scala
class Number
class Integer(i: Int) extends Number
class FloatNumber(f: Float) extends Number

val numbers: PersistentList[Number] = PersistentList[Number]() //ok
val ints: PersistentList[Number] = PersistentList[Integer]() //compile time error
val integers: PersistentList[Integer] = PersistentList[Integer]() //ok
new FolatNumber(10.0) :: integers //compile time error
number ++ integes //compile time error
```

To fix these compilation errors we need to make our `PersistentList` covariant by type `E` adding `+` in the type definition of `PersistentList[+E]`

```scala
sealed trait PersistentList[+E] {
    //...
}
```
```sh
covariant type E occurs in contravariant position in type E of value item
[error]     def ::(item: E): PersistentList[E] = Cons(item, this)
covariant type E occurs in contravariant position in type PersistentList[E] of value other
[error]     def ++(other: PersistentList[E]): PersistentList[E] = this match {
```

Making `E` type parameter covariant we break some our code. To make compiler happy we have to do so called `flip`, we need to introduce one more type parameter for `::` and `++` methods that will be contravariant

```scala
def ::[A >: E](item: A): PersistentList[A] = Cons(item, this)
//...
def ++[A >: E](other: PersistentList[A]): PersistentList[A] = this match {
    case Cons(head, tail) => head :: (tail ++ other)
    case Empty() => other
}
```

And now we can make `Empty` an `object` again.

```scala
private case object Empty extends PersistentList[Nothing]
```

>
> Generics is one of the hardest topics to understand in static compiled languages. However, you don't need to have a thorough understanding of it; remember the two rules:
> * returned value must be covariant
> * parameters must be contravariant
>
> You may ask if `returned value must be covariant` then why we return `PersistentList[A]` in `::` if `A` is `contravariant`. The answer is that compiler must ensure that no one can use properties or methods of `E` type on the `head` of the returned list which type is `A`.
>

# The final code

```scala
//PersistentList.scala

sealed trait PersistentList[+E] {
    import PersistentList._
    import scala.annotation.tailrec

    def ::[A >: E](item: A): PersistentList[A] = Cons(item, this)

    def head: Option[E] = this match {
        case Cons(head, _) => Some(head)
        case _ => None
    }

    def tail: PersistentList[E] = this match {
        case Cons(_, tail) => tail
        case _ => Empty
    }

    def ++[A >: E](other: PersistentList[A]): PersistentList[A] = this match {
        case Cons(head, tail) => head :: (tail ++ other)
        case Empty => other
    }

    def drop(n: Int): PersistentList[E] = this match {
        case Cons(_, tail) if n > 0 => tail.drop(n - 1)
        case _ => this
    }

    def dropWhile(predicate: E => Boolean): PersistentList[E] = this match {
        case Cons(head, tail) if predicate(head) => tail.dropWhile(predicate)
        case _ => this
    }

    def take(n: Int): PersistentList[E] = this match {
        case Cons(head, tail) if n > 0 => head :: (tail.take(n - 1))
        case _ => Empty
    }

    def takeWhile(predicate: E => Boolean): PersistentList[E] = this match {
        case Cons(head, tail) if predicate(head) => head :: (tail.takeWhile(predicate))
        case _ => Empty
    }

    def map[A](mapper: E => A): PersistentList[A] = this match {
        case Cons(head, tail) => Cons(mapper(head), tail.map(mapper))
        case _ => Empty
    }

    override def toString(): String = {
        @tailrec
        def loop(builder: StringBuilder, list: PersistentList[E]): String = list match {
            case Cons(head, tail) => 
                if (builder.nonEmpty) builder.append(", ")
                loop(builder.append(head), tail)
            case Empty => builder.toString
        }


        "[" + loop(new StringBuilder(), this) + "]"
    }
}

object PersistentList {
    def apply[E](): PersistentList[E] = Empty

    private case object Empty extends PersistentList[Nothing]

    private case class Cons[E](item: E, next: PersistentList[E]) extends PersistentList[E]
}

//PersistentListTest.scala

import org.scalatest.{FlatSpec, Matchers}

class PersistentListTest extends FlatSpec with Matchers {

    val emptyList: PersistentList[Int] = PersistentList[Int]();
    val nonemptyList: PersistentList[Int] = 30 :: 20 :: 10 :: emptyList

    it should "creat an empty list" in {
        emptyList.toString() shouldBe "[]"
    }

    it should "prepend an item to a list" in {
        (10 :: emptyList).toString() shouldBe "[10]"
    }

    it should "prepend many items to a list" in {
        nonemptyList.toString() shouldBe "[30, 20, 10]"
    }

    "None" should "be head of empty list" in {
        emptyList.head shouldBe None
    }

    "Some(30)" should "be head of nonempty list" in {
        nonemptyList.head shouldBe Some(30)
    }

    "tail of nonempty list" should "contain all items except the first one" in {
        nonemptyList.tail.toString shouldBe "[20, 10]"
    }

    "tail of empty list" should "be the empty list" in {
        emptyList.tail.toString shouldBe "[]"
    }

    "Result list" should "be an empty list when two empty lists are concatenated" in {
        (emptyList ++ emptyList).toString shouldBe "[]"
    }

    it should "be the nonempty list when nonempty and empty list are concatenated" in {
        (nonemptyList ++ emptyList).toString shouldBe "[30, 20, 10]"
        (emptyList ++ nonemptyList).toString shouldBe "[30, 20, 10]"
    }

    it should "contain items of both nonempty lists" in {
        val listOne = 30 :: 20 :: 10 :: emptyList
        val listTwo = 60 :: 50 :: 40 :: emptyList

        (listOne ++ listTwo).toString shouldBe "[30, 20, 10, 60, 50, 40]"
        (listTwo ++ listOne).toString shouldBe "[60, 50, 40, 30, 20, 10]"
    }

    "Empty list" should "drop nothing" in {
        emptyList.drop(10).toString shouldBe "[]"
    }

    "Nonempty list" should "drop nothing" in {
        nonemptyList.drop(0).toString shouldBe "[30, 20, 10]"
        nonemptyList.drop(-1).toString shouldBe "[30, 20, 10]"
    }

    it should "drop two items" in {
        nonemptyList.drop(2).toString shouldBe "[10]"
    }

    "Empty list" should "drop nothing by predicate" in {
        emptyList.dropWhile(i => i == 10).toString shouldBe "[]"
    }

    "Nonempty list" should "drop nothing by predicate" in {
        nonemptyList.dropWhile(i => i > 40).toString shouldBe "[30, 20, 10]"
    }

    it should "drop two items by predicate" in {
        nonemptyList.dropWhile(i => i > 10).toString shouldBe "[10]"
    }

    "it" should "take nothing from empty list" in {
        emptyList.take(10).toString shouldBe "[]"
    }

    it should "take nothing from nonempty list" in {
        nonemptyList.take(0).toString shouldBe "[]"
        nonemptyList.take(-1).toString shouldBe "[]"
    }

    it should "take two first items from nonempty list" in {
        nonemptyList.take(2).toString shouldBe "[30, 20]"
    }

    it should "take nothing from empty list by predicate" in {
        emptyList.takeWhile(i => i > 10).toString shouldBe "[]"
    }

    it should "take nothing from nonempty list by predicate" in {
        nonemptyList.takeWhile(i => i > 50).toString shouldBe "[]"
    }

    it should "take two items from nonempty list by predicate" in {
        nonemptyList.takeWhile(i => i > 10).toString shouldBe "[30, 20]"
    }

    it should "map content to Doubles" in {
        nonemptyList.map(_.toDouble).toString shouldBe "[30.0, 20.0, 10.0]"
    }
}
```

# Wrap up

It turns out that the article is much longer than I expected it will be from the beginning. I hope that you find the article as a good example how to program functionally; especially if you are a beginner in this journey. Nevertheless, I haven't cover lots of other useful method on the list, such as:
```scala
/**
 * Return `true` if `predicate` holds at least for one item in the list
 */
def exists(predicate: E => Boolean): Boolean
/**
 * Return `true` if `predicate` holds for each item in the list
 */
def forall(predicate: E => Boolean): Boolean
/**
 * Return a reversed list to the current
 */
def reverse: PersistentList[E]
/**
 * Return a list that contains items that met `predicate`
 */
def filter()(predicate: E => Boolean): PersistentList[E]
/**
 * First each item should be mapped to a list and then all the lists should be flatten to resulted list
 */
def flatMap[A]()(map: E => PersistentList[A]): PersistentList[A]
/**
 * Iterates over the list from left to right and folds its items in accumulator using `func`
 */
def foldLeft[A](acc: A)(func: (E, A) => A): A
/**
 * Iterates over the list from right to left and folds its items in accumulator using `func`
 */
def foldRight[A](acc: A)(func: (A, E) => A): A
```

These methods you may implement as exercises to practice functional programming style.
