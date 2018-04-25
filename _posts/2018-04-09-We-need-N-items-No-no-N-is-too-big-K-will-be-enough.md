---
layout: post
title: We need N items. No, no, N is too big; K will be enough
tags: [Java, algorithms, TDD]
---

[TDD](https://en.wikipedia.org/wiki/Test-driven_development) is a very useful discipline for implementing software. The more you practice it the more you ask where and how you can use it. The question that I was recently asked, by others and myself, is “Can TDD help me to solve algorithmic problems? If yes, then how?”. This article pursues the idea of what I personally do when solving an algorithmic task in TDD cycle.

## The Task

The algorithmic problem is the following:

> Given a non-empty array of integers, return the `k` maximum numbers from the array in any order.
> For example `array` is `[1, 3, 8, 4, 2, 6, 7]` and `k` is `4` then the result should contain `[8, 6, 7, 4]`

After thinking a little bit about the problem, I came up with the following list of test cases:

 * An array has one item and `k` is `1`
 * An array has two items, the max item is the first one and `k` is `1`
 * An array has two items, the max item is the second one and `k` is `1`
 * An array has three items, the max item is the last one and `k` is `1`
 * An array has three items and `k` is `2`

## We always need to start

Personally, I struggle with solving algorithmic problems. To tackle this as effortlessly as possible my first test has to have the simplest possible input values to the problem.

```java
//KMaxItemsTest.java
public class KMaxItemsTest {
  @Test
  public void singleItemArray_oneMaxItem() {
    assertThat(new KMaxItems().from(new int[]{1}, 1))
      .isEqualTo(new int[]{1});
  }
}

//KMaxItems
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    return null;
  }
}
```

```sh
JUnit version 4.12
.E
Time: 0.117
There was 1 failure:
1) singleItemArray_oneMaxItem(KMaxItemsTest)
org.junit.ComparisonFailure: expected:<[1]> but was:<null>

FAILURES!!!
Tests run: 1,  Failures: 1
```

The beginning is not so bad, we have defined API for our solution and have basic input data to drive the implementation. I could use **"fake it"** strategy, but then we would have data duplication in the test and the code, therefore, I return `numbers` in `from` method.

```java
//KMaxItems.java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    return numbers;
  }
}
```

```sh
JUnit version 4.12
.
Time: 0.098

OK (1 test)
```

 * ~~An array has one item and `k` is `1`~~ :heavy_check_mark:
 * An array has two items, the max item is the first one and `k` is `1`
 * An array has two items, the max item is the second one and `k` is `1`
 * An array has three items, the max item is the last one and `k` is `1`
 * An array has three items and `k` is `2`

One go - four left.

## Descending order

The other thing that I have found very hard is that you need a special test strategy. Your test should be small enough not to force you to implement a **"brute force"** or a **"highly optimized"** algorithm and in the same time should be big enough to drive you to the insight of **"How to implement"** the solution. The next test in the tests list is the following:

```java
//KMaxItemsTest.java
@Test
public void twoItemsArray_maxIsFirst_oneMaxItem() {
  assertThat(new KMaxItems().from(new int[]{2, 1}, 1))
    .isEqualTo(new int[]{2});
}
```

```sh
JUnit version 4.12
.E.
Time: 0.107
There was 1 failure:
1) twoItemsArray_maxIsFirst_oneMaxItem(KMaxItemsTest)
org.junit.ComparisonFailure: expected:<[2[]]> but was:<[2[, 1]]>

FAILURES!!!
Tests run: 2,  Failures: 1
```

Notice that the array is in descending order. This fact helps us make a step toward to the solution. The fix of the test is to return a new array that contains the first item of the input array.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    return new int[]{numbers[0]};
  }
}
```

```sh
JUnit version 4.12
..
Time: 0.104

OK (2 tests)
```

 * ~~An array has one item and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the first one and `k` is `1`~~ :heavy_check_mark:
 * An array has two items, the max item is the second one and `k` is `1`
 * An array has three items, the max item is the last one and `k` is `1`
 * An array has three items and `k` is `2`

## Ascending order

It might sound ridiculous, however, in the next test we need to use an array that is in ascending order. If it makes you feel better, imagine a pendulum that moves back and forth and eventually stops in the middle. This is our strategy, to write such tests that narrow our code to the solution of the problem.

```java
//KMaxItemsTest.java
@Test
public void twoItemsArray_maxIsLast_oneMaxItem() {
  assertThat(new KMaxItems().from(new int[]{1, 2}, 1))
    .isEqualTo(new int[]{2});
}
```

```sh
JUnit version 4.12
...E
Time: 0.113
There was 1 failure:
1) twoItemsArray_maxIsLast_oneMaxItem(KMaxItemsTest)
org.junit.ComparisonFailure: expected:<[[2]]> but was:<[[1]]>

