package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.lexer.Token;

public interface ContolFlowStatement extends Statement {    
    public void validate(BlockStatement parent, Token returnType);
}
