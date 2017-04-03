# Language server

It analyzes a Java file and given a cursor position, it outputs, if applicable:
- a tool tip
- the declaration (in provided source)
- all the references (in provided source)
The above output is bundled in a Json payload (see example bellow).

## Prerequisites

- Java 8 (JDK)
- Maven

## Compile and run

Compile standalone jar:
```
$ mvn clean compile assembly:single
```

Run standalone jar by line and column:
```
$ java -jar target/language-server.jar 5:19 src/test/resources/Sample.java
```

Run standalone jar by location (char offset starting at 1):
```
$ java -jar target/language-server.jar 60 src/test/resources/Sample.java
```

Example of output:
```
{
  "toolTip" : "public int doStuff() ",
  "declarationPosition" : 55,
  "referencePositions" : [ 256, 298, 465 ]
}
```

## Run test

```
$ mvn clean verify
```

## Details

### Implementation
The parsing of the Java source into an AST is done via `org.eclipse.jdt`. Once the AST is built, 

### Short comings

##### 1. Tootip
The tool tip is created by using `toString()` of the AST node binding. In the case of a method, it's more or less what we want (though package is missing) but in the case of a type (eg: `class Sample ...`), it contains many uncessary details. A proper approach would be to individualy retrieve:
  - package name
  - modifier
  - etc
The tool tip does not include any _Javadoc_ at this point.

##### 2. Exhaustive AST traversing
For every position, the tree is entirely DFS traversed to get the corresponding AST leaf. This could easily be improved the following 2 steps:
  - avoid going down nodes for which position is outside
  - stop once leaf has been found

##### 3. Avoid AST traversing
...
