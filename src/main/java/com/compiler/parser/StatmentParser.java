package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;
import com.compiler.ast.expressions.AssignmentExpression;
import com.compiler.ast.expressions.FunctionCallExpression;
import com.compiler.ast.expressions.IdentifierExpression;
import com.compiler.ast.expressions.UnaryOperationExpression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
import com.compiler.ast.statments.ParameterStatment;
import com.compiler.ast.statments.control_flow.DoWhileStatment;
import com.compiler.ast.statments.control_flow.ForStatment;
import com.compiler.ast.statments.control_flow.IfStatment;
import com.compiler.ast.statments.control_flow.WhileStatment;
import com.compiler.ast.statments.declaration.DeclarationFunctionStatment;
import com.compiler.ast.statments.declaration.DeclarationVariableStatment;
import com.compiler.errors.ExpectedError;
import com.compiler.errors.UnexpectedSyntaxError;
import com.compiler.lexer.TokenKind;

public class StatmentParser {
    public static BlockStatment parseBlockStatment(Parser parser) {
        parser.expect(TokenKind.OPEN_CURLY);
        var block = parseBlockStatment(true, parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return block;
    }

    public static BlockStatment parseBlockStatment(boolean inner, Parser parser) throws ExpectedError {
        var body = new ArrayList<Statment>();

        while (parser.hasTokens()) {
            if (inner && parser.currentTokenKind() == TokenKind.CLOSE_CURLY) {
                break;
            }
            var handler = PrattRegistry.stmtLU.get(parser.currentTokenKind());
            if (handler == null) {
                throw new UnexpectedSyntaxError(parser, parser.currentToken());
            }
            body.add(handler.handle(parser));
            continue;
        }

        return new BlockStatment(body);
    }

    // ------------------------------------------------------------------------------
    // parse typed variable statment
    // ------------------------------------------------------------------------------

    public static DeclarationStatment parseDeclaratioStatment(Parser parser) throws ExpectedError {
        var type = parser.currentToken();
        if (type.kind() == TokenKind.VAR) {
            return parseVarStatment(parser);
        }
        if (!TokenKind.isPrimitiveType(type)) {
            throw new ExpectedError(parser, "type", type);
        }
        if (parser.getToken(2).kind() == TokenKind.OPEN_PAREN) {
            return parseFunctionStatment(parser);
        }
        parser.advance();
        var expressions = new ArrayList<Expression>();
        while (true) {
            var identifier = parser.expect(TokenKind.IDENTIFIER);
            if (parser.currentTokenKind() == TokenKind.ASSIGNMENT) {
                var assignment = parser.advance();
                var expression = Parser.parseExpression(parser);
                expressions.add(new AssignmentExpression(identifier, assignment, expression));
            }
            // ends with ,
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                expressions.add(new IdentifierExpression(identifier));
                parser.advance();
                continue;
            }
            // ends with ;
            if (parser.currentTokenKind() == TokenKind.SEMI) {
                break;
            }
            throw new ExpectedError(parser, ";", parser.currentToken());
        }
        parser.expect(TokenKind.SEMI);
        return new DeclarationVariableStatment(type, expressions);
    }

