package com.compiler.ast.types;

import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class SingleType implements Type {
    String _c = "SingleType";
    Token type;

    public SingleType(Token type) {
        this.type = type;
    }

    @Override
    public void type() {
        throw new UnsupportedOperationException("Unimplemented method 'type'");
    }
    
}
