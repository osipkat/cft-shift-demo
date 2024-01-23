# cft-shift-demo

## Description
* Java version: 21.0.2, min JRE version: 8
* maven 3.6.3

## Dependencies
```
<dependency>
  <groupId>info.picocli</groupId>
  <artifactId>picocli</artifactId>
  <version>4.7.5</version>
</dependency>
```
## Build
```
cd cft-shift-demo/
mvn clean compile assembly:single
```
## Run example
```
java -jar target/demo-0.1-jar-with-dependencies.jar -s -a -p sample- in1.txt in2.txt
```
