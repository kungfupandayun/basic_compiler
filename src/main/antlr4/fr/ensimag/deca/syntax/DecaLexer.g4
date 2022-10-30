lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}


// Mots reserves
ASM: 'asm';
INSTANCEOF: 'instanceof';
PRINTLN: 'println';
TRUE: 'true';
CLASS: 'class';
NEW: 'new';
PRINTLNX: 'printlnx';
WHILE: 'while';
EXTENDS: 'extends';
NULL: 'null';
PRINTX: 'printx';
ELSE: 'else';
READINT: 'readInt';
PROTECTED: 'protected';
FALSE: 'false';
READFLOAT: 'readFloat';
RETURN: 'return';
IF: 'if';
PRINT: 'print';
THIS: 'this';


// Identificateurs

fragment LETTER: ('a' .. 'z' | 'A' .. 'Z');
fragment DIGIT: '0' .. '9';
IDENT: (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

// Symboles speciaux
LT: '<';
GT: '>';
GEQ: '>=';
LEQ: '<=';
EQUALS: '=';
PLUS: '+';
MINUS: '-';
TIMES:'*';
SLASH: '/';
PERCENT: '%';
DOT: '.';
OPARENT: '(';
CPARENT: ')';
SEMI: ';';
EXCLAM: '!';
OBRACE: '{';
CBRACE: '}';
COMMA: ',';
NEQ: '!=';
EQEQ:'==';
AND: '&&';
OR: '||';



// literaux entiers
fragment POSITIVE_DIGIT : '1' .. '9';
INT: ('0' |POSITIVE_DIGIT DIGIT*);

// Littéraux flottants
fragment NUM: DIGIT+;
fragment SIGN: '+' |'-'|;
fragment EXP : ('E'|'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F'|'f'|);
fragment DIGITHEX : ('0' .. '9')|('A'.. 'F')|('a'..'f');
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX: ('0x'|'0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f'|);
FLOAT: FLOATDEC | FLOATHEX;

// Chaine de caratère
fragment STRING_CAR : ~('"'|'\\'|'\n');
STRING              : '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING   : '"' (STRING_CAR | '\n' | '\\"' | '\\\\')* '"';

// Commentaire
fragment COMMENT: '/*' .*? '*/';
fragment COMMENTONELINE: '//' (~'\n')* '\n'?;

// Séparateur
// Ignore spaces, tabs, newlines and whitespaces
WS  :   ( ' ' | '\t' | '\r' | '\n' | COMMENT | COMMENTONELINE )
  { skip(); // avoid producing a token
  } ;

// Include du fichier
fragment FILENAME: (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"' {
      doInclude(getText());
      skip();
};
