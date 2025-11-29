package com.compiler.ast.statments;

import com.compiler.ast.Statment;

public class FunctionCallStatment implements Statment {
    String _class = "FunctionCallStatment";
    String name;

    public FunctionCallStatment(String name) {
        this.name = name;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
