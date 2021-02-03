grammar LLang;

//options { tokenVocab=CommonLexer; }
import CommonLexer;

@header {
package task7;
}

classDeclaration
    : CLASS IDENTIFIER
      (EXTENDS typeType)?
      (IMPLEMENTS typeList)?
      classBody
    ;

classBody
    : '{' memberDeclaration* '}'
    ;

memberDeclaration
    : functionDeclaration
//    | genericFunctionDeclaration
    | variableDeclarators ';'
    // | constructorDeclaration
    // | genericConstructorDeclaration
//     | interfaceDeclaration
    // | annotationTypeDeclaration
    | classDeclaration
    // | enumDeclaration
    ;

functionDeclaration
    : (STATIC|BUILTIN)? typeTypeOrVoid? IDENTIFIER formalParameters
      (THROWS qualifiedNameList)?
      functionBody
    ;


functionBody
    : block
    | ';'
    ;

typeTypeOrVoid
    : typeType
    | VOID
    ;

qualifiedNameList
    : qualifiedName (',' qualifiedName)*
    ;

formalParameters
    : '(' formalParameterList? ')'
    ;

formalParameterList
    : formalParameter (',' formalParameter)* (',' lastFormalParameter)?
    | lastFormalParameter
    ;

formalParameter
    : typeType IDENTIFIER
    ;

lastFormalParameter
    : typeType '...' IDENTIFIER
    ;

variableModifier
    : FINAL
    //| annotation
    ;

qualifiedName
    : IDENTIFIER ('.' IDENTIFIER)*
    ;

fieldDeclaration
    //: typeType variableDeclarators ';'
    : variableDeclarators ';'
    ;

constructorDeclaration
    : IDENTIFIER formalParameters (THROWS qualifiedNameList)? constructorBody=block
    ;

variableDeclarators
    : (STATIC)? typeType variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    : IDENTIFIER ('=' variableInitializer)?
    ;

variableInitializer
    : arrayInitializer
    | expression
    ;

arrayInitializer
    : '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
    ;

classOrInterfaceType
    : IDENTIFIER ('.' IDENTIFIER)*
    //: IDENTIFIER
    ;

typeArgument
    : typeType
    | '?' ((EXTENDS | SUPER) typeType)?
    ;

literal
    : integerLiteral
    | floatLiteral
    | CHAR_LITERAL
    | STRING_LITERAL
    | BOOL_LITERAL
    | NULL_LITERAL
    ;

integerLiteral
    : DECIMAL_LITERAL
    | HEX_LITERAL
    | OCT_LITERAL
    | BINARY_LITERAL
    ;

floatLiteral
    : FLOAT_LITERAL
    | HEX_FLOAT_LITERAL
    ;

// STATEMENTS / BLOCKS
prog
    : (importDeclaration)*
    | blockStatements
    ;

importDeclaration
    : 'import' IDENTIFIER ('.' IDENTIFIER)*
    ;


block
    : '{' blockStatements '}'
    ;

blockStatements
    : blockStatement*
    ;

blockStatement
    : variableDeclarators ';'
    | statement
   // | localTypeDeclaration
    | functionDeclaration
    | classDeclaration
    ;

statement
    : blockLabel=block
    // | ASSERT expression (':' expression)? ';'
    | IF parExpression statement (ELSE statement)?
    | FOR '(' forControl ')' statement
    | WHILE parExpression statement
    | DO statement WHILE parExpression ';'
    //| TRY block (catchClause+ finallyBlock? | finallyBlock)
    //| TRY resourceSpecification block catchClause* finallyBlock?
    | SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'
    //| SYNCHRONIZED parExpression block
    | RETURN expression? ';'
    //| THROW expression ';'
    | BREAK IDENTIFIER? ';'
    | CONTINUE IDENTIFIER? ';'
    | SEMI
    | statementExpression=expression ';'
    | identifierLabel=IDENTIFIER ':' statement
    ;

/** Matches cases then statements, both of which are mandatory.
 *  To handle empty cases at the end, we add switchLabel* to statement.
 */
switchBlockStatementGroup
    : switchLabel+ blockStatement+
    ;

switchLabel
    : CASE (constantExpression=expression | enumConstantName=IDENTIFIER) ':'
    | DEFAULT ':'
    ;

forControl
    : enhancedForControl
    | forInit? ';' expression? ';' forUpdate=expressionList?
    ;

forInit
    : variableDeclarators
    | expressionList
    ;

enhancedForControl
    : typeType variableDeclaratorId ':' expression
    ;

// EXPRESSIONS

parExpression
    : '(' expression ')'
    ;

expressionList
    : expression (',' expression)*
    ;

functionCall
    : IDENTIFIER '(' expressionList? ')'
    | THIS '(' expressionList? ')'
    | SUPER '(' expressionList? ')'
    ;

expression
    : primary
    | expression bop='.'
      ( IDENTIFIER
      | functionCall
      | THIS
    //   | NEW nonWildcardTypeArguments? innerCreator
    //   | SUPER superSuffix
    //   | explicitGenericInvocation
      )
    | expression '[' expression ']'
    | functionCall
    // | NEW creator   //不用new关键字，而是用类名相同的函数直接生成对象。
    // | '(' typeType ')' expression
    | expression postfix=('++' | '--')
    | prefix=('+'|'-'|'++'|'--') expression
    | prefix=('~'|'!') expression
    | prefix=('~'|'!') expression
    | expression bop=('*'|'/'|'%') expression
    | expression bop=('+'|'-') expression
    | expression ('<' '<' | '>' '>' '>' | '>' '>') expression
    | expression bop=('<=' | '>=' | '>' | '<') expression
    | expression bop=INSTANCEOF typeType
    | expression bop=('==' | '!=') expression
    | expression bop='&' expression
    | expression bop='^' expression
    | expression bop='|' expression
    | expression bop='&&' expression
    | expression bop='||' expression
    | expression bop='?' expression ':' expression
    | <assoc=right> expression
      bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
      expression
    // | lambdaExpression // Java8

    // Java 8 functionReference
    // | expression '::' typeArguments? IDENTIFIER
    // | typeType '::' (typeArguments? IDENTIFIER | NEW)
    // | classType '::' typeArguments? NEW
    ;


primary
    : '(' expression ')'
    | THIS
    | SUPER
    | literal
    | IDENTIFIER
    | newArray
//     | typeTypeOrVoid '.' CLASS
    ;

typeList
    : typeType (',' typeType)*
    ;

typeType
    : (classOrInterfaceType| functionType | primitiveType) ('[' ']')*
    ;

newArray
    : (classOrInterfaceType| functionType | primitiveType) '[' expression ']' ('[' expression ']')*
    ;

functionType
    : FUNCTION typeTypeOrVoid '(' typeList? ')'
    ;

primitiveType
    : BOOLEAN
    | CHAR
    | BYTE
    | SHORT
    | INT
    | LONG
    | FLOAT
    | DOUBLE
    | STRING    //added on 2019-08-29 by Richard Gong
    ;

creator
    : IDENTIFIER arguments
    ;

superSuffix
    : arguments
    | '.' IDENTIFIER arguments?
    ;

arguments
    : '(' expressionList? ')'
    ;