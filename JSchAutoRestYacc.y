
%{
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
%}


%token ID, URI, DEFINITIONS, TYPE
%token STRING, INTEGER, NUMBER, BOOLEAN, NULL, ARRAY, OBJECT
%token TITLE, DESCRIPTION
%token MINLENGTH, MAXLENGTH, PATTERN, MINIMUM, EXCLUSIVEMINIMUM, MAXIMUM, EXCLUSIVEMAXIMUM, MULTIPLEOF
%token ITEMS, ADDITIONALITEMS, MINITEMS, MAXITEMS, UNIQUEITEMS
%token PROPERTIES, ADDITIONALPROPERTIES, REQUIRED, MINPROPERTIES, MAXPROPERTIES, DEPENDENCIES, PATTERNPROPERTIES
%token ANYOF, ALLOF, ONEOF, NOT, ENUM
%token TRUE, FALSE, INT, DEC, LITERAL

%type <sval> LITERAL
%type <sval> string
%type <sval> DEC
%type <ival> INT

%%

JSDoc : '{' '"' idDefJSch '}' ;

idDefJSch 	:	id ',' '"' defsJSch
			|	defsJSch ;
defsJSch	:	defs  ',' '"' JSchD
			|	JSchD ;

id : ID '"' ':' '"' uri '"' ;

defs : DEFINITIONS '"' ':' '{' kSch kSchL '}' ;

kSchL :
		|	',' kSch kSchL ;
kSch : kword ':' '{' JSch '}' ;

JSchL :
		|	',' '{' JSch '}' JSchL ;
JSch : '"' JSchD ;
JSchD : res resL ;

kword : string { message("kword: " + $1); }

resL :
		|	',' '"' res resL ;

res : type | strRes | arrRes | objRes | multRes | refSch | title | description | numRes ;
type : TYPE '"' ':' typeVal ;
typeVal : 	'[' typename typenameL ']'
			|	typename ;
typenameL	:
			|	',' typename typenameL ;
typename : '"' typenameVal '"' ;
typenameVal : STRING | INTEGER | NUMBER | BOOLEAN | NULL | ARRAY | OBJECT ;

title : TITLE '"' ':'  string ;
description : DESCRIPTION '"' ':'  string ;

strRes :  minLen | maxLen | pattern ;
minLen : MINLENGTH '"' ':' INT ;
maxLen : MAXLENGTH '"' ':' INT ;
pattern : PATTERN '"' ':' "regExp" ;

numRes : min exMin | max exMax | multiple ;
min : MINIMUM '"' ':' DEC  ;
exMin : 	',' '"' res
		|	',' '"' EXCLUSIVEMINIMUM '"' ':' bool ;
max : MAXIMUM '"' ':' DEC  ;
exMax : 	',' '"' res
		|	',' '"' EXCLUSIVEMAXIMUM '"' ':' bool ;
multiple : MULTIPLEOF '"' ':' DEC ;

arrRes : items | additems | minitems | maxitems  | unique ;
items : ITEMS '"' ':' itemDecl ;
itemDecl : 	sameitems
			|  	varitems ;
sameitems	: '{' JSch '}' ;
varitems 	: '[' '{' JSch '}' JSchL ']' ;
additems 	:  ADDITIONALITEMS '"' ':' additionalValue ;
additionalValue : bool
				| 	'{' JSch '}' ;
minitems : MINITEMS '"' ':' INT ;
maxitems : MAXITEMS '"' ':' INT ;
unique : UNIQUEITEMS '"' ':' bool ;

objRes : prop | addprop | req | minprop | maxprop | dep | pattprop ;
prop : PROPERTIES '"' ':' '{' kSch kSchL '}' ;
addprop : ADDITIONALPROPERTIES '"' ':' additionalValue ;
req : REQUIRED '"' ':' '[' kword kwordL ']' ;
kwordL :
		|	',' kword kwordL ;
minprop : MINPROPERTIES '"' ':' INT ;
maxprop : MAXPROPERTIES '"' ':' INT ;
dep : DEPENDENCIES '"' ':' '{' kDep kDepL '}' ;
kDepL :
		|	',' kDep kDepL ;
kDep : kword ':' kDepVal ;
kDepVal :		'[' kword kwordL ']'
			|	'{' JSch '}' ;
pattprop : PATTERNPROPERTIES '"' ':' '{' patSch patSchL '}' ;
patSchL :
			|	',' patSch patSchL ;
patSch : "regExp" ':' '{' JSch '}' ;

multRes : allOf | anyOf| oneOf | not | enum ;
anyOf : ANYOF multResArr ;
allOf : ALLOF multResArr ;
oneOf : ONEOF multResArr ;
multResArr : '"' ':' '[' '{' JSch '}' JSchL ']' ;
not : NOT '"' ':' '{' JSch '}' ;
enum : ENUM '"' ':' '[' Jval JvalL ']' ;
JvalL :
		|	',' Jval JvalL ;
refSch : "$ref" ':' '"' uriRef '"' ;
uriRef : addressVal JPointerVal ;
addressVal :
				|	address ;
JPointerVal :
				|	'#' '/' JPointer ;
JPointer : '/' path ;
path : escaped ;
escaped : '~' '0' | '~' '1' ;
uri : '#' ;
bool : TRUE | FALSE ;
string : '"' LITERAL '"' { $$ = $2 ;};
Jval : string | INT | DEC | array | object | bool | NULL ;
array : '[' Jval ']' ;
object : '{' kword ':' Jval '}' ;
address : 'w' ;
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
		if(yydebug){
			//dump_stacks(statestk.length);
			dump_stacks(10);
		}
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

	public static void message(String msg){
		System.out.println(msg);
	}
