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

NUM = [0-9]+
DEC = [0-9]+(\.[0-9]+)?
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
"\[" |
"\]"                        { return (int) yycharat(0); }

{NUM}                       { yyparser.yylval = new JSchParserVal(yytext());
                                return JSchParser.INT; }
{DEC}                       { yyparser.yylval = new JSchParserVal(yytext());
                                return JSchParser.DEC; }

\"definitions\"             { return JSchParser.DEFINITIONS;     }
\"not\"                     { return JSchParser.NOT; }
\"allOf\"                   { return JSchParser.ALLOF; }
\"anyOf\"                   { return JSchParser.ANYOF; }
\"enum\"                    { return JSchParser.ENUM; }
\"\$ref\"                   { return JSchParser.REF; }
\"type\"                    { return JSchParser.TYPE; }
\"string\"                  { return JSchParser.STRING; }
\"minLength\"               { return JSchParser.MINLEN; }
\"maxLength\"               { return JSchParser.MAXLEN; }
\"pattern\"                 { return JSchParser.PATTERN; }
\"number\"                  { return JSchParser.NUMBER; }
\"integer\"                 { return JSchParser.INTEGER; }
\"minimum\"                 { return JSchParser.MINIMUM; }
\"exclusiveMinimum\"        { return JSchParser.EXMINIMUM; }
\"maximum\"                 { return JSchParser.MAXIMUM; }
\"exclusiveMaximum\"        { return JSchParser.EXMAXIMUM; }
\"multipleOf\"              { return JSchParser.MULTIPLEOF; }
\"object\"                  { return JSchParser.OBJECT; }
\"properties\"              { return JSchParser.PROPERTIES; }
\"additionalProperties\"    { return JSchParser.ADDITIONAOPROP; }
\"required\"                { return JSchParser.REQUIRED; }
\"patternProperties\"       { return JSchParser.PATTERNPROP; }
\"array\"                   { return JSchParser.ARRAY; }
\"items\"                   { return JSchParser.ITEMS; }
\"minItems\"                { return JSchParser.MINITEMS; }
\"maxItems\"                { return JSchParser.MAXITEMS; }
\"uniqueItems\"             { return JSchParser.UNIQUEITEMS; }
true                        { return JSchParser.TRUE; }
false                       { return JSchParser.FALSE; }

\"[^\n]+\"                  { return JSchParser.LITERAL;   }

[^]                         { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
