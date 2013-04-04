# Mycontainer

Test Light Weight Container

Mycontainer is a generic light weight java embeddable container.
The main implementation is a Java EE without some common features like pools, clusters and others.
Nice to be used for tests (integrations or units)

## @Before

You will need java and maven

## Start a local web server

No pom.xml required :) And it is nice to quick start html, javascript and css projects.

    $ mvn com.googlecode.mycontainer:maven-mycontainer-plugin:web -Dmycontainer.web.port=8080

## Embedding Mycontainer to do some Java EE stuff

Configure InitialContext. You can do with [jndi.properties](./mycontainer-test/mycontainer-test-web/src/test/resources/jndi.properties)

Code like [MycontainerTestHelper.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerTestHelper.java) to embed anywhere.

Here is a junit sample: 
[AbstractWebBaseTestCase.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/AbstractWebBaseTestCase.java)
/ [MycontainerWebTest.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerWebTest.java)

## Starting all modules from maven

Configure mycontainer maven plugin: [pom.xml](./mycontainer-usage-parent/pom.xml)

Write the beanshell: [mycontainer-start.bsh](./mycontainer-test/mycontainer-test-starter/src/test/resources/mycontainer-start.bsh).
Remeber you can write this in any java class and just use that in beanshell

    mvn mycontainer:start

## Maven Repository

This project is deployed to [maven central repository](http://repo1.maven.org/maven2/com/googlecode/mycontainer/). 
Example:

    <dependency>
        <groupId>com.googlecode.mycontainer</groupId>
        <artifactId>mycontainer-web</artifactId>
        <version>${mycontainer.version}</version>
    </dependency>
    
Not all versions are deployed to central. 
But you can find them all at my private repository http://repo.pyrata.org/release/maven2/com/googlecode/mycontainer/

It is highly recommended that you **avoid** linking this repository in your `pom.xml` since I can not ensure it's availability.

## Build

    mvn clean install

Use `-Ddist` to assembly a all-in-one jar and a binary zip.

## Some features
 * Embeddable on any java application (junit tests, jetty, tomcat, and any others)
 * Programmatic configuration and deploy
 * Light weight
 * Fast boot
 * No hijack the Java Virtual Machine (real embeddable):
   * No change JVM URL protocols configs
   * No dynamic classloader
   * No classloader isolation

## It has just come from googlecode

It means you still should go to http://mycontainer.googlecode.com to read the [wikis](https://code.google.com/p/mycontainer/w/list).

## Let's do it

We really like to merge **pull requests**


