---
layout: post
title: Catch me if you can
---

If you are a Java software engineer as myself you probably have written tones of `try-catch-finally` block of code. But do you know how it works in reality. Let's take a look at how `Java Virtual Machine(JVM)` handles exceptions.

# Let's try, and see if it catches

Starting point is always should be the easiest one. We starting with simple class with few methods and go through all possible variants of handling exceptions by `JVM`.

```java
public class CatchMeIfYouCan {

    public void tryCatch() {
        try {
            tryItOut();
        } catch(Exception e) {
            handleException(e);
        }
    }

    private void tryItOut() throws Exception {
    }

    private void handleException(Exception e) {
    }
}
```

We got here public class `CatchMeIfYouCan`, one public method `tryCatch` and two private methods `truItOut` and `handleException`. Too see byte code that will be evaluated by `JVM` we need to compile code with `javac` and decompile it with `javap`. We don't need byte code of private methods, thus, I use only `-c` flag of `javap`.

```sh
$ javac CatchMeIfYouCan.java
$ javap -c CatchMeIfYouCan.class > CatchMeIfYouCan.bc
```

If you open `CatchMeIfYouCan.bc` with any text editor you will see the following byte code instructions.

```bytecode
Compiled from "CatchMeIfYouCan.java"
public class kata.joins.CatchMeIfYouCan {
  public kata.joins.CatchMeIfYouCan();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public void tryCatch();
    Code:
       0: aload_0
       1: invokespecial #2                  // Method tryItOut:()V
       4: goto          13
       7: astore_1
       8: aload_0
       9: aload_1
      10: invokespecial #4                  // Method handleException:(Ljava/lang/Exception;)V
      13: return
    Exception table:
       from    to  target type
           0     4     7   Class java/lang/Exception
}
```

>
> If you are not familiar with byte code instruction you might want to look at them on the Internet. Here I briefly explain what is going on in the constructor.
>
> As you may know if programmer has not provide constructor for a class `javac` automatically will generate it.
>
