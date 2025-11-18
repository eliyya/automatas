enum TokenKind {
    // ...
    COMMA
}
type Token = {
    tokenKind: TokenKind
    value: string
}
type Handler = (lexer: Lexer, regex: RegExp) => any
type RegexPattern = [RegExp, Handler]

class Lexer {
    tokens: Token[] = []
    source: string = ""
    pos: number = 0
    patterns: RegexPattern[] = [
        [/,/, defaultHandler(TokenKind.COMMA, ",")]
    ]
}

function defaultHandler(kind: TokenKind, value: String): Handler {
    return (lexer, regex) => {
        //...
    }
}