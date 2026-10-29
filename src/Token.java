public class Token {
    private TokenType type;
    private String lexeme;
    private int lineNo;

    public Token(TokenType type, String lexeme, int lineNo) {
        this.type = type;
        this.lexeme = lexeme;
        this.lineNo = lineNo;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNo() {
        return lineNo;
    }
}