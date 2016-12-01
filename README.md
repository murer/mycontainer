# Mycontainer

[![Build Status](https://travis-ci.org/murer/mycontainer.png)](https://travis-ci.org/murer/mycontainer)

## Purpose

Mycontainer is a generic test and development enviroment. Check it out.

 * [Embedding Mycontainer to do some Java EE stuff](#embedding-mycontainer-to-do-some-java-ee-stuff)
 * [Embedding Mycontainer to do some GAE stuff](#embedding-mycontainer-to-do-some-gae-stuff)
 * [Starting all modules from maven](#starting-all-modules-from-maven)
 * [Start a local web server](#start-a-local-web-server)
 * [TCP Tunnels](#tcp-tunnels)
 * [Checking for non us-ascii files](#checking-for-non-us-ascii-files)
 * [Installing PhantomJS](#installing-phantomjs)
 * [Maven Repository](#maven-repository)
 * [Building Mycontainer](#building-mycontainer)

## @Before

You will need java and maven

## Embedding Mycontainer to do some Java EE stuff

Configure InitialContext. You can do with [jndi.properties](./mycontainer-test/mycontainer-test-web/src/test/resources/jndi.properties)

Code like [MycontainerTestHelper.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerTestHelper.java) to embed anywhere.

Here is a junit sample: 
[AbstractWebBaseTestCase.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/AbstractWebBaseTestCase.java)
/ [MycontainerWebTest.java](./mycontainer-test/mycontainer-test-web/src/test/java/com/googlecode/mycontainer/test/web/MycontainerWebTest.java)

 * Embeddable on any java application (junit tests, jetty, tomcat, and any others)
 * Programmatic configuration and deploy
 * Light weight
 * Fast boot
 * Do not hijack the Java Virtual Machine (real embeddable):
   * Do not change JVM URL protocols configs
   * No dynamic classloader
   * No classloader isolation

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

## TCP Tunnels

Mycontainer can start multiple tcp tunnels. You can do this using `java -cp` or `mvn` (pom.xml is not required)

    $ java -cp mycontainer-util.jar com.googlecode.mycontainer.util.tunnel.Tunnels Redirect 0.0.0.0:6667:chat.freenode.net:6667 5080:google.com:80

You can get this jar [here](http://central.maven.org/maven2/com/googlecode/mycontainer/mycontainer-util/)

The first argument can be `Console` (System.out) or `Log` (slf4j/jdk-logging) to show transfered data. Or `Redirect` to just redirect it silently.

Others arguments describes tunnels to startup. `[local-host]:local-port:remote-host:remote-port`.

`local-host` is not required (default 127.0.0.1). Use 0.0.0.0 to bind on all interfaces.

`local-port` can be zero to bind on a free random port.

You can also start it by maven (pom.xml is not required). It is an aggregator plugin.

    $  mvn com.googlecode.mycontainer:mycontainer-maven-plugin:tunnels -Dmycontainer.tunnels.list=5000:localhost:6000,0.0.0.0:6667:chat.freenode.net:6667 -Dmycontainer.tunnels.handler=Console

Like the `java -cp` you need to tell `Redirect`, `Console` or `Log` (`mycontainer.tunnels.handler`) and tunnels (`mycontainer.tunnels.list`) separeted by comma.

## Checking for non us-ascii files

Checking recursively for non-ascii files (pom.xml is not required):

    $ mvn com.googlecode.mycontainer:mycontainer-maven-plugin:us-ascii
    
You can configure your pom.xml to check it on test maven phase like we do [pom.xml](./mycontainer-test/pom.xml).
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
    
## Building Mycontainer

    mvn clean install

Use `-Ddist` to assembly a all-in-one jar and a binary zip.

Speed up the process:

    mvn clean install -Dmaven.test.skip.exec -T 10 && mvn test


