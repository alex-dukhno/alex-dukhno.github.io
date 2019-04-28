---
layout: post
title: From Imperative To Reactive Part I
tags: [Java, Reactive, TDD]
---

Many projects have been written in an imperative style. That’s fine when the end-to-end flow of your application is sequential. However, it becomes incredibly complex if you introduce parallel or concurrent programming in your application. Your application must handle many users requests, thus you start making your code parallel and concurrent. Experienced engineers advocate reactive programming as a way to handle this complexity. The reactive style turns into a trend for last few years. But, how can we use the reactive style with our old imperative applications? In this article, I am going to illustrate how you can create a reactive stream from a binary tree.

## Problem description

There is a website - [leetcode](https://leetcode.com/). It has lots of algorithmic problems that you can be asked to solve on a technical interview. I will borrow one of them for purposes of this article. The task is the following:

> Given a binary tree and a sum, find all root-to-leaf paths where each path's sum equals the given sum.
> For example:
> Given the below binary tree and sum = 22,
>```
>              5
>             / \
>            4   8
>           /   / \
>          11  13  4
>         /  \    / \
>        7    2  5   1
>```
> return
>```[
>   [5,4,11,2],
>   [5,8,4,5]
>]```


That’s it. For example, we have a tree with root equals `5`, the left leaf equals `3` and the right leaf equals `7` and sum equals `12`, then our solution must return a reactive stream of lists with a list of `5` and `7`. I will use [reactor-core version 3.1.5](http://projectreactor.io/) as a Java implementation of reactive streams and [JUnit version 4](https://junit.org/junit4/) for unit testing. We have to pass the following list of tests to implement such functionality:
 * Reactive stream is empty when tree is empty
 * Reactive stream has single event when tree has only root and root value is equals to the target sum
 * Reactive stream is empty when tree has only root and root value is not equal to the target sum
 * Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum
 * Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum
 * Reactive stream has list of root-to-leaf paths
Lets begin.

## First test

My first test is always a dummy empty test method. It can sound crazy if you are not familiar with [Test Driven Development](https://en.wikipedia.org/wiki/Test-driven_development). However, when I run the test and see that it passes, then I know for sure that my infrastructure for testing is ready to go.

```java
//ReactiveTreeTest.java
import org.junit.Test;

public class ReactiveTreeTest {
  @Test
  public void nothing() throws Exception {
  }
}
```

Running the test

```sh
ReactiveTreeTest > nothing PASSED
```

Test environment :heavy_check_mark:

## Reactive stream is empty when tree is empty

The simplest test that I can think of is to return an empty stream when we get an empty tree. I don't want to make things complicated and implement my own binary tree. I will use simple class with three fields `value`, `left` and `right`, as in the following code snippet:

```java
class Tree {
  int   value;
  Tree  left;
  Tree  right;
}
```

A null value will represent an empty tree; given that our test looks like this:

```java
//ReactiveTreeTest.java
import org.junit.Test;
import reactor.test.StepVerifier;

public class ReactiveTreeTest {
  @Test
  public void emptyStream_whenGivenEmptyTree() throws Exception {
    new PathSum().findPaths(null, 10)
      .as(StepVerifier::create)
      .verifyComplete();
  }
}
```

> `StepVerifier` is the class from the [reactor-test](http://projectreactor.io/docs/core/release/reference/docs/index.html#testing) artifact. It allows you to examine a reactive stream behavior. For example, you can assert that stream sends a complete signal by calling `verifyComplete()` method. We will see other methods that help us verify what events reactive stream sends.

We get a compile-time error when we try to run tests; that's because we don't have `PathSum` class that will contain the solution to our task. Let's create one with the `findPaths` method with dummy implementation that returns `null`. The `Tree` class will live there too.

```java
//PathSum.java
import java.util.List;
import reactor.core.publisher.Flux;

public class PathSum {
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    return null;
  }

  public static class Tree {
    int   value;
    Tree  left;
    Tree  right;
  }
}
```

> `Flux` is the class from the `reactor-core` artifact that represents a stream with one or more values.

We get expected `NullPointerException` after running the test.

```sh
ReactiveTreeTest > emptyStream_whenGivenEmptyTree FAILED
    java.lang.NullPointerException at ReactiveTreeTest.java:9

1 test completed, 1 failed
```

The fix is very simple to the problem - we need to return empty `Flux`; that we can do with `Flux.empty()`

```java
//PathSum.java
public class PathSum {
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    return Flux.empty();
  }
}
```

```sh
ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED
```

 * ~~Reactive stream is empty when tree is empty~~ :heavy_check_mark:
 * Reactive stream has single event when tree has only root and root value is equals to the target sum
 * Reactive stream is empty when tree has only root and root value is not equal to the target sum
 * Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum
 * Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum
 * Reactive stream has list of root-to-leaf paths

## Sum of one item

Now, we need to handle cases when tree contains at least one item. There are two such cases when root either has `value` that equals to `sum` or not. Lets handle positive case first.

```java
//ReactiveTreeTest.java
import org.junit.Test;
import reactor.test.StepVerifier;
import java.util.List;

public class ReactiveTreeTest {
  @Test
  public void emptyStream_whenGivenEmptyTree() throws Exception {
    new PathSum().findPaths(null, 10)
      .as(StepVerifier::create)
      .verifyComplete();
  }

  @Test
  public void streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(10), 10)
      .as(StepVerifier::create)
      .expectNext(List.of(10))
      .verifyComplete();
  }
}
```

Test checks that stream sends a `[10]` list before completion. That does not compile. We need to implement a constructor for `Tree` class

```java
//PathSum.java
//...
  public static class Tree {
    int   value;
    Tree  left;
    Tree  right;

    public Tree(int value) {
      this.value = value;
    }
  }
```

We fixed compile errors but have a failed test.

```sh
ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum FAILED
    java.lang.AssertionError: expectation "expectNext([10])" failed (expected: onNext([10]); actual: onComplete())
        //... skipped stack trace

2 tests completed, 1 failed
```

Lets fix this with simple `if` statement.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    if (tree != null) {
      return Flux.just(List.of(10));
    }
    return Flux.empty();
  }
```

```sh
ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED
```

We need to eliminate data duplication in the test and the code before we move further. The `List.of(10)` is actually `List.of(tree.value)`. Let's change that and we are free to go to the next case.

 * ~~Reactive stream is empty when tree is empty~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has only root and root value is equals to the target sum~~ :heavy_check_mark:
 * Reactive stream is empty when tree has only root and root value is not equal to the target sum
 * Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum
 * Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum
 * Reactive stream has list of root-to-leaf paths

## No ~~money~~ match no ~~honey~~ list

Now we need to test that stream will send complete event immediately if a tree has one item and its `value` is not equal to the target `sum`. To cover that functionality we need to write the following test:

```java
  @Test
  public void streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(20), 10)
      .as(StepVerifier::create)
      .verifyComplete();
  }
```

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum FAILED
    java.lang.AssertionError: expectation "expectComplete" failed (expected: onComplete(); actual: onNext([10]))
        //...skipped stack trace

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

3 tests completed, 1 failed
```

It fails as expected; the solution for that is `if` statement again.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    if (tree != null) {
      if (tree.value == sum) {
        return Flux.just(List.of(tree.value));
      }
    }
    return Flux.empty();
  }
