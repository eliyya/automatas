package com.compiler.ast.statments.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;

public class WhileStatment implements ContolFlowStatment {
    String _c = "WhileStatment";
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
