# Mycontainer

Test Light Weight Container

## Dependencies

This project is deployed to [maven central repository](http://repo1.maven.org/maven2/com/googlecode/mycontainer/). 
Example:

    <dependency>
        <groupId>com.googlecode.mycontainer</groupId>
        <artifactId>mycontainer-web</artifactId>
        <version>${mycontainer.version}</version>
    </dependency>
    
Not all versions are deployed to central. 
But you can find them all at my private repository [here](http://repo.pyrata.org/release/maven2/com/googlecode/mycontainer/).

It is highly recommended that you **avoid** linking this repository in your `pom.xml` since I can not ensure their availability.

## Build

You need java and [maven](http://maven.apache.org/).

    mvn clean install

Use `-Ddist` to assembly a all-in-one jar and a binary zip.

