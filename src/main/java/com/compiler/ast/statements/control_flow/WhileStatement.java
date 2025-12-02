package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class WhileStatement implements ContolFlowStatement {
    String _c = "WhileStatement";
    Expression condition;
    BlockStatement body;

    public WhileStatement(Expression condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "while (" + condition + ") " + body;
    }

    @Override
    public void validate(BlockStatement parent) {
        this.condition.validateType(new Token(TokenKind.BOOLEAN, "boolean", this.condition.getToken().line(),
                this.condition.getToken().column(), this.condition.getToken().textLine()), parent);
        if (!this.condition.isBoolean(parent)) {
            throw new InvalidTypeError("boolean", this.condition.getToken());
        }
    }

    @Override
    public void validate(BlockStatement parent, Token returnType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }

}
