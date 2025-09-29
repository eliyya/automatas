package com.tokens;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TokenType {
    public static final String IDENTIFICADOR = "IDENTIFICADOR";
    public static final String DESCONOCIDO = "DESCONOCIDO";
    public static final String RESERVADA = "RESERVADA";
    public static final String SEPARADOR = "SEPARADOR";
    public static final String OPERADOR = "OPERADOR";
    public static final String LITERAL = "LITERAL";
    public static final String TIPO = "TIPO";
    public static final String EOF = "EOF";

    public static final Set<String> valores = new HashSet<>(Arrays.asList(
        IDENTIFICADOR,
        DESCONOCIDO,
        RESERVADA,
        SEPARADOR,
        OPERADOR,
        LITERAL,
        TIPO,
        EOF
    ));
}
