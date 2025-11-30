package com.compiler.ast.types;

import com.compiler.ast.Type;

public class ArrayType implements Type {
    String _c = "ArrayType";
    Type inner;

    public ArrayType(Type inner) {
        this.inner = inner;
    }

    @Override
    public void type() {
        throw new UnsupportedOperationException("Unimplemented method 'type'");
    }

}
