package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;

public class ReturnStatement implements Statement {
    Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }
    
}
