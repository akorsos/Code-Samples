
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JackTokenizer {

    //Declare keywords and symbols
    static String keyword = "KEYWORD";
    static String symbol = "SYMBOL";
    static String identifier = "IDENTIFIER";
    static String int_const = "INT_CONST";
    static String string_const = "STRING_CONST";

    final String[] keywords = {
            "class", "method", "function", "constructor",
            "int", "boolean", "char", "void", "var",
            "static", "field", "let", "do", "if",
            "else", "while", "return", "true",
            "false", "null", "this"
    };

    final char[] symbols = {
            '{', '}', '(', ')', '[', ']', '.', ',', ';',
            '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
    };

    public static String currentToken;
    int lineNumber;
    String[] currentLineTokens;
    int currentLineTokenIndex;

    BufferedReader br;

    //Constructor that opens a reader and sets up the tokenizer
    public JackTokenizer(String filename) {
        try {
            br = new BufferedReader(new FileReader(filename));
            currentLineTokenIndex = 0;
            lineNumber = 1;
        } catch (Exception e){
            System.out.println("Error reading file: " +e);
        }
    }

    //Function to check if the reader has more tokens
    public boolean hasMoreTokens() throws IOException {
        return br.ready();
    }

    //Function to retrieve next token
    public void advance() {
        try {
            //Checks to make sure that there's a token to get
            if (!hasMoreTokens()) {
                return;
            }

            //Makes sure the array of tokens isn't empty, ie starting
            if (currentLineTokenIndex == 0) {
                getLine();
            }

            do {
                currentToken = currentLineTokens[currentLineTokenIndex];
                currentLineTokenIndex++;
            } while (currentToken.isEmpty());

            if (currentLineTokenIndex == currentLineTokens.length) {
                currentLineTokenIndex = 0;
            }
        } catch (Exception e){
            System.out.println("Error getting next token: " +e);
        }
    }

    //Returns the type of the current token
    public String tokenType() {
        //First check of there is a token
        if(currentToken.isEmpty())
            return null;

        //Checks if token is a keyword
        for(int i=0; i < keywords.length; i++)
            if(currentToken.equals(keywords[i]))
                return keyword;

        //Checks if token is a symbol
        for(int i=0; i < symbols.length; i++)
            if(currentToken.charAt(0) == symbols[i])
                return symbol;

        //Checks if token is an identifier
        if(currentToken.matches("^[^\\d][\\w]*"))
            return identifier;

        //Checks if token is an int_const
        try {
            int i = Integer.parseInt(currentToken);
            if(i >= 0 && i <= 32767)
                return int_const;
        } catch (NumberFormatException nfe) {
            //Checks if token is a string_const
            if(currentToken.matches("^[\"]([(\\S| )&&[^\"]])*[\"]"))
                return string_const;
        }

        return null;
    }

    //Return keyword of current token, called when token is keyword
    public String keyword() {
        if(!tokenType().equals(keyword)) {
            return null;
        } else {
            return currentToken;
        }
    }

    //Return symbol of current token, called when token is symbol
    public char symbol() {
        if(!tokenType().equals(symbol)) {
            return '#';
        } else {
            return currentToken.charAt(0);
        }
    }

    //Return identifier of current token, called when token is identifier
    public String identifier() {
        if(!tokenType().equals(identifier)) {
            return null;
        } else {
            return currentToken;
        }
    }

    //Return integer value of current token, called when token is int_const
    public int intVal() {
        if(!tokenType().equals(int_const)) {
            return Integer.MIN_VALUE;
        } else {
            return Integer.parseInt(currentToken);
        }
    }

    //Return string value of current token, called when token is string_const
    public String stringVal() {
        if(!tokenType().equals(string_const)) {
            return null;
        } else {
            return currentToken.substring(1, currentToken.length() - 1);
        }
    }

    //Function to return the line number
    public int getNumLine() {
        return lineNumber;
    }

    public void getLine() throws IOException {
        String currentLine;

        do {
            currentLine = br.readLine();
            lineNumber++;

            //This makes the assumption that comments are only one line
            int line;
            if((line = currentLine.indexOf("/**")) != -1) {
                int line2;

                do {
                    line2 = currentLine.indexOf("*/");
                    currentLine = br.readLine();
                }while(line2 == -1);
            }

            if((line = currentLine.indexOf("//")) != -1) {
                currentLine = currentLine.substring(0, line);
            }

            //Send currentLine out to quotes() to handle ""
            currentLine = quotes(currentLine);

            //Parse and split the line into an array
            currentLine = currentLine.replaceAll("\\s+", " ");
            currentLineTokens = currentLine.split(" ");

        }while(currentLine.isEmpty() | currentLine.matches("\\s+"));

        List<String> list = new ArrayList<>();
        for(int i=0; i < currentLineTokens.length; i++){
            //Prevents OutOfBoundsExceptions
            if(currentLineTokens[i].isEmpty()) {
                continue;
            }

            String token = currentLineTokens[i];

            //Prevents OutOfBoundsExceptions
            if(token.isEmpty()) {
                continue;
            }

            boolean hasSymbol = false;
            for(int j=0; j < symbols.length; j++){

                if(token.equals(symbols[j]+"")) {
                    break;
                } else {

                    if(token.contains(symbols[j]+"")){
                        hasSymbol = true;
                        int previous = 0;

                        for(int current=0; current < token.length(); current++){

                            if(token.charAt(current) == '\"') {
                                current++;

                                while(token.charAt(current) != '\"') {
                                    current++;
                                }

                                String str = token.substring(previous, ++current);
                                str = str.replaceAll("#s@", " ");
                                list.add(str);
                                previous = current;
                            } else {

                                for(int l=0; l < symbols.length; l++){

                                    if(token.charAt(current) == symbols[l]){

                                        if(previous != current)
                                            list.add(token.substring(previous, current));
                                        list.add(symbols[l] + "");
                                        previous = current+1;
                                    }
                                }
                            }

                            if(previous < token.length() && current == token.length()-1) {
                                list.add(token.substring(previous, token.length()));
                            }
                        }
                        break;
                    }
                }
            }

            if(!hasSymbol) {
                list.add(currentLineTokens[i]);
            }
        }

        String[] out = new String[list.size()];

        for(int i=0; i < list.size(); i++) {
            out[i] = list.get(i);
        }

        currentLineTokens = out;
    }

    //Assume Quotes are on one line
    public String quotes(String s) {
        String line = s;
        int index;

        if((index = s.indexOf("\"")) != -1) {
            line = "";
            index++;
            line += s.substring(0, index);

            char character;
            while((character=s.charAt(index)) != '\"') {
                if(character != ' ') {
                    line += character;
                } else {
                    line += "#s@";
                }
                index++;
            }

            if(character == '\"') {
                line += "\"";
                index++;
                String tmp2 = s.substring(index, s.length());
                line += quotes(tmp2);
            }else {
                line += s;
            }
        }
        return line;
    }
}