package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class ParameterStatement implements Statement {
    String _c = "ParameterStatment";
    Type type;
    Token name;

    public ParameterStatement(Type type, Token name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