FAILURES!!!
Tests run: 3,  Failures: 1
```

We need to check the length of the input array, which of the items is greater and return it as an array in order no to break previous tests and fix the current one.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    if (numbers.length > 1) {
      if (numbers[1] > numbers[0]) {
        return new int[]{numbers[1]};
      }
    }
    return new int[]{numbers[0]};
  }
}
```

```sh
JUnit version 4.12
...
Time: 0.182

OK (3 tests)
```

 * ~~An array has one item and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the first one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the second one and `k` is `1`~~ :heavy_check_mark:
 * An array has three items, the max item is the last one and `k` is `1`
 * An array has three items and `k` is `2`

## Increase the size

We do well in the previous step, let's make one more step in that direction and increase the input array length on one item.

```java
@Test
public void threeItemsArray_maxIsLast_oneMaxItem() {
  assertThat(new KMaxItems().from(new int[]{1, 2, 3}, 1))
    .isEqualTo(new int[]{3});
}
```

```sh
JUnit version 4.12
..E..
Time: 0.115
There was 1 failure:
1) threeItemsArray_maxIsLast_oneMaxItem(KMaxItemsTest)
org.junit.ComparisonFailure: expected:<[[3]]> but was:<[[2]]>

FAILURES!!!
Tests run: 4,  Failures: 1
```

The fix for the test is in the same direction as the previous fix. (Doesn't it feel a little strange?) Copy paste the array length and which of the item is greater checks with incrementing each constant.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    if (numbers.length > 1) {
      if (numbers.length > 2) {
        if (numbers[2] > numbers[1]) {
          return new int[]{numbers[2]};
        }
      }
      if (numbers[1] > numbers[0]) {
        return new int[]{numbers[1]};
      }
    }
    return new int[]{numbers[0]};
  }
}
```

```sh
JUnit version 4.12
....
Time: 0.091

OK (4 tests)
```

One more down.

 * ~~An array has one item and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the first one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the second one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has three items, the max item is the last one and `k` is `1`~~ :heavy_check_mark:
 * An array has three items and `k` is `2`

## Switch to smaller steps when you are stuck

If you take a look at our current code you may wonder what we will do next. If you look a little closer. It seems like we are moving nowhere. We have a lot of code duplication that we need approach somehow. This is the time to TDD magic comes in. First, I introduce `max` variable that is `int` type (it is because I know that `k` is always `1` for now) and assign `numbers[0]` to it.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    if (numbers.length > 1) {
      if (numbers.length > 2) {
        if (numbers[2] > numbers[1]) {
          return new int[]{numbers[2]};
        }
      }
      if (numbers[1] > numbers[0]) {
        return new int[]{numbers[1]};
      }
    }
    return new int[]{numbers[0]};
  }
}
```

If you think that I am crazy then watch my next two steps. First, I replace all `numbers[0]` with the `max`.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    if (numbers.length > 1) {
      if (numbers.length > 2) {
        if (numbers[2] > numbers[1]) {
          return new int[]{numbers[2]};
        }
      }
      if (numbers[1] > max) {
        return new int[]{numbers[1]};
      }
    }
    return new int[]{max};
  }
}
```

And, second, I change `return new int[]{numbers[1]};` to `max = numbers[1];`.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    if (numbers.length > 1) {
      if (numbers.length > 2) {
        if (numbers[2] > numbers[1]) {
          return new int[]{numbers[2]};
        }
      }
      if (numbers[1] > max) {
        max = numbers[1];
      }
    }
    return new int[]{max};
  }
}
```

I did three changes so I felt incomfortable and rerun tests.

```sh
JUnit version 4.12
....
Time: 0.089

OK (4 tests)
```

Good!
What if I place `if (numbers[1] > max)` above of the `if (numbers.length > 2)` statement? Will it work? Let's check!

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    if (numbers.length > 1) {
      if (numbers[1] > max) {
        max = numbers[1];
      }
      if (numbers.length > 2) {
        if (numbers[2] > numbers[1]) {
          return new int[]{numbers[2]};
        }
      }
    }
    return new int[]{max};
  }
}
```

```sh
JUnit version 4.12
....
Time: 0.092

OK (4 tests)
```

Cool! Now I pretty sure that I can replace `numbers[1]` with the `max` and instead of `return new int[]{numbers[2]};` I can write `max = numbers[2]`.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    if (numbers.length > 1) {
      if (numbers[1] > max) {
        max = numbers[1];
      }
      if (numbers.length > 2) {
        if (numbers[2] > max) {
          max = numbers[2];
        }
      }
    }
    return new int[]{max};
  }
}
```

