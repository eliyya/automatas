package com.compiler.ast.statments;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;

public class ReturnStatment implements Statment {
    Expression expression;

    public ReturnStatment(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
