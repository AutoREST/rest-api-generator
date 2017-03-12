
%{
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
%}


%token ID, URI, DEFINITIONS, TYPE
%token STRING, INTEGER, NUMBER, BOOLEAN, NULL, ARRAY, OBJECT
%token TITLE, DESCRIPTION
%token MINLENGTH, MAXLENGTH, PATTERN, MINIMUM, EXCLUSIVEMINIMUM, MAXIMUM, EXCLUSIVEMAXIMUM, MULTIPLEOF
%token ITEMS, ADDITIONALITEMS, MINITEMS, MAXITEMS, UNIQUEITEMS
%token PROPERTIES, ADDITIONALPROPERTIES, REQUIRED, MINPROPERTIES, MAXPROPERTIES, DEPENDENCIES, PATTERNPROPERTIES
%token ANYOF, ALLOF, ONEOF, NOT, ENUM, REF
%token TRUE, FALSE, INT, DEC, LITERAL

%type <sval> LITERAL
%type <sval> string
%type <sval> kword
%type <sval> typeRes
%type <sval> strRes
%type <sval> arrRes
%type <sval> objRes
%type <sval> numRes
%type <sval> uriRef
%type <sval> address
%type <sval> addressVal
%type <sval> JPointerVal
%type <sval> JPointer
%type <dval> DEC
%type <ival> INT
%type <obj> real
%type <obj> array
%type <obj> object
%type <obj> bool
%type <obj> typenameVal
%type <obj> JSch
%type <obj> JSchD
%type <obj> Jval
%type <obj> multResArr
%type <obj> additionalValue

%%

JSDoc : '{' '"' idDefJSch '}' 	{	if(verbose){
										if(mainJSchema != null)
											message(mainJSchema.toString());
										if(definitionsMap != null)
											message(definitionsMap.toString());
									}
								} ;

idDefJSch 	:	id ',' '"' defsJSch
			|	defsJSch ;
defsJSch	:	defs  ',' '"' JSchD { mainJSchema = (JSchRestriction)$4; };
			|	JSchD { mainJSchema = (JSchRestriction)$1;};

id : ID '"' ':' '"' uri '"' ;

defs : DEFINITIONS '"' ':' '{' { newMap(); definitionsMap=(Map)tempMap; } kSch kSchL '}' { lastMap(); } ;

kSchL :
		|	',' kSch kSchL ;
kSch : kword ':' '{' JSch '}' { tempMap.put($1,$4); } ;

JSchL :
		|	',' '{' JSch { tempList.add($3); } '}' JSchL ;
JSch : '"' JSchD { $$ = cJSch; lastJSch(); } ;
JSchD : { newJSch(); } res resL { $$ = cJSch; };

kword : string { $$=$1; }

resL :
		|	',' '"' res resL ;

res : type | refSch | title | description | multRes | typeRes { if(!validKeyword($1)) return 1; };
typeRes : strRes { $$ = $1; }
		| arrRes { $$ = $1; }
		| objRes { $$ = $1; }
		| numRes { $$ = $1; } ;
type : TYPE '"' ':' typeVal ;
typeVal : 	'[' typename typenameL ']'
			|	typename ;
typenameL	:
			|	',' typename typenameL ;
typename : '"' typenameVal '"' { cJSch.addType((JSONType)$2); };
typenameVal : STRING { $$ = JSONType.STRING; }
			| INTEGER { $$ = JSONType.INTEGER; }
			| NUMBER { $$ = JSONType.NUMBER; }
			| BOOLEAN { $$ = JSONType.BOOLEAN; }
			| NULL { $$ = JSONType.NULL; }
			| ARRAY { $$ = JSONType.ARRAY; }
			| OBJECT { $$ = JSONType.OBJECT; } ;

title : TITLE '"' ':'  string { cJSch.setTitle($4); } ;
description : DESCRIPTION '"' ':'  string { cJSch.setDescription($4); };

strRes :  minLen { $$ = "minLength"; }
		| maxLen { $$ = "maxLength"; }
		| pattern{ $$ = "pattern"; } ;
minLen : MINLENGTH '"' ':' INT { cJSch.setMinLength((Integer)$4); } ;
maxLen : MAXLENGTH '"' ':' INT { cJSch.setMaxLength((Integer)$4); } ;
pattern : PATTERN '"' ':' string { cJSch.setPattern($4); } ;

numRes : 	min exMin { $$ = "minimum"; }
		| 	max exMax { $$ = "maximum"; }
		| 	multiple { $$ = "multipleOf"; };
min : MINIMUM '"' ':' real { cJSch.setMinimum(new Double($4.toString())); } ;
exMin :		'}' { lexer.yypushback(1); }
		|	',' '"' res
		|	',' '"' EXCLUSIVEMINIMUM '"' ':' bool { cJSch.setExMinimum((Boolean)$6); } ;
