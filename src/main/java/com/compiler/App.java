package com.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.compiler.ast.Statement;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.parser.Parser;
import com.compiler.utils.ConsoleColor;
import com.compiler.utils.JSON;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            IO.println("----------------------------------------------");
            IO.println(format("No se proporcionó ningún archivo", ConsoleColor.RED));
            IO.println("especifique la ruta del archivo como argumento");
            IO.println(
                    "Ejemplo: " + format("java", ConsoleColor.YELLOW) + format(" -jar", ConsoleColor.BLACK_BRIGHT)
                            + " App.jar " + format("test.java.txt", ConsoleColor.BLUE_UNDERLINED));
            IO.println("----------------------------------------------");
            return;
        }

        var source = Files.readString(Path.of(args[0]));

        IO.println("----------------------------------------------");
        IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
        IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
        IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
        IO.println(format("Presiona Enter para continuar", ConsoleColor.BLACK_BRIGHT));
        IO.println("----------------------------------------------");
        IO.readln();
        // --------------------
        // lexer - tokenization
        // --------------------
        try {
            var lexer = new Lexer(source);
            var tokens = lexer.tokenize();
            // print tokens
            printTokens(tokens);
            var successTokens = writeTokens(tokens);
            writeTokensSucces(successTokens);
            IO.println();
            IO.println("----------------------------------------------");
            IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
            IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
            IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
            IO.println(format("Presiona Enter para continuar", ConsoleColor.BLACK_BRIGHT));
            IO.println("----------------------------------------------");
            IO.readln();
            clearConsole();

            // ----------------
            // parser - parsing
            // ----------------
            try {
                var ast = Parser.parse(tokens);
                printAST(ast);
                writeTokensSucces(successTokens);
                var successAST = writeAST(ast);
                writeASTSucces(successAST);
                var successTree = writeASTTree(ast);
                writeTreeSucces(successTree);
                IO.println();
                IO.println("----------------------------------------------");
                IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
                IO.println(format("Presiona Enter para continuar", ConsoleColor.BLACK_BRIGHT));
                IO.println("----------------------------------------------");
                IO.readln();
                clearConsole();
                IO.println();
                // ----------------
                // validator
                // ----------------
                try {
                    ast.validate();
                    writeTokensSucces(successTokens);
                    writeASTSucces(successAST);
                    writeTreeSucces(successTree);
                    IO.println();
                    IO.println("----------------------------------------------");
                    IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                    IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                    IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                    IO.println("----------------------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                    IO.println("----------------------------------------------");
                    IO.println(format("Error al validar el AST", ConsoleColor.RED));
                    IO.println("----------------------------------------------");
                    IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                    IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                    IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Fallido", ConsoleColor.RED));
                    IO.println("----------------------------------------------");
                }
            } catch (Exception e) {
                e.printStackTrace();
                IO.println("----------------------------------------------");
                IO.println(format("Error al generar el AST", ConsoleColor.RED));
                IO.println("----------------------------------------------");
                IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Exitoso", ConsoleColor.GREEN));
                IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Fallido", ConsoleColor.RED));
                IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Fallido", ConsoleColor.RED));
                IO.println("----------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            IO.println("----------------------------------------------");
            IO.println(format("Error al generar tokens", ConsoleColor.RED));
            IO.println("----------------------------------------------");
            IO.println("Fase 1: " + format("Tokenización ", ConsoleColor.BLUE_BOLD) + format("Fallido", ConsoleColor.RED));
            IO.println("Fase 2: " + format("Parseo ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
            IO.println("Fase 3: " + format("Validación ", ConsoleColor.BLUE_BOLD) + format("Pendiente", ConsoleColor.BLACK_BRIGHT));
            IO.println("----------------------------------------------");
        }
    }

    private static void printTokens(List<Token> tokens) {
        var tnl = getMaxNameLen(tokens);
        var tvl = getMaxValueLen(tokens);
        for (var token : tokens) {
            // name
            IO.println(format(token.kind().toString(), ConsoleColor.WHITE_BRIGHT)
                    + " ".repeat(tnl - token.kind().toString().length())
                    // value
                    + " (" + format(token.value(), ConsoleColor.GREEN_BOLD_BRIGHT) + ") "
                    + " ".repeat(tvl - token.value().length())
                    // position
                    + "[" + format(token.line(), ConsoleColor.BLUE_BOLD_BRIGHT) + ":"
                    + format(token.column(), ConsoleColor.BLUE_BOLD_BRIGHT) + "]");
        }
    }

    private static void printAST(Statement ast) {
        var json = JSON.serialize(ast);
        json = json.replaceAll("(?s)\\{\\s*\"_c\"\\s*:\\s*\"([^\"]+)\"\\s*,?", "$1 {");
        json = json.replaceAll("\"([^\"]*)\"\\:", format("$1", ConsoleColor.BLACK_BRIGHT) + ":");
        json = json.replaceAll("\"([^\"]*)\"", "\"" + format("$1", ConsoleColor.GREEN) + "\"");
        json = json.replaceAll(": (true|false)", ": " + format("$1", ConsoleColor.GREEN));
        json = json.replaceAll(": (\\d*)", ": " + format("$1", ConsoleColor.GREEN));
        json = colorizeBraces(json);
        IO.println(json);
    }

    private static boolean writeTokens(List<Token> tokens) {
        try (var writer = new FileWriter("tokens.log")) {
            for (var token : tokens) {
                writer.write(token.kind().toString()
                        + " (" + token.value() + ") "
                        + "[" + token.line() + ":" + token.column() + "]\n");
            }
            return true;
        } catch (IOException e) {
            IO.println("");
            IO.println("Error al escribir tokens");
            IO.println("");
            return false;
        }
    }

    private static void writeTokensSucces(boolean success) {
        if (!success) {
            return;
        }
        IO.println("");
        IO.println("--------------------------------------------------------");
        IO.println(
                "Se han escrito los " + format("tokens", ConsoleColor.GREEN_BOLD) + " para su análisis en "
                        + format("tokens.log", ConsoleColor.BLUE_UNDERLINED));
        IO.println("Contiene por cada token en una linea:");
        IO.println("  - el tipo de token " + format("TOKEN_TYPE", ConsoleColor.WHITE_BOLD_BRIGHT));
        IO.println("  - su valor entre paréntesis " + format("(", ConsoleColor.WHITE_BOLD_BRIGHT)
                + format("value", ConsoleColor.GREEN_BOLD) + format(")", ConsoleColor.WHITE_BOLD_BRIGHT));
        IO.println("  - su posición en el archivo " + format("[", ConsoleColor.WHITE_BOLD_BRIGHT)
                + format("linea", ConsoleColor.BLUE_BOLD) + format(":", ConsoleColor.WHITE_BOLD_BRIGHT)
                + format("columna", ConsoleColor.BLUE_BOLD) + format("]", ConsoleColor.WHITE_BOLD_BRIGHT));
        IO.println("--------------------------------------------------------");
    }

    private static boolean writeAST(Statement ast) {
        try (var writer = new FileWriter("ast.json")) {
            writer.write(JSON.serialize(ast));
            return true;
        } catch (IOException e) {
            IO.println("");
            IO.println("Error al escribir AST");
            IO.println("");
            return false;
        }
    }
    
    private static void writeASTSucces(boolean success) {
        if (!success) {
            return;
        }
        IO.println("");
        IO.println("--------------------------------------------------------");
        IO.println(
                "Se ha generado el " + format("Abstract Syntax Tree", ConsoleColor.GREEN) + " ("
                        + format("AST", ConsoleColor.GREEN_BOLD) + ") en "
                        + format("ast.json", ConsoleColor.BLUE_UNDERLINED));
        IO.println("--------------------------------------------------------");
    }

    private static boolean writeASTTree(Statement ast) {
        try (var writer = new FileWriter("tree.json")) {
            var tree = JSON.serialize(ast).replaceAll("\"_c\"", "\"\"");
            writer.write(tree);
            return true;
        } catch (IOException e) {
            IO.println("");
            IO.println("Error al escribir AST Tree");
            IO.println("");
            return false;
        }
    }
    
    private static void writeTreeSucces(boolean success) {
        if (!success) {
            return;
        }
        IO.println("");
        IO.println("--------------------------------------------------------");
        IO.println(
                "Se ha escrito un archivo especial basado en el "
                        + format("ast.json", ConsoleColor.BLUE_UNDERLINED));
        IO.println("en " + format("tree.json", ConsoleColor.BLUE_UNDERLINED)
                + " para su análisis de manera visual creado");
        IO.println(
                "para la herramienta online: "
                        + format("https://jsoncrack.com/editor", ConsoleColor.BLUE_UNDERLINED));
        IO.println("Solamente necesita:");
        IO.println("  - abrir la herramienta online");
        IO.println("  - dirigirse a " + format("File", ConsoleColor.BLUE) + " -> "
                + format("Import", ConsoleColor.BLUE));
        IO.println(
                "  - arrastrar el archivo " + format("tree.json", ConsoleColor.BLUE_UNDERLINED)
                        + " a la herramienta");
        IO.println("--------------------------------------------------------");
    }

    private static String format(Object text, ConsoleColor color) {
        return ConsoleColor.format(text, color);
    }

    private static int getMaxNameLen(List<Token> tokens) {
        int max = 0;
        for (var token : tokens) {
            if (token.kind().toString().length() > max) {
                max = token.kind().toString().length();
            }
        }
        return max;
    }

    private static int getMaxValueLen(List<Token> tokens) {
        int max = 0;
        for (var token : tokens) {
            if (token.value().length() > max) {
                max = token.value().length();
            }
        }
        return max;
    }

    public static String colorizeBraces(String json) {
        ConsoleColor[] palette = new ConsoleColor[] {
                ConsoleColor.CYAN,
                ConsoleColor.RED,
                ConsoleColor.YELLOW,
                ConsoleColor.GREEN,
                ConsoleColor.BLUE,
                ConsoleColor.PURPLE,
        };

        StringBuilder result = new StringBuilder();
        Deque<ConsoleColor> stack = new ArrayDeque<>();
        int level = 0;

        for (int i = 0; i < json.length(); i++) {
            var c = json.charAt(i);

            // Detectar secuencia ANSI: \033[
            if (c == '\033' && i + 1 < json.length() && json.charAt(i + 1) == '[') {
                int start = i;
                i += 2;

                while (i < json.length()) {
                    char cc = json.charAt(i);
                    if ((cc >= 'A' && cc <= 'Z') || (cc >= 'a' && cc <= 'z')) {
                        i++;
                        break;
                    }
                    i++;
                }

                var ansi = json.substring(start, i);
                result.append(ansi);
                i--;
                continue;
            }

            if (c == '{' || c == '[') {
                var col = palette[level % palette.length];
                stack.push(col);

                result.append(format(String.valueOf(c), col));
                level++;
            } else if (c == '}' || c == ']') {
                level--;
                var col = stack.pop();
                result.append(format(String.valueOf(c), col));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                new ProcessBuilder("clear")
                    .inheritIO()
                    .start()
                    .waitFor();
            }
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

}
