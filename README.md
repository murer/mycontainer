# Mycontainer

[![Build Status](https://travis-ci.org/murer/mycontainer.png)](https://travis-ci.org/murer/mycontainer)

## Purpose

Mycontainer purpose has been increased to be a generic test container (and not only Java EE one).

It means you can use mycontainer to help you test your project. Check it out.

## Since 1.4.4

You need to change `maven-mycontainer-plugin` to `mycontainer-maven-plugin` (thanks maven 3.x)

## @Before

You will need java and maven

## Embedding Mycontainer to do some Java EE stuff

Configure InitialContext. You can do with [jndi.properties](./mycontainer-test/mycontainer-test-web/src/test/resources/jndi.properties)

Code like [MycontainerTestHelper.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerTestHelper.java) to embed anywhere.

Here is a junit sample: 
[AbstractWebBaseTestCase.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/AbstractWebBaseTestCase.java)
/ [MycontainerWebTest.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerWebTest.java)

## Embedding Mycontainer to do some GAE stuff

Configure [pom.xml](./mycontainer-gae/mycontainer-gae-test/pom.xml) to [Google App Engine](https://developers.google.com/appengine/)

Use [GAETestHelper.java](./mycontainer-gae/mycontainer-gae-web/src/main/java/com/googlecode/mycontainer/gae/web/GAETestHelper.java) or code like that.

Google has [LocalServiceTestHelper](https://developers.google.com/appengine/docs/java/tools/localunittesting) 
to do unit tests, but it requires a thread environment.
It means we need to keep the env to each request thread.
This filter [LocalServiceTestHelperFilter.java](./mycontainer-gae/mycontainer-gae-web/src/main/java/com/googlecode/mycontainer/gae/web/LocalServiceTestHelperFilter.java)
does the job using a non-documented google class ApiProxy.

## Starting all modules from maven

Configure mycontainer maven plugin: [pom.xml](./mycontainer-usage-parent/pom.xml)

Write the beanshell: [mycontainer-start.bsh](./mycontainer-test/mycontainer-test-starter/src/test/resources/mycontainer-start.bsh).
You can write this in any java class and just use that in beanshell

    mvn mycontainer:start

## Start a local web server

No pom.xml required. And it is nice to quick start html, javascript and css projects.

    $ mvn com.googlecode.mycontainer:mycontainer-maven-plugin:web -Dmycontainer.web.port=8080
    
## Checking for non us-ascii files

Checking recursively for non-ascii files (pom.xml is not required):

    $ mvn com.googlecode.mycontainer:mycontainer-maven-plugin:us-ascii
    
You can configure your pom.xml do check it on test maven phase like we do [pom.xml](./mycontainer-test/pom.xml).
Use `<inherited>false</inherited>` if you are configuring your root pom.xml at multi modules project.

## Installing PhantomJS

Mycontainer has a plugin to install [PhantomJS](http://phantomjs.org). 

 * Download packages from original host: http://code.google.com/p/phantomjs/downloads/
 * Install the correct package (windows, macosx, linux).
 * No pom.xml required
 * It set a maven property to the phantomjs executable: `mycontainer.phantomjs.executable`

Maven command:

    $ mvn com.googlecode.mycontainer:mycontainer-maven-plugin:phantomjs-install -Dmycontainer.phantomjs.dest=target/phantomjs -Dmycontainer.phantomjs.version=1.9.2

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

## Building Mycontainer

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



