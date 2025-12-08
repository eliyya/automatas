package com.compiler.ast.types;

import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class ArrayType implements Type {
    Type inner;

    public ArrayType(Type inner) {
        this.inner = inner;
    }

    @Override
    public Token token() {
        return this.inner.token();
    }

    public Type inner() {
        return this.inner;
    }

    @Override
    public String toString() {
        return "ArrayList<" + this.inner.toString(true) + ">";
    }

    @Override
    public String toString(boolean generic) {
        return "ArrayList<" + this.inner.toString(generic) + ">";
    }

    

}
