package com.compiler;

import java.util.HashSet;
import java.util.List;

public class Token {

    public static HashSet<String> reserved = new HashSet<>(
            List.of(
                    "abstract", "assert", "break", "case", "catch", "class", "const", "continue", "default",
                    "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if", "implements", "import",
                    "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return",
                    "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
                    "try", "void", "volatile", "while", "true", "false", "null", "var", "yield", "record"));

    public static HashSet<String> types = new HashSet<>(
            List.of("int", "long", "float", "double", "boolean", "char", "byte", "short", "void"));

    public static HashSet<String> operators = new HashSet<>(
            List.of(
                    // ternarios
                    "?", ":",
                    // logicos ternarios
                    "&&", "||", "!",
                    // aritmeticos
                    "+", "-", "*", "/", "%", "++", "--",
                    // relacionales
                    "==", "!=", ">", "<", ">=", "<=",
                    // bitwise
                    "&", "|", "^", "~", "<<", ">>",
                    // asignacion
                    "=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>="));
    
    public static HashSet<String> asignations = new HashSet<>(
            List.of("=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>="));

    public static HashSet<String> separators = new HashSet<>(
            List.of("(", ")", "{", "}", "[", "]", ";", ",", "."));

    private final String value;
    private final TokenType type;
    private final int line;

    public Token(String value, TokenType type, int line) {
        this.value = value;
        this.type = type;
        this.line = line;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }
}
