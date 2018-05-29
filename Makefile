all: clear

clean:
	$(RM) ./target/*.jar
	$(RM) ./target/classes/main/java/server/*.class

compile:
	mvn compile

package:
	mvn package

install:
	mvn install

run:
	java -jar ./target/adobe-java-webserver-1.0-jar-with-dependencies.jar 8080