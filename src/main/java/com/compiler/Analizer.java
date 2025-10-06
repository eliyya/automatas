package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.compiler.utils.BindingPower;
import com.compiler.utils.CompilerConstants;
import com.compiler.utils.SyntaxError;
import com.compiler.utils.TokenType;
import com.google.gson.GsonBuilder;

public final class Analizer {

    private final ArrayList<Token> tokens = new ArrayList<>();
    private final File file;
    private final ArrayList<Node> lines = new ArrayList<>();

    public Analizer(File file) throws FileNotFoundException {
        this.file = file;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    void writeTokensFile() {
        try (var log = new FileWriter("tokens.log")) {
            for (var token : this.tokens) {
                log.write("Tipo: "
                        + token.type().name()
                        + " ".repeat(15 - token.type().name().length()) + "Valor: "
                        + token.value() + "\n");
            }
            System.out.println("Tokens guardados en tokens.log");
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
        }
    }

    public void tokenizeLine(String input, int lineNum) {
        StringBuilder buffer = new StringBuilder();

        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                flushBuffer(buffer, lineNum);
                continue;
            }

            if (c == '"') {
                flushBuffer(buffer, lineNum);
                StringBuilder str = new StringBuilder();
                str.append(c);
                i++;

                while (i < length) {
                    char next = input.charAt(i);
                    str.append(next);
                    if (next == '\\') {
                        // Escapar siguiente caracter (p. ej. \")
                        if (i + 1 < length) {
                            i++;
                            str.append(input.charAt(i));
                        }
                    } else if (next == '"') {
                        break;
                    }
                    i++;
                }
                flushBuffer(str, lineNum);
                continue;
            }

            if (c == '/' && input.charAt(i + 1) == '/' && buffer.toString().isEmpty()) {
                return;
            }

            if (i + 1 < length) {
                String two = "" + c + input.charAt(i + 1);
                if (two.matches("==|!=|<=|>=|\\+\\+|--|\\+=|-=|\\*=|/=")) {
                    flushBuffer(buffer, lineNum);
                    flushBuffer(new StringBuilder(two), lineNum);
                    i++;
                    continue;
                }
            }

            if ("=+-*/;(),{}".indexOf(c) >= 0) {
                flushBuffer(buffer, lineNum);
                buffer.append(c);
                flushBuffer(buffer, lineNum);
                continue;
            }

            buffer.append(c);
        }

