---
layout: post
title: JVM class file format
tags: JVM Java
---

I received [Java Virtual Machine Specification](https://www.amazon.com/gp/product/013390590X/ref=oh_aui_detailpage_o00_s00?ie=UTF8&psc=1) approximately a week ago. I had been waiting for it three weeks that is why I started to read it immediately. Reading the other chapter of the specification, I realized that I start to understand how and why `JVM` works in such a way. For better understanding, I am going to write a few articles about it. I will start by looking at generated bytecode by `javac` and how it interpreted by `java` runtime. In this post, I intended to outline the `class` file format in short.

# Say hello to bytecode

The start is always hard, let's start from the beginning, let's start with the "Hello, world!" example.

```java
public class HelloWorld {
    public static void main(String[] args) {
        new HelloWorld().sayHello();
    }

    private void sayHello() {
    }
}
```

To see the bytecode instructions of the below class you need to compile it with `javac` and then run `javap` as the follows:

```sh
$ javac HelloWorld.java
$ javap -c -p -v HelloWorld.class > HelloWorld.bc
```

> `javap` prints bytecode instructions to the standard output, so I transferred output into the file called `HelloWorld.bc`. You can call it for your choice and with any extension or none. It just convenient to me `.java` - source code, `.class` - compiled class, `.bc` - bytecode representation.

The result of above commands is the following

```
Classfile /Users/alex-diez/Projects/jvm-internals/HelloWorld.class
  Last modified Apr 2, 2017; size 344 bytes
  MD5 checksum 25a868af9590095c294cae89fbb9d195
  Compiled from "HelloWorld.java"
public class HelloWorld
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #5.#15         // java/lang/Object."<init>":()V
   #2 = Class              #16            // HelloWorld
   #3 = Methodref          #2.#15         // HelloWorld."<init>":()V
   #4 = Methodref          #2.#17         // HelloWorld.sayHello:()V
   #5 = Class              #18            // java/lang/Object
   #6 = Utf8               <init>
   #7 = Utf8               ()V
   #8 = Utf8               Code
   #9 = Utf8               LineNumberTable
  #10 = Utf8               main
  #11 = Utf8               ([Ljava/lang/String;)V
  #12 = Utf8               sayHello
  #13 = Utf8               SourceFile
  #14 = Utf8               HelloWorld.java
  #15 = NameAndType        #6:#7          // "<init>":()V
  #16 = Utf8               HelloWorld
  #17 = NameAndType        #12:#7         // sayHello:()V
  #18 = Utf8               java/lang/Object
{
  public HelloWorld();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1       // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 1: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: new           #2       // class HelloWorld
         3: dup
         4: invokespecial #3       // Method "<init>":()V
         7: invokespecial #4       // Method sayHello:()V
        10: return
      LineNumberTable:
        line 3: 0
        line 4: 10

  private void sayHello();
    descriptor: ()V
    flags: ACC_PRIVATE
    Code:
      stack=0, locals=1, args_size=1
         0: return
      LineNumberTable:
        line 7: 0
}
SourceFile: "HelloWorld.java"
```

First four lines provide information about the path to the `.class` file on your computer, last modification date, size, md5 checksum and the `.java` source file name. Then you can see the class name, major and minor versions and class's flags. `JVM` use the `constant pool` to resolve classes, methods, method's argument types, primitive and `String` constants, etc. I will describe it in more details in future posts.

Let start with the `HelloWorld` constructor.

```
  public HelloWorld();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1       // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 1: 0
```

You may notice that I haven't written a constructor for the `HelloWorld` class. However, `javac` generated it for me. The `descriptor` shows that it is a method that _does not accept parameters_ and return `void`, that presented by `V` symbol. You may spot that `args_size` is equal to 1. Constructors are special instance methods; therefore, their first argument is always `this`. `locals` is the size of `local variables` array and `stack` is the size of `stack operand`, why their sizes are 1 I will write about it later.

> `Java` - `JVM` types table
>
> |  Java type  |  bytecode signature  |
> |:-:|:-:|
> | `void` | `V` |
> | `byte` | `B` |
> | `short` | `S` |
> | `char` | `C` |
> | `int` | `I` |
> | `float` | `F` |
> | `long` | `J` |
> | `double` | `D` |
> | `boolean` | `Z` |
> | `reference` | `L` |
> | `array` | `[` |

Each method has `local variables` array and `operand stack`. Their sizes computed during compilation. `JVM` copies all method arguments into `local variables` array. That is why `HelloWorld` constructor `operand stack` and `local variables` array have size 1. `aload_0` loads element of `local variables` array with index `0`, which is `this`, onto the `operand stack`. `invokespecial` pops the head of the `operand stack` and executes `<init>` method of `java.lang.Object` with it. After that `return` finish method invocation with `void` result.

> `<init>` stands for initialization and is a constructor. The line `java/lang/Object."<init>":()V` means invoke `new Object()`.

`sayHello` is a simple method which I wrote just to show how `JVM` invokes `private` methods in bytecode instruction. Let's have a look at `main` method.

```
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: new           #2       // class HelloWorld
         3: dup
         4: invokespecial #3       // Method "<init>":()V
         7: invokespecial #4       // Method sayHello:()V
        10: return
      LineNumberTable:
        line 3: 0
        line 4: 10
```

The bytecode of `main` method is interesting. `new` operation allocates memory for our `HelloWorld` object and put reference onto the stack, `dup` duplicates the first item on the `operand stack`. The first `invokespecial` invoke `<init>` method with the first reference on `HelloWorld` and the second is invoke `sayHello` with the second reference to the `HelloWorld` instance.

`main` method `operand stack` looks like:

> `new`
>
>| Stack |
>|:-:|
>| `L HelloWorld` |
>
> `dup`
>
>| Stack |
>|:-:|
>| `L HelloWorld` |
>| `L HelloWorld` |
>
> `invokespecial "<init>":()V`
>
>| Stack |
>|:-:|
>| `L HelloWorld` |
>
> `invokespecial sayHello:()V`
>
>| Stack |
>|:-:|
>| |

# Wrap it up

In this article, we see three bytecode instructions `new`, `dup` and `invokespecial`. `new` allocates memory for an object. Thus, it means that when you write `new MyObject()` `JVM` needs to do a lot of work. It is not an atomic operation, `MyObject` constructor is not allocating memory, it only initializes fields. `dup` duplicates head of `operand stack`; I will cover this instruction in future posts. And `invokespecial` instruction means that `JVM` needs to invoke a method of the concrete class and don't need to look up the method in the `virtual table`.