max : MAXIMUM '"' ':' real { cJSch.setMaximum(new Double($4.toString())); } ;
exMax : 	'}' { lexer.yypushback(1); }
		|	',' '"' res
		|	',' '"' EXCLUSIVEMAXIMUM '"' ':' bool { cJSch.setExMaximum((Boolean)$6); } ;
multiple : MULTIPLEOF '"' ':' real { cJSch.setMultipleOf(new Double($4.toString())); };

arrRes : 	items { $$ = "items"; }
		| 	additems { $$ = "additionalItems"; }
		| 	minitems { $$ = "minItems"; }
		| 	maxitems { $$ = "maxItems"; }
		| 	unique { $$ = "unique"; };
items : ITEMS '"' ':' itemDecl ;
itemDecl : 		sameitems
			|  	varitems ;
sameitems	: '{' JSch '}' { cJSch.setSameItems((JSchRestriction)$2); } ;
varitems 	: '[' { newList(); } '{' JSch { tempList.add($4); } '}' JSchL ']'
					{ cJSch.setVariItems((List)tempList); lastList(); } ;
additems 	:  ADDITIONALITEMS '"' ':' additionalValue { cJSch.setAdditionalItems($4); };
additionalValue : bool { $$=$1; }
				| 	'{' JSch '}' { $$=$2; } ;
minitems : MINITEMS '"' ':' INT { cJSch.setMinItems($4); } ;
maxitems : MAXITEMS '"' ':' INT { cJSch.setMaxItems($4); } ;
unique : UNIQUEITEMS '"' ':' bool { cJSch.setUniqueItems((Boolean)$4); } ;

objRes : 	prop { $$ = "properties"; }
		| 	addprop { $$ = "additionalProperties"; }
		| 	req { $$ = "required"; }
		| 	minprop { $$ = "minProperties"; }
		| 	maxprop { $$ = "maxProperties"; }
		| 	dep { $$ = "dependencies"; }
		| 	pattprop { $$ = "patternProperties"; };
prop : PROPERTIES '"' ':' '{' { newMap(); } kSch kSchL '}' { cJSch.setProperties((Map)tempMap); lastMap(); } ;
addprop : ADDITIONALPROPERTIES '"' ':' additionalValue { cJSch.setAdditionalProperties($4); } ;
req : REQUIRED '"' ':' '[' 	{ newList(); } kword
							{ tempList.add($6); } kwordL ']'
							{ cJSch.setRequired((List)tempList); lastList(); };
kwordL :
		|	',' kword { tempList.add($2); } kwordL ;
minprop : MINPROPERTIES '"' ':' INT { cJSch.setMinProperties($4); } ;
maxprop : MAXPROPERTIES '"' ':' INT { cJSch.setMaxProperties($4); } ;
dep : DEPENDENCIES '"' ':' '{' { newMap(); } kDep kDepL '}' { cJSch.setDependencies((Map)tempMap); lastMap();} ;
kDepL :
		|	',' kDep kDepL ;
kDep : kword ':' { newDep(); } kDepVal { tempMap.put($1,tempDep); lastDep(); } ;
kDepVal :		'[' { newList(); } kword
					{ tempList.add($3); } kwordL ']'
					{ tempDep.setKwords((List)tempList); lastList(); }
			|	'{' JSch '}' { tempDep.setJSch((JSchRestriction)$2); } ;
pattprop : PATTERNPROPERTIES '"' ':' '{'	{ newMap(); } patSch patSchL '}'
											{ cJSch.setPatternProperties((Map)tempMap); lastMap(); } ;
patSchL :
			|	',' patSch patSchL ;
patSch : string ':' '{' JSch '}' { tempMap.put($1,$4); };

multRes : allOf | anyOf| oneOf | not | enum ;
anyOf : ANYOF multResArr { cJSch.setAnyOf((List<JSchRestriction>)$2); } ;
allOf : ALLOF multResArr { cJSch.setAllOf((List<JSchRestriction>)$2); } ;
oneOf : ONEOF multResArr { cJSch.setOneOf((List<JSchRestriction>)$2); } ;
multResArr : '"' ':' '[' { newList(); }
						'{' JSch '}' { tempList.add((JSchRestriction)$6); }
						JSchL ']' { $$ = tempList; lastList(); } ;
not : NOT '"' ':' '{' JSch { cJSch.setNot((JSchRestriction)$5); } '}' ;
enum : ENUM '"' ':' '[' { newList(); } Jval { tempList.add($6); } JvalL ']' { cJSch.setEnumValues(tempList); lastList(); } ;
JvalL :
		|	',' Jval { tempList.add($2); } JvalL ;
bool : TRUE { $$ = true; }
	| FALSE { $$ = false; };
string : '"' { expectLiteral=true; } LITERAL { expectLiteral=false; } '"' { $$ = $3; } ;
Jval :		string { $$ = $1; }
		|	real { $$ = $1; }
		|	array { $$ = $1; }
		|	object { $$ = $1; }
		|	bool { $$ = $1; }
		|	NULL { $$ = null; };
