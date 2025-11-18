package com.compiler.ast.statments;

import java.util.List;

import com.compiler.ast.Statment;

public class BlockStatment implements Statment {
    List<Statment> body;

    public BlockStatment(List<Statment> body) {
        this.body = body;
    }
    
    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
}
