package com.compiler.ast.statments;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;

public class ExpressionStatment implements Statment {
    final String _c = "ExpressionStatment";
    Expression expression;

    public ExpressionStatment(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
}
