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
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }

    @Override
    public String toString() {
        if (expression == null) {
            return type.value() + " " + identifier.value() + ";";
        }
        return type.value() + " " + identifier.value() + " = " + expression + ";";
    }
    
}
