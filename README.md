# Mycontainer

Test Light Weight Container

## @Before

You need java and maven

## Start a local web server

No pom.xml required :) And it is nice to quick start html, javascript and css projects.

    $ mvn com.googlecode.mycontainer:maven-mycontainer-plugin:web -Dmycontainer.web.port=8080

## Embedding Mycontainer to do some Java EE stuff

Configure InitialContext. You can do with [jndi.properties](./mycontainer-test/mycontainer-test-web/src/test/resources/jndi.properties)

Code like [MycontainerTestHelper.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerTestHelper.java)

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

It is highly recommended that you **avoid** linking this repository in your `pom.xml` since I can not ensure their availability.

## Build

You need java and [maven](http://maven.apache.org/).

    mvn clean install

Use `-Ddist` to assembly a all-in-one jar and a binary zip.

## It has just come from googlecode

It means you still should go http://mycontainer.googlecode.com to read the [wikis](https://code.google.com/p/mycontainer/w/list).

## Let's do it

I really like to merge **pull requests**


