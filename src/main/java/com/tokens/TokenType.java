package com.tokens;

public enum TokenType {
    IDENTIFICADOR,
    DESCONOCIDO,
    RESERVADA,
    SEPARADOR,
    OPERADOR,
    LITERAL,
    TIPO,
    EOF;

    public static String getTokenType(TokenType tokenType) {
        return tokenType.name();
    }
}
