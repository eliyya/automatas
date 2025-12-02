package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.utils.JsonIgnore;

public class ReturnStatement implements Statement {
    String _c = "ReturnStatement";
    @JsonIgnore
    Token returnToken;
    Expression expression;

    public ReturnStatement(Expression expression, Token returnToken) {
        this.expression = expression;
        this.returnToken = returnToken;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }
    
    public void validate(BlockStatement parent, Token returnType) {
        if (this.expression == null) {
            if (!returnType.value().equals("void")) {
                throw new InvalidTypeError(returnType, this.returnToken);
            }
            return;
        }
        this.expression.validateType(returnType, parent);
    }

    @Override
    public String toString() {
        return "return " + expression + ";";
    }
    
}
