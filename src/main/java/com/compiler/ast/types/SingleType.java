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

    @Override
    public String toString(boolean generic) {
        if (!generic) return this.toString();
        return switch (this.type.kind()) {
            case STRING -> "String";
            case CHAR -> "Character";
            case INT -> "Integer";
            case FLOAT -> "Float";
            case BOOLEAN -> "Boolean";
            case LONG -> "Long";
            case SHORT -> "Short";
            case BYTE -> "Byte";
            case DOUBLE -> "Double";
            case OBJECT -> "Object";
            default -> this.toString();
        };
    }
    
}
