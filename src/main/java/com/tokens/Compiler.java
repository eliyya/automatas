package com.tokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
    private ArrayList<Token> tokens;
    private ArrayList<Token> identificators = new ArrayList<Token>();

    Compiler(File file) throws FileNotFoundException {
        // ! tokenize file
        this.tokens = Analizer.tokenizeFile(file);

        writeTokensFile(this.tokens);

        System.out.print(" Analizando asignacion: ");
        for (var token : this.tokens) System.out.print(token.getValue() + " ");

        // ! parse asignation
        var asignation = Analizer.parseExpression(this.tokens);

        writeAsignationFile(asignation);
        writeAsignationTreeFile(asignation);
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

    private void writeAsignationFile(Expression expression) {
        System.out.println("Expresion encontrada: " + expression.toString());
        try (var log = new FileWriter("asignation.json")) {
            log.write("Expresion encontrada: " + expression.toString() + "\n");
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
        }
    }

    private void writeAsignationTreeFile(Expression asignation) {
        try (FileWriter writer = new FileWriter("tree.json")) {
            writer.write(asignation.toJSON().replaceAll("\"op\" : (\".*\")", "\"\" : $1").replaceAll("(\"\\w+\") : (\".+\")", "$1 : { \"\" : $2 }"));
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo: " + e.getMessage());
        }
    }
}
