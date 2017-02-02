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
"{" |
"}" |
"," |
"\"" |
"\[" |
"\]"					{ return (int) yycharat(0); }

{real}					{ yyparser.yylval = new JSchParserVal(yytext());
							if(yyparser.yylval.sval.indexOf(".")>0)
								return JSchParser.DEC;
							return JSchParser.INT; }

id						{ return JSchParser.ID; }
uri						{ return JSchParser.URI; }
definitions				{ return JSchParser.DEFINITIONS; }
type					{ return JSchParser.TYPE; }
string					{ return JSchParser.STRING; }
integer					{ return JSchParser.INTEGER; }
number					{ return JSchParser.NUMBER; }
boolean					{ return JSchParser.BOOLEAN; }
null					{ return JSchParser.NULL; }
array					{ return JSchParser.ARRAY; }
object					{ return JSchParser.OBJECT; }
title					{ return JSchParser.TITLE; }
description				{ return JSchParser.DESCRIPTION; }
minLength				{ return JSchParser.MINLENGTH; }
maxLength				{ return JSchParser.MAXLENGTH; }
pattern					{ return JSchParser.PATTERN; }
minimum					{ return JSchParser.MINIMUM; }
exclusiveMinimum		{ return JSchParser.EXCLUSIVEMINIMUM; }
maximum					{ return JSchParser.MAXIMUM; }
exclusiveMaximum		{ return JSchParser.EXCLUSIVEMAXIMUM; }
multipleOf				{ return JSchParser.MULTIPLEOF; }
items					{ return JSchParser.ITEMS; }
additionalItems			{ return JSchParser.ADDITIONALITEMS; }
minItems				{ return JSchParser.MINITEMS; }
maxItems				{ return JSchParser.MAXITEMS; }
uniqueItems				{ return JSchParser.UNIQUEITEMS; }
properties				{ return JSchParser.PROPERTIES; }
additionalProperties	{ return JSchParser.ADDITIONALPROPERTIES; }
required				{ return JSchParser.REQUIRED; }
minProperties			{ return JSchParser.MINPROPERTIES; }
maxProperties			{ return JSchParser.MAXPROPERTIES; }
dependencies			{ return JSchParser.DEPENDENCIES; }
patternProperties		{ return JSchParser.PATTERNPROPERTIES; }
anyOf					{ return JSchParser.ANYOF; }
allOf					{ return JSchParser.ALLOF; }
oneOf					{ return JSchParser.ONEOF; }
not						{ return JSchParser.NOT; }
enum					{ return JSchParser.ENUM; }
true					{ return JSchParser.TRUE; }
false					{ return JSchParser.FALSE; }

[a-zA-Z_0-9]+			{ 	yyparser.yylval = new JSchParserVal(yytext().substring(0, yylength()));
							return JSchParser.LITERAL;   }

[^]						{ System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
