package com.compiler.ast.statments.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;

public class DoStatment implements ContolFlowStatment {
    BlockStatment body;
    Expression condition;

    public DoStatment(BlockStatment body, Expression condition) {
        this.body = body;
        this.condition = condition;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
