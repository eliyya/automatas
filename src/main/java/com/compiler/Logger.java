package com.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Logger {
    private final File file;
    public Logger(File file) {
        this.file = file;
    }

    public Logger clear() {
        try {
            var writer = new FileWriter(file, false);
            writer.write("");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void log(String message) {
        try {
            var writer = new FileWriter(file, true);
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logTokens(ArrayList<Token> tokens) {
        log(tokens.stream().map(token -> token.value()).collect(Collectors.joining("•")) + "\n");
    }
}
