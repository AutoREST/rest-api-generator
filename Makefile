# only works with the Java extension of yacc:
# byacc/j from http://troi.lincom-asg.com/~rjamison/byacc/

JFLEX  = java -jar JFlex.jar
BYACCJ = ./yacc.linux -tv -J -Jclass=PFISCompiler
JAVAC  = javac

all: PFISCompiler.class

run: PFISCompiler.class
	java PFISCompiler

build: clean PFISCompiler.class

clean:
	rm -f *~ *.class *.o *.s Yylex.java PFISCompiler*.java y.output

PFISCompiler.class: Yylex.java PFISCompiler.java
	$(JAVAC) PFISCompiler.java

Yylex.java: lexical.flex
	$(JFLEX) lexical.flex

PFISCompiler.java: JSchAutoRestYacc.y
	$(BYACCJ) JSchAutoRestYacc.y
