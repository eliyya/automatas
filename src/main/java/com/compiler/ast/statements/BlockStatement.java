package com.compiler.ast.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compiler.ast.Statement;
import com.compiler.ast.statements.declaration.DeclarationFunctionStatement;
import com.compiler.ast.types.SingleType;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;
import com.compiler.utils.JsonIgnore;

public class BlockStatement implements Statement {
    final String _c = "BlockStatement";
    List<Statement> body;
    @JsonIgnore
    private Map<String, Token> vars = new HashMap<>();
    @JsonIgnore
    private Map<String, List<DeclarationFunctionStatement>> funcs = new HashMap<>();

    public BlockStatement(List<Statement> body) {
        this.body = body;
    }

    public Token getVar(String identifier) {
        return vars.get(identifier);
    }

    public void addVar(String identifier, Token token) {
        vars.put(identifier, token);
    }

    public Map<String, Token> getVars() {
        return vars;
    }

    public void addFunc(String identifier, DeclarationFunctionStatement func) {
        var e = funcs.get(identifier);
        if (e == null) {
            funcs.put(identifier, new ArrayList<>());
        }
        funcs.get(identifier).add(func);
    }

    public List<DeclarationFunctionStatement> getFuncs(String identifier) {
        return funcs.get(identifier);
    }

    public Map<String, List<DeclarationFunctionStatement>> getFuncs() {
        return funcs;
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

    public void validate() {
        for (var elem : body) {
            elem.validate(this);
        }
    }

    public void validate(Map<String, Token> vars, Map<String, List<DeclarationFunctionStatement>> funcs) {
        this.vars.putAll(vars);
        this.funcs.putAll(funcs);
        for (var elem : body) {
            elem.validate(this);
        }
    }

    public void validate(Map<String, Token> vars, Map<String, List<DeclarationFunctionStatement>> funcs, Token returnType) {
        this.vars.putAll(vars);
        this.funcs.putAll(funcs);
        for (var elem : body) {
            if (elem instanceof ReturnStatement returnStatement) {
                returnStatement.validate(this, returnType);
            } else if (elem instanceof ContolFlowStatement controlFlowStatment) {
                controlFlowStatment.validate(this, returnType);
            } else {
                elem.validate(this);
            }
        }
    }

    public BlockStatement poblate() {
        Map<String, List<DeclarationFunctionStatement>> funcs = new HashMap<>();
        funcs.put("println", this.printLn());
        this.funcs.putAll(funcs);
        return this;
    }

    private ArrayList<DeclarationFunctionStatement> printLn() {
        var type = new SingleType(new Token(TokenKind.VOID, "void", 0, 0, ""));
        var name = new Token(TokenKind.IDENTIFIER, "printLn", 0, 0, "");
        var params = new ArrayList<ParameterStatement>();
        var objParam = new ParameterStatement(new SingleType(new Token(TokenKind.OBJECT, "object", 0, 0, "")), new Token(TokenKind.IDENTIFIER, "obj", 0, 0, ""));
        params.add(objParam);
        var body = new BlockStatement(new ArrayList<>());
        var dec = new DeclarationFunctionStatement(type, name, params, body);
        var arr = new ArrayList<DeclarationFunctionStatement>();
        arr.add(dec);
        return arr;
    }
    
    @Override
    public void validate(BlockStatement parent) {
        for (var elem : body) {
            elem.validate(parent);
        }
    }

    public String getScript() {
        var text = "";
        for (var elem : body) {
            if (elem instanceof DeclarationFunctionStatement) {
                // do nothing
            } else {
                text += elem.toString() + "\n";
            }
        }
        return text;
    }
}
