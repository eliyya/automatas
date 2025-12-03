package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Type;
import com.compiler.ast.Expression;
import com.compiler.ast.Statement;
import com.compiler.ast.expressions.AssignmentExpression;
import com.compiler.ast.expressions.DeclarativeExpression;
import com.compiler.ast.expressions.FunctionCallExpression;
import com.compiler.ast.expressions.IdentifierExpression;
import com.compiler.ast.expressions.PrefixExpression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.BreakStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.ast.statements.DeclarationStatement;
import com.compiler.ast.statements.ExpressionStatement;
import com.compiler.ast.statements.ParameterStatement;
import com.compiler.ast.statements.ReturnStatement;
import com.compiler.ast.statements.control_flow.DoWhileStatement;
import com.compiler.ast.statements.control_flow.ForStatement;
import com.compiler.ast.statements.control_flow.IfStatement;
import com.compiler.ast.statements.control_flow.LabeledCicleStatement;
import com.compiler.ast.statements.control_flow.WhileStatement;
import com.compiler.ast.statements.declaration.DeclarationFunctionStatement;
import com.compiler.ast.statements.declaration.DeclarationVariableStatement;
import com.compiler.ast.types.ArrayType;
import com.compiler.ast.types.SingleType;
import com.compiler.errors.ExpectedError;
import com.compiler.errors.UnexpectedSyntaxError;
import com.compiler.lexer.TokenKind;

public class StatementParser {
    public static BlockStatement parseBlockStatement(Parser parser) {
        parser.expect(TokenKind.OPEN_CURLY);
        var block = parseBlockStatement(true, parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return block;
    }

    public static BlockStatement parseBlockStatement(boolean inner, Parser parser) throws ExpectedError {
        var body = new ArrayList<Statement>();

        while (parser.hasTokens()) {
            if (inner && parser.currentTokenKind() == TokenKind.CLOSE_CURLY) {
                break;
            }
            var handler = PrattRegistry.stmtLU.get(parser.currentTokenKind());
            if (handler == null) {
                throw new UnexpectedSyntaxError(parser.currentToken());
            }
            body.add(handler.apply(parser));
        }

        return new BlockStatement(body);
    }

    // ------------------------------------------------------------------------------
    // parse typed variable Statement
    // ------------------------------------------------------------------------------

    public static DeclarationStatement parseDeclaratioStatement(Parser parser) throws ExpectedError {
        var type = parser.currentToken();
        if (type.kind() == TokenKind.VAR) {
            return parseVarStatement(parser);
        }
        if (!TokenKind.isPrimitiveType(type.kind())) {
            throw new ExpectedError("type", type);
        }
        if (parser.getToken(2).kind() == TokenKind.OPEN_PAREN) {
            return parseFunctionStatement(parser);
        }
        if (parser.getToken(1).kind() == TokenKind.OPEN_BRACKET) {
            return parseArrayStatement(parser);
        }
        parser.advance();
        var expressions = new ArrayList<DeclarativeExpression>();
        while (true) {
            var identifier = parser.expect(TokenKind.IDENTIFIER);
            if (parser.currentTokenKind() == TokenKind.ASSIGNMENT) {
                var assignment = parser.advance();
                var expression = Parser.parseExpression(parser);
                expressions.add(new AssignmentExpression(identifier, assignment, expression));
                // ends with ,
                if (parser.currentTokenKind() == TokenKind.COMMA) {
                    parser.advance();
                    continue;
                }
                // ends with ;
                if (parser.currentTokenKind() == TokenKind.SEMI) {
                    break;
                }
            }
            // ends with ,
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                expressions.add(new IdentifierExpression(identifier));
                parser.advance();
                continue;
            }
            // ends with ;
            if (parser.currentTokenKind() == TokenKind.SEMI) {
                expressions.add(new IdentifierExpression(identifier));
                break;
            }
            throw new ExpectedError(";", parser.currentToken());
        }
        parser.expect(TokenKind.SEMI);
        return new DeclarationVariableStatement(new SingleType(type), expressions);
    }

