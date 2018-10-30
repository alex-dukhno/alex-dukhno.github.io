---
layout: post
title: Methods invocation inside of a Java Virtual Machine
tags: [JVM]
---

[Java Virtual Machine](https://en.wikipedia.org/wiki/Java_virtual_machine) (JVM) is an abstract virtual machine that executes [Java bytecode](https://en.wikipedia.org/wiki/Java_bytecode). JVM has lots of [implementations](https://en.wikipedia.org/wiki/List_of_Java_virtual_machines). [HostSpot JVM](https://en.wikipedia.org/wiki/HotSpot), as the most popular JVM, will be taken as an example of the implementation throughout the article.
Initially, JVM was designed at [Sun Microsystems](https://en.wikipedia.org/wiki/Sun_Microsystems) to execute [Java](https://en.wikipedia.org/wiki/Java_(software_platform)) programs. Despite the fact that Java and JVMs are influencing each other designs; engineers that work on JVMs are trying to make them as general as possible. That is why JVM is a great execution engine for other languages than Java; such as [Scala](https://www.scala-lang.org/) and [Kotlin](https://kotlinlang.org/) which are statically typed and [Groovy](http://groovy-lang.org/) and [Ruby](http://ruby-lang.org/) which are dynamically typed languages. In this article, I will take a look at how JVM handles method invocation and its support for static and dynamic programming languages.

## JVM Method Frame

Let's get started with methods structure at runtime. Every time when JVM invokes any method it creates a frame for the method on a current thread stack. JVM method frame contains local variables array, operand stack and reference to a current class constant pool. A local variables array is used to store method's local values and method's argument values. JVM allocates memory for local variables array and puts all method arguments at the beginning of the array. The size of the array is known during execution time and is precomputed before the runtime phase. The local variable array contains values of local variables for primitives and references to objects on the heap. JVM uses the operand stack to execute bytecode instruction. JVM either executes bytecode instruction that pushes an operand on the stack or pops needed operands number for an instruction and executes the instruction. The size of the operand stack is also precomputed. Thus JVM knows exactly how much memory it should allocate on a thread stack for a particular method. A class constant pool is kind of storage of different data, such as names of other classes and methods, variable types and so on. It serves for linking other parts of the runtime image with a current method.
Let's have a look how JVM executes bytecodes operations with a simple example of two numbers addition. Here is the Java code:

```java
int a = 1;
int b = 2;
int c = a + b;
```

And the following is the bytecode representation:

```bytecode
0: iconst_1
1: istore_1
2: iconst_2
3: istore_2
4: iload_1
5: iload_2
6: iadd
7: istore_3
```

When JVM encounter `iconst_1` instruction it pushes `1` on the operand stack. `i` in `iconst_1` means `integer` type, `const` - constant, `1` - a value of the constant. (JVM bytecode instruction set contains such instructions for constants up to value `5`). Then it reads next instruction which is `istore_1`. This tells JVM to pop a value from the stack and store it into the array of local variables under index `1`. Then JVM repeats the same operation with the constant `2`. Instructions `iload_1` and `iload_2` tell JVM to push values from the local variables array at indexes `1` and `2` onto operand stack; and `iadd` pops both variables from the operand stack, add them and pushes result on the operand stack. `istore_3` simply pops result of addition from operand stack and store it to local variables array under the third index.

## JVM methods invocations

JVM instruction set has 5 bytecode instructions for method invocation. In this part of the article I briefly look through four of them: `invokestatic`, `invokeinterface`, `invokevirtual` and `invokespecial`; `invokedynamic` deserves its own part in this article. Let's have a look at the first four instructions.

#### `invokestatic`

`invokestatic` instruction means that method that will be invoked is a method of a class not an instance method. Also, they are called `static` because you need to use `static` keyword in Java to say that a method is a class method, not an instance method.

#### `invokespecial`

`invokespecial` instruction indicates an invocation of `private` instance methods, superclass methods or `constructor`s.

#### `invokevirtual`

`invokevirtual` instruction is generated for methods with `public`, `package private or default` and `protected` access modifiers. Those methods are virtual and could be overridden by subclasses.

#### `invokeinterface`

`invokeinterface` instruction tells JVM that it should invoke a class implementation of an interface method.

## JVM method invocation instructions closer look

It seems a little odd that JVM specifies 4 different instruction basically for only one procedure - a method invocation. We need to consider what happens before JVM starts a method invocation to understand why it has so many instructions. Before invoking a method JVM must perform two actions: method resolution and method lookup. Let's have a look at both these actions separately. Every method invocation instruction has an index to an entry in a class constant pool. The entry consist of a method name and a method descriptor. You can find this data in decompiled java code. Let's examine the code below:

```java
public class Main {
  public static void main(String[] args) {
    someMethod(1, "2", 3L, new Object());
  }

  private static void someMethod(int i, String s, long l, Object o) {
  }
}
```

The code snippet can be compiled with

```sh
javac Main.java
```

and then decompiled with

```sh
javap -c -p -v Main.class
```

After that we can see the following class constant pool and bytecode instructions of `main` method.

```
//...
Constant pool:
   #1 = Methodref          #5.#18         // java/lang/Object."<init>":()V
   #2 = String             #19            // 2
   #3 = Long               3l
   #5 = Class              #20            // java/lang/Object
   #6 = Methodref          #7.#21         // Main.someMethod:(ILjava/lang/String;JLjava/lang/Object;)V
   #7 = Class              #22            // Main
   #8 = Utf8               <init>
   #9 = Utf8               ()V
  #10 = Utf8               Code
  #11 = Utf8               LineNumberTable
  #12 = Utf8               main
  #13 = Utf8               ([Ljava/lang/String;)V
  #14 = Utf8               someMethod
  #15 = Utf8               (ILjava/lang/String;JLjava/lang/Object;)V
  #16 = Utf8               SourceFile
  #17 = Utf8               Main.java
  #18 = NameAndType        #8:#9          // "<init>":()V
  #19 = Utf8               2
  #20 = Utf8               java/lang/Object
  #21 = NameAndType        #14:#15        // someMethod:(ILjava/lang/String;JLjava/lang/Object;)V
  #22 = Utf8               Main
//...
 0: iconst_1
 1: ldc           #2                  // String 2
 3: ldc2_w        #3                  // long 3l
 6: new           #5                  // class java/lang/Object
 9: dup
10: invokespecial #1                  // Method java/lang/Object."<init>":()V
13: invokestatic  #6                  // Method someMethod:(ILjava/lang/String;JLjava/lang/Object;)V
16: return
```

> `javap` will generate a method name and its descriptor in a comment next to the method invocation

The `13: invokestatic  #6` points to the sixth entry in the class constant pool that contains the other two indexes `#6 = Methodref #7.#21`. `#7` is resolved into string value of class name `Main` and `#21` into the string representation of method name `someMethod` and string value of the method descriptor `(ILjava/lang/String;JLjava/lang/Object;)V`. Parameter types are enclosed by parentheses and the method return type is after them. In this particular example `I` symbol stands for `int` type, `Ljava/lang/String;` - a reference to a instance of `java.lang.String`, `J` - `long` and `Ljava/lang/Object;` - a reference to a `java.lang.Object` instance.

Having this data JVM perform a method resolution which is an process of finding that the class has the method with the method name and its attributes that specified in the method descriptor. Thereafter JVM proceeds with a method lookup. It is an operation of finding the method's index in the class methods table. Knowing that let's dig into how HotSpot JVM takes advantages of these facts.

#### `invokestatic`

When HotSpot JVM encounters this instruction at the first time it performs method resolution and lookup. However, knowing that `static` methods can't be overridden HotSpot wires method invocation inside a caller, and when JVM execute current method next time it does perform neither method resolution nor lookup; it immediately invokes the method.

#### `invokespecial`

When HotSpot JVM come across `invokespecial` instruction it executes the same procedure as with `invokestatic` except that in addition, it should check that a class instance is not null every time when the method is invoked.

#### `invokevirtual`

All Java Developers know that instance methods can be overridden and the method signature in a subclass has to match the method signature in a superclass. Those, HotSpot JVM saves the subclass method at the same index in method table as in the superclass. Because of that HotSpot JVM after a method resolution can save method index and use it for method lookup next time when the method is invoked.

#### `invokeinterface`

When HotSpot JVM encounters `invokeinterface` instruction it completes both method resolution and method lookup every time when a method is invoked, because methods that a class implements from an interface are stored in different methods table and therefore each class has a different index to their implementations of those methods.

## Invokedynamic

`invokedynamic` is a quite new bytecode instruction added in JVM 1.7. Before diving inside, let's have a look at a problem why `invokedynamic` was added to bytecode instruction set.

#### A problem

JVM is a great execution runtime, however, dynamic languages have a big problem to be implemented on it. The problem is that JVM requires a method to be invoked has an explicit receiver type (class or its instance), parameters types and a return type. Let's take a look at an example with a Ruby code

```ruby
def add(a, b)
  a + b
end
```

Here, arguments `a` and `b` can have any type and `+` is a method that is invoked on object `a`. `a` and `b` could be integers or strings or both, and because of dynamic nature of Ruby all checks: what method with which arguments should be called; are made during runtime. This problem can be solved with JVM reflection API.

#### Reflection

JVM provides a way to invoke a method indirectly; to do that you need to know a `Class`, a method name and list of parameter types. Here is a trivial example of this procedure:

```java
Method substring = String.class.getDeclaredMethod("substring", int.class, int.class);
String object = "Hello, World!";
Object hello = substring.invoke(object, 0, 5);
System.out.println(hello); // will print out "Hello"
```

One of the problems with reflection is that it will be always slower than a direct invocation of a method because JVM has to check method visibility, receiver and arguments types, parameters should be collected into an array (that produce "garbage"). All these checks must be made every time when a method is invoked that prevents JVM kicks JIT compiler in and optimize the code. For these and other reasons, `invokedynamic` bytecode instruction was added.

#### Invokedynamic

Despite the fact that `invokedynamic` is a bytecode instruction and most of the time generated by compilers such as `javac` or `scalac`. Java has an API that allows application developers to use it directly. The same example code from the previous part will look like this:

```java
MethodHandles.Lookup lookup = MethodHandles.lookup();
MethodType methodType = MethodType.methodType(String.class, int.class, int.class);
MethodHandle substring = lookup.findVirtual(String.class, "substring", methodType); //findStatic; findSpecial
Object hello = substring.invoke("Hello, World!", 0, 5);
System.out.println(hello); // will print out "Hello"
```

When the JVM executes `MethodHandle#invoke` method first time it will make all checks and wired up `MethodHandle` with exact method thus next `MethodHandle#invoke` method invocations will be direct calls of a method that was looked up.

## InvokeConclusion

In this article, I briefly take a look at different JVM bytecode instruction for methods invocation. They are `invokestatic`, `invokespecial`, `invokevirtual`, `invokeinterface` and `invokedynamic`. Of cause, there are a lot of things going inside a JVM when it handles a method invocation and this article is a simple introduction to the complex machinery that happens under the hood of Java Virtual Machine.
