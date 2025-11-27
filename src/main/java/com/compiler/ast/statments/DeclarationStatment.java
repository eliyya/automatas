package com.compiler.ast.statments;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class DeclarationStatment implements Statment {
    final String _c = "DeclarationStatment";
    Token type;
    Token identifier;
    Expression expression;

    public DeclarationStatment(Token type, Token identifier, Expression expression) {
        this.type = type;
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public void statment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }

    @Override
    public String toString() {
        return "DeclarationStatment [type=" + type.value() + ", identifier=" + identifier.value() + ", expression=" + expression + "]";
    }
    
}
