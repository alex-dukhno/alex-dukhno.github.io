---
layout: post
title: Moving From Imperative To Declarative Solution Supported By Tests
tags: [Java, algorithms, TDD]
---

Imperative programming language were and is dominative on the market. However, functional programming, gaining in popularity, brings declarative style of programming. even java got sort of functional improvments in 8th version. But if you like me, a guy who programs in imperative style most of their carrier you probably has problems of writing in declerative style. I like solve algorithmic problems imperatively and then wonder out if it is possible to solve them decleratively. in this article I move through one of such examples that I encounter not some much time ago

## The problem

The problem that we are going to look in the article is quite simple computational exercise that any one of us could have on technical interview. It stands as the following:

> Given a 'triangle' as an List of Lists of integers, with each list representing a level of the triangle, find the minimum sum achieved by following a top-down path and adding the integer at each level along the path. Movement is restricted to adjacent numbers from the top to the bottom.
> Note:
> - You can only traverse through adjacent nodes while moving up or down the triangle.
> - An adjacent node is defined as a node that is reached by moving down and left or down and right from a level.

Let's walk through a solution within an example of 'triangle':

> [
>      [1],
>     [2, 3],
>   [4, 5, 6],
>  [7, 8, 9, 10]
> ]

the minimum sum will be 14 = 1 + 2 + 4 + 7. To get that result we need to:
 * allocate array with the size of the lowest list
 * populate it with items of the lowest list
 * start from 'the lowest - 1' list and to the top of the triangle
 * loop over current level and add to it min item from prev level with the same index and index + 1
 * in the end the allocated array will hold the answer in the first cell

To make it clear bellow I describe each step with above example
> step 0
> [
>      [1],
>     [2, 3],
>   [4, 5, 6],
>  [7, 8, 9, 10] // => current level
> ]
>
>  [7, 8, 9, 10] // => allocated array

> step 1
> [
>      [1],
>     [2, 3],
>   [4, 5, 6],   // => current level
>  [7, 8, 9, 10]
> ]
>
>  [7 + 4, 5 + 8, 6 + 9, 10] // => allocated array

> step 2
> [
>      [1],
>     [2, 3],    // => current level
>   [4, 5, 6],
>  [7, 8, 9, 10]
> ]
>
>  [11 + 2, 13 + 3, 15, 10] // => allocated array

> step 3
> [
>      [1],      // => current level
>     [2, 3],
>   [4, 5, 6],
>  [7, 8, 9, 10]
> ]
>
>  [13 + 1, 16, 15, 10] // => allocated array



# Problem definition and test cases:
### Give a problem (actually an algorithmic task*) and a list of minimal test cases that is required to implement a solution to the given problem

# Implementing the imperative solution:
### Describe solution in general with input & output example
### Following the test cases list describe and implement an imperative solution to the problem. (I have 4 test cases, thus, roughly speaking, this part will have 4 sections in the article.)

# Implementing the declarative solution:
### Showing & describing what part of the imperative code match one of the functional/declarative pattern. Refactor code from the imperative to the declarative solution (in slow motion). This part is going to contain +/- 3 to 4 sections.

# Conclusion:
### In the conclusion I want to emphasize that refactoring from one style to another without tests is a bad idea (actually any refactoring without tests is a bad idea), and the other thing that I would like to highlight is that tests encourage you to experiment with code refactoring

