package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.lexer.Token;

public class ParameterStatement implements Statement {
    String _c = "ParameterStatment";
    Token type;
    Token name;

    public ParameterStatement(Token type, Token name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type.value() + " " + name;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
