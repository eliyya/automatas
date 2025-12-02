package com.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import com.compiler.ast.statements.BlockStatement;

public class Compiler {
    public static void compile(BlockStatement ast) {
        String gen = """
                public class App {
                    void println(Object obj) { IO.println(obj); }
                    public static void main(String[] args) {
                """;
        gen += ast.getScript().lines().map(s -> "        " + s).collect(Collectors.joining("\n"));
        gen += """

                    }
                }
                """;
        try (var writer = new FileWriter("App.java")) {
            writer.write(gen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
