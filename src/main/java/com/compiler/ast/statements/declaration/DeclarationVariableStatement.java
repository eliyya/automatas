package com.compiler.ast.statements.declaration;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.DeclarationStatement;
import com.compiler.lexer.Token;

public class DeclarationVariableStatement implements DeclarationStatement {
    final String _c = "DeclarationVariableStatment";
    Token type;
    List<Expression> identifiers;

    public DeclarationVariableStatement(Token type, List<Expression> identifiers) {
        this.type = type;
        this.identifiers = identifiers;
    }

    @Override
    public void statement() {
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