    private static DeclarationVariableStatement parseArrayStatement(Parser parser) {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type.kind())) {
            throw new ExpectedError("type", type);
        }
        Type innerType = new SingleType(type);
        while (parser.currentTokenKind() == TokenKind.OPEN_BRACKET) {
            parser.expect(TokenKind.OPEN_BRACKET);
            parser.expect(TokenKind.CLOSE_BRACKET);
            innerType = new ArrayType(innerType);
        }
        var expressions = new ArrayList<DeclarativeExpression>();
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        if (parser.currentTokenKind() == TokenKind.SEMI) {
            parser.advance();
            expressions.add(new IdentifierExpression(identifier));
            return new DeclarationVariableStatement(innerType, expressions);
        }
        var assignment = parser.expect(TokenKind.ASSIGNMENT);
        expressions.add(new AssignmentExpression(identifier, assignment, Parser.parseExpression(parser)));
        parser.expect(TokenKind.SEMI);
        return new DeclarationVariableStatement(innerType, expressions);
    }

    private static DeclarationVariableStatement parseVarStatement(Parser parser) {
        var type = parser.expect(TokenKind.VAR);
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        var assignment = parser.expect(TokenKind.ASSIGNMENT);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.SEMI);
        var assignmentExpression = new AssignmentExpression(identifier, assignment, expression);
        return new DeclarationVariableStatement(new SingleType(type), List.of(assignmentExpression));
    }

    public static Statement parseIdentifierStatement(Parser parser) {
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        // ++ or --
        if (TokenKind.isUnaryOperation(parser.currentTokenKind())) {
            var operator = parser.advance();
            parser.expect(TokenKind.SEMI);
            var expression = new IdentifierExpression(identifier);
            var uaryeExpression = new PrefixExpression(operator, expression, true);
            return new ExpressionStatement(uaryeExpression);
        }
        if (TokenKind.isAssignment(parser.currentTokenKind())) {
            var assignment = parser.advance();
            var expression = Parser.parseExpression(parser);
            parser.expect(TokenKind.SEMI);
            var assignmentExpression = new AssignmentExpression(identifier, assignment, expression);
            return new ExpressionStatement(assignmentExpression);
        }
        if (parser.currentTokenKind() == TokenKind.OPEN_PAREN) {
            parser.expect(TokenKind.OPEN_PAREN);
            var parameters = new ArrayList<Expression>();
            while (parser.currentTokenKind() != TokenKind.CLOSE_PAREN) {
                parameters.add(Parser.parseExpression(parser));
                if (parser.currentTokenKind() != TokenKind.COMMA) {
                    break;
                }
                parser.expect(TokenKind.COMMA);
            }
            parser.expect(TokenKind.CLOSE_PAREN);
            var functionCall = new FunctionCallExpression(identifier, parameters);
            parser.expect(TokenKind.SEMI);
            return new ExpressionStatement(functionCall);
        }
        if (parser.currentTokenKind() == TokenKind.COLON) {
            parser.expect(TokenKind.COLON);
            var next = parser.currentToken();
            var cicle = StatementParser.parseControlFlowStatement(parser);
            if (cicle instanceof IfStatement) {
                throw new ExpectedError("cicle", next);
            }
            // if (cicle instanceof IfStatement) {
            // throw new ExpectedError("cicle", next);
            // }
            return new LabeledCicleStatement(identifier, cicle);
        }
        throw new UnexpectedSyntaxError(parser.currentToken());
    }

    public static Statement parseUaryOperationStatement(Parser parser) {
        var operator = parser.advance();
        if (operator.kind() != TokenKind.PLUS_PLUS && operator.kind() != TokenKind.MINUS_MINUS) {
            throw new ExpectedError("++", operator);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.SEMI);
        var expression = new IdentifierExpression(identifier);
        var uaryeExpression = new PrefixExpression(operator, expression);
        return new ExpressionStatement(uaryeExpression);
    }

    public static ContolFlowStatement parseControlFlowStatement(Parser parser) {
        var token = parser.currentToken();
        switch (token.kind()) {
            case WHILE -> {
                return parseWhileStatement(parser);
            }
            case IF -> {
                return parseIfStatement(parser);
            }
            case FOR -> {
                return parseForStatement(parser);
            }
            case DO -> {
                return parseDoStatement(parser);
            }
            default -> {
                throw new UnexpectedSyntaxError(token);
            }
        }
    }

    public static ReturnStatement parseReturnStatement(Parser parser) {
        var rt = parser.expect(TokenKind.RETURN);
        if (parser.currentTokenKind() == TokenKind.SEMI) {
            parser.expect(TokenKind.SEMI);
            return new ReturnStatement(null, rt);
        }
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.SEMI);
        return new ReturnStatement(expression, rt);
    }

    public static BreakStatement parseBreakStatement(Parser parser) {
        parser.expect(TokenKind.BREAK);
        if (parser.currentTokenKind() == TokenKind.SEMI) {
            return new BreakStatement(null);
        }
        var expression = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.SEMI);
        return new BreakStatement(expression);
    }

    // // -----------------------
    // // parse function Statement
    // // -----------------------
    private static DeclarationFunctionStatement parseFunctionStatement(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type.kind())) {
            throw new ExpectedError("type", type);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.OPEN_PAREN);
        var parameters = new ArrayList<ParameterStatement>();
        while (parser.currentTokenKind() != TokenKind.CLOSE_PAREN) {
            parameters.add(parseParameterStatement(parser));
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                parser.advance();
                continue;
            }
            break;
        }
        parser.expect(TokenKind.CLOSE_PAREN);
        var body = parseBlockStatement(parser);
        return new DeclarationFunctionStatement(new SingleType(type), identifier, parameters, body);
    }

    private static ParameterStatement parseParameterStatement(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type.kind())) {
            throw new ExpectedError("type", type);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        return new ParameterStatement(new SingleType(type), identifier);
    }

    // ------------------
    // parse control flow
    // ------------------
    private static WhileStatement parseWhileStatement(Parser parser) {
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        var body = parseBlockStatement(parser);
        return new WhileStatement(expression, body);
    }

    private static IfStatement parseIfStatement(Parser parser) {
        // if
        parser.expect(TokenKind.IF);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        var body = parseBlockStatement(parser);
        if (parser.currentTokenKind() != TokenKind.ELSE) {
            return new IfStatement(expression, body);
        }
        // else
        parser.advance();
        // if again
        if (parser.currentTokenKind() == TokenKind.IF) {
            var elseIfBody = parseIfStatement(parser);
            var elseBody = new BlockStatement(List.of(elseIfBody));
            return new IfStatement(expression, body, elseBody);
        }
        var elseBody = parseBlockStatement(parser);
        return new IfStatement(expression, body, elseBody);
    }

    private static ForStatement parseForStatement(Parser parser) {
        parser.expect(TokenKind.FOR);
        var open = parser.expect(TokenKind.OPEN_PAREN);
        // foreach
        if (parser.getToken(2).kind() == TokenKind.COLON) {
            var type = parser.advance();
            if (!TokenKind.isPrimitiveType(type.kind())) {
                throw new ExpectedError("type", type);
            }
            var identifier = parser.expect(TokenKind.IDENTIFIER);
            var stat = new DeclarationVariableStatement(new SingleType(type),
                    List.of(new IdentifierExpression(identifier)));
            parser.expect(TokenKind.COLON);
            var collection = Parser.parseExpression(parser);
            parser.expect(TokenKind.CLOSE_PAREN);
            var body = parseBlockStatement(parser);
            return new ForStatement(stat, collection, body);
        }
        // for
        var handler = PrattRegistry.stmtLU.get(parser.currentTokenKind());
        if (handler == null) {
            throw new UnexpectedSyntaxError(parser.currentToken());
        }
        var stat = handler.apply(parser);
        if (stat instanceof DeclarationVariableStatement) {

        } else if (stat instanceof ExpressionStatement) {
            
        } else {
            throw new ExpectedError("declaration", stat.token());
        }
        if (stat instanceof DeclarationVariableStatement dec) {
            var condition = Parser.parseExpression(parser);
            parser.expect(TokenKind.SEMI);
            var increment = Parser.parseExpression(parser);
            parser.expect(TokenKind.CLOSE_PAREN);
            var body = parseBlockStatement(parser);
            return new ForStatement(dec, condition, increment, body);
        }
        throw new ExpectedError("declaration", open);
    }

    private static DoWhileStatement parseDoStatement(Parser parser) {
        parser.expect(TokenKind.DO);
        var body = parseBlockStatement(parser);
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.SEMI);
        return new DoWhileStatement(body, expression);
    }
}
