package com.tokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Compiler {

    private ArrayList<Token> tokens;
    private final ArrayList<Node> lines = new ArrayList<>();
    private final File file;

    Compiler(File file) {
        this.file = file;
    }

    public void compile() throws FileNotFoundException {
        // ! tokenize file
        this.tokens = Analizer.tokenizeFile(file);
        writeTokensFile(this.tokens);

        System.out.print(" Analizando asignacion: ");
        for (var token : this.tokens) {
            System.out.print(token.getValue() + " ");
        }

        // ! parse asignation
        while (!tokens.isEmpty()) {
            var instruction = parseInstruction();
            lines.add(instruction);
        }

        writeAsignationFile();
        writeAsignationTreeFile();
    }

    private Instruction parseInstruction() {
        Node lhs;
        if (tokens.get(0).getType() == TokenType.TIPO || tokens.get(0).getType() == TokenType.IDENTIFICADOR) {
            lhs = Analizer.parseDeclaration(tokens);
        } else {
            lhs = Analizer.parseExpression(tokens);
        }
        var semi = tokens.get(0);
        if (semi.getValue().equals(";")) {
            tokens.remove(0);
        } else {
            throw new RuntimeException("Expected ;");
        }
        var rhst = tokens.get(0);
        if (rhst.getType() == TokenType.EOF) {
            return new Instruction(lhs, tokens.remove(0));
        } else {
            return new Instruction(lhs, parseInstruction());
        }
    }

    private void writeTokensFile(ArrayList<Token> tokens) {
        System.out.println("Tokens encontrados: " + tokens.size());
        System.out.println();
        try (var log = new FileWriter("tokens.log")) {
            for (var token : tokens) {
                log.write("Tipo: "
                        + token.getType().name()
                        + " ".repeat(15 - token.getType().name().length()) + "Valor: "
                        + token.getValue() + "\n");
            }
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
        }
    }

    private void writeAsignationFile() {
        // System.out.println("Expresion encontrada: " + expression.toString());
        try (var log = new FileWriter("asignation.json")) {
            var arr = new ArrayList<HashMap<String, Object>>();
            for (var line : lines) {
                arr.add(line.toHashMap());
            }
            log.write(new GsonBuilder().setPrettyPrinting().create().toJson(arr));
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
        }
    }

    private void writeAsignationTreeFile() {
        try (FileWriter writer = new FileWriter("tree.json")) {
            var arr = new ArrayList<HashMap<String, Object>>();
            for (var line : lines) {
                arr.add(line.toHashMap());
            }
            writer.write(
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(arr)
                            .replaceAll("\"op\" : (\".*\")", "\"\" : $1")
                            .replaceAll("(\"\\w+\") : (\".+\")", "$1 : { \"\" : $2 }")
            );
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo: " + e.getMessage());
        }
    }
}
