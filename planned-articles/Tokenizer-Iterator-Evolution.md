# Tokenizer. Iterator. Evolution.

In my opinion every programmer, if they want to become better in technical perspective, should write their own operation system, programming language, and client server application. Many programmer out there would argue that it does not bring any value. There are a lot of operation systems and programming languages. However, these long term exercises would disclose a lot of engineering principles.

As for me, I chose to write client server application (with programming language parser at the same time). My pet project is a simple database management system. I decided to start from a SQL query parser. Read few articles about how to write front end for compilers I started from a tokenizer. The idea of the tokenizer is very simple it has a string as input and produce a list of tokens as output. Let's look at example to be more clear.

```sql
select 1 from dual;
```

As you can see it is a simple query which requests '1' from some table 'dual'. The output of tokenizer could be the following:

```
+------+-+----+----+-+
|select|1|from|dual|;|
+------+-+----+----+-+
```

Here you see each token in its own box. Now, have some basic idea of what tokenizer should do with input string let's implement it. For now let tokens will be just a strings. From the first look at example I might think that the input string can be split by white spaces and semicolon can be skipped. Ok let's write some code in rust.

```rust
let mut tokens_str = String::from("select 1 from dual;");
tokens_str.pop();
let tokens = tokens_str.split_whitespace().collect::<Vec<&str>>();
assert_eq!(tokens, vec!["select", "1", "from", "dual"]);
```

Pretty cool, ha? But what if we have a predicate in our query and a user of our product does not put white spaces after each token.

```sql
select col1 from table1 where col2<=5 and col3 < 0;
```

But it's not a big problem we can write loop which iterate over the string character by character and we can collect symbols to buffer and when needed push them to tokens vector.

```rust
fn tokenize(query: &str) -> Vec<Token> {
    let mut tokens = Vec::default();
    let mut buffer = String::default();
    for c in query.chars() {
        if c == ' ' {
            tokens.push(buffer.clone());
            buffer.clear();
        }
        else if c == '<' {
            // handle < and <=
        }
        buffer.push(c);
    }
    tokens
}

#[test]
fn tokenize_test() {
    assert_eq!(
        tokenize("select col1 from dual where col2<=5 and col3 < 0;"),
        vec!["select", "col1", "from", "dual", "where", "col2", "<=", "5", "and", "col3", "<", "0", ";"]
    );
}
```

Well yeah, we got problem with less and less-or-equal token. Here we need "look ahead" what character is next in our input string. Let's rewrite using iterator implicitly.

```rust
fn tokenize(query: &str) -> Vec<Token> {
    let mut tokens = Vec::default();
    let mut buffer = String::default();
    let mut chars = query.chars().peekable();
    loop {
        match chars.peek().cloned() {
            Some(' ') => {
                chars.next();
                if !buffer.is_empty() {
                    tokens.push(buffer.clone());
                    buffer.clear();
                }
            },
            Some(c @ 'a'...'z') | Some(c @ '0'...'9') => { chars.next(); buffer.push(c); },
            Some('<') => {
                chars.next();
                if !buffer.is_empty() {
                    tokens.push(buffer.clone());
                    buffer.clear();
                }
                match chars.peek().cloned() {
                    Some('=') => {
                        chars.next();
                        tokens.push(String::from("<="));
                    },
                    _ => tokens.push(String::from("<")),
                }
            }
            Some(';') => {
                chars.next();
                if !buffer.is_empty() {
                    tokens.push(buffer.clone());
                    buffer.clear();
                }
                tokens.push(String::from(";"));
            }
            _ => break,
        }
    }
    tokens
}
```

As you can notice the tokenize function grown significantly.

On my project the tokenize method is growing every time when I write up pice of code to cover the other part of SQL syntax. It has become very huge and unreadable for person who not familiar with project. That's bad. That's what I want to avoid. For now I left it as technical debt but I will come back to it when read something new about language's parsers and. That's why I started this project. To learn new.
