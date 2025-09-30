package com.tokens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * este es un comentario de varias lineas
 */
public class App {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No se proporcionó ningún archivo. especifique la ruta del archivo como argumento.");
            return;
        }
        
        ArrayList<Token> tokens;
        try {
            tokens = Analizer.tokenizeFile(new File(args[0]));
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo.");
            return;
        }

        System.out.println("Tokens encontrados: " + tokens.size());
        System.out.println();

        // write tokens to file
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

        System.out.print(" Analizando expresión: ");
        for (var token : tokens) {
            System.out.print(token.getValue() + " ");
        }

        var expression = Analizer.parseExpressions(tokens, 0);
        System.out.println("Expresión encontrada: " + expression.toString());
        
        try (FileWriter writer = new FileWriter("expression.json")) {
            writer.write(expression.toJSON());
        } catch (IOException e) {
            System.out.println("No se pudo escribir el archivo: " + e.getMessage());
        }

    }
}
