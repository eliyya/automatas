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
    Token ifToken;

    public IfStatement(Expression condition, BlockStatement body, Token ifToken) {
        this.condition = condition;
        this.body = body;
        this.ifToken = ifToken;
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
        this.condition.validateType(BlockStatement.BooleanType, parent);
        this.body.validate(parent);
        if (this.elseBody != null) {
            this.elseBody.validate(parent);
        }
    }

    @Override
    public void validate(BlockStatement parent, Type returnType) {
        this.condition.validateType(BlockStatement.BooleanType, parent);
        this.body.validate(parent.getVars(), parent.getFuncs(), returnType);
        if (this.elseBody != null) {
            this.elseBody.validate(parent.getVars(), parent.getFuncs(), returnType);
        }
    }

    @Override
    public Token token() {
        return this.ifToken;
    }
}
