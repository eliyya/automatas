package com.tokens;

import java.io.File;
import java.io.IOException;

/**
 * este es un comentario de varias lineas
 */
public class App {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No se proporcionó ningún archivo. especifique la ruta del archivo como argumento.");
            return;
        }
        try {
            var tokens = Analizer.tokenize(new File(args[0]));

            System.out.println("Tokens encontrados: " + tokens.size());
            System.out.println();

            // for (var token : tokens) {
            //     System.out.println("Tipo: "
            //             + (token.getType().equals(TokenType.DESCONOCIDO) ? ConsoleColors.ANSI_RED + token.getType().name() + ConsoleColors.ANSI_RESET
            //             : token.getType().equals(TokenType.LITERAL) ? ConsoleColors.ANSI_GREEN + token.getType().name() + ConsoleColors.ANSI_RESET
            //             : token.getType().equals(TokenType.IDENTIFICADOR) ? ConsoleColors.ANSI_BLUE + token.getType().name() + ConsoleColors.ANSI_RESET
            //             : token.getType().equals(TokenType.RESERVADA) ? ConsoleColors.ANSI_PURPLE + token.getType().name() + ConsoleColors.ANSI_RESET
            //             : token.getType().name())
            //             + " ".repeat(15 - token.getType().name().length()) + "Valor: "
            //             + (token.getType() == TokenType.EOF ? "" : token.getValue())
            //     );
            // }

            // // write tokens to file
            // try (var log = new FileWriter("tokens.log")) {
            //     for (var token : tokens) {
            //         log.write("Tipo: "
            //                 + token.getType().name()
            //                 + " ".repeat(15 - token.getType().name().length()) + "Valor: "
            //                 + token.getValue() + "\n");
            //     }
            // } catch (IOException e) {
            //     System.out.println("No se pudo escribir el archivo.");
            // }

            System.out.print(" Analizando expresión: ");
            for (var token : tokens) {
                System.out.print(token.getValue() + " ");
            }

            var expression = Analizer.parseExpressions(tokens, 0);
            System.out.println("Expresión encontrada: " + expression.toString());
            
        } catch (NullPointerException e) {
            System.out.println("No se encontró el archivo.");
        }
    }
}
