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

        // ! parse instructions
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
        try (var log = new FileWriter("tokens.log")) {
            for (var token : tokens) {
                log.write("Tipo: "
                        + token.getType().name()
                        + " ".repeat(15 - token.getType().name().length()) + "Valor: "
                        + token.getValue() + "\n");
            }
            System.out.println("Tokens guardados en tokens.log");
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
        }
    }

    private void writeAsignationFile() {
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

    private void writeAsignationTreeFile() {
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
