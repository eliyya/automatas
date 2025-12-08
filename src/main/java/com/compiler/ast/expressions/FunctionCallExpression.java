package com.compiler.ast.expressions;

import java.util.List;
import java.util.stream.Collectors;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.declaration.DeclarationFunctionStatement;
import com.compiler.errors.UnresolvedError;
import com.compiler.ast.Type;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class FunctionCallExpression implements Expression {
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
    public void validateType(Type type, BlockStatement parent) {
        if (type == BlockStatement.ObjectType) {
            return;
        }
        if (this.getType(parent).equals(type)) {
            return;
        }
        throw new InvalidTypeError(type.token().value(), this.token()); 
    }

    @Override
    public Token getIdentifier() {
        return this.name;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isBoolean'");
    }

    Type getType(BlockStatement parent) {
        var funcs = parent.getFuncs(this.name.value());
        if (funcs == null) {
            throw new UnresolvedError(this.name);
        }
        DeclarationFunctionStatement coin = null;
        for (var fn : funcs) {
            if (this.parameters.size() != fn.parameters().size()) continue;
            var isfn = false;
            for (var i = 0; i < fn.parameters().size(); i++) {
                var p = fn.parameters().get(i);
                try {
                    this.parameters.get(i).validateType(p.type(), parent);
                    isfn = true;
                } catch (Exception e) {
                    isfn = false;
                }
            }
            if (isfn) {
                coin = fn;
                break;
            }
        }
        if (coin == null) {
            throw new UnresolvedError(this.name);
        }
        return coin.type();
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        var k = getType(parent).token().kind();
        var isNumber = k == TokenKind.NUMBER_EXPRESSION 
        || k == TokenKind.INT 
        || k == TokenKind.FLOAT
        || k == TokenKind.DOUBLE
        || k == TokenKind.LONG
        || k == TokenKind.BYTE
        || k == TokenKind.SHORT;
        return isNumber;
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
                param.validateType(funcParam.type(), parent);
            }
            return true;
        }
        return false;
    }

    @Override
    public Token token() {
        return this.name;
    }
    
}
