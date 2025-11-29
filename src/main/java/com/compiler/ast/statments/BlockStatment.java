package com.compiler.ast.statments;

import java.util.List;
import java.util.stream.Collectors;

import com.compiler.ast.Statment;

public class BlockStatment implements Statment {
    final String _c = "BlockStatmennt";
    List<Statment> body;

    public BlockStatment(List<Statment> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        var text = "{\n";
        for (var elem : body) {
            text += elem + "\n";
        }
        text += "}";
        return text;
    }
    
    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
}
