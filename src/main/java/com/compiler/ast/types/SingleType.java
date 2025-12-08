package com.compiler.ast.types;

import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class SingleType implements Type {
    Token type;

    public SingleType(Token type) {
        this.type = type;
    }

    @Override
    public Token token() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.value();
    }
    
}
