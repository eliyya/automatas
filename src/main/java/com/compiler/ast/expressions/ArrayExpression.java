package com.compiler.ast.expressions;

import java.util.List;

import com.compiler.ast.Expression;

public class ArrayExpression implements Expression {
    String _c = "ArrayExpression";
    List<Expression> elements;

    public ArrayExpression(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
