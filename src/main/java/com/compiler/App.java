package com.compiler;

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
        var tokenizer = Tokenizer.tokenize(new File(args[0]));
        var analizer = new Analizer(tokenizer);
        analizer.analize();
    }
}
