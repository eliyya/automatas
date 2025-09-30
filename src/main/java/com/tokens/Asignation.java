package com.tokens;

public class Asignation {
    private Token operator;
    private Token name;
    private Expression value;

    public Asignation(Token operator, Token name, Expression value) {
        this.operator = operator;
        this.name = name;
        this.value = value;
    }

    public Token getOperator() {
        return operator;
    }

    public Token getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name.getValue() + " " + operator.getValue() + " " + value.toString();
    }

    public String toJSON(int indent) {
        return "{\n" + "    ".repeat(indent) + "\"op\" : \"" + operator.getValue() 
        + ("\",\n" + "    ".repeat(indent) + "\"name\" : \"" + name.getValue())
        + ("\",\n" + "    ".repeat(indent) + "\"value\": " + value.toJSON(indent + 1))
        + "\n" + "    ".repeat(indent - 1) + "}";
    }

    public String toJSON() {
        return toJSON(1);
    }
}
