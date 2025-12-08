package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.utils.JsonIgnore;

public class ReturnStatement implements Statement {
    @JsonIgnore
    Token returnToken;
    Expression expression;

    public ReturnStatement(Expression expression, Token returnToken) {
        this.expression = expression;
        this.returnToken = returnToken;
    }

    @Override
    public void validate(BlockStatement parent) {
        if (this.expression == null) {
            if (!parent.returnType.token().value().equals("void")) {
                throw new InvalidTypeError(parent.returnType.token(), this.returnToken);
            }
            return;
        }
        this.expression.validateType(parent.returnType, parent);
    }

    @Override
    public String toString() {
        if (this.expression != null) {
            return "return " + expression + ";";
        } else {
            return "return;";
        }
    }
    
    @Override
    public Token token() {
        return returnToken;
    }
}
