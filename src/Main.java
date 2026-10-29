import java.io.*;

public class Main {

    public static void main(String[] args) {

        Lexer lexer = new Lexer();

        lexer.tokenize("input.txt");

        try {
            PrintWriter out = new PrintWriter("output.txt");

            out.println("Token\tLexeme\tLine No");

            for (Token token : lexer.getTokens()) {
                out.println(
                        token.getType() + "\t" +
                                token.getLexeme() + "\t" +
                                token.getLineNo()
                );
            }

            if (!lexer.getErrors().isEmpty()) {
                out.println();
                out.println("Errors");

                for (String error : lexer.getErrors()) {
                    out.println(error);
                }
            }

            out.close();

            System.out.println("Lexical analysis completed.");
            System.out.println("Check output.txt");

        } catch (IOException e) {
            System.out.println("Unable to create output file.");
        }
    }
}