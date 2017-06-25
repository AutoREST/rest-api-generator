package autorest.generator;
%%

%byaccj

%{
	private PFISCompiler yyparser;

	public Yylex(java.io.Reader r, PFISCompiler yyparser) {
		this(r);
		this.yyparser = yyparser;
		yyline = 1;
	}

	public int getLine() {
		return yyline;
	}

	public void setString(){
		String str = yytext().substring(0, yylength());
		yyparser.yylval = new PFISCompilerVal(str);
	}

	private int setAndReturn(int token){
		if(yyparser.expectLiteral){
			setString();
			return PFISCompiler.LITERAL;
		}
		return token;
	}
%}

digit 	= [0-9]
integer = {digit}+
real 	= ({digit}+[.]{digit}+)|({integer})
NL  = \n | \r | \r\n

%%


"$TRACE_ON"  { yyparser.setDebug(true);  }
"$TRACE_OFF" { yyparser.setDebug(false); }


{NL}   {yyline++;}
[ \t]+ { }

/* operadores */

":" |
"#" |
"{" |
"}" |
"/"	|
"," |
"\"" |
"\[" |
"\]"					{ return (int) yycharat(0); }

{real}					{ 	String value = yytext();
							if(value.indexOf(".")>0){
								yyparser.yylval = new PFISCompilerVal(new Double(value).doubleValue());
								return setAndReturn(PFISCompiler.DEC);
							}
							yyparser.yylval = new PFISCompilerVal(new Integer(value).intValue());
							return setAndReturn(PFISCompiler.INT);
						}

id						{ return setAndReturn(PFISCompiler.ID); }
uri						{ return setAndReturn(PFISCompiler.URI); }
"$ref"					{ return setAndReturn(PFISCompiler.REF); }
definitions				{ return setAndReturn(PFISCompiler.DEFINITIONS); }
type					{ return setAndReturn(PFISCompiler.TYPE); }
string					{ return setAndReturn(PFISCompiler.STRING); }
integer					{ return setAndReturn(PFISCompiler.INTEGER); }
number					{ return setAndReturn(PFISCompiler.NUMBER); }
boolean					{ return setAndReturn(PFISCompiler.BOOLEAN); }
null					{ return setAndReturn(PFISCompiler.NULL); }
array					{ return setAndReturn(PFISCompiler.ARRAY); }
object					{ return setAndReturn(PFISCompiler.OBJECT); }
title					{ return setAndReturn(PFISCompiler.TITLE); }
description				{ return setAndReturn(PFISCompiler.DESCRIPTION); }
minLength				{ return setAndReturn(PFISCompiler.MINLENGTH); }
maxLength				{ return setAndReturn(PFISCompiler.MAXLENGTH); }
pattern					{ return setAndReturn(PFISCompiler.PATTERN); }
minimum					{ return setAndReturn(PFISCompiler.MINIMUM); }
exclusiveMinimum		{ return setAndReturn(PFISCompiler.EXCLUSIVEMINIMUM); }
maximum					{ return setAndReturn(PFISCompiler.MAXIMUM); }
exclusiveMaximum		{ return setAndReturn(PFISCompiler.EXCLUSIVEMAXIMUM); }
multipleOf				{ return setAndReturn(PFISCompiler.MULTIPLEOF); }
items					{ return setAndReturn(PFISCompiler.ITEMS); }
additionalItems			{ return setAndReturn(PFISCompiler.ADDITIONALITEMS); }
minItems				{ return setAndReturn(PFISCompiler.MINITEMS); }
maxItems				{ return setAndReturn(PFISCompiler.MAXITEMS); }
uniqueItems				{ return setAndReturn(PFISCompiler.UNIQUEITEMS); }
properties				{ return setAndReturn(PFISCompiler.PROPERTIES); }
additionalProperties	{ return setAndReturn(PFISCompiler.ADDITIONALPROPERTIES); }
required				{ return setAndReturn(PFISCompiler.REQUIRED); }
minProperties			{ return setAndReturn(PFISCompiler.MINPROPERTIES); }
maxProperties			{ return setAndReturn(PFISCompiler.MAXPROPERTIES); }
dependencies			{ return setAndReturn(PFISCompiler.DEPENDENCIES); }
patternProperties		{ return setAndReturn(PFISCompiler.PATTERNPROPERTIES); }
anyOf					{ return setAndReturn(PFISCompiler.ANYOF); }
allOf					{ return setAndReturn(PFISCompiler.ALLOF); }
oneOf					{ return setAndReturn(PFISCompiler.ONEOF); }
not						{ return setAndReturn(PFISCompiler.NOT); }
enum					{ return setAndReturn(PFISCompiler.ENUM); }
true					{ return setAndReturn(PFISCompiler.TRUE); }
false					{ return setAndReturn(PFISCompiler.FALSE); }

[a-zA-Z_0-9]+[a-zA-Z_0-9 \.]*	{	setString(); return PFISCompiler.LITERAL;   }

[^]						{ System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
