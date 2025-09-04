package com.tokens;

import java.util.HashSet;
import java.util.List;

public class Token {
    public static HashSet<String> reservadas = new HashSet<>(
            List.of(
                    "abstract", "assert", "break", "case", "catch", "class", "const", "continue", "default",
                    "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if", "implements", "import",
                    "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return",
                    "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
                    "try", "void", "volatile", "while", "true", "false", "null", "var", "yield", "record"));

    public static HashSet<String> tipos = new HashSet<>(
            List.of("int", "long", "float", "double", "boolean", "char", "byte", "short", "void"));

    public static HashSet<String> operadores = new HashSet<>(
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

    public static HashSet<String> separadores = new HashSet<>(
            List.of("(", ")", "{", "}", "[", "]", ";", ",", "."));

    private final String valor;
    private final String tipo;

    public Token(String valor, String tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }
}
