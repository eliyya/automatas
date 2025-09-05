package com.oraciones;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Oraciones {
    public static void main(String[] args) {
        // Creo una oracion
        var oracion = "El perro corre rápido";
        System.out.println("Oración original: " + oracion);

        // Creo un HashSet de verbos
        var verbos = new HashSet<String>(); // 59940 verbos

        try {
            // leo un archivo y agrego los verbos al HashSet
            try (var file = new Scanner(new File("./verbos.txt"))) {
                while (file.hasNext())
                    verbos.add(file.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo.");
            return;
        }

        // tokenizo la oracion por espacios
        var tokens = oracion.split("\\s+");
        System.out.println("Tokens: " + tokens.length);

        var posicionVerbo = -1;
        // busco los verbos en la oracion
        for (var i = 0; i < tokens.length; i++) {
            if (!verbos.contains(tokens[i]))
                continue;

            System.out.println("Verbo: " + tokens[i]);
            posicionVerbo = i;
            break;
        }

        if (posicionVerbo == -1) {
            System.out.println("No se encontro un verbo en la oracion.");
            return;
        }

        // Creo el sujeto
        String[] sujeto = new String[posicionVerbo];
        for (var i = 0; i < sujeto.length; i++)
            sujeto[i] = tokens[i];
        System.out.println("Sujeto: " + String.join(" ", sujeto));

        // Creo el predicado
        String[] predicado = new String[tokens.length - sujeto.length];
        for (var i = posicionVerbo; i < tokens.length; i++)
            predicado[i - posicionVerbo] = tokens[i];
        System.out.println("Predicado: " + String.join(" ", predicado));

    }
}
