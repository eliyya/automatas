package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.ast.Type;

public interface ContolFlowStatement extends Statement {    
    public void validate(BlockStatement parent, Type returnType);
}
