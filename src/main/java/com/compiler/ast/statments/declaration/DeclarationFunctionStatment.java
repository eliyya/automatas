package com.compiler.ast.statments.declaration;

import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ParameterStatment;
import com.compiler.lexer.Token;

public class DeclarationFunctionStatment implements DeclarationStatment {
    String _class = "DeclarationFunctionStatment";
    Token type;
    Token name;
    List<ParameterStatment> parameters;
    BlockStatment body;

    public DeclarationFunctionStatment(Token type, Token name, List<ParameterStatment> parameters, BlockStatment body) {
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
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