```

 * ~~Reactive stream is empty when tree is empty~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has only root and root value is equals to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream is empty when tree has only root and root value is not equal to the target sum~~ :heavy_check_mark:
 * Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum
 * Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum
 * Reactive stream has list of root-to-leaf paths

## When you need to go left

If you follow tests and code carefully you might think that all we do is add another test and `if` statement. Well, on one hand, it really looks like that. On the other hand, we are gathering tests for cases that we could miss after changing the code. This part contains some refactoring. Let's write a test that pushes us further.

```java
//ReactiveTreeTest.java
  @Test
  public void streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(3, new PathSum.Tree(4)), 7)
      .as(StepVerifier::create)
      .expectNext(List.of(3, 4))
      .verifyComplete();
  }
```

First, we need add one more constructor to `Tree` class.

```java
//PathSum.java
    public Tree(int value, Tree left) {
      this(value);
      this.left = left;
    }
```

We have a clear message what is missing after running the tests. Our stream is empty because `Tree` root value is not equal to the target `sum`.

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum FAILED
    java.lang.AssertionError: expectation "expectNext([3, 4])" failed (expected: onNext([3, 4]); actual: onComplete())
        //...skipped stack trace(ReactiveTreeTest.java:35)

4 tests completed, 1 failed
```

A quick fix is to copy paste of two `if` statements. We need to check against `tree.left` node this time.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    if (tree != null) {
      if (tree.value == sum) {
        return Flux.just(List.of(tree.value));
      }
      if (tree.left != null) {
        if (tree.value + tree.left.value == sum) {
          return Flux.just(List.of(tree.value, tree.left.value));
        }
      }
    }
    return Flux.empty();
  }
