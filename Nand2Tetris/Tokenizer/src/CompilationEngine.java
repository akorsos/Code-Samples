import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class CompilationEngine {
    private JackTokenizer jackTokenizer;
    private BufferedWriter bw;

    //Create new compilation even with and inout and output
    public CompilationEngine(String input, String output){
        File file = new File(output);

        try {
            if (!file.exists())
                file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            System.out.println(file.getAbsoluteFile());

            jackTokenizer = new JackTokenizer(input);
            bw = new BufferedWriter(fw);

            compileClass();
            bw.close();
        } catch (Exception e){
            System.out.println("Error creating file: " +e);
        }
    }

    //Compiles a complete class
    public void compileClass(){
        jackTokenizer.advance();

        try {
            bw.write("<class>\n");

            //Checks for class keyword
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("class")) {
                bw.write("<keyword> class </keyword>\n");
            }

            jackTokenizer.advance();

            //Checks for an identifier
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            }

            jackTokenizer.advance();

            //Checks for '{'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            jackTokenizer.advance();

            //Checks for a VarDec class
            while (jackTokenizer.keyword() != null &&
                    (jackTokenizer.keyword().equals("static") || jackTokenizer.keyword().equals("field"))) {
                compileClassVarDec();
            }

            //Checks for a Subroutine
            String t = jackTokenizer.keyword();
            while (t != null && (t.equals("constructor") | t.equals("function") | t.equals("method"))) {
                compileSubroutine();
                jackTokenizer.advance();
                t = jackTokenizer.keyword();
            }

            //Checks for '}'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            bw.write("</class>\n");
        } catch (Exception e){
            System.out.println("Error compiling class: " +e);
        }
    }

    //Compiles static declaration or a field declaration
    public void compileClassVarDec() {
        try {
            bw.write("<classVarDec>\n");

            if (jackTokenizer.keyword() != null &&
                    (jackTokenizer.keyword().equals("static") || jackTokenizer.keyword().equals("field"))) {
                bw.write("<keyword> " + jackTokenizer.keyword() + " </keyword>\n");
            }

            jackTokenizer.advance();
            String keyword = jackTokenizer.keyword();

            //Checks for a primitive type
            if (keyword != null && (keyword.equals("int") | keyword.equals("char") | keyword.equals("boolean"))) {
                bw.write("<keyword> " + keyword + " </keyword>\n");
            } else {
                if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }
            }

            jackTokenizer.advance();

            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            }

            jackTokenizer.advance();

            //Checks for ','
            while (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                bw.write("<symbol> , </symbol>\n");

                jackTokenizer.advance();

                if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }

                jackTokenizer.advance();
            }

            //Checks for ';'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
                bw.write("<symbol> ; </symbol>\n");
            }

            jackTokenizer.advance();

            bw.write("</classVarDec>\n");
        } catch(Exception e){
            System.out.println("Error compiling variable declaration class: " +e);
        }
    }

    public void compileSubroutine() {
        try {
            bw.write("<subroutineDec>\n");

            //constructor | function | method
            String t = jackTokenizer.keyword();
            if (t != null && (t.equals("constructor") | t.equals("function") | t.equals("method"))) {
                bw.write("<keyword> " + t + " </keyword>\n");
            }

            jackTokenizer.advance();
            t = jackTokenizer.keyword();

            //void to type
            if (t != null && (t.equals("void") | t.equals("int") | t.equals("char") | t.equals("boolean"))) {
                bw.write("<keyword> " + t + " </keyword>\n");
            } else {
                if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }
            }

            jackTokenizer.advance();

            //subroutineName
            if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            }

            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                bw.write("<symbol> ( </symbol>\n");
            }

            jackTokenizer.advance();
            //Handles a parameter list
            compileParameterList();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                bw.write("<symbol> ) </symbol>\n");
            }

            bw.write("<subroutineBody>\n");
            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            jackTokenizer.advance();

             while (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("var")) {
                compileVarDec();
                jackTokenizer.advance();
            }

            compileStatements();

            //Calling compileStatements prior makes an advance unnecessary
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            bw.write("</subroutineBody>\n");
            bw.write("</subroutineDec>\n");
        } catch (Exception e){
            System.out.println("Error compiling subroutine: " +e);
        }
    }

    //Compiles a parameter list, not including the enclosing "()"
    public void compileParameterList(){
        try {
            bw.write("<parameterList>\n");

            String keyword = jackTokenizer.keyword();
            String type = jackTokenizer.tokenType();

            if (keyword != null && type != null && (type.equals(JackTokenizer.keyword) ||
                    type.equals(JackTokenizer.identifier))) {
                if (type.equals(JackTokenizer.keyword) && (keyword.equals("int") | keyword.equals("char") |
                        keyword.equals("boolean"))) {
                    bw.write("<keyword> " + keyword + "</keyword>\n");
                } else {
                    if (type.equals(JackTokenizer.identifier)) {
                        bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                    }
                }

                jackTokenizer.advance();

                //varName
                if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }

                jackTokenizer.advance();

                //Check for ','
                while (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                    bw.write("<symbol> , </symbol>\n");

                    jackTokenizer.advance();
                    keyword = jackTokenizer.keyword();

                    //Check for primitive type
                    if (keyword != null && (keyword.equals("int") | keyword.equals("char") | keyword.equals("boolean"))) {
                        bw.write("<keyword> " + keyword + "</keyword>\n");
                    } else {
                        if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                            bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                        }
                    }

                    //varName
                    jackTokenizer.advance();
                    if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                        bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                    }

                    jackTokenizer.advance();
                }

            }

            bw.write("</parameterList>\n");
        } catch (Exception e){
            System.out.println("Error compiling parameter list: " +e);
        }
    }

    //Compiles a var declaration
    public void compileVarDec(){
        try {
            bw.write("<varDec>\n");

            //Check for 'var' keyword
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("var")) {
                bw.write("<keyword> var </keyword>\n");
            }

            jackTokenizer.advance();
            String t = jackTokenizer.keyword();

            //Check for primitive type
            if (t != null && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
                bw.write("<keyword> " + t + " </keyword>\n");
            } else {
                if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }
            }

            jackTokenizer.advance();

            //Check for variable name
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            }

            jackTokenizer.advance();

            //Checks for multiple variable declarations on the same line
            while (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                bw.write("<symbol> , </symbol>\n");

                jackTokenizer.advance();

                if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }

                jackTokenizer.advance();
            }

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
                bw.write("<symbol> ; </symbol>\n");
            }

            bw.write("</varDec>\n");
        } catch (Exception e){
            System.out.println("Error compiling variable declaration: " +e);
        }
    }

    //Compiles a sequence of statements, not including the enclosing "{}"
    public void compileStatements() {
        try {
            bw.write("<statements>\n");
            String keyword = jackTokenizer.keyword();

            while (keyword != null && (keyword.equals("let") || keyword.equals("if") || keyword.equals("while") ||
                    keyword.equals("do") || keyword.equals("return"))) {

                switch (keyword) {
                    case "let":
                        keyword.equals("let");
                        compileLet();
                        jackTokenizer.advance();
                        break;
                    case "if":
                        keyword.equals("if");
                        compileIf();
                        break;
                    case "while":
                        keyword.equals("while");
                        compileWhile();
                        jackTokenizer.advance();
                        break;
                    case "do":
                        keyword.equals("do");
                        compileDo();
                        jackTokenizer.advance();
                        break;
                    case "return":
                        keyword.equals("return");
                        compileReturn();
                        jackTokenizer.advance();
                        break;
                    default: //do nothing, cleaner than an if-else chain
                        break;
                }
                keyword = jackTokenizer.keyword();
            }

            bw.write("</statements>\n");
        } catch (Exception e){
            System.out.println("Error compiling statements: " +e);
        }
    }

    //Compiles a do statement
    public void compileDo(){
        try {
            bw.write("<doStatement>\n");

            //Checks for 'do'
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("do")) {
                bw.write("<keyword> do </keyword>\n");
            }

            jackTokenizer.advance();

            //subroutineName > className > varName
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            }

            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                bw.write("<symbol> ( </symbol>\n");
            } else {
                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '.') {
                    bw.write("<symbol> . </symbol>\n");

                    jackTokenizer.advance();

                    //subroutineName
                    if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                        bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                    }

                    jackTokenizer.advance();

                    if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                        bw.write("<symbol> ( </symbol>\n");
                    }
                }
            }

            jackTokenizer.advance();
            //Make this method advance to next token for all cases, like parameterList
            compileExpressionList();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                bw.write("<symbol> ) </symbol>\n");
            }

            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
                bw.write("<symbol> ; </symbol>\n");
            }

            bw.write("</doStatement>\n");
        } catch (Exception e){
            System.out.println("Error in compileDo: " +e);
        }
    }

    //Compiles a let statement
    public void compileLet() {
        try {
            bw.write("<letStatement>\n");

            //Checks for 'let'
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("let")) {
                bw.write("<keyword> let </keyword>\n");
            }

            //varName
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            }

            jackTokenizer.advance();

            //[] expression
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '[') {
                bw.write("<symbol> [ </symbol>\n");

                jackTokenizer.advance();
                compileExpression();

                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ']') {
                    bw.write("<symbol> ] </symbol>\n");
                }

                jackTokenizer.advance();
            }

            //Check for '='
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '=') {
                bw.write("<symbol> = </symbol>\n");
            }

            jackTokenizer.advance();
            compileExpression();

            //Check for ';'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
                bw.write("<symbol> ; </symbol>\n");
            }

            bw.write("</letStatement>\n");
        } catch (Exception e){
            System.out.println("Error in compileLet: " +e);
        }
    }

    //Compiles a while statement
    public void compileWhile() {
        try {
            bw.write("<whileStatement>\n");

            //Check for 'while' keyword
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("while")) {
                bw.write("<keyword> while </keyword>\n");
            }

            //Check for '('
            jackTokenizer.advance();
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                bw.write("<symbol> ( </symbol>\n");
            }

            jackTokenizer.advance();
            compileExpression();

            //Check for ')'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                bw.write("<symbol> ) </symbol>\n");
            }

            jackTokenizer.advance();

            //Check for  '{'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            //statements
            jackTokenizer.advance();
            compileStatements();

            //Check for  '}'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            bw.write("</whileStatement>\n");
        }catch (Exception e){
            System.out.println("Error in compileWhen: " +e);
        }
    }

    //Compiles a return statement
    public void compileReturn() {
        try {
            bw.write("<returnStatement>\n");

            //Checks for return keyword
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("return")) {
                bw.write("<keyword> return </keyword>\n");
            }

            jackTokenizer.advance();
            String type = jackTokenizer.tokenType();

            //Accounts for anything that may be after 'return'
            if (type != null && (type.equals(JackTokenizer.int_const) || type.equals(JackTokenizer.string_const) ||
                    type.equals(JackTokenizer.keyword) || type.equals(JackTokenizer.identifier)) ||
                    (type.equals(JackTokenizer.symbol)) && (jackTokenizer.symbol() == '(' ||
                            jackTokenizer.symbol() == '-' || jackTokenizer.symbol() == '~')) {
                compileExpression();
            }

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
                bw.write("<symbol> ; </symbol>\n");
            }

            bw.write("</returnStatement>\n");
        } catch (Exception e){
            System.out.println("Error in compileReturn: " +e);
        }
    }

    //Compiles an if statement. possibly with a trailing else clause.
    public void compileIf() {
        try {
            bw.write("<ifStatement>\n");

            //Checks for if
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("if")) {
                bw.write("<keyword> if </keyword>\n");
            }

            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                bw.write("<symbol> ( </symbol>\n");
            }

            //Checks all expressions, advances regardless of expression
            jackTokenizer.advance();
            compileExpression();


            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                bw.write("<symbol> ) </symbol>\n");
            }

            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            jackTokenizer.advance();
            compileStatements();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            jackTokenizer.advance();

            //Checks for an else
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("else")) {
                bw.write("<keyword> else </keyword>\n");

                jackTokenizer.advance();

                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                    bw.write("<symbol> { </symbol>\n");
                }

                jackTokenizer.advance();
                compileStatements();

                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                    bw.write("<symbol> } </symbol>\n");
                }

                //To match case when there isn't an else
                jackTokenizer.advance();
            }

            bw.write("</ifStatement>\n");
        } catch (Exception e) {
            System.out.println("Error in compileIf: " +e);
        }
    }

    //Compiles an expression
    public void compileExpression() {
        try {
            bw.write("<expression>\n");
            compileTerm();
            char symbol = jackTokenizer.symbol();

            while (symbol != '#' && (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' ||
                    symbol == '&' || symbol == '|' || symbol == '<' || symbol == '>' || symbol == '=')) {
                String task = "";

                switch (symbol) {
                    case '<':
                        symbol = '<';
                        task = "&lt;";
                        break;
                    case '>':
                        symbol = '<';
                        task = "&gt;";
                        break;
                    case '&':
                        symbol = '<';
                        task = "&amp;";
                        break;
                    default:
                        task = symbol + "";
                        break;
                }

                bw.write("<symbol> " + task + " </symbol>\n");

                jackTokenizer.advance();
                compileTerm();
                symbol = jackTokenizer.symbol();
            }

            bw.write("</expression>\n");
        } catch(Exception e){
            System.out.println("Error in compileExpression: " +e);
        }
    }

    //Compiles a term
    public void compileTerm() {
        try {
            bw.write("<term>\n");

            String type = jackTokenizer.tokenType();
            if (type != null) {

                //integerConstant
                if (type.equals(JackTokenizer.int_const)) {
                    bw.write("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>\n");
                    jackTokenizer.advance();
                } else {

                    //stringConstant
                    if (type.equals(JackTokenizer.string_const)) {
                        bw.write("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>\n");
                        jackTokenizer.advance();
                    } else {

                        //keywordConstant
                        if (type.equals(JackTokenizer.keyword)) {
                            bw.write("<keyword> " + jackTokenizer.keyword() + " </keyword>\n");
                            jackTokenizer.advance();
                        } else {

                            //varName
                            if (type.equals(JackTokenizer.identifier)) {

                                //just varName
                                bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");

                                //varName AND expression
                                jackTokenizer.advance();

                                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '[') {
                                    bw.write("<symbol> [ </symbol>\n");

                                    jackTokenizer.advance();
                                    compileExpression();

                                    if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ']') {
                                        bw.write("<symbol> ] </symbol>\n");
                                    }

                                    jackTokenizer.advance();
                                } else {

                                    //subroutineCall > subroutineName (expressions) . subroutineName (expressions)
                                    if (jackTokenizer.symbol() != '#' &&
                                            (jackTokenizer.symbol() == '(' || jackTokenizer.symbol() == '.')) {
                                        if (jackTokenizer.symbol() == '(') {
                                            bw.write("<symbol> ( </symbol>\n");
                                        } else {
                                            if (jackTokenizer.symbol() == '.') {
                                                bw.write("<symbol> . </symbol>\n");

                                                //subroutineName
                                                jackTokenizer.advance();

                                                if (jackTokenizer.tokenType() != null &&
                                                        jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                                                    bw.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                                                }

                                                jackTokenizer.advance();

                                                //Checks for '('
                                                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                                                    bw.write("<symbol> ( </symbol>\n");
                                                }
                                            }
                                        }

                                        jackTokenizer.advance();
                                        compileExpressionList();

                                        //Checks for ')'
                                        if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                                            bw.write("<symbol> ) </symbol>\n");
                                        }

                                        jackTokenizer.advance();
                                    }
                                }
                            } else {
                                //() expression
                                if (type.equals(JackTokenizer.symbol) && jackTokenizer.symbol() == '(') {
                                    bw.write("<symbol> ( </symbol>\n");

                                    jackTokenizer.advance();
                                    compileExpression();

                                    if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                                        bw.write("<symbol> ) </symbol>\n");
                                    }

                                    jackTokenizer.advance();
                                } else {
                                    if (type.equals(JackTokenizer.symbol) &&
                                            (jackTokenizer.symbol() == '-' || jackTokenizer.symbol() == '~')) {
                                        bw.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");

                                        jackTokenizer.advance();
                                        compileTerm();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        bw.write("</term>\n");
        } catch (Exception e){
            System.out.println("Error in compileTerm: " +e);
        }
    }

    //Compiles a comma-separated list of expressions
    public void compileExpressionList() throws IOException {
        bw.write("<expressionList>\n");

        String t = jackTokenizer.tokenType();

        if(t != null && (t.equals(JackTokenizer.int_const) || t.equals(JackTokenizer.string_const) ||
                t.equals(JackTokenizer.keyword) ||
                t.equals(JackTokenizer.identifier)) || (t.equals(JackTokenizer.symbol)) &&
                (jackTokenizer.symbol() == '(' || jackTokenizer.symbol() == '-' || jackTokenizer.symbol() == '~' )) {

            compileExpression();

            while(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                bw.write("<symbol> , </symbol>\n");

                jackTokenizer.advance();
                compileExpression();
            }
        }

        bw.write("</expressionList>\n");
    }
}