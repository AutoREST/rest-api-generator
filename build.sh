#/bin/bash
mkdir src -p
mkdir dist -p

java -jar JFlex.jar lexical.flex

./yacc.linux -tv -J -Jclass=JSchParser JSchAutoRestYacc.y

mv *.java src/

cd src

javac JSchParser.java

cd ..

mv src/*.class dist/

#rm -f *~ *.class *.o *.s Yylex.java JSchParser*.java y.output
