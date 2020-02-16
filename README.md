Build Status
-------------

[![Build Status](https://travis-ci.org/LoyolaChicagoCode/hpjpc-source-java.svg?branch=master)](https://travis-ci.org/LoyolaChicagoCode/hpjpc-source-java)

About HPJPC
------------

HPJPC is a legacy repository to accompany the book, High-Peformance Java Platform Computing, written by Thomas W. Christopher and George K. Thiruvathukal for Prentice Hall PTR in 1999.

HPJPC was an independent effort to create frameworks for concurrent and parallel computing in Java, previously known as the JHPC class library, which was mentioned as part of existing and prior art in [JSR-166: Concurrency Utilities](https://jcp.org/en/jsr/detail?id=166). Although we were not part of the JCP effort, we appreciate the recognition our efforts.

Beyond the basic concurrency ideas, HPJPC also featured a number of abstractions for shared memory parallelism, including dataflow computing, shared tables of queues, and demonstrated how to use these on emerging multicore systems. (It's nice to have so many more cores at our disposal today!). In addition, we provided support for early distributed systems ideas, including a networked version of shared table of queues (similar to tuple spaces) and lightweight remote procedure calling.

Of interest to viewers of this site are likely to be the many threads and networking examples from our original book, which is no longer in print but still available at [Loyola University Chicago's Digital Commons](https://ecommons.luc.edu/cs_facpubs/3/.)

Please note that although we no longer provide official support for the book or the source code, you can e-mail any questions to gkt@cs.luc.edu.

Building HPJPC
----------------

The build process is completely straightforward with Maven:

```
mvn compile
mvn package assembly:single
```

This will create a standalone jar file in the `target` subdirectory.

Running Examples
-----------------

There are many example programs. See EXAMPLES.md (coming soon) for how to run these.

In the meantime, you can try the Dining philosophers examples.

To run the version with *deadlock*:

```
java -classpath ./target/hpjpc-<VERSION>.jar info.jhpc.textbook.chapter03.Diners0
```

To run the version *without deadlock*:

```
java -classpath ./target/hpjpc-<VERSION>.jar info.jhpc.textbook.chapter03.Diners1
```

