package com.compiler.ast.types;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayType)) return false;
        ArrayType other = (ArrayType) o;
        return Objects.equals(this.inner, other.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.inner);
    }

}
