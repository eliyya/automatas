package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.ast.statements.declaration.DeclarationVariableStatement;
import com.compiler.lexer.Token;

enum ForType {
    FORI,
    FOREACH
}

public class ForStatement implements ContolFlowStatement {
    String _c = "ForStatement";
    ForType type;
    DeclarationVariableStatement statement;
    Expression collection;
    Expression condition;
    Expression increment;
    BlockStatement body;
    Token forToken;
    
    public ForStatement(DeclarationVariableStatement Statement, Expression collection, BlockStatement body, Token forToken) {
        this.statement = Statement;
        this.collection = collection;
        this.body = body;
        this.type = ForType.FOREACH;
        this.forToken = forToken;
    }

    public ForStatement(DeclarationVariableStatement Statement, Expression condition, Expression increment, BlockStatement body, Token forToken) {
        this.statement = Statement;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
        this.type = ForType.FORI;
        this.forToken = forToken;
    }

    @Override
    public String toString() {
        if (type == ForType.FOREACH) {
            return "for (" + statement + " : " + collection.toString() + ") " + body;
        }
        return "for (" + statement + "; " + condition + "; " + increment + ") " + body;
    }

    @Override
    public void validate(BlockStatement parent) {
        if (this.type == ForType.FORI) {
            this.statement.validate(parent);
            this.condition.validateType(BlockStatement.BooleanType, parent);
            this.increment.validateType(BlockStatement.ObjectType, parent);
        }
        this.body.validate(parent);
    }

    @Override
    public void validate(BlockStatement parent, Type returnType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }

    @Override
    public Token token() {
        return this.forToken;
    }
    
}
