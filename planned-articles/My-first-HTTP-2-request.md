# My first HTTP/2 request

HTTP/2 is the most widespread web protocol. [Its RFC #7540](https://tools.ietf.org/html/rfc7540) was published by Internet Engineer Task Force in May 2015. One and a half years has passed. Have you noticed that some web sites support it? Because the latest version of the most popular web-servers do support. In this article, I share my experience how I added handling of simple HTTP/2 request feature to my [web server](https://github.com/CodeInten/http2-ws). This is one of my pet projects on which I work on in my free time.

Developing project by TDD and handling any HTTP/2 request is kind of serious feature for a web server. Therefore I need to write a functional test. Here is a simplified code snippet:

```java
public class WebServerEndToEndTest {

    private Process webServer;
    private HttpClient client;

    @Before
    public void startWebServer() throws IOException, InterruptedException {
        webServer = new ProcessBuilder("java", "-cp", "build/classes/main", "ua.http.ws.WebServer", "-p", "8080")
                .start();
        webServer.waitFor(1, TimeUnit.SECONDS);
    }

    @After
    public void stopWebServer() throws Exception {
        webServer.destroy();
    }

    @After
    public void stopClient() throws Exception {
        client.stop();
    }

    // ... here are bunch of other functional tests

    @Test
    public void serverShouldSendOk_whenClientRequestRoot_byHttp2() throws Exception {
        HTTP2Client lowLevelClient = new HTTP2Client();
        lowLevelClient.start();

        client = new HttpClient(new HttpClientTransportOverHTTP2(lowLevelClient), null);
        client.start();
        ContentResponse response = client.newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .timeout(10, TimeUnit.SECONDS)
                .version(HttpVersion.HTTP_2)
                .send();

        assertThat(response.getStatus(), is(HttpStatus.Code.OK.getCode()));
        assertThat(response.getVersion(), is(HttpVersion.HTTP_2));
    }
}
```

`startWebServer` starts web server process that listen to 8080 port for TCP connection. `stopWebServer` stops web server process and `stopClient` stops http client. I use [jetty http-2 client libraries](http://www.eclipse.org/jetty/documentation/9.3.x/http-client.html) in my tests. Obviously, when I ran this test it failed.

If you open network sniffer, for instance `wireshark`, and filter out packages by `http2` you will see something similar to `PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n`. My wireshark shows:

```{r, engine='bash'}
HyperText Transfer Protocol 2
    Stream: Magic
        Magic: PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n
    Stream: SETTINGS, Stream ID: 0, Length 6
        Length: 6
        Type: SETTINGS (4)
        Flags: 0x00
            .... ...0 = ACK: False
            0000 000. = Unused: 0x00
        0... .... .... .... .... .... .... .... = Reserved: 0x0
        .000 0000 0000 0000 0000 0000 0000 0000 = Stream Identifier: 0
        Settings - Initial Windows size : 65535
```

If you familiar with HTTP protocol you may notice that `PRI` is a kind of request method, `*` is a kind of url, `HTTP/2.0` is a protocol version and `SM` kind of a body. If you take close look at HTTP/2 RFC you will find the following lines:

```
The connection preface starts with the string PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n). This sequence MUST be followed by a SETTINGS frame, which MAY be empty...

The server connection preface consists of a potentially empty SETTINGS frame that MUST be the first frame the server sends in the HTTP/2 connection.

The SETTINGS frames received from a peer as part of the connection preface MUST be acknowledged after sending the connection preface.
```

These lines say that server has to send some strange SETTING frame to client. Working with HTTP I thought that clients and servers send HTTP request/response back and forth. What the hell is HTTP frame? And what is SETTING frame? From HTTP/2 specification:

```
This specification defines a number of frame types, each identified by a unique 8-bit type code. Each frame type serves a distinct purpose in the establishment and management either of the connection as a whole or of individual streams.
```

Streams? What the hell? Again from specification:

```
A "stream" is an independent, bidirectional sequence of frames exchanged between the client and server within an HTTP/2 connection
```

Ok. In short, client establishes connection with server send HTTP PRI request. Server with a stream sends SETTING frame to the client to acknowledge HTTP/2 protocol support. Stream has to have an id and if it is a client-server handshake then stream has to have specified id? The specification says that:

```
The SETTINGS frame (type=0x4) ... SETTINGS frames always apply to a connection, never a single stream. The stream identifier for a SETTINGS frame MUST be zero (0x0)
```

Ok. Now we have all the information to write a first unit test for the web server. Here it is:

```java
public class WebServerTest {

    //setup and tear down are skipped

    @Test(timeout = 10_000)
    public void serverSend_settingFrameAfterGetMagicPriRequest() throws Exception {
        sendRequestToServer(
            "PRI * HTTP/2.0\r\n" +
                "\r\n" +
                "SM\r\n" +
                "\r\n"
        );

        waitForResponse();

        byte[] response = readResponseAsByteArray();

        assertThat(decodePayloadLength(response), is(0));
        assertThat(decodeFrameType(response), is(4));
        assertThat(decodeFlags(response), is(0));
        assertThat(decodeStreamIdentifier(response), is(0));
    }

    private int decodePayloadLength(byte[] response) {
        return response[0] << 16 | response[1] << 8 | response[2];
    }

    private int decodeFrameType(byte[] response) {
        return response[3];
    }

    private int decodeFlags(byte[] response) {
        return response[4];
    }

    private int decodeStreamIdentifier(byte[] response) {
        return 0x7F_FF_FF_FF & (response[5] << 24 | response[6] << 16 | response[7] << 8 | response[8]);
    }
}
```

The code is straightforward. It sends PRI request to the server, waits for a response from the server and checks if it is an empty SETTING frame sending by stream with id equals to zero. Obviously, that test fails by time out because the server does not understand PRI request and does not respond.

Before teach server how to handle PRI request, let's look at the existing algorithm of requests handling.

```java
public class WebServer {
    private Socket clientConnection;
    private ServerSocket serverSocket;

    public WebServer(String[] params) {}

    public static void main(String[] args) throws IOException, InterruptedException {
        new WebServer(args).start();
    }

    public void start() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(8080);
        new Thread(() -> {
            try {
                clientConnection = serverSocket.accept();
                while (true) {
                    RequestReader reader = new RequestReader(clientConnection.getInputStream());
                    RequestDeserializer deserializer = new RequestDeserializer();
                    Request request = deserializer.deserializeRequest(reader.readSingleRequest());
                    RequestHandler handler = new RequestHandler();
                    Response response = handler.handle(request);
                    ResponseWriter writer = new ResponseWriter(clientConnection.getOutputStream());
                    ResponseSerializer serializer = new ResponseSerializer();
                    writer.writeSingleResponse(serializer.serialize(response));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}
```

The main part is the while loop. Server accepts new TCP connection from a client, reads from socket input, deserializes request from byte form, somehow handles request, serializes response into byte form and sends it to the client.
First of all we need to write code to deserialize of a new type of request. Again start with a test.

```java
public class RequestDeserializerTest {

    //setup method is skipped

    @Test
    public void deserializePriRequest() throws Exception {
        Request request = deserializer.deserializeRequest(
                requestStringToByteArray(
                        "PRI * HTTP/2.0\r\n" +
                                "\r\n" +
                                "SM\r\n" +
                                "\r\n"
                )
        );

        assertThat(request.httpMethod, is(Method.PRI));
        assertThat(request.url, is("*"));
        assertThat(request.httpVersion, is(Version.HTTP_2_0));
        assertThat(request.body, is("SM"));
    }
}
```

Here we check that `RequestDeserializer` creates HTTP request object from bytes array with `PRI` as a method, `*` as a url, `HTTP_2_0` as a version and `SM` as a body. `RequestDeserializer` looks like the following:

```java
public class RequestDeserializer {
    public RequestDeserializer() {}

    public Request deserializeRequest(byte[] request) {
        Map<String, String> headers = parseHttpHeaders(request);
        return new Request(Method.GET, "/", Version.HTTP_1_1, headers);
    }

    private Map<String, String> parseHttpHeaders(byte[] request) {
        Map<String, String> headers = new HashMap<>();
        for (int i = skipStartHttpLine(); i < getHeadersLength(request); i++) {
            StringBuilder nameBuilder = new StringBuilder();
            while (request[i] != (byte)':') {
                nameBuilder.append((char)request[i]);
                i += 1;
            }
            String headerName = nameBuilder.toString().trim();
            i += 1;
            StringBuilder valueBuilder = new StringBuilder();
            while (request[i] != (byte)'\r') {
                valueBuilder.append((char)request[i]);
                i += 1;
            }
            String headerValue = valueBuilder.toString().trim();
            headers.put(headerName, headerValue);
            i += 1;
        }
        return headers;
    }

    private int getHeadersLength(byte[] request) {
        return request.length - 2;
    }

    private int skipStartHttpLine() {
        return 16;
    }
}
```

It always returns GET HTTP/1.1 request to the root directory and parses only headers. Now we need to parse also method, url and version to pass `RequestDeserializerTest.deserializePriRequest` test. Let's add this functionality. Here is the new version of `RequestDeserializer`.

```java
public class RequestDeserializer {
    private final byte[] rawRequest;
    private int index;

    public RequestDeserializer(byte[] rawRequest) {
        this.rawRequest = rawRequest;
    }

    public Request deserializeRequest() {
        Method method = Method.valueOf(parseMethod());
        String url = parseUrl();
        Version version = Version.valueOf(parseVersion().replace(".", "_").replace("/", "_"));
        Map<String, String> headers = parseHttpHeaders();
        return new Request(method, url, version, headers, "SM");
    }

    private String parseMethod() {
        StringBuilder builder = new StringBuilder();
        for (; rawRequest[index] != ' '; index++) {
            builder.append((char)rawRequest[index]);
        }
        index += 1;
        return builder.toString();
    }

    private String parseUrl() {
        String url = String.valueOf((char)rawRequest[index]);
        index += 2;
        return url;
    }

    private String parseVersion() {
        StringBuilder builder = new StringBuilder();
        while (notEndOfLine()) {
            builder.append((char)rawRequest[index]);
            index++;
        }
        index += 2;
        return builder.toString();
    }

    private boolean notEndOfLine() {
        return rawRequest[index] != '\r' && rawRequest[index + 1] != '\n';
    }

    private Map<String, String> parseHttpHeaders() {
        Map<String, String> headers = new HashMap<>();
        while (notEndOfLine()) {
            StringBuilder nameBuilder = new StringBuilder();
            while (rawRequest[index] != (byte)':') {
                nameBuilder.append((char)rawRequest[index]);
                index += 1;
            }
            String headerName = nameBuilder.toString().trim();
            index += 1;
            StringBuilder valueBuilder = new StringBuilder();
            while (rawRequest[index] != (byte)'\r') {
                valueBuilder.append((char)rawRequest[index]);
                index += 1;
            }
            String headerValue = valueBuilder.toString().trim();
            headers.put(headerName, headerValue);
            index += 2;
        }
        return headers;
    }
}
```

The next step is a test for the requests handling procedure. All we need to check is that the response object is a `SettingFrame`.

```java
public class RequestHandlerTest {

    //setup method is skipped

    @Test
    public void handlePriRequest() throws Exception {
        Response response = handler.handle(
                new RequestBuilder()
                        .withMethod(Method.PRI)
                        .withUrl("*")
                        .withVersion(Version.HTTP_2_0)
                        .withBody("SM")
                        .build()
        );

        assertThat(response, instanceOf(SettingFrame.class));
    }
}
```

The main thing is how we serialize new `SettingFrame` class. Let's write a test for it.

```java
public class ResponseSerializerTest {

    //setup method is skipped

    @Test
    public void serializeSettingFrame() throws Exception {
        SettingFrame frame = new SettingFrame();

        byte[] serialized = serializer.serialize(frame);

        assertThat(decodePayloadLength(serialized), is(0));
        assertThat(decodeFrameType(serialized), is(4));
        assertThat(decodeFlags(serialized), is(0));
        assertThat(decodeStreamIdentifier(serialized), is(0));
    }

    private int decodePayloadLength(byte[] response) {
        return response[0] << 16 | response[1] << 8 | response[2];
    }

    private int decodeFrameType(byte[] response) {
        return response[3];
    }

    private int decodeFlags(byte[] response) {
        return response[4];
    }

    private int decodeStreamIdentifier(byte[] response) {
        return 0x7F_FF_FF_FF & (response[5] << 24 | response[6] << 16 | response[7] << 8 | response[8]);
    }
}
```

You may notice that the test assertions are the same as `WebServerTest.serverSend_settingFrameAfterGetMagicPriRequest` test. Some of you may think that this is a code duplication. Yes, it has some methods that duplicate functionality of each other, however, I believe it is a sign that particular functionality has to be refactored out; I have not yet found where. Also it helps take every class and module in our web server under control. To pass above test I added only three lines of code to `ResponseSerializer`:

```java
public class ResponseSerializer {
    public byte[] serialize(Response response) {
        if (response instanceof SettingFrame) {
            return new byte[] {0, 0, 0, 4, 0, 0, 0, 0, 0};
        }
        //...other logic
    }
}
```

First three bytes is responsible for payload length, fourth byte is a type of a frame, fifth contains the frame flags and the last four bytes represent stream identifier. Now we can remove `@Ignore` from `WebServerTest.serverSend_settingFrameAfterGetMagicPriRequest` and it should pass. Let's rerun our functional test. It fails... Hm. Let use network sniffer again. Aha, client sent next frame ... HEADERS frame type. My wireshark shows:

```
HyperText Transfer Protocol 2
    Stream: HEADERS, Stream ID: 1, Length 38
        Length: 38
        Type: HEADERS (1)
        Flags: 0x05
            .... ...1 = End Stream: True
            .... .1.. = End Headers: True
            .... 0... = Padded: False
            ..0. .... = Priority: False
            00.0 ..0. = Unused: 0x00
        0... .... .... .... .... .... .... .... = Reserved: 0x0
        .000 0000 0000 0000 0000 0000 0000 0001 = Stream Identifier: 1
        [Pad Length: 0]
        Header Block Fragment: 8682418aa0e41d139d09b8f01e078450839bd9ab7a90ca54...
        [Header Length: 150]
        [Header Count: 6]
        Header: :scheme: http
        Header: :method: GET
        Header: :authority: localhost:8080
        Header: :path: /
        Header: accept-encoding: gzip
        Header: user-agent: Jetty/9.3.12.v20160915
        Padding: <MISSING>
```

`:scheme`, `:method`, `:authority` and `:path` have never been headers in HTTP protocol. If you have a look at the specification:

```
While HTTP/1.x used the message start-line to convey the target URI, the method of the request, and the status code for the response, HTTP/2 uses special pseudo-header fields beginning with ':' character (ASCII 0x3a) for this purpose...
Pseudo-header fields are not HTTP header fields.
```

The HTTP/2 uses headers compression, so we need to decode headers from `Header Block Fragment`. The specification says that it uses [Huffman coding](https://en.wikipedia.org/wiki/Huffman_coding) for compressions.
Let's repeat our test cycle. First, write a test for the web server.

```java
public class WebServerTest {

    //other tests are skipped

    @Test(timeout = 7_500L)
    public void serverSend_headerFrameWith_status200_onHeaderFrame_toRoot() throws Exception {
        int maxHeaderSize = 4096;
        int maxHeaderTableSize = 4096;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Encoder encoder = new Encoder(maxHeaderTableSize);
        encoder.encodeHeader(out, ":scheme".getBytes(), "http".getBytes(), false);
        encoder.encodeHeader(out, ":method".getBytes(), "GET".getBytes(), false);
        encoder.encodeHeader(out, ":authority".getBytes(), "localhost:8080".getBytes(), false);
        encoder.encodeHeader(out, ":path".getBytes(), "/".getBytes(), false);
        encoder.encodeHeader(out, "accept-encoding".getBytes(), "gzip".getBytes(), false);
        encoder.encodeHeader(out, "user-agent".getBytes(), "Jetty/9.3.12.v20160915".getBytes(), false);

        sendRequestToServer(out.toByteArray());

        waitForResponse();

        byte[] response = readResponseAsByteArray();
        Decoder decoder = new Decoder(maxHeaderSize, maxHeaderTableSize);
        Map<String, String> headers = new HashMap<>();
        decoder.decode(new ByteArrayInputStream(response), (name, value, sensitive) -> headers.put(new String(name), new String(value)));
        decoder.endHeaderBlock();

        assertThat(headers, hasEntry(":status", "200"));
    }
}
```

A test for `ReqeustDeserializer`:

```java
public class RequestDeserializerTest {

    //other tests are skipped

    @Test
    public void deserializeHttp2Request_rootDirectory() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0);out.write(0);out.write(38);
        out.write(1);out.write(5);
        out.write(0);out.write(0);out.write(0);out.write(1);

        Encoder encoder = new Encoder(4096);
        encoder.encodeHeader(out, ":scheme".getBytes(), "http".getBytes(), false);
        encoder.encodeHeader(out, ":method".getBytes(), "GET".getBytes(), false);
        encoder.encodeHeader(out, ":authority".getBytes(), "localhost:8080".getBytes(), false);
        encoder.encodeHeader(out, ":path".getBytes(), "/".getBytes(), false);
        encoder.encodeHeader(out, "accept-encoding".getBytes(), "gzip".getBytes(), false);
        encoder.encodeHeader(out, "user-agent".getBytes(), "Jetty/9.3.12.v20160915".getBytes(), false);

        RequestDeserializer deserializer = new RequestDeserializer(out.toByteArray());

        Request headers = deserializer.deserializeRequest();

        assertThat(headers, instanceOf(HeaderFrame.class));
    }
}
```

Implementation of deserialization is very simple. Because it is the only one HTTP/2 frame recognized by the web server and payload length is very small the first byte of the frame will be zero. Let's use that fact.

```java
public class RequestDeserializer {
    public Request deserializeRequest() {
        if (rawRequest[0] == 0) {
            return new HeaderFrameRequest();
        }
        //everything else is skipped
    }
}
```

The test and implementation for `RequestHandler` is quite simple. Getting `HeaderFrameRequest` handler should return `HeaderFrameResponse`.

```java
public class RequestHandlerTest {

    private RequestHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new RequestHandler();
    }

    // other tests here

    @Test
    public void handleHeaderRequest_onRootDirectory() throws Exception {
        Response response = handler.handle(new HeaderFrameRequest());

        assertThat(response, instanceOf(HeaderFrameResponse.class));
    }
}

public class RequestHandler {
    public Response handle(Request request) {
        if (request instanceof HeaderFrameRequest) {
            return new HeaderFrameResponse();
        }
        //everything else here
    }
}
```

Here is a test for serializing the headers frame. All that it should only contain a header with name `:status` and value `200`.

```java
public class ResponseSerializerTest {

    @Test
    public void serializeResponseHeaders_onRootDirectory() throws Exception {
        HeaderFrameResponse frame = new HeaderFrameResponse();

        byte[] serialized = serializer.serialize(frame);

        assertThat(decodePayloadLength(serialized), is(1));
        assertThat(decodeFrameType(serialized), is(1));
        assertThat(decodeFlags(serialized), is(5));
        assertThat(decodeStreamIdentifier(serialized), is(1));
    }
}
```

If you wonder why payload length is one you need to look at [HPACK: Header Compression for HTTP/2](https://tools.ietf.org/html/rfc7541) RFC. It describes headers table which consists of two parts: static and dynamic. `:status` header with value of `200` is in static table and it is represented as index of the table in `HEADERS` frame. Flags with value of `5` means that it is end of HTTP/2 headers and stream. Because of client sent request in the stream with id `1` our web server must send response in the same stream.
The implementation is as follows:

```java
public class ResponseSerializer {
    public byte[] serialize(Response response) {
        if (response instanceof HeaderFrameResponse) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(new byte[]{0, 0, 1, 1, 5, 0, 0, 0, 1});
                Encoder encoder = new Encoder(4096);
                encoder.encodeHeader(out, ":status".getBytes(), "200".getBytes(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return out.toByteArray();
        }
        //other logic
    }
}
```

Here I use [Twitter Hpack](https://github.com/twitter/hpack) implementation. The last thing we need is to add Twitter Hpack library to `classpath` for functional tests. First we need add a new task to `gradle` script and change the task for running functional tests.

```groovy
task copyRuntimeDependencies(type: Copy) {
    from configurations.compile
    into 'build/runtime/'
}

task acceptance(type: Test) {
    dependsOn(test, copyRuntimeDependencies)
    testClassesDir = sourceSets.acceptance.output.classesDir
    classpath = sourceSets.acceptance.runtimeClasspath
}
```

```java
public class WebServerEndToEndTest {

    @Before
    public void startWebServer() throws IOException, InterruptedException {
        webServer = new ProcessBuilder(
                "java",
                "-cp", "build"+separator+"classes"+ separator+"main"+ pathSeparator+"build"+ separator+"runtime"+ separator+"*",
                "ua.http.ws.WebServer",
                "-p", "8080"
        ).start();
        webServer.waitFor(1, TimeUnit.SECONDS);
    }

    //functional tests are here
}
```

In this article I described how I implemented simple HTTP/2 request handling. In future articles I will  write how and what features I add to the web server.
