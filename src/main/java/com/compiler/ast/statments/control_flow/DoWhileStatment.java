package com.compiler.ast.statments.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;

public class DoWhileStatment implements ContolFlowStatment {
    String _c = "DoWhileStatment";
    BlockStatment body;
    Expression condition;

    public DoWhileStatment(BlockStatment body, Expression condition) {
        this.body = body;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "do " + body + " while (" + condition + ");";
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