        flushBuffer(buffer, lineNum);
        tokens.add(new Token(CompilerConstants.EOF, TokenType.EOF, lineNum));
    }

    private void flushBuffer(StringBuilder buffer, int lineNum) {
        var text = buffer.toString();
        buffer.setLength(0);
        if (text.length() <= 0) {
            return;
        }
        if (CompilerConstants.RESERVED.contains(text)) {
            tokens.add(new Token(text, TokenType.RESERVED, lineNum));
            return;
        }

        if (CompilerConstants.ASIGNATIONS.contains(text)) {
            tokens.add(new Token(text, TokenType.ASSIGNATION, lineNum));
            return;
        }

        if (CompilerConstants.OPERATORS.contains(text)) {
            tokens.add(new Token(text, TokenType.OPERATOR, lineNum));
            return;
        }

        if (CompilerConstants.TYPES.contains(text)) {
            tokens.add(new Token(text, TokenType.TYPE, lineNum));
            return;
        }

        if (CompilerConstants.SEPARATORS.contains(text)) {
            tokens.add(new Token(text, TokenType.SEPARATOR, lineNum));
            return;
        }

        if (text.equals(".")) {
            tokens.add(new Token(text, TokenType.SEPARATOR, lineNum));
            return;
        }

        if (text.matches("\\d+(?:\\.\\d+)?|")) {
            tokens.add(new Token(text, TokenType.LITERAL, lineNum));
            return;
        }

        if (text.matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
            tokens.add(new Token(text, TokenType.IDENTIFICATOR, lineNum));
            return;
        }

        if (text.matches("^\".*\"$")) {
            tokens.add(new Token(text, TokenType.LITERAL, lineNum));
            return;
        }

        tokens.add(new Token(text, TokenType.UNKNOWN, lineNum));
        System.out.println("Unknown token: " + text);
    }

    public ArrayList<Token> tokenize() throws FileNotFoundException {
        try (var scanner = new Scanner(file)) {
            int lineNum = 1;
            while (scanner.hasNext()) {
                tokenizeLine(scanner.nextLine().trim(), lineNum++);
            }
        }
        return tokens;
    }

    public static Node parseExpression(ArrayList<Token> tokens, float minBp) {
        Node lhs = null;
        // left hand side
        var ft = tokens.remove(0);
        if (ft == null) {
            throw new RuntimeException("Empty list");
        }
        if (ft.value().equals("(")) {
            lhs = parseExpression(tokens, 0);
            if (!tokens.remove(0).value().equals(")")) {
                throw new SyntaxError(")", tokens.remove(0).value(), ft.line());
            }
        } else {
            if (!(ft.type() == TokenType.LITERAL || ft.value().equals("true") || ft.value().equals("false") || ft.type() == TokenType.IDENTIFICATOR || ft.type() == TokenType.OPERATOR || ft.type() == TokenType.ASSIGNATION)) {
                throw new SyntaxError(TokenType.LITERAL.name(), ft.type().name(), ft.line());
            }
        }
        if (lhs == null) {
            lhs = new Node(ft);
        }
        while (true) {
            // operator
            if (tokens.isEmpty()) {
                break;
            }
            var op = tokens.get(0);
            if (op.type() == TokenType.EOF || op.value().equals(";") || op.value().equals(")")) {
                break;
            }
            if (!(op.type() == TokenType.OPERATOR || op.value().equals(",") || CompilerConstants.ASIGNATIONS.contains(op.value()))) {
                throw new SyntaxError(TokenType.OPERATOR.name(), op.value(), op.line());
            }
            // right hand side
            var bp = getBindingPower(op.value());
            if (bp.left() < minBp) {
                break;
            }
            tokens.remove(0);
            var rhs = parseExpression(tokens, bp.right());
            lhs = new Node(op, lhs, rhs);
        }
        return lhs;
    }

    static BindingPower getBindingPower(String op) {
        var normalized = normalizeOperator(op);
        return switch (normalized) {
            case "," ->
                new BindingPower(1.0f, 1.1f);
            case "op" ->
                new BindingPower(2.0f, 2.1f);
            case "+" ->
                new BindingPower(3.0f, 3.1f);
            case "-" ->
                new BindingPower(3.0f, 2.1f);
            case "*" ->
                new BindingPower(4.0f, 4.1f);
            case "/" ->
                new BindingPower(4.0f, 4.1f);
            default ->
                new BindingPower(0, 0);
        };
    }

    static String normalizeOperator(String op) {
        if (CompilerConstants.ASIGNATIONS.contains(op)) {
            return "op";
        }
        return op;
    }

    public ArrayList<Node> analize() {
        while (!tokens.isEmpty()) {
            if (tokens.get(0).type() == TokenType.EOF) {
                lines.add(new Node(tokens.remove(0)));
            }
            lines.add(parseInstruction());
        }
        return lines;
    }

    private Node parseInstruction() {
        Node lhs;
        if (tokens.get(0).type() == TokenType.TYPE) {
            lhs = parseDeclaration(tokens);
        } else {
            lhs = parseExpression(tokens, 0);
        }
        var semi = tokens.get(0);
        if (semi.value().equals(";")) {
            tokens.remove(0);
        } else {
            throw new SyntaxError(";", semi.value(), semi.line());
        }
        var rhst = tokens.get(0);
        if (rhst.type() == TokenType.EOF) {
            return new Node(semi, lhs, new Node(tokens.remove(0)));
        } else {
            return new Node(semi, lhs, parseInstruction());
        }
    }

    private Node parseDeclaration(ArrayList<Token> tokens) {
        var type = tokens.remove(0);
        if (!(type.type() == TokenType.TYPE)) {
            throw new SyntaxError(TokenType.TYPE.name(), type.type().name(), type.line());
        }
        var expression = parseExpression(tokens, 0);
        return new Node(new Token(CompilerConstants.DECLARATION, TokenType.DECLARATION, type.line()), new Node(type), expression);
    }

    public void writeAsignationFile() {
        try (var log = new FileWriter("analized.json")) {
            var arr = new ArrayList<HashMap<String, Object>>();
            for (var line : lines) {
                arr.add(line.toHashMap());
            }
            log.write(
                    new GsonBuilder()
                            .disableHtmlEscaping()
                            .setPrettyPrinting()
                            .create()
                            .toJson(arr)
            );
            System.out.println("Analisis guardado en analized.json");
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
        }
    }

    public void writeAsignationTreeFile() {
        try (FileWriter writer = new FileWriter("tree.json")) {
            var arr = new ArrayList<HashMap<String, Object>>();
            for (var line : lines) {
                arr.add(line.toHashMap());
            }
            writer.write(
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .disableHtmlEscaping()
                            .create()
                            .toJson(arr)
                            .replaceAll("\"op\": (\".*\")", "\"\" : $1")
                            .replaceAll("(\"\\w+\"): (\".+\")", "$1 : { \"\" : $2 }")
            );
            System.out.println("Arbol guardado en tree.json");
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo: " + e.getMessage());
        }
    }
}
