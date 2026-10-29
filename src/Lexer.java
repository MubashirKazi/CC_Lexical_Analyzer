import java.io.*;
import java.util.*;

public class Lexer {

    private final Set<String> keywords = Set.of(
            "auto", "break", "case", "char", "const", "continue",
            "default", "do", "double", "else", "enum", "extern",
            "float", "for", "goto", "if", "int", "long", "register",
            "return", "short", "signed", "sizeof", "static", "struct",
            "switch", "typedef", "union", "unsigned", "void",
            "volatile", "while"
    );

    private final Set<String> operators = Set.of(
            "+", "-", "*", "/", "%", "=",
            "==", "!=", "<", ">", "<=", ">=",
            "&&", "||", "!", "++", "--",
            "+=", "-=", "*=", "/=", "%=",
            "&", "|", "^", "?", ":"
    );

    private final Set<Character> separators = Set.of(
            ';', ',', '(', ')', '{', '}', '[', ']'
    );

    private List<Token> tokens = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public void tokenize(String fileName) {
        boolean inComment = false;
        int commentLine = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                int i = 0;
                boolean error = false;

                while (i < line.length()) {

                    if (inComment) {
                        int end = line.indexOf("*/", i);

                        if (end == -1) {
                            break;
                        }

                        inComment = false;
                        i = end + 2;
                        continue;
                    }

                    char ch = line.charAt(i);

                    if (Character.isWhitespace(ch)) {
                        i++;
                        continue;
                    }

                    if (ch == '/' && i + 1 < line.length()
                            && line.charAt(i + 1) == '*') {
                        inComment = true;
                        commentLine = lineNo;
                        i += 2;
                        continue;
                    }

                    if (Character.isLetter(ch)) {
                        int start = i;

                        while (i < line.length() &&
                                (Character.isLetterOrDigit(line.charAt(i))
                                        || line.charAt(i) == '_')) {
                            i++;
                        }

                        String word = line.substring(start, i);

                        if (keywords.contains(word)) {
                            tokens.add(new Token(TokenType.KEYWORD, word, lineNo));
                        } else {
                            tokens.add(new Token(TokenType.IDENTIFIER, word, lineNo));
                        }

                        continue;
                    }

                    if (Character.isDigit(ch)) {
                        int start = i;

                        while (i < line.length() &&
                                Character.isDigit(line.charAt(i))) {
                            i++;
                        }

                        if (i < line.length() &&
                                (Character.isLetter(line.charAt(i))
                                        || line.charAt(i) == '_')) {

                            while (i < line.length() &&
                                    (Character.isLetterOrDigit(line.charAt(i))
                                            || line.charAt(i) == '_')) {
                                i++;
                            }

                            String word = line.substring(start, i);

                            errors.add("Line " + lineNo +
                                    ": Invalid identifier '" + word + "'");
                            error = true;
                            break;
                        }

                        String number = line.substring(start, i);
                        tokens.add(new Token(TokenType.INTEGER, number, lineNo));
                        continue;
                    }

                    if (ch == '"') {
                        int end = i + 1;

                        while (end < line.length() && line.charAt(end) != '"') {
                            end++;
                        }

                        if (end == line.length()) {
                            errors.add("Line " + lineNo +
                                    ": String constant exceeds line");
                            error = true;
                            break;
                        }

                        String value = line.substring(i, end + 1);
                        tokens.add(new Token(TokenType.STRING, value, lineNo));
                        i = end + 1;
                        continue;
                    }

                    if (ch == '\'') {
                        int end = i + 1;

                        while (end < line.length() && line.charAt(end) != '\'') {
                            end++;
                        }

                        if (end == line.length()) {
                            errors.add("Line " + lineNo +
                                    ": Char constant too long");
                            error = true;
                            break;
                        }

                        String value = line.substring(i + 1, end);

                        if (value.length() != 1) {
                            errors.add("Line " + lineNo +
                                    ": Char constant too long");
                            error = true;
                            break;
                        }

                        tokens.add(new Token(
                                TokenType.CHAR,
                                line.substring(i, end + 1),
                                lineNo
                        ));

                        i = end + 1;
                        continue;
                    }

                    String twoChars = "";

                    if (i + 1 < line.length()) {
                        twoChars = line.substring(i, i + 2);
                    }

                    if (operators.contains(twoChars)) {
                        tokens.add(new Token(
                                TokenType.OPERATOR,
                                twoChars,
                                lineNo
                        ));
                        i += 2;
                        continue;
                    }

                    if (operators.contains(String.valueOf(ch))) {
                        tokens.add(new Token(
                                TokenType.OPERATOR,
                                String.valueOf(ch),
                                lineNo
                        ));
                        i++;
                        continue;
                    }

                    if (separators.contains(ch)) {
                        tokens.add(new Token(
                                TokenType.SEPARATOR,
                                String.valueOf(ch),
                                lineNo
                        ));
                        i++;
                        continue;
                    }

                    errors.add("Line " + lineNo +
                            ": Undefined symbol '" + ch + "'");
                    error = true;
                    break;
                }

                if (error) {
                    continue;
                }
            }

            br.close();

            if (inComment) {
                errors.add("Line " + commentLine +
                        ": Un-terminated comment");
            }

        } catch (IOException e) {
            errors.add("File error: " + e.getMessage());
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<String> getErrors() {
        return errors;
    }
}