real : INT { $$ = $1; }
	| DEC { $$ = $1; };
array : '[' { newList(); } Jval { tempList.add($3); } JvalL ']' { $$ = tempList; lastList();};
object : '{'{ newMap();} objProps '}' { $$ = tempMap; lastMap();} ;
objProps : kword ':' Jval { tempMap.put($1,$3); } objPropsL ;
objPropsL :
			| ',' objProps;
refSch : REF '"' ':' '"' uriRef { cJSch.setRef($5); } '"' ;
uriRef : addressVal JPointerVal { $$ = $1 + $2; };
addressVal : 				{ $$ = ""; }
				|	address { $$ = ""; };
JPointerVal :		{ $$ = ""; }
				|	'#' JPointer { $$ = "#" + $2; };
JPointer :
	{ $$ = ""; }
	| '/' { expectLiteral=true; } LITERAL { expectLiteral=false; } JPointer { $$="/"+$3+$5;} ;
uri : '#' ;
address : 'w' ;

%%

	private Yylex lexer;
	private String file;
	private boolean verbose;

	//private Stack<Integer> pRot = new Stack<Integer>();
	//private int proxRot = 1;

	//public static int ARRAY = 100;
	private PFSHandler pfsHandler;
	private JSchSemantics semValidator;
	private Stack<JSchRestriction> stackJSch;
	private JSchRestriction cJSch;
	private List<Object> tempList;
	private Stack<List<Object>> stackLists;
	private Map<String, JSchRestriction> definitionsMap;
	private JSchRestriction mainJSchema;
	private Map<String, Object> tempMap;
	private Stack<Map<String, Object>> stackMaps;
	private JSchDependence tempDep;
	private Stack<JSchDependence> stackDeps;
	public Boolean expectLiteral;

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
		System.err.println ("Error: " + error + "  line: " + lexer.getLine() + " text: " + lexer.yytext());
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

	private void initialize(){
		stackJSch = new Stack<>();
		stackLists = new Stack<>();
		stackMaps = new Stack<>();
		stackDeps = new Stack<>();
		definitionsMap = null;
		mainJSchema = null;
		cJSch = null;
		tempList = null;
		tempMap = null;
		tempDep = null;
		expectLiteral = false;
	}

	public void setDebug(boolean debug) {
		yydebug = debug;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean parse(){
		initialize();
		boolean result = (yyparse() == 0);
		if(verbose)
			System.out.println("["+file+"] : Parse = " + result);
		if(result){
			try{
				pfsHandler = new PFSHandler(definitionsMap, mainJSchema);
			}
			catch(Exception ex){
				pfsHandler = null;
				System.out.println(ex);
			}
		}
		else{
			pfsHandler = null;
		}
		return result;
	}

	public PFSHandler getPFSHandler(){
		return pfsHandler;
	}

	public static void message(String msg){
		System.out.println(msg);
	}

	public void printCurrentTypes(){
		for(JSONType t : cJSch.getTypes())
			System.out.println(t.toString());
	}

	private boolean validKeyword(String keyword){
		List<JSONType> types = cJSch.getTypes();
		boolean valid = semValidator.Compatible(types, keyword);
		if(yydebug || !valid){
			String sType = "[";
			for(JSONType t : types)
				sType += t.toString();
			sType+="]";
			if(valid) System.out.println(keyword + " is valid for " + sType);
			else System.out.println(keyword + " is not valid for " + sType);
		}
		return valid;
	}

	private JSchRestriction getJSchFromPointer(String path){
		int i;
		String pathComp[] = path.split("/");
		for(i=0;i<pathComp.length;i++){
			System.out.println("path["+i+"]: "+pathComp[i]);
		}
		return null;
	}

	private void newJSch(){
		if(cJSch != null) stackJSch.push(cJSch);
		cJSch = new JSchRestriction();
	}

	private void lastJSch(){
		if(stackJSch.empty()) cJSch = null;
		else cJSch = stackJSch.pop();
	}

	private void newList(){
		if(tempList != null) stackLists.push(tempList);
		tempList = new ArrayList<>();
	}

	private void lastList(){
		if(stackLists.empty()) tempList = null;
		else tempList = stackLists.pop();
	}

	private void newMap(){
		if(tempMap != null) stackMaps.push(tempMap);
		tempMap = new HashMap<>();
	}

	private void lastMap(){
		if(stackMaps.empty()) tempMap = null;
		else tempMap = stackMaps.pop();
	}

	private void newDep(){
		if(tempDep != null) stackDeps.push(tempDep);
		tempDep = new JSchDependence();
	}

	private void lastDep(){
		if(stackDeps.empty()) tempDep = null;
		else tempDep = stackDeps.pop();
	}