```sh
JUnit version 4.12
....
Time: 0.067

OK (4 tests)
```

We still have some code duplication of `if` statements. Let's use "small steps" magic again! First, introduce an `i` variable with value `1` and replace `1` with it.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    int i = 1;
    if (numbers.length > i) {
      if (numbers[i] > max) {
        max = numbers[i];
      }
      if (numbers.length > 2) {
        if (numbers[2] > max) {
          max = numbers[2];
        }
      }
    }
    return new int[]{max};
  }
}
```

To get `2` I need to increment `i`, and again replace `2` with `i`.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    int i = 1;
    if (numbers.length > i) {
      if (numbers[i] > max) {
        max = numbers[i];
      }
      i++;
      if (numbers.length > i) {
        if (numbers[i] > max) {
          max = numbers[i];
        }
      }
    }
    return new int[]{max};
  }
}
```

```sh
JUnit version 4.12
....
Time: 0.102

OK (4 tests)
```

Hm... what if I put "useless" `i++` after the second `if (numbers[i] > max)` statement?

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    int i = 1;
    if (numbers.length > i) {
      if (numbers[i] > max) {
        max = numbers[i];
      }
      i++;
      if (numbers.length > i) {
        if (numbers[i] > max) {
          max = numbers[i];
        }
        i++;
      }
    }
    return new int[]{max};
  }
}
```

Oh my goodness, this is a loop! To check that, I need to change the first `if (numbers.length > i)` on `while (numbers.length > i)` and comment out the second one.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    int i = 1;
    while (numbers.length > i) {
      if (numbers[i] > max) {
        max = numbers[i];
      }
      i++;
      // if (numbers.length > i) {
      //   if (numbers[i] > max) {
      //     max = numbers[i];
      //   }
      //   i++;
      // }
    }
    return new int[]{max};
  }
}
```

```sh
JUnit version 4.12
....
Time: 0.09

OK (4 tests)
```

Great job! Let's remove comments and change a `while` on a `for` loop and move on.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int max = numbers[0];
    for (int i = 1; numbers.length > i; i++) {
      if (numbers[i] > max) {
        max = numbers[i];
      }
    }
    return new int[]{max};
  }
}
```

 * ~~An array has one item and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the first one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the second one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has three items, the max item is the last one and `k` is `1`~~ :heavy_check_mark:
 * An array has three items and `k` is `2`

## This is not the last one

If you notice, we wrote the code to find the maximum item in the input array. The next step is to generalize the solution and the last test will help us with it:

```java
@Test
public void threeItemsArray_twoMaxItems() {
  assertThat(new KMaxItems().from(new int[]{1, 2, 3}, 2))
    .contains(2, 3);
}
```

```sh
JUnit version 4.12
..E...
Time: 0.106
There was 1 failure:
1) threeItemsArray_twoMaxItems(KMaxItemsTest)
java.lang.AssertionError:
Expecting:
 <[3]>
to contain:
 <[2, 3]>
but could not find:
 <[2]>

FAILURES!!!
Tests run: 5,  Failures: 1
```

Let's move our current code under `if(k == 1)` condition and in the `else` we copy paste it and instead of one `max` variable we use `max1` and `max2`.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    if (k == 1) {
      int max = numbers[0];
      for (int i = 1; numbers.length > i; i++) {
        if (numbers[i] > max) {
          max = numbers[i];
        }
      }
      return new int[]{max};
    } else {
      int max1 = numbers[0];
      int max2 = numbers[1];
      for (int i = 1; numbers.length > i; i++) {
        if (numbers[i] > max1) {
          max1 = numbers[i];
        }
        if (numbers[i] > max2) {
          max2 = numbers[i];
        }
      }
      return new int[]{max1, max2};
    }
  }
}
```

```sh
JUnit version 4.12
..E...
Time: 0.12
There was 1 failure:
1) threeItemsArray_twoMaxItems(KMaxItemsTest)
java.lang.AssertionError:
Expecting:
 <[3, 3]>
to contain:
 <[2, 3]>
but could not find:
 <[2]>

FAILURES!!!
Tests run: 5,  Failures: 1
```

Woa! We need to check and exchange only one of maximum either `max1` or `max2`.

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    if (k == 1) {
      int max = numbers[0];
      for (int i = 1; numbers.length > i; i++) {
        if (numbers[i] > max) {
          max = numbers[i];
        }
      }
      return new int[]{max};
    } else {
      int max1 = numbers[0];
      int max2 = numbers[1];
      for (int i = 1; numbers.length > i; i++) {
        if (numbers[i] > max1) {
          max1 = numbers[i];
        } else if (numbers[i] > max2) {
          max2 = numbers[i];
        }
      }
      return new int[]{max1, max2};
    }
  }
}
```

```sh
JUnit version 4.12
.....
Time: 0.069

