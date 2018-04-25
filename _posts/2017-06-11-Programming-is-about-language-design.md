---
layout: post
title: Programming is all about language design
tags: [personal-opinion]
---

On my career of software developer, I often heard that a good engineer knows their domain well. The reason is very intuitive, and I did not need an explanation. However, I had many talks with people who disagree with this point, and think that it is not worthy and would never pay you off.

## What brings me home

Today, when I started reading the fourth chapter of ["Structure and Interpretation of Computer Programs"](https://www.amazon.com/gp/product/0262510871) it reminded me of ["Domain Driven Design"](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215/) that I read the other day. The reason why I remembered it that the author states:

>"However, as we confront increasingly complex problems, we will find that Lisp, or indeed any fixed programming language, is not sufficient for our needs. We must constantly turn to new languages in order to express our ideas more effectively. Establishing new languages is a powerful strategy for controlling complexity in engineering design; we can often enhance our ability to deal with a complex problem by adopting a new language that enables us to describe (and hence to think about) the problem in a different way, using primitives, means of combination, and means of abstraction that are particularly well suited to the problem at hand."

That is it. The new language, that author writes about, is a domain language. Using your favorite programming language, you construct abstractions and APIs that talk your domain specific language.

Whatever your sort of developer: application, system, web or compiler. What we are doing on our daily job is writing a program that evaluates domain expressions; that consist of primitives, objects, and entities. It is easier to see from a compiler engineer perspective because they write a program that compiles the other programs according to the design of the programming language that those programs are written in. Nonetheless, it is crucial to understand it; we as programmers can define another higher level languages using a particular programming language to be as much explicit as we want in our programs. That might bring the most fundamental idea of software engineering, that compiler is just the other program.

## Then why it is extremely important

Because when you appreciate this point, you change your attitude to the software development; you change your way of looking at yourself as a programmer. You programs become the other compiler of your domain language within the view. You start to look at APIs as building blocks that help you express your intent to other programmers. And right at that point, you will try to write as convenient as possible API to be clear and striking in the declaration of your programs intentions.
