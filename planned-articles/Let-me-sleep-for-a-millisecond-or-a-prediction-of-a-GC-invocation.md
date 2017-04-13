# Let me sleep for a millisecond or a prediction of a GC invocation

You might know that JVM collects garbage in unpredictable ways. However, [this tweet](https://twitter.com/shipilev/status/806514141401903104) inspired me to make some experiments. Let's do them now! For my tests I use `WeakHashMap`. It is the same as a `HashMap`, but with one small idiosyncrasy; it has weak references to its keys. As you may know, weak reference can be discarded during GC. My tests look like this:

```java
@Test
public void sanity() throws Exception {
    Map<String, String> m = new WeakHashMap<>();
    for (int i = 0; i < 100; i++) {
        m.put(String.valueOf(i), String.valueOf(i + 1));
    }
    for (int i = 0; i < 100; i++) {
        assertThat(m.containsKey(String.valueOf(i)), is(true));
    }
}

@Test
public void sleepless() throws Exception {
    Map<String, String> m = new WeakHashMap<>();
    for (int i = 0; i < 100; i++) {
        m.put(String.valueOf(i), String.valueOf(i + 1));
    }
    System.gc();
    assumeThat(m.size(), is(not(0)));
}

@Test
public void whoIsSayingThatIWasSleeping() throws Exception {
    Map<String, String> m = new WeakHashMap<>();
    for (int i = 0; i < 100; i++) {
        m.put(String.valueOf(i), String.valueOf(i + 1));
    }
    System.gc();
    Thread.sleep(1);
    assertThat(m.size(), is(0));
}
```

First test is just a sanity. I put one hundred `String`s into a `WeakHashMap` and check if they are still there.

In the `sleepless` test I put one hundred `String`s into a `WeakHashMap`, call `System.gc()` and make an **assumption** that the size of the map won't be equal to zero. (Let me remind you that test with `org.junit.Assume.assumeThat` will be passed if the assumption is true - and ignored if the assumption is false.) In the `whoIsSayingThatIWasSleeping` test I put a hundred `String`s into a `WeakHashMap`, call `System.gc()`, call `Thread.sleep(1)` and make an **assertion** that the size of the map is zero.

So what would happen in `sleepless` and `whoIsSayingThatIWasSleeping` tests?

For better understanding I ran a suite of tests ten times. The results are in the table below

| Test name                     | Passed | Failed | Ignored |
|-------------------------------|--------|--------|---------|
| sanity                        |   10   |   0    |    0    |
| sleepless                     |    3   |   0    |    7    |
| whoIsSayingThatIWasSleeping   |   10   |   0    |    0    |

The table tells us that GC was invoked every time when we call `Thread.sleep(1)`. On the other hand, GC was invoked 7 in 10 times if we don't call `Thread.sleep(1)`.

What happening here is so called JVM savepoints. `Thread.sleep` is just one of them. There is [a great article](http://psy-lob-saw.blogspot.com/2015/12/safepoints.html) which describes this topic in more detail.
