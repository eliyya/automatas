package com.tokens;

public class SyntaxError extends RuntimeException {
    private final String message;
    private final int line;
    
    public SyntaxError(String spected, String found, int line) {
        this.message = "Expected " + spected + " but found " + found + " on line " + line;
        this.line = line;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }
}