```

If we look closer on `if(tree != null)` and `if(tree.left != null)` it is actually an examination that we reach bottom of the tree. It reminds me tree traversal in depth. The other two ifs, `if(tree.value == sum)` and `if (tree.value + tree.left.value == sum)`, is in fact an examination that sum from current node to the root of the tree is equal to the target `sum`. It is clear that it is a recursive problem. However, let's iterate in short steps to see where it drives us.

What we have to do is to create an empty list if tree node is not null add its value to the list and path the list to `Flux.just()` method.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<Integer> path = new ArrayList<>();
    if (tree != null) {
      path.add(tree.value);
      if (tree.value == sum) {
        return Flux.just(path);
      }
      if (tree.left != null) {
        path.add(tree.left.value);
        if (tree.value + tree.left.value == sum) {
          return Flux.just(path);
        }
      }
    }
    return Flux.empty();
  }
```

That works!

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED
```

A sum from a current node to the root node is the sum of items in the list; iteration over a list deserves its separated method.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<Integer> path = new ArrayList<>();
    if (tree != null) {
      path.add(tree.value);
      if (sum(path) == sum) {
        return Flux.just(path);
      }
      if (tree.left != null) {
        path.add(tree.left.value);
        if (sum(path) == sum) {
          return Flux.just(path);
        }
      }
    }
    return Flux.empty();
  }

  private int sum(List<Integer> path) {
    int sum = 0;
    for (int e : path) {
      sum += e;
    }
    return sum;
  }
```

We can create a method that recursively iterates over the tree, but it is better to stop here and write a test for the next case.

 * ~~Reactive stream is empty when tree is empty~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has only root and root value is equals to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream is empty when tree has only root and root value is not equal to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum~~ :heavy_check_mark:
 * Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum
 * Reactive stream has list of root-to-leaf paths

## Twins

The next case to consider is when the left and the right leaf in the paths; it is only the case when they have the same value. And here is the test:

```java
//ReactiveTreeTest.java
  @Test
  public void streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(3, new PathSum.Tree(4), new PathSum.Tree(4)), 7)
      .as(StepVerifier::create)
      .expectNext(List.of(3, 4))
      .expectNext(List.of(3, 4))
      .verifyComplete();
  }
```

First, we make it compile by adding one more constructor to `Tree` class and then run tests.

```java
//PathSum.java
    public Tree(int value, Tree left, Tree right) {
      this(value, left);
      this.right = right;
    }
```

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths FAILED
    java.lang.AssertionError: expectation "expectNext([3, 4])" failed (expected: onNext([3, 4]); actual: onComplete())
        //...skip stack trace

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

5 tests completed, 1 failed
```

As you may guess the fix is my favorite `if` statement that is a copy pasted of `tree.left` case.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<Integer> path = new ArrayList<>();
    if (tree != null) {
      path.add(tree.value);
      if (sum(path) == sum) {
        return Flux.just(path);
      }
      if (tree.left != null) {
        path.add(tree.left.value);
        if (sum(path) == sum) {
          return Flux.just(path);
        }
      }
      if (tree.right != null) {
        path.add(tree.right.value);
        if (sum(path) == sum) {
          return Flux.just(path);
        }
      }
    }
    return Flux.empty();
  }
```

Wow, that does not work! The `findPaths` function returns right after processing the left leaf. I don't want to dive deep in reactors API in this article; maybe, I will do that in the next one. That's why I am going to do the following: remove my last "fix" that doesn't help, `ignore` the last test with `@org.junit.Ignore` annotation and refactor the code to use a list of lists of integer values from tree nodes.

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths SKIPPED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED
```

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    if (tree != null) {
      path.add(tree.value);
      if (sum(path) == sum) {
        paths.add(path);
      }
      if (tree.left != null) {
        path.add(tree.left.value);
        if (sum(path) == sum) {
          paths.add(path);
        }
      }
    }
    return Flux.fromIterable(paths);
  }
```

I constantly rerun tests during my refactoring to make sure that I haven't broken anything. We are ready to remove `@Ignore` annotation check test is failing and make needed changes.

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths FAILED
    java.lang.AssertionError: expectation "expectNext([3, 4])" failed (expected: onNext([3, 4]); actual: onComplete())
        //... skipped stack trace

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

