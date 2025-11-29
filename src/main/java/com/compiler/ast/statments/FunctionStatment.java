package com.compiler.ast.statments;

import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class FunctionStatment implements Statment {
    String _class = "FunctionStatment";
    Token type;
    String name;
    BlockStatment body;

    public FunctionStatment(Token type, String name, BlockStatment body) {
        this.type = type;
        this.name = name;
        this.body = body;
    }

    @Override
    public String toString() {
        return type.value() + " " + name + "()" + body;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