OK (5 tests)
```

And the last test is green.

 * ~~An array has one item and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the first one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has two items, the max item is the second one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has three items, the max item is the last one and `k` is `1`~~ :heavy_check_mark:
 * ~~An array has three items and `k` is `2`~~ :heavy_check_mark:

### You finish when you are done, but not when all tests are green

This section shows the refactoring steps, thus I concentrate only on k equals 2 part of the code. To recap:

```java
int max1 = numbers[0];
int max2 = numbers[1];
for (int i = 1; numbers.length > i; i++) {
  if (numbers[i] > max1) {
    max1 = numbers[i];
  } else if (numbers[i] > max2) {
    max2 = numbers[i];
  }
}
return new int[]{max1, max2};
```

First, I'd like to instantiate an array, change its content and return as a result of the function instead of working with **hard coded** two variables.

```java
int[] max = new int[2];
max[0] = numbers[0];
max[1] = numbers[1];
int max1 = numbers[0];
int max2 = numbers[1];
for (int i = 1; numbers.length > i; i++) {
  if (numbers[i] > max[0]) {
    max[0] = numbers[i];
  } else if (numbers[i] > max[1]) {
    max[1] = numbers[i];
  }
}
// return new int[]{max1, max2};
return max;
```

Seems like we copy first `k` items from the input array to `max` array, let's change `max` array size to `k` and copy items in a loop.


```java
int[] max = new int[k];
for (int j = 0; j < k; j++) {
  max[j] = numbers[j];
}
for (int i = 1; numbers.length > i; i++) {
  if (numbers[i] > max[0]) {
    max[0] = numbers[i];
  } else if (numbers[i] > max[1]) {
    max[1] = numbers[i];
  }
}
return max;
```

If we have copied first `k` items then we can start looking for maximum items from the `k`th item not the second. Thus, change `int i = 1` to `int i = k` in the `for` loop.

```java
int[] max = new int[k];
for (int j = 0; j < k; j++) {
  max[j] = numbers[j];
}
for (int i = k; numbers.length > i; i++) {
  if (numbers[i] > max[0]) {
    max[0] = numbers[i];
  } else if (numbers[i] > max[1]) {
    max[1] = numbers[i];
  }
}
return max;
```

Great! We almost finish. The only thing is left is putting one of `numbers` maximum into `max` array. This code looks very familiar if we do the following changes.

```java
int[] max = new int[k];
for (int j = 0; j < k; j++) {
  max[j] = numbers[j];
}
for (int i = k; numbers.length > i; i++) {
  int j = 0;
  if (numbers[i] > max[j]) {
    max[j] = numbers[i];
  } else {
    j++;
    if (numbers[i] > max[j]) {
      max[j] = numbers[i];
    } else {
      j++;
    }
  }
}
return max;
```

Oh, this is a loop, again; but a little different. We need "break" it when we find that one of `max` items is less then current `numbers[i]`.

```java
int[] max = new int[k];
for (int j = 0; j < k; j++) {
  max[j] = numbers[j];
}
for (int i = k; numbers.length > i; i++) {
  int j = 0;
  while (j < k) {
    if (numbers[i] > max[j]) {
      max[j] = numbers[i];
      break;
    } else {
      j++;
    }
  }
}
return max;
```

I assume we are ready to remove `if (k == 1)` branch. Yep! This is working!

```java
public class KMaxItems {
  public int[] from(int[] numbers, int k) {
    int[] max = new int[k];
    for (int j = 0; j < k; j++) {
      max[j] = numbers[j];
    }
    for (int i = k; numbers.length > i; i++) {
      int j = 0;
      while (j < k) {
        if (numbers[i] > max[j]) {
          max[j] = numbers[i];
          break;
        } else {
          j++;
        }
      }
    }
    return max;
  }
}
```

### Sum up

In this article, I tried to show the steps that I do when solving this particular algorithmic problem. The thing is that I still need to think about the solution before starting to code. And here you can have one of those questions 1) so why I need TDD if I need to think about a solution and 2) how I can solve other algorithmic problems with TDD. The answer to both questions is that TDD does not imply thoughtless coding, you need to come up with an idea how to solve a problem and think what tests will you need. Think carefully about tests, because they will guide you to the right solution, and support you in your refactoring if you are stuck.

P.S.: May the ~~force~~ tests be with you
