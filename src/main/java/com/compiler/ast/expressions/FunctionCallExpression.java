package com.compiler.ast.expressions;

import java.util.List;
import java.util.stream.Collectors;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public class FunctionCallExpression implements Expression {
    String _c = "FunctionCallExpression";
    Token name;
    List<Expression> parameters;

    public FunctionCallExpression(Token name, List<Expression> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return name.value() + "(" + parameters.stream().map(e -> e.toString()).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }

    @Override
    public Token getToken() {
        return this.name;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isBoolean'");
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isNumber'");
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        var funcs = parent.getFuncs(this.name.value());
        if (funcs == null) {
            return false;
        }
        for (var func : funcs) {
            var params = func.parameters();
            if (params.size() != this.parameters.size()) {
                continue;
            }
            for (int i = 0; i < params.size(); i++) {
                var param = this.parameters.get(i);
                var funcParam = params.get(i);
                param.validateType(funcParam.type().token(), parent);
            }
            return true;
        }
        return false;
    }
    
}
