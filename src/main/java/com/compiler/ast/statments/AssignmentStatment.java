package com.compiler.ast.statments;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class AssignmentStatment implements Statment {
    final String _c = "AssignmentStatment";
    Token identifier;
    Token operator;
    Expression expression;

    public AssignmentStatment(Token identifier, Token operator, Expression expression) {
        this.identifier = identifier;
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
