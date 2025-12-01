package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;

public class ExpressionStatement implements Statement {
    final String _c = "ExpressionStatement";
    Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        return expression + ";";
    }
    
    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }
}
