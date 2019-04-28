---
layout: post
title: From Imperative To Reactive Part II
tags: [Java, Reactive, TDD]
---

This is a follow up of my [previous article](https://alex-diez.github.io/2018-03-31-From-Imperative-To-Reactive-Part-I/) about reactive programming. That article describes a step by step approach of converting a static binary tree in a stream of events. However, only API had reactive nature. This article illustrates one of the ways how can we refactor imperative implementation into a reactive one.

## The ways how to refactor code

In my opinion, there are at least two possible approaches how can one refactors any code. Both of them require suite of tests though :trollface:. The first one is a radical way, with xUnit framework facility ignore all tests except one, remove or mock old functionality, and then one by one unignore each test implementing the functionality in a new way. The second one is slowly change old code piece by piece in teeny tiny steps. They are both valid and have their own advantages and disadvantages. The first one is faster but you might be less confident in you changes, the second one is the opposite. Here, I choose to move slowly because I assume the readers aren't familiar with [reactor](http://projectreactor.io/) and that helps me to introduce it in small pieces as possible, however I assume basic knowledge of [Java 8 Stream API](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html).

## Emitting events

First of all, let's revisit the `findPaths` method

```java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    addPathRecursively(tree, new ArrayList<>(), paths, sum);
    return Flux.fromIterable(paths);
  }
```

The only line that has some “reactivity” is the last one. It contains a call to `Flux.fromIterable(Iterable<E>)` method. As our goal is to make our implementation more reactive we need to change the way how we create a `Flux` object. `Reactor` has lots of factory methods to create `Flux` and we need `Flux.create(Consumer<? super FluxSink<E>> emitter)`. `FluxSink` is an interface that has `next`, `complete` and `error` methods. Thanks to Java 8 we can implement a lambda method that handles each item of paths list and pass it into `Flux.create` method.

> `Flux` is an abstract class from the `reactor-core` artifact that represents a stream with one or more values.

```java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    addPathRecursively(tree, new ArrayList<>(), paths, sum);
    return Flux.create(
        emitter -> {
          paths.forEach(p -> emitter.next(p));
          emitter.complete();
        }
    );
  }
```

```sh
examples.ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

examples.ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

examples.ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasEventsOf_rootToLeafPaths PASSED
```

Tests passed; we haven't screwed anything up; let's go further.

## A List as a Stream

Next step is to get rid of the list of lists and use `FluxSink` instead. Rather than make one big step I will make lots of small ones. The first is moving invocation of `addPathRecursively` method into the lambda method.

```java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    // addPathRecursively(tree, new ArrayList<>(), paths, sum);
    return Flux.<List<Integer>>create(
        emitter -> {
          addPathRecursively(tree, new ArrayList<>(), paths, sum);
          paths.forEach(p -> emitter.next(p));
          emitter.complete();
        }
    );
  }
```

It is a good habit to rerun tests after each step if you want to be confident in your changes.

```sh
examples.ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

examples.ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

examples.ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasEventsOf_rootToLeafPaths PASSED
```

The second one is adding `FluxSink` interface as an argument to `addPathRecursively` method and making the code compile.

```java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    return Flux.<List<Integer>>create(
        emitter -> {
          addPathRecursively(tree, new ArrayList<>(), paths, sum, emitter);
          paths.forEach(p -> emitter.next(p));
          emitter.complete();
        }
    );
  }

  private void addPathRecursively(Tree node, List<Integer> path, List<List<Integer>> paths, int sum, FluxSink<List<Integer>> emitter) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        paths.add(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), paths, sum, emitter);
        addPathRecursively(node.right, new ArrayList<>(path), paths, sum, emitter);
      }
    }
  }
```

The third one is passing `path` variable into `emitter.next` instead of inserting into `paths` inside the `addPathRecursively` method.

```java
  private void addPathRecursively(Tree node, List<Integer> path, List<List<Integer>> paths, int sum, FluxSink<List<Integer>> emitter) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        // paths.add(path);
        emitter.next(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), paths, sum, emitter);
        addPathRecursively(node.right, new ArrayList<>(path), paths, sum, emitter);
      }
    }
  }
```

```sh
examples.ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

examples.ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

examples.ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasEventsOf_rootToLeafPaths PASSED
```

Now we can remove `paths` from the arguments list of `addPathRecursively` method.

```java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    return Flux.<List<Integer>>create(
        emitter -> {
          //addPathRecursively(tree, new ArrayList<>(), sum, paths, emitter);
          addPathRecursively(tree, new ArrayList<>(), sum, emitter);
          paths.forEach(p -> emitter.next(p));
          emitter.complete();
        }
    );
  }

  private void addPathRecursively(Tree node, List<Integer> path, int sum,/* List<List<Integer>> paths,*/ FluxSink<List<Integer>> emitter) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        emitter.next(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), sum, emitter);
        addPathRecursively(node.right, new ArrayList<>(path), sum, emitter);
      }
    }
  }
```

And finally remove `paths.forEach(p -> emitter.next(p))` and `paths` variable all together.

```java
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    // List<List<Integer>> paths = new ArrayList<>();
    return Flux.<List<Integer>>create(
        emitter -> {
          addPathRecursively(tree, new ArrayList<>(), sum, emitter);
          // paths.forEach(p -> emitter.next(p));
          emitter.complete();
        }
    );
  }

  private void addPathRecursively(Tree node, List<Integer> path, int sum, FluxSink<List<Integer>> emitter) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        emitter.next(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), sum, emitter);
        addPathRecursively(node.right, new ArrayList<>(path), sum, emitter);
      }
    }
  }
```

```sh
examples.ReactiveTreeTest > streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum PASSED

examples.ReactiveTreeTest > emptyStream_whenGivenEmptyTree PASSED

examples.ReactiveTreeTest > streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum PASSED

examples.ReactiveTreeTest > streamHasEventsOf_rootToLeafPaths PASSED
```

## Source code

The source code for this article you can find [here](https://github.com/alex-dukhno/alex-diez.github.io/tree/master/source-code/java-examples/reactive-tree/part-two/src)

## Wrap up

By doing this small refactoring we reach our goal of having more reactive style in our implementation. Also, we got another benefit, instead of accumulating events in a list we are emitting them immediately.
