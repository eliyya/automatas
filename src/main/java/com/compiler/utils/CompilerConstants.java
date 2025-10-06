package com.compiler.utils;

import java.util.Set;

public final class CompilerConstants {
    public static final Set<String> RESERVED = Set.of(
        "abstract", "assert", "break", "case", "catch", "class", "const", "continue", "default",
        "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if", "implements", "import",
        "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return",
        "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
        "try", "void", "volatile", "while", "true", "false", "null", "var", "yield", "record"
    );

    public static final Set<String> TYPES = Set.of(
        "int", "long", "float", "double", "boolean", "char", "byte", "short", "void"
    );

    public static final Set<String> OPERATORS = Set.of(
        "?", ":", "&&", "||", "!", "+", "-", "*", "/", "%", "++", "--",
        "==", "!=", ">", "<", ">=", "<=", "&", "|", "^", "~", "<<", ">>",
        "=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>="
    );

    public static final Set<String> ARITHMETIC = Set.of(
        "+", "-", "*", "/", "%"
    );

    public static final Set<String> ASIGNATIONS = Set.of(
        "=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>="
    );

    public static final Set<String> SEPARATORS = Set.of(
        "(", ")", "{", "}", "[", "]", ";", ",", "."
    );

    public static final String EOF = "{EOF}";
    public static final String DECLARATION = "{Declaration}";

    private CompilerConstants() { }
}

