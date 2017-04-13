# How I handle errors in my SQL database with Rust

In the beginning of my project all I did with error handling was skipping it. I didn't want to think about "What to do if query would have syntax error?" or "What if query inserts data into table that does not exist?". But because I wrote suits of tests I was not afraid of the time when I will be force to think how to handle error situation in my application.

On the early stage of my project, queries' execution flow had three phases: `tokenizer -> parser -> executer`. However, I came to cases when I had to check type of data and table's column where query wants to insert. So I had to add stages to execution flow. Now it contains six phases: `tokenizer -> parser -> type inferior -> type checker -> validator -> executor`. All of them can failed with an error and it should be handled properly.

In Rust you may handle errors with `Option` or `Result` enums as return type of functions. `Option` has two variants `Some` (to show that it contains value) and `None` (to show that it is empty). On the other hand, `Result` has also two variants `Err` (to show that operation performed with error) and `Ok`. In my case I use `Result` trait as it can represent either correctly evaluated result or error if something goes wrong.

`Result` trait has bunch of different control flow methods. I use `and_then()`. It accepts a closure as parameter and return `Result`. The closure is invoked only when `Result` is `Ok` and has to produce the other `Result`.

Here is why I choose `and_then()` method
 - because I can write more readable code that shows all queries' execution flow

 ```rust
tokenize("sql query")
    .and_then(|tokens| parse(tokens))
    .and_then(|ast| type_inferring(ast))
    .and_then(|typed_ast| check_types(typed_ast))
    .and_then(|statement| validate(statement))
    .and_then(|query| execute(query))
 ```

 - each phase may produce some result or error
 - if error occur on any phase the flow will be interrupted and error will be returned
