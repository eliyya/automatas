package com.compiler.ast.statements;

import java.util.Objects;

import com.compiler.ast.Statement;
import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class ParameterStatement implements Statement {
    String _c = "ParameterStatement";
    Type type;
    Token name;

    public ParameterStatement(Type type, Token name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }

    public Type type() {
        return type;
    }

    public Token name() {
        return name;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterStatement)) return false;
        ParameterStatement other = (ParameterStatement) o;
        return Objects.equals(type.token().value(), other.type.token().value());
    }
    
}
