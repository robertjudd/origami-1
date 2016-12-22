## Synopsis

Completed exercise for the initial Origami Energy coding test.
 
Implementation of a ForgettingMap which is instantiated with a maximum capacity and loses the least frequently
looked up vales from the collection when the capacity is reached. 

### Implementation Details

The chosen implementation approach is composition where a java.util.HashMap is decorated by a new class. This approach
was favoured over inheritance to maintain control over access to the underlying data store for tracking 'finds' on keys
and removing least searched items in all data access scenarios. 

Thread safety is achieved by declaring the two new methods as synchronized to prevent race conditions.  
 
Other options for implementation include inheriting from the java.util.concurrent.ConcurrentHashMap (which has the 
benefit of the new class being a member of the Collections API) and implementing a map data store from scratch.  

## Code Example

```
ForgettingConcurrentHashMap<String, Double> myForgetfullMap = new ForgettingConcurrentHashMap<>(10);
 
myForgetfullMap.add("valOne", 12.2D);
 
Double d = myForgetfullMap.find("valOne");

```

## Installation

The project uses the Scala Build Tool (SBT) for dependency management, compilation and for running the test cases.

## Tests

Tests can be run using the command `> sbt clean compile test` 

Future enhancements to the test cases should include concurrency testing.  

## Contributors

Robert Judd