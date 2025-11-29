package com.compiler.ast.expressions;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.lexer.Token;

public class FunctionCallExpression implements Expression {
    String _class = "FunctionCallExpression";
    Token name;
    List<Expression> parameters;

    public FunctionCallExpression(Token name, List<Expression> parameters) {
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
