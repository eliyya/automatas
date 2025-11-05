package com.compiler;

import java.util.ArrayList;
import java.util.HashMap;

import com.compiler.utils.TokenType;

public class Analizer {


    private final Tokenizer tokenizer;
    private final HashMap<String, String> types = new HashMap<>();

    public Analizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void analize() {
        ArrayList<Token> tokens = (ArrayList<Token>) new ArrayList<Token>(tokenizer.tokens);
        while (!tokens.isEmpty()) {
            var line = nextLine(tokens);
            while (!line.isEmpty()) {
                if (line.get(0).type() == TokenType.EOF) {
                    break;
                }
                analizeFragment(line);
            }
        }
    }

    private void analizeFragment(ArrayList<Token> line) {
        var firstToken = line.get(0);
        if (Tokenizer.TYPES.contains(firstToken.value())) {
            analizeDeclaration(line, line.remove(0).value());
        }else {
            line.remove(0);
        }
    }

    private ArrayList<Token> nextLine(ArrayList<Token> tokens) {
        var line = new ArrayList<Token>();
        while (peekToken(tokens).type() != TokenType.EOF) {
            line.add(nextToken(tokens));
        }
        line.add(nextToken(tokens));
        return line;
    }

    private Token nextToken(ArrayList<Token> tokens) {
        return tokens.remove(0);
    }

    private Token peekToken(ArrayList<Token> tokens) {
        return tokens.get(0);
    }

    private void analizeDeclaration(ArrayList<Token> line, String type) {
        var name = line.remove(0);
        if (name.type() != TokenType.IDENTIFICATOR) {
            throw new SyntaxError("identifier", name);
        }
        var nt = line.get(0);
        if (nt.value().equals(",")) {
            line.remove(0);
            analizeDeclaration(line, type);
        } else if (nt.value().equals(";")) {
            line.remove(0);
        } else if (nt.value().equals("=")) {
            line.remove(0);
            var value = line.remove(0);
            if (!type.equals(getValueType(value))) {
                throw new SyntaxError(type, value);
            }
        }
    }

    private String getValueType(Token token) {
        if (token.value().matches("^-?\\d+$")) {
            return "int";
        } 
        if (token.value().matches("^\".*\"$")) {
            return "String";
        }
        if (token.value().matches("^\\'.*\\'$")) {
            return "char";
        }
        if (token.value().matches("^true|false$")) {
            return "boolean";
        }
        if (token.value().matches("^-?\\d+\\.\\d+[fF]$")) {
            return "float";
        }
        if (token.value().matches("^-?\\d+\\.\\d+[dD]$")) {
            return "double";
        }
        return "";
    }
}

class SyntaxError extends RuntimeException {
    public SyntaxError(String expected, Token token) {
        var message = "Expected " + expected + ", found \"" + token.value() + "\" at line " + token.line() + " column " + token.column();
        System.out.println(token.original());
        System.out.println(" ".repeat(token.column()) + "^");
        System.out.print(" ".repeat(token.column()));
        super(message);
    }
}