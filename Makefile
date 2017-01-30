# only works with the Java extension of yacc:
# byacc/j from http://troi.lincom-asg.com/~rjamison/byacc/

JFLEX  = java -jar JFlex.jar
BYACCJ = ./yacc.linux -tv -J -Jclass=JSchParser
JAVAC  = javac

all: JSchParser.class

run: JSchParser.class
	java JSchParser

build: clean JSchParser.class

clean:
	rm -f *~ *.class *.o *.s Yylex.java JSchParser*.java y.output

JSchParser.class: Yylex.java JSchParser.java
	$(JAVAC) JSchParser.java

Yylex.java: lexical.flex
	$(JFLEX) lexical.flex

JSchParser.java: JSchAutoRestYacc.y
	$(BYACCJ) JSchAutoRestYacc.y
