# Matrix client library for Java

This is a Java client-side library to interact with the Matrix protocol.

Some of the aims of this project are:

- Maintain low quantity of dependencies
- Leverage modern Java features to decrease code complexity
- Maintain the client asynchronous

Current features:

- Post text messages (unformatted, no rich text yet)
- Post images, files, audio content
- Human readable error messages
- Basic presence with /messages payload

### Usage:

```java
MatrixAPIClient matrixAPIClient = new MatrixAPIClient("https://matrix.org", "example", "authTokenGoesHere");
```

Now you are able to make use of all the features, the following above is an example in which we leverage the
asynchronous api:

```java
        var roomId = "!fslCrggPzCliBLCmgo:kde.org";
        MatrixAPIClient matrixAPIClient = MatrixAPIClient.createAsync("https://example.org", "YOURUSERNAME", "YOURTOKEN").join();


        MatrixEvent textEvent = new MatrixText("Test");
        

        Path image = Path.of("/path/to/file.txt");
        var eventIdFuture = MatrixAPIClient.createAsync(wireMockServer.baseUrl(), USER, AUTH_TOKEN)
        .thenCompose(matrixAPIClient1 -> matrixAPIClient1.uploadResource(result.tempFile).thenCompose(mxc -> {
            MatrixFile file = new MatrixFile("Test caption", null, result.tempFile.toString(), null, null, null, URI.create(mxc));
            return matrixAPIClient1.publishRoomMessage(result.roomId(), file);
        }));

        Path file = Path.of("/home/user/Descargas/ankercore.txt");
        var event2 = matrixAPIClient.uploadResource(file).thenCompose(mxc -> {
            MatrixEvent fileEvent = new MatrixFile("File caption", file.getFileName().toString(), URI.create(mxc));
            return matrixAPIClient.publishRoomMessage(roomId, fileEvent);

        });
        

        CompletableFuture<List<String>> posts = CompletableFuture.allOf(event, event2, event3)
                .thenApply(v -> {
                    List<String> list = new ArrayList<>();
                    list.add(event2.join());
                    list.add(event3.join());
                    return list;
                });

        var messages = matrixAPIClient.getListOfMessages(roomId, ChronologicalDirectionEvent.REVERSE_CHRONOLOGICAL_ORDER, QueryParametersMessages.defaultParams()).join();
```