package com.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.tools.ToolProvider;

import com.compiler.ast.statements.BlockStatement;

public class Compiler {
    public static boolean compile(BlockStatement ast) {
        String gen = """
                public class App {
                    static void println(Object obj) { IO.println(obj); }
                    public static void main(String[] args) {
                """;
        gen += ast.getScript().lines().map(s -> "        " + s).collect(Collectors.joining("\n"));
        gen += """

                    }
                }
                """;
        var file = new File("App.java");
        try (var writer = new FileWriter(file)) {
            writer.write(gen);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        var jc = ToolProvider.getSystemJavaCompiler();
        var r = jc.run(System.in, System.out, System.err, file.getPath());
        // file.delete();
        return r == 0;
    }
}
