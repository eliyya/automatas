package com.compiler.ast.statments.declaration;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.lexer.Token;

public class DeclarationVariableStatment implements DeclarationStatment {
    final String _c = "DeclarationVariableStatment";
    Token type;
    List<Expression> identifiers;

    public DeclarationVariableStatment(Token type, List<Expression> identifiers) {
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
