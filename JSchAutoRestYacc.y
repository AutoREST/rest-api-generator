
%{
import java.io.*;
import java.util.List;
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
%type <sval> typeRes
%type <sval> strRes
%type <sval> arrRes
%type <sval> objRes
%type <sval> numRes
%type <sval> DEC
%type <ival> INT
%type <obj> typenameVal

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

res : type | refSch | title | description | multRes | typeRes { if(!validKeyword($1)) return 1; };
typeRes : strRes { $$ = $1; }
		| arrRes { $$ = $1; }
		| objRes { $$ = $1; }
		| numRes { $$ = $1; } ;
type : TYPE '"' ':' { currentTypes = new ArrayList<>(); } typeVal ;
typeVal : 	'[' typename typenameL ']'
			|	typename ;
typenameL	:
			|	',' typename typenameL ;
typename : '"' typenameVal '"' { currentTypes.add($2); };
typenameVal : STRING { $$ = JSONType.STRING; }
			| INTEGER { $$ = JSONType.INTEGER; }
			| NUMBER { $$ = JSONType.NUMBER; }
			| BOOLEAN { $$ = JSONType.BOOLEAN; }
			| NULL { $$ = JSONType.NULL; }
			| ARRAY { $$ = JSONType.ARRAY; }
			| OBJECT { $$ = JSONType.OBJECT; } ;

title : TITLE '"' ':'  string ;
description : DESCRIPTION '"' ':'  string ;

strRes :  minLen { $$ = "minLength"; }
		| maxLen { $$ = "maxLength"; }
		| pattern{ $$ = "pattern"; } ;
minLen : MINLENGTH '"' ':' INT ;
maxLen : MAXLENGTH '"' ':' INT ;
pattern : PATTERN '"' ':' "regExp" ;

numRes : 	min exMin { $$ = "minimum"; }
		| 	max exMax { $$ = "maximum"; }
		| 	multiple { $$ = "multipleOf"; };
min : MINIMUM '"' ':' real ;
exMin :		'}' { lexer.yypushback(1); }
		|	',' '"' res
		|	',' '"' EXCLUSIVEMINIMUM '"' ':' bool ;
max : MAXIMUM '"' ':' real  ;
exMax : 	'}' { lexer.yypushback(1); }
		|	',' '"' res
		|	',' '"' EXCLUSIVEMAXIMUM '"' ':' bool ;
multiple : MULTIPLEOF '"' ':' real ;

arrRes : 	items { $$ = "items"; }
		| 	additems { $$ = "additionalItems"; }
		| 	minitems { $$ = "minItems"; }
		| 	maxitems { $$ = "maxItems"; }
		| 	unique { $$ = "unique"; };
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

objRes : 	prop { $$ = "properties"; }
		| 	addprop { $$ = "additionalProperties"; }
		| 	req { $$ = "required"; }
		| 	minprop { $$ = "minProperties"; }
		| 	maxprop { $$ = "maxProperties"; }
		| 	dep { $$ = "dependencies"; }
		| 	pattprop { $$ = "patternProperties"; };
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
bool : TRUE | FALSE ;
string : '"' LITERAL '"' { $$ = $2 ;};
Jval : string | real | array | object | bool | NULL ;
real : INT | DEC;
array : '[' Jval ']' ;
object : '{' kword ':' Jval '}' ;

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
address : 'w' ;

%%

	private Yylex lexer;
	private String file;
	private boolean verbose;

	//private Stack<Integer> pRot = new Stack<Integer>();
	//private int proxRot = 1;

	//public static int ARRAY = 100;
	private List<Object> currentTypes = null;
	private JSchSemantics semValidator;

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

	public JSchParser(String file) throws IOException {
		this(new FileReader(file));
		this.file = file;
	}

	public JSchParser(Reader r) {
		lexer = new Yylex(r, this);
		semValidator = new JSchSemantics();
		this.verbose = false;
	}

	public void setDebug(boolean debug) {
		yydebug = debug;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean parse(){
		boolean result = yyparse() == 0;
		if(verbose)
			System.out.println("["+file+"] : Parse = " + result);
		return result;
	}

	public static void main(String args[]) throws IOException {
		if ( args.length > 0 ) {
			JSchParser yyparser;
			yyparser = new JSchParser(args[0]);
			if(args.length > 1){
				for(int i=1; i < args.length; i++){
					if(args[i].equals("-v")){
						yyparser.setVerbose(true);
					}
				}
			}
			yyparser.parse();
		}
		else {
			System.out.println("\n\tFormat: java JSchParser inputFile\n");
		}
	}

	public static void message(String msg){
		System.out.println(msg);
	}

	public void printCurrentTypes(){
		for(Object t : currentTypes)
			System.out.println(t.toString());
	}

	public List<JSONType> typesList(){
		List<JSONType> types = new ArrayList<>();
		for(Object t : currentTypes)
			types.add((JSONType)t);
		return types;
	}

	private boolean validKeyword(String keyword){
		List<JSONType> types = typesList();
		boolean valid = semValidator.Compatible(types, keyword);
		if(yydebug){
			String sType = "[";
			for(JSONType t : types)
				sType += t.toString();
			sType+="]";
			if(valid)
				System.out.println(keyword + " is valid for " + sType);
			else
				System.out.println(keyword + " is not valid for " + sType);
		}
		return valid;
	}
