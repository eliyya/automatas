package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.lexer.Token;

public class IfStatement implements ContolFlowStatement {
    String _c = "IfStatement";
    Expression condition;
    BlockStatement body;
    BlockStatement elseBody;

    public IfStatement(Expression condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    public IfStatement(Expression condition, BlockStatement body, BlockStatement elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public String toString() {
        if (elseBody == null) {
            return "if (" + condition + ") " + body;
        }
        return "if (" + condition + ") " + body + " else " + elseBody;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate(BlockStatement parent, Type returnType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }
    
}
