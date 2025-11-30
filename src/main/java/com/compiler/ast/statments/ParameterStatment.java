package com.compiler.ast.statments;

import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class ParameterStatment implements Statment {
    String _c = "ParameterStatment";
    Token type;
    Token name;

    public ParameterStatment(Token type, Token name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type.value() + " " + name;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
