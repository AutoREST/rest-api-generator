
%{
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
%}


%token DEFINITIONS, LITERAL, NOT, ALLOF, ANYOF, ENUM, REF, TYPE
%token STRING, MINLEN, INT, MAXLEN, PATTERN, NUMBER, INTEGER, MINIMUM
%token DEC, EXMINIMUM, MAXIMUM, EXMAXIMUM, MULTIPLEOF, OBJECT, PROPERTIES
%token ADDITIONAOPROP, REQUIRED, PATTERNPROP, ARRAY, ITEMS, MINITEMS, MAXITEMS, UNIQUEITEMS
%token TRUE, FALSE

%%

JSDoc : '{' defs JSch '}' ;

defs : DEFINITIONS ':' '{' defsL '}' ',' |  ;

defsL : def defsTail ;
defsTail : ',' def defsTail | ;

def : LITERAL ':' '{' JSch '}' ;

JSch : 	strSch
	|	numSch
	|	intSch
	|	objSch
	|	arrSch
	|	refSch
	|	not
	|	allOf
	|	anyOf
	|	enum	;

not : NOT ':' '{' JSch '}' ;
allOf : ALLOF ':' '[' JSchL ']' ;
anyOf : ANYOF '[' JSchL ']' ;
enum : ENUM ':' '[' Jval JvalL ']' ;

JSchL : '{' JSch '}' JSchLTail ;
JSchLTail : ',' '{' JSch '}' JSchLTail | ;

JvalL : ',' Jval JvalL | ;

refSch : REF ':' '"' '#' JPointer '"'

strSch : TYPE ':' STRING strResL ;
strResL : ',' strRes strResL | ;
strRes : 	minLength
		|	maxLength
		|	pattern		;

minLength : MINLEN ':' INT ;
maxLength : MAXLEN ':' INT ;
pattern : PATTERN ':' '"' regExp '"' ;

numSch : TYPE ':' NUMBER numResL
intSch : TYPE ':' INTEGER numResL
numResL : ',' numRes numResL | ;
numRes : 	min
		|	exMin
		|	max
		|	exMax
		|	mult	;
min : MINIMUM ':' DEC
exMin : EXMINIMUM ':' TRUE
max : MAXIMUM ':' DEC
exMax : EXMAXIMUM ':' TRUE
mult : MULTIPLEOF ':' DEC

objSch : TYPE ':' OBJECT objResL ;
objResL : ',' objRes objResL | ;
objRes :	prop
		|	addPr
		|	patPr
		|	req	;
prop : PROPERTIES '{' kSch kSchL '}' ;
kSchL : ',' kSch kSchL | ;
kSch : LITERAL ':' '{' JSch '}' ;
addPr : ADDITIONAOPROP ':' FALSE ;
req : REQUIRED ':' '[' LITERAL litL ']' ;
litL : ',' LITERAL litL | ;
patPr : PATTERNPROP ':' '{' patSch patSchL '}' ;
patSchL : ',' patSch patSchL ;
patSch : '"' regExp '"' ':' '{' JSch '}' ;

arrSch : TYPE ':' ARRAY arrResL ;
arrResL : ',' arrRes arrResL | ;
arrRes : 	itemo
		|	itema
		|	minIt
		|	maxIt
		|	unique	;
itemo : ITEMS ':' '{' JSch '}' ;
itema : ITEMS ':' '[' JSchL ']' ;
minIt : MINITEMS ':' INT ;
maxIt : MAXITEMS ':' INT ;
unique : UNIQUEITEMS ':' TRUE ;

regExp : 'r' ;

%%

	private Yylex lexer;

	//private Stack<Integer> pRot = new Stack<Integer>();
	//private int proxRot = 1;

	//public static int ARRAY = 100;


	private int yylex () {
		int yyl_return = -1;
		try {
			yylval = new JSchParserVal(0);
			yyl_return = lexer.yylex();
		}
		catch (IOException e) {
			System.err.println("IO error :"+e);
		}
		return yyl_return;
	}


	public void yyerror (String error) {
		System.err.println ("Error: " + error + "  line: " + lexer.getLine());
	}

	public JSchParser(Reader r) {
		lexer = new Yylex(r, this);
	}

	public void setDebug(boolean debug) {
		yydebug = debug;
	}

	public static void main(String args[]) throws IOException {
		JSchParser yyparser;
		if ( args.length > 0 ) {
			// parse a file
			yyparser = new JSchParser(new FileReader(args[0]));
			yyparser.yyparse();
		}
		else {
			// interactive mode
			System.out.println("\n\tFormat: java JSchParser inputFile\n");
		}
	}

	public void testOutput(String test){
		System.out.println("\n\tHello " + test);
	}
