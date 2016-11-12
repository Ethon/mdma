grammar Mdma;

////////////////////////////////////////////////////////////
// Parser Rules
////////////////////////////////////////////////////////////

module: topLevelStatement*;

// Top-Level
topLevelStatement: functionDefinition;

// Function
functionDefinition: functionHeader statementBlock;
functionHeader: 'def' Ident '(' functionArgumentList ')' returnSpec?;
functionArgumentList: (variableTypeNamePair (',' variableTypeNamePair)*)?;
returnSpec: '->' type;

// Statement
statementBlock: '{' statement* '}';
expressionStatement: expression ';';
emptyStatement: ';';
variableDeclarationStatement: variableTypeNamePair ('=' expression)?;
loopStatement: forLoop;
forLoop: forRangeLoop;
forRangeLoop: 'for' '(' variableTypeNamePair ':' expression ')' statementBlock;
ifStatement: 'if' '(' expression ')' statementBlock elifStatement* elseStatement?;
elifStatement: 'elif' '(' expression ')' statementBlock;
elseStatement: 'else' statementBlock;
statement: statementBlock | expressionStatement | emptyStatement | loopStatement | variableDeclarationStatement | ifStatement;

// Expression
decIntExpression: DecInt;
intExpression: decIntExpression;
boolExpression: Bool;
listExpression: '[' (expression (',' expression)*)+ ']';
identifierExpression: Ident;
parenthesizedExpression: '(' expression ')';
primaryExpression: intExpression | listExpression | boolExpression | identifierExpression | parenthesizedExpression;

indexExpression: primaryExpression '[' expression ']';
callExpression: primaryExpression '(' callArgumentList ')';
callArgumentList: (expression (',' expression)*)?;
postfixExpression: indexExpression | callExpression | primaryExpression;

negateExpression: '!' prefixExpression;
prefixExpression: negateExpression | postfixExpression;

// LTR
multiplyExpression: prefixExpression ('*' multiplicativeExpression)+;
moduloExpression: prefixExpression ('%' multiplicativeExpression)+;
multiplicativeExpression: multiplyExpression | moduloExpression | prefixExpression;

// RTL
rangeExpression: multiplicativeExpression '..' rangeLikeExpression;
rangeLikeExpression: rangeExpression | multiplicativeExpression;

// LTR
equalExpression: rangeLikeExpression ('==' equalityExpression)+;
equalityExpression: equalExpression | rangeLikeExpression;

// RTL
assignExpression: equalityExpression '=' assignmentExpression;
assignmentExpression: assignExpression | equalityExpression;

expression: assignmentExpression;

// Types
namedType: Ident;
listType: '[' type ']';
type: namedType | listType;
variableTypeNamePair: type Ident;


////////////////////////////////////////////////////////////
// Lexer Rules
////////////////////////////////////////////////////////////

Bool: 'true' | 'false';
Ident  : [a-zA-Z_][a-zA-Z0-9_]* ;
DecInt: [1-9][0-9]* | '0';
WS  : [ \t\r\n]+ -> skip ;