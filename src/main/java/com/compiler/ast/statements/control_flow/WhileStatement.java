package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.ast.types.SingleType;
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
        this.condition.validateType(new SingleType(new Token(TokenKind.BOOLEAN, "boolean", this.condition.getIdentifier().line(),
                this.condition.getIdentifier().column(), this.condition.getIdentifier().textLine())), parent);
        if (!this.condition.isBoolean(parent)) {
            throw new InvalidTypeError("boolean", this.condition.getIdentifier());
        }
    }

    @Override
    public void validate(BlockStatement parent, Type returnType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }

}
