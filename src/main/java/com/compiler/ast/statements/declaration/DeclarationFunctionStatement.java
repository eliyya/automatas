package com.compiler.ast.statements.declaration;

import java.util.List;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.DeclarationStatement;
import com.compiler.ast.statements.ParameterStatement;
import com.compiler.lexer.Token;
import com.compiler.ast.Type;
import com.compiler.errors.DuplicateError;

public class DeclarationFunctionStatement implements DeclarationStatement {
    String _c = "DeclarationFunctionStatement";
    Type type;
    Token name;
    List<ParameterStatement> parameters;
    BlockStatement body;

    public DeclarationFunctionStatement(Type type, Token name, List<ParameterStatement> parameters, BlockStatement body) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public List<ParameterStatement> parameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return type.toString() + " " + name + "(" + parameters + ")" + body;
    }

    @Override
    public void validate(BlockStatement parent) {
        var dec = parent.getFuncs(this.name.value());
        if (dec == null) {
            parent.addFunc(this.name.value(), this);
            body.validate(parent.getVars(), parent.getFuncs(), this.type);
            return;
        }
        // check if exist
        var exists = false;
        for (var elem : dec) {
            if (elem.parameters.size() != this.parameters.size()) continue;
            exists = elem.parameters.equals(this.parameters);
            if (exists) break;
        }
        if (!exists) {
            parent.addFunc(this.name.value(), this);
            body.validate(parent.getVars(), parent.getFuncs());
            return;
        }
        throw new DuplicateError(this.name);
    }

    @Override
    public Token token() {
        return this.type.token();
    }
    
}