    private static DeclarationVariableStatment parseVarStatment(Parser parser) {
        var type = parser.expect(TokenKind.VAR);
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        var assignment = parser.expect(TokenKind.ASSIGNMENT);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.SEMI);
        var assignmentExpression = new AssignmentExpression(identifier, assignment, expression);
        return new DeclarationVariableStatment(type, List.of(assignmentExpression));
    }

    public static Statment parseIdentifierStatment(Parser parser) {
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        // ++ or --
        if (TokenKind.isUnaryOperation(parser.currentTokenKind())) {
            var operator = parser.advance();
            parser.expect(TokenKind.SEMI);
            var uaryeExpression = new UnaryOperationExpression(identifier, operator, false);
            return new ExpressionStatment(uaryeExpression);
        }
        if (TokenKind.isAssignment(parser.currentTokenKind())) {
            var assignment = parser.advance();
            var expression = Parser.parseExpression(parser);
            parser.expect(TokenKind.SEMI);
            var assignmentExpression = new AssignmentExpression(identifier, assignment, expression);
            return new ExpressionStatment(assignmentExpression);
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
            return new ExpressionStatment(functionCall);
        }
        throw new UnexpectedSyntaxError(parser, parser.currentToken());
    }

    public static Statment parseUaryOperationStatment(Parser parser) {
        var operator = parser.advance();
        if (operator.kind() != TokenKind.PLUS_PLUS && operator.kind() != TokenKind.MINUS_MINUS) {
            throw new ExpectedError(parser, "++", operator);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.SEMI);
        var uaryeExpression = new UnaryOperationExpression(identifier, operator, true);
        return new ExpressionStatment(uaryeExpression);
    }

    public static ContolFlowStatment parseControlFlowStatment(Parser parser) {
        var token = parser.currentToken();
        switch (token.kind()) {
            case WHILE -> {
                return parseWhileStatment(parser);
            }
            case IF -> {
                return parseIfStatment(parser);
            }
            case FOR -> {
                return parseForStatment(parser);
            }
            case DO -> {
                return parseDoStatment(parser);
            }
            default -> {
                throw new UnexpectedSyntaxError(parser, token);
            }
        }
    }

    // // -----------------------
    // // parse function statment
    // // -----------------------
    private static DeclarationFunctionStatment parseFunctionStatment(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type)) {
            throw new ExpectedError(parser, "type", type);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.OPEN_PAREN);
        var parameters = new ArrayList<ParameterStatment>();
        while (parser.currentTokenKind() != TokenKind.CLOSE_PAREN) {
            parameters.add(parseParameterStatment(parser));
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                parser.advance();
                continue;
            }
            break;
        }
        parser.expect(TokenKind.CLOSE_PAREN);
        var body = parseBlockStatment(parser);
        return new DeclarationFunctionStatment(type, identifier, parameters, body);
    }

    // private static ExpressionStatment parseFunctionCallStatment(Parser parser)
    // throws ExpectedError {
    // var functionCall = ExpressionParser.parseFunctionExpression(parser);
    // parser.expect(TokenKind.SEMI);
    // return new ExpressionStatment(functionCall);
    // }

    private static ParameterStatment parseParameterStatment(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type)) {
            throw new ExpectedError(parser, "type", type);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        return new ParameterStatment(type, identifier);
    }

    // private static Statment parseFunctionStatment(Parser parser) throws
    // ExpectedError {
    // var identifier = parser.advance();
    // if (identifier.kind() != TokenKind.IDENTIFIER) {
    // throw new ExpectedError(parser, "identifier", identifier);
    // }
    // var openParent = parser.advance();
    // if (openParent.kind() != TokenKind.OPEN_PAREN) {
    // throw new ExpectedError(parser, "(", openParent);
    // }
    // var closeParent = parser.advance();
    // if (closeParent.kind() != TokenKind.CLOSE_PAREN) {
    // throw new ExpectedError(parser, ")", closeParent);
    // }
    // return new ExpressionStatment(new FunctionExpression(identifier));
    // }

    // // ------------------
    // // parse control flow
    // // ------------------
    private static WhileStatment parseWhileStatment(Parser parser) {
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        var body = parseBlockStatment(parser);
        return new WhileStatment(expression, body);
    }

    private static IfStatment parseIfStatment(Parser parser) {
        // if
        parser.expect(TokenKind.IF);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        var body = parseBlockStatment(parser);
        if (parser.currentTokenKind() != TokenKind.ELSE) {
            return new IfStatment(expression, body);
        }
        // else
        parser.advance();
        // if again
        if (parser.currentTokenKind() == TokenKind.IF) {
            var elseIfBody = parseIfStatment(parser);
            var elseBody = new BlockStatment(List.of(elseIfBody));
            return new IfStatment(expression, body, elseBody);
        }
        var elseBody = parseBlockStatment(parser);
        return new IfStatment(expression, body, elseBody);
    }

    private static ForStatment parseForStatment(Parser parser) {
        parser.expect(TokenKind.FOR);
        var open = parser.expect(TokenKind.OPEN_PAREN);
        // foreach
        if (parser.getToken(2).kind() == TokenKind.COLON) {
            var type = parser.advance();
            if (!TokenKind.isPrimitiveType(type)) {
                throw new ExpectedError(parser, "type", type);
            }
            var identifier = parser.expect(TokenKind.IDENTIFIER);
            var stat = new DeclarationVariableStatment(type, List.of(new IdentifierExpression(identifier)));
            parser.expect(TokenKind.COLON);
            var collection = parser.expect(TokenKind.IDENTIFIER);
            parser.expect(TokenKind.CLOSE_PAREN);
            var body = parseBlockStatment(parser);
            return new ForStatment(stat, collection, body);
        }
        // for
        var stat = parseDeclaratioStatment(parser);
        if (stat instanceof DeclarationVariableStatment dec) {
            var condition = Parser.parseExpression(parser);
            parser.expect(TokenKind.SEMI);
            var increment = Parser.parseExpression(parser);
            parser.expect(TokenKind.CLOSE_PAREN);
            var body = parseBlockStatment(parser);
            return new ForStatment(dec, condition, increment, body);
        }
        throw new ExpectedError(parser,"declaration",open);
    }

    private static DoWhileStatment parseDoStatment(Parser parser) {
        parser.expect(TokenKind.DO);
        var body = parseBlockStatment(parser);
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = Parser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.SEMI);
        return new DoWhileStatment(body, expression);
    }
}
