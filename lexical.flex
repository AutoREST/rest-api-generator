package autorest.generator;
%%

%byaccj

%{
	private JSchParser yyparser;

	public Yylex(java.io.Reader r, JSchParser yyparser) {
		this(r);
		this.yyparser = yyparser;
		yyline = 1;
	}

	public int getLine() {
		return yyline;
	}

	public void setString(){
		String str = yytext().substring(0, yylength());
		yyparser.yylval = new JSchParserVal(str);
	}

	private int setAndReturn(int token){
		if(yyparser.expectLiteral){
			setString();
			return JSchParser.LITERAL;
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
								yyparser.yylval = new JSchParserVal(new Double(value).doubleValue());
								return setAndReturn(JSchParser.DEC);
							}
							yyparser.yylval = new JSchParserVal(new Integer(value).intValue());
							return setAndReturn(JSchParser.INT);
						}

id						{ return setAndReturn(JSchParser.ID); }
uri						{ return setAndReturn(JSchParser.URI); }
"$ref"					{ return setAndReturn(JSchParser.REF); }
definitions				{ return setAndReturn(JSchParser.DEFINITIONS); }
type					{ return setAndReturn(JSchParser.TYPE); }
string					{ return setAndReturn(JSchParser.STRING); }
integer					{ return setAndReturn(JSchParser.INTEGER); }
number					{ return setAndReturn(JSchParser.NUMBER); }
boolean					{ return setAndReturn(JSchParser.BOOLEAN); }
null					{ return setAndReturn(JSchParser.NULL); }
array					{ return setAndReturn(JSchParser.ARRAY); }
object					{ return setAndReturn(JSchParser.OBJECT); }
title					{ return setAndReturn(JSchParser.TITLE); }
description				{ return setAndReturn(JSchParser.DESCRIPTION); }
minLength				{ return setAndReturn(JSchParser.MINLENGTH); }
maxLength				{ return setAndReturn(JSchParser.MAXLENGTH); }
pattern					{ return setAndReturn(JSchParser.PATTERN); }
minimum					{ return setAndReturn(JSchParser.MINIMUM); }
exclusiveMinimum		{ return setAndReturn(JSchParser.EXCLUSIVEMINIMUM); }
maximum					{ return setAndReturn(JSchParser.MAXIMUM); }
exclusiveMaximum		{ return setAndReturn(JSchParser.EXCLUSIVEMAXIMUM); }
multipleOf				{ return setAndReturn(JSchParser.MULTIPLEOF); }
items					{ return setAndReturn(JSchParser.ITEMS); }
additionalItems			{ return setAndReturn(JSchParser.ADDITIONALITEMS); }
minItems				{ return setAndReturn(JSchParser.MINITEMS); }
maxItems				{ return setAndReturn(JSchParser.MAXITEMS); }
uniqueItems				{ return setAndReturn(JSchParser.UNIQUEITEMS); }
properties				{ return setAndReturn(JSchParser.PROPERTIES); }
additionalProperties	{ return setAndReturn(JSchParser.ADDITIONALPROPERTIES); }
required				{ return setAndReturn(JSchParser.REQUIRED); }
minProperties			{ return setAndReturn(JSchParser.MINPROPERTIES); }
maxProperties			{ return setAndReturn(JSchParser.MAXPROPERTIES); }
dependencies			{ return setAndReturn(JSchParser.DEPENDENCIES); }
patternProperties		{ return setAndReturn(JSchParser.PATTERNPROPERTIES); }
anyOf					{ return setAndReturn(JSchParser.ANYOF); }
allOf					{ return setAndReturn(JSchParser.ALLOF); }
oneOf					{ return setAndReturn(JSchParser.ONEOF); }
not						{ return setAndReturn(JSchParser.NOT); }
enum					{ return setAndReturn(JSchParser.ENUM); }
true					{ return setAndReturn(JSchParser.TRUE); }
false					{ return setAndReturn(JSchParser.FALSE); }

[a-zA-Z_0-9]+[a-zA-Z_0-9 \.]*	{	setString(); return JSchParser.LITERAL;   }

[^]						{ System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
