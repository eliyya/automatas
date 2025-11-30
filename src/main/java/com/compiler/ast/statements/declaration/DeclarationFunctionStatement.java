package com.compiler.ast.statements.declaration;

import java.util.List;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.DeclarationStatement;
import com.compiler.ast.statements.ParameterStatement;
import com.compiler.lexer.Token;

public class DeclarationFunctionStatement implements DeclarationStatement {
    String _c = "DeclarationFunctionStatment";
    Token type;
    Token name;
    List<ParameterStatement> parameters;
    BlockStatement body;

    public DeclarationFunctionStatement(Token type, Token name, List<ParameterStatement> parameters, BlockStatement body) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        return type.value() + " " + name + "(" + parameters + ")" + body;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
