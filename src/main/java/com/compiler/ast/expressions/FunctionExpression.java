package com.compiler.ast.expressions;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.lexer.Token;

public class FunctionExpression implements Expression {
    String _class = "FunctionExpression";
    Token name;
    List<Expression> parameters;

    public FunctionExpression(Token name, List<Expression> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return name.value() + "(" + parameters + ")";
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
