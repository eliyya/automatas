package com.compiler.ast.statments;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class DeclarationStatment implements Statment {
    final String _c = "DeclarationStatment";
    Token type;
    List<Expression> identifiers;

    public DeclarationStatment(Token type, List<Expression> identifiers) {
        this.type = type;
        this.identifiers = identifiers;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }

    @Override
    public String toString() {
        return "";
        // if (expression == null) {
        //     return type.value() + " " + identifier.value() + ";";
        // }
        // return type.value() + " " + identifier.value() + " = " + expression + ";";
    }
    
}
