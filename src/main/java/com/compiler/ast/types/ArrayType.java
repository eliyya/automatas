package com.compiler.ast.types;

import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class ArrayType implements Type {
    String _c = "ArrayType";
    Type inner;

    public ArrayType(Type inner) {
        this.inner = inner;
    }

    @Override
    public Token token() {
        return this.inner.token();
    }

}
