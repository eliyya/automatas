package com.compiler.utils;

import java.io.IOException;

public class Console {
    public static String format(Object text, ConsoleColor color) {
        return color + text.toString() + ConsoleColor.RESET;
    }

    public static void clear() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                new ProcessBuilder("clear")
                    .inheritIO()
                    .start()
                    .waitFor();
            }
        } catch (IOException | InterruptedException e) {
            IO.println("\033[H\033[2J");
        }
    }
}
