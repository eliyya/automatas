package com.compiler.ast.statments;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;

public class WhileStatment implements Statment {
    String _c = "WhileStatment";
    String column;
    Expression condition;
    BlockStatment body;

    public WhileStatment(Expression condition, BlockStatment body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
