grammar Expression;

start
    : expr EOF # StartExpr
    ;
expr
    : expr '&&' par_expr # ExprAnd
    | expr '||' par_expr # ExprOr
    | par_expr # ExprPar
    ;
par_expr
    : bool_expr # ParExprBool
    | '(' expr ')' # ParExprExpr
    ;
bool_expr
    : 'true' # BoolExprTrue
    | 'false' # BoolExprFalse
    | eq_elem '==' eq_elem # BoolExprEq
    | eq_elem '!=' eq_elem # BoolExprNeq
    | ineq_elem '<' ineq_elem # BoolExprLt
    | ineq_elem '>' ineq_elem # BoolExprGt
    | ineq_elem '<=' ineq_elem # BoolExprLe
    | ineq_elem '>=' ineq_elem # BoolExprGe
    ;
eq_elem
    : factor # EqElemFactor
    | 'null' # EqElemNull
    | 'true' # EqElemTrue
    | 'false' # EqElemFalse
    ;
ineq_elem
    : factor # IneqElemFactor
    ;
factor
    : literal # FactorLiteral
    | path_root # FactorPathRoot
    ;
literal
    : INT # LiteralInt
    | FLOAT # LiteralFloat
    | STRING # LiteralString
    ;
path_root
    : ID path # PathRootId
    ;
path
    : '.' ID path # PathId
    | '[' UINT ']' path # PathInd
    | # PathEmpty
    ;
ID
    : [a-zA-Z] [a-zA-Z0-9_\-]*
    ;
INT
    : UINT
    | '-' UINT
    ;
UINT
    : [1-9] [0-9]*
    | '0'
    ;
FLOAT
    : INT '.' [0-9]+ EXP
    ;
STRING
    : '"' CHAR* '"'
    ;
WS
    : [ \t\r\n]+ -> skip
    ;
fragment EXP
    : EXPSYM EXPSIGN UINT
    |
    ;
fragment EXPSYM
    : 'e'
    | 'E'
    ;
fragment EXPSIGN
    : '+'
    | '-'
    |
    ;
fragment CHAR
    : ESC
    | SAFECODEPOINT
    ;
fragment ESC
    : '\\' ["\\/bfnrt]
    | '\\' UNICODE
    ;
fragment UNICODE
    : 'u' HEX HEX HEX HEX
    ;
fragment HEX
    : [0-9a-fA-F]
    ;
fragment SAFECODEPOINT
    : ~["\\\u0000-\u001F]
    ;
