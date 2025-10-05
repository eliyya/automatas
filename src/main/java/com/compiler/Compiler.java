package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.compiler.nodes.Declaration;
import com.compiler.nodes.Expression;
import com.compiler.nodes.Instruction;
import com.compiler.nodes.Node;

public class Compiler {
    private final Analizer analizer;
    private ArrayList<Node> lines;

    Compiler(File file) throws FileNotFoundException {
        this.analizer = new Analizer(file);
    }

    public void compile() throws FileNotFoundException {
        // ! tokenize file
        this.analizer.tokenize();

        this.analizer.writeTokensFile();

        // ! parse instructions
        this.lines = this.analizer.analize();

        this.analizer.writeAsignationFile();
        this.analizer.writeAsignationTreeFile();
    }

    public void check() {
        // Hasmap<nombre, tipo>
        var types = new HashMap<String, String>();
        for (var line : this.lines) {
            
        }
    }

    private void checkLine(Node line) {
        
    }

    private void checkInstruction(Instruction instruction) {
        
    }

    private void checkDeclaration(Declaration declaration) {
        
    }

    private void checkExpression(Expression expression) {
        
    }
}
