package com.tokens;

public class Declaration {
    private Token type;
    private Expression value;

    public Declaration(Token type, Expression value) {
        this.type = type;
        this.value = value;
    }

    public Token getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type.getValue() + " " + value.toString();
    }

    public String toJSON() {
        return "{ \"type\" : \"" + type.getValue() + "\", \"value\" : " + value.toJSON() + " }";
    }
}
