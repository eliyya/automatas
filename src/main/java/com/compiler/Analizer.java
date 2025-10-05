package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.compiler.nodes.Declaration;
import com.compiler.nodes.Expression;
import com.compiler.nodes.Instruction;
import com.compiler.nodes.Node;
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

    private void addToken(Token token, boolean semicolon) {
        tokens.add(token);
        if (semicolon) {
            tokens.add(new Token(";", TokenType.SEPARATOR, token.line()));
        }
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

    private static String normalizeLine(String line) {
        var nuevaLinea = "";
        var lit = false;
        for (String ch : line.split("")) {
            if (lit) {
                nuevaLinea += ch;
                int countBackslashes = 0;
                if (!nuevaLinea.endsWith("\"")) {
                    continue;
                }
                for (int i = nuevaLinea.length() - 2; i >= 0 && nuevaLinea.charAt(i) == '\\'; i--) {
                    countBackslashes++;
                }

                if (countBackslashes % 2 == 0) {
                    nuevaLinea += " ";
                    lit = false;
                }
                continue;
            }
            if (ch.equals("\"")) {
                lit = true;
                nuevaLinea += " \"";
                continue;
            }
            if (ch.equals(";")) {
                nuevaLinea += " ; ";
                continue;
            }
            if (ch.equals("(")) {
                nuevaLinea += " ( ";
                continue;
            }
            if (ch.equals(")")) {
                nuevaLinea += " ) ";
                continue;
            }
            if (ch.equals("{")) {
                nuevaLinea += " { ";
                continue;
            }
            if (ch.equals("}")) {
                nuevaLinea += " } ";
                continue;
            }
            if (ch.equals("[")) {
                nuevaLinea += " [ ";
                continue;
            }
            if (ch.equals("]")) {
                nuevaLinea += " ] ";
                continue;
            }
            if (ch.equals("<")) {
                nuevaLinea += " < ";
                continue;
            }
            if (ch.equals(">")) {
                nuevaLinea += " > ";
                continue;
            }
            if (ch.equals(".")) {
                nuevaLinea += " . ";
                continue;
            }
            if (ch.equals(",")) {
                nuevaLinea += " , ";
                continue;
            }
            if (ch.equals("!")) {
                nuevaLinea += " ! ";
                continue;
            }
            nuevaLinea += ch;
            if (nuevaLinea.endsWith("--")) {
                nuevaLinea = nuevaLinea.substring(0, nuevaLinea.length() - 2) + " -- ";
                continue;
            }
            if (nuevaLinea.endsWith("++")) {
                nuevaLinea = nuevaLinea.substring(0, nuevaLinea.length() - 2) + " ++ ";
                // continue;
            }
        }
        return nuevaLinea;
    }

    private void tokenizeLine(String line, boolean[] comentarioMultiLinea, int lineNum) {
        if (line.isEmpty()) {
            addToken(new Token("\n", TokenType.EOF, lineNum), false);
            return;
        }

        var literal = "";
        var semicolon = false;
        for (String token : line.trim().split("\\s+")) {
            if (token.trim().isEmpty()) {
                continue;
            }

            // validadas anteriormente
            if (comentarioMultiLinea[0]) {
                if (token.endsWith("*/")) {
                    comentarioMultiLinea[0] = false;
                }
                continue;
            }

            if (literal.length() > 0) {
                literal += " " + token;
                if (token.endsWith("\"")) {
                    addToken(new Token(literal, TokenType.LITERAL, lineNum), semicolon);
                    literal = "";
                }
                continue;
            }

            // validaciones
            if (token.startsWith("//")) {
                break;
            }

            if (token.startsWith("/*")) {
                comentarioMultiLinea[0] = true;
                break;
            }

            if (CompilerConstants.RESERVED.contains(token)) {
                addToken(new Token(token, TokenType.RESERVED, lineNum), semicolon);
                continue;
            }

            if (CompilerConstants.ASIGNATIONS.contains(token)) {
                addToken(new Token(token, TokenType.ASSIGNATION, lineNum), semicolon);
                continue;
            }

            if (CompilerConstants.OPERATORS.contains(token)) {
                addToken(new Token(token, TokenType.OPERATOR, lineNum), semicolon);
                continue;
            }

            if (CompilerConstants.TYPES.contains(token)) {
                addToken(new Token(token, TokenType.TYPE, lineNum), semicolon);
                continue;
            }

            if (CompilerConstants.SEPARATORS.contains(token)) {
                addToken(new Token(token, TokenType.SEPARATOR, lineNum), semicolon);
                continue;
            }

            if (token.startsWith("\"")) {
                literal = "\"";
                var ntoken = token.substring(1);
                if (ntoken.endsWith("\"")) {
                    addToken(new Token(literal + ntoken, TokenType.LITERAL, lineNum), semicolon);
                    literal = "";
                } else {
                    literal += ntoken;
                }
                continue;
            }

            if (token.equals(".")) {
                addToken(new Token(token, TokenType.SEPARATOR, lineNum), semicolon);
                continue;
            }

            if (token.matches("[0-9]+\\.?[0.9]*")) {
                addToken(new Token(token, TokenType.LITERAL, lineNum), semicolon);
                continue;
            }

            if (token.matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
                addToken(new Token(token, TokenType.IDENTIFICATOR, lineNum), semicolon);
                continue;
            }

            addToken(new Token(token, TokenType.UNKNOWN, lineNum), semicolon);

        }

        tokens.add(new Token("\n", TokenType.EOF, lineNum));
    }

    public ArrayList<Token> tokenize() throws FileNotFoundException {
        try (var scanner = new Scanner(file)) {
            boolean[] comentarioMultiLinea = {false};
            int lineNum = 1;
            while (scanner.hasNext()) {
                var linea = normalizeLine(scanner.nextLine().trim());
                tokenizeLine(linea, comentarioMultiLinea, lineNum);
                lineNum++;
            }
        }
        return tokens;
    }

    public static Expression parseExpression(ArrayList<Token> tokens, float minBp) {
        Expression lhs = null;
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
            if (!(ft.type() == TokenType.LITERAL || ft.type() == TokenType.IDENTIFICATOR)) {
                throw new SyntaxError(TokenType.LITERAL.name(), ft.type().name(), ft.line());
            }
        }
        if (lhs == null) {
            lhs = new Expression(ft);
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
            lhs = new Expression(op, lhs, rhs);
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
            var instruction = parseInstruction();
            lines.add(instruction);
        }
        return lines;
    }

    private Instruction parseInstruction() {
        Node lhs;
        if (tokens.get(0).type() == TokenType.TYPE || tokens.get(0).type() == TokenType.IDENTIFICATOR) {
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
            return new Instruction(lhs, tokens.remove(0));
        } else {
            return new Instruction(lhs, parseInstruction());
        }
    }

    private Declaration parseDeclaration(ArrayList<Token> tokens) {
        var type = tokens.remove(0);
        if (!(type.type() == TokenType.TYPE || type.type() == TokenType.IDENTIFICATOR)) {
            throw new SyntaxError(TokenType.TYPE.name(), type.type().name(), type.line());
        }
        var expression = parseExpression(tokens, 0);
        return new Declaration(type, expression);
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
