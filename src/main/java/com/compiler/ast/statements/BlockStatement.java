package com.compiler.ast.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.compiler.ast.Statement;
import com.compiler.ast.statements.declaration.DeclarationFunctionStatement;
import com.compiler.ast.types.SingleType;
import com.compiler.ast.Type;
import com.compiler.ast.types.ArrayType;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;
import com.compiler.utils.JsonIgnore;

public class BlockStatement implements Statement {
    final String _c = "BlockStatement";
    List<Statement> body;
    @JsonIgnore
    private Map<String, Type> vars = new HashMap<>();
    @JsonIgnore
    Map<String, List<DeclarationFunctionStatement>> funcs = new HashMap<>();
    @JsonIgnore
    Map<String, List<DeclarationFunctionStatement>> pfuncs = new HashMap<>();
    @JsonIgnore
    Token openToken;
    @JsonIgnore
    Set<String> labels = new HashSet<>();

    public BlockStatement(List<Statement> body, Token openToken) {
        this.openToken = openToken;
        this.body = body;
    }

    public Type getVar(String identifier) {
        return vars.get(identifier);
    }

    public void addVar(String identifier, Type token) {
        vars.put(identifier, token);
    }

    public Map<String, Type> getVars() {
        return vars;
    }

    public void addFunc(String identifier, DeclarationFunctionStatement func) {
        var e = funcs.get(identifier);
        if (e == null) {
            funcs.put(identifier, new ArrayList<>());
        }
        funcs.get(identifier).add(func);
        var a = this.pfuncs.get(identifier);
        if (a == null) {
            this.pfuncs.put(identifier, new ArrayList<>());
        }
        this.pfuncs.get(identifier).add(func);
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

    public void validate(BlockStatement parent, Type returnType) {
        this.funcs.putAll(parent.funcs);
        this.vars.putAll(parent.vars);
        this.labels = parent.labels;
        for (var elem : body) {
            switch (elem) {
                case ReturnStatement returnStatement -> returnStatement.validate(this, returnType);
                case ContolFlowStatement controlFlowStatment -> controlFlowStatment.validate(this, returnType);
                default -> elem.validate(this);
            }
        }
    }

    public BlockStatement poblate() {
        Map<String, List<DeclarationFunctionStatement>> funcs = new HashMap<>();
        funcs.put("println", this.genPrintLn());
        this.funcs.putAll(funcs);

        this.vars.put("args", this.genArgs());
        return this;
    }

    private ArrayList<DeclarationFunctionStatement> genPrintLn() {
        var name = new Token(TokenKind.IDENTIFIER, "printLn", 0, 0, "");
        var params = new ArrayList<ParameterStatement>();
        var objParam = new ParameterStatement(BlockStatement.ObjectType,
                new Token(TokenKind.IDENTIFIER, "obj", 0, 0, ""));
        params.add(objParam);
        var body = new BlockStatement(new ArrayList<>(), new Token(TokenKind.OPEN_CURLY, "", 0, 0, ""));
        var dec = new DeclarationFunctionStatement(BlockStatement.VoidType, name, params, body);
        var arr = new ArrayList<DeclarationFunctionStatement>();
        arr.add(dec);
        return arr;
    }

    private ArrayType genArgs() {
        return new ArrayType(BlockStatement.StringType);
    }

    @Override
    public void validate(BlockStatement parent) {
        this.funcs.putAll(parent.funcs);
        this.vars.putAll(parent.vars);
        this.labels = parent.labels; // copy reference
        for (var elem : body) {
            elem.validate(this);
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

    public Token token() {
        return this.openToken;
    }

    public String getFunctions() {
        var text = new StringBuilder();
        this.pfuncs.forEach((name, list) -> {
            for (var fns : list) {
                text.append("static ").append(fns.toString()).append("\n");
            }
        });
        return text.toString();
    }

    public static SingleType BooleanType = new SingleType(new Token(TokenKind.BOOLEAN, "boolean", 0, 0, "global"));
    public static SingleType ObjectType = new SingleType(new Token(TokenKind.OBJECT, "object", 0, 0, "global"));
    public static SingleType VoidType = new SingleType(new Token(TokenKind.VOID, "void", 0, 0, "global"));
    public static SingleType StringType = new SingleType(new Token(TokenKind.STRING, "String", 0, 0, "global"));
}
