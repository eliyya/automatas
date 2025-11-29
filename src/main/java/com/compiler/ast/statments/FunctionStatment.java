package com.compiler.ast.statments;

import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class FunctionStatment implements Statment {
    String _class = "FunctionStatment";
    Token type;
    Token name;
    List<ParameterStatment> parameters;
    BlockStatment body;

    public FunctionStatment(Token type, Token name, List<ParameterStatment> parameters, BlockStatment body) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        return type.value() + " " + name + "(" + parameters + ")" + body;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
