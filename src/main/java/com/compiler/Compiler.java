package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;

public class Compiler {
    private final Analizer analizer;

    Compiler(File file) throws FileNotFoundException {
        this.analizer = new Analizer(file);
    }

    public void compile() throws FileNotFoundException {
        // ! tokenize file
        this.analizer.tokenize();

        this.analizer.writeTokensFile();

        // ! parse instructions
        this.analizer.analize();

        this.analizer.writeAsignationFile();
        this.analizer.writeAsignationTreeFile();
    }
}
