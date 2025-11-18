package com.compiler.parser;

public enum BindingPower {
    DEFAULT_BP,
    COMMA,
    ASSIGNMENT,
    LOGICAL,
    RELATIONAL,
    ADDITIVE,
    MULTIPLICATIVE,
    UNARY,
    CALL,
    MEMBER,
    PRIMARY
}