5 tests completed, 1 failed
```

Yes, the test fails as before, to fix this we need to create a separated list of integers for left and right leaves. Let's check that it works.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    if (tree != null) {
      path.add(tree.value);
      if (sum(path) == sum) {
        paths.add(path);
      }
      if (tree.left != null) {
        List<Integer> leftPath = new ArrayList<>(path);
        leftPath.add(tree.left.value);
        if (sum(leftPath) == sum) {
          paths.add(leftPath);
        }
      }
      if (tree.right != null) {
        List<Integer> rightPath = new ArrayList<>(path);
        rightPath.add(tree.right.value);
        if (sum(rightPath) == sum) {
          paths.add(rightPath);
        }
      }
    }
    return Flux.fromIterable(paths);
  }
```

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED
```

We are not done yet. We have a code duplication. Let's create a method that recursively invoke itself with the following parameters: a tree node, a list of integers value before the node, a list of list and the target sum; and inside the method we will do all logic.

```java
//PathSum.java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    addPathRecursively(tree, new ArrayList<>(), paths, sum);
    return Flux.fromIterable(paths);
  }

  private void addPathRecursively(Tree node, List<Integer> path, List<List<Integer>> paths, int sum) {
    if (node != null) {
      path.add(node.value);
      if (sum(path) == sum) {
        paths.add(path);
      }
      addPathRecursively(node.left, new ArrayList<>(path), paths, sum);
      addPathRecursively(node.right, new ArrayList<>(path), paths, sum);
    }
  }
```

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED
```

We cleaned our code up and ready to move to the next test.

 * ~~Reactive stream is empty when tree is empty~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has only root and root value is equals to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream is empty when tree has only root and root value is not equal to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum~~ :heavy_check_mark:
 * Reactive stream has list of root-to-leaf paths

## Last but not least

One step has left before we finish our transition from the binary tree to reactive stream. We haven't implement logic where we check that our list of lists has only root-to-leaf paths. It is a very tricky case. How can we test that? To be honest, I stuck here for good five minutes, and that because of our refactoring in the previous section. I took a look at the code and could not see what test data I need. But when I went scrolled to the previous code snippet where we had path sum check for the tree root it strikes me immediately.

```java
if (tree != null) {
  if (sum(path) == sum) {
    paths.add(path);
  }
  //... omitted code
```

What if root's value is equal to the target `sum` and it has at least one leaf that equals zero? Here is the test:

```java
  @Test
  public void streamHasEventsOf_rootToLeafPaths() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(1, new PathSum.Tree(0)), 1)
      .as(StepVerifier::create)
      .expectNext(List.of(1, 0))
      .verifyComplete();
  }
```

It fails!

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasEventsOf_rootToLeafPaths FAILED
    java.lang.AssertionError: expectation "expectNext([1, 0])" failed (expected value: [1, 0]; actual value: [1])
        //... skipped stack trace

6 tests completed, 1 failed
```

All we need to add is a check of condition when a node does not have children.

```java
//PathSum.java
  private void addPathRecursively(Tree node, List<Integer> path, List<List<Integer>> paths, int sum) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        paths.add(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), paths, sum);
        addPathRecursively(node.right, new ArrayList<>(path), paths, sum);
      }
    }
  }
```

```sh
ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

ReactiveTreeTest > streamHasEventsOf_rootToLeafPaths PASSED
```

That is it. We are done. If you don't believe, you can use example from the leetcode exercise.

 * ~~Reactive stream is empty when tree is empty~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has only root and root value is equals to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream is empty when tree has only root and root value is not equal to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream has single event when tree has root and left leaf and sum of their values is equal to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream has two events when tree has root and both leaves, root+left and root+right values are equal to the target sum~~ :heavy_check_mark:
 * ~~Reactive stream has list of root-to-leaf paths~~ :heavy_check_mark:

## Source code

You may find source code [here](https://github.com/alex-dukhno/alex-diez.github.io/tree/master/source-code/java-examples/reactive-tree/part-one).

## Conclusion

The final version of the code has only one call of `reactor` API - `Flux.fromIterable`. Some of you might think that it is not very "reactive". However, what we accomplish is that now we have functionality that transforms a binary tree structure into a reactive stream. We build a bridge between imperative and reactive style. That was our goal in this article.
