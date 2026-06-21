> [!CAUTION]
> This project is in very early stages, use at your own risk.

# Matrix client library for Java

This is a Java client-side library to interact with the Matrix protocol.

Some of the aims of this project are:

- Maintain low quantity of external dependencies
- Leverage modern Java features to improve developer experience
- Maintain the client simple, allowing consumers to pass their own data for serialization in most cases

Current features:

- Support for the serialization of most of `m.room.message` type events
- Support for room creation and message reading

### Installation:

Declare the library in your pom