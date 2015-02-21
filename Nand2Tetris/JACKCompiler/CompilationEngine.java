
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class CompilationEngine {
    JackTokenizer jackTokenizer;
    SymbolTable symbolTable;
    VMWriter vw;
    BufferedWriter bw;

    String className, buffer;
    final String ifTrue = "IF_TRUE";
    final String ifFalse = "IF_FALSE";
    final String ifEnd = "IF_END";
    final String whileExp = "WHILE_EXP";
    final String whileEnd = "WHILE_END";

    int parameters, ifCtr, whileCtr;

    //Create new compilation engine with and input and output
    public CompilationEngine(String input, String output){
        try {
            className = "";
            buffer = "";
            parameters = 0;
            ifCtr = 0;
            whileCtr = 0;
            File file = new File(output + ".xml");

            if (!file.exists())
                file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());

            symbolTable = new SymbolTable();
            jackTokenizer = new JackTokenizer(input);
            bw = new BufferedWriter(fw);
            vw = new VMWriter(output + ".vm");

            compileClass();
            bw.close();
        } catch (Exception e){
            System.out.println("Error creating file: " +e);
        }
    }

    //Compiles a complete class
    public void compileClass() {
        try {
            jackTokenizer.advance();

            bw.write("<class>\n");

            //Checks for class keyword
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("class")) {
                bw.write("<keyword> class </keyword>\n");
            }

            jackTokenizer.advance();

            //Checks for an identifier
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                String add = " (class, defined)";
                className = jackTokenizer.identifier();
                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }

            //Checks for '{'
            jackTokenizer.advance();
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            //Checks for a VarDec class
            jackTokenizer.advance();
            while (jackTokenizer.keyword() != null &&
                    (jackTokenizer.keyword().equals("static") || jackTokenizer.keyword().equals("field"))) {
                compileClassVarDec();
            }

            //Checks for a Subroutine
            String kw = jackTokenizer.keyword();
            while (kw != null && (kw.equals("constructor") | kw.equals("function") | kw.equals("method"))) {
                symbolTable.startSubroutine(kw);
                CompileSubroutine();
                jackTokenizer.advance();
                kw = jackTokenizer.keyword();
            }

            //Checks for '}'
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            bw.write("</class>\n");
            vw.close();
        } catch (Exception e){
            System.out.println("Error creating file: " +e);
        }
    }

    //Compiles static declaration or a field declaration
    public void compileClassVarDec() {
        try {
            bw.write("<classVarDec>\n");

            String name, kind = "";
            String def = "defined";
            String type = "";

            //Checks if Static or Field
            if (jackTokenizer.keyword() != null && (jackTokenizer.keyword().equals("static") ||
                    jackTokenizer.keyword().equals("field"))) {
                if (jackTokenizer.keyword().equals("static")) {
                    kind = SymbolTable.Static;
                } else {
                    kind = SymbolTable.Field;
                }

                bw.write("<keyword> " + jackTokenizer.keyword() + " </keyword>\n");
            }

            jackTokenizer.advance();
            String t = jackTokenizer.keyword();

            //Checks for a primitive type
            if (t != null && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
                type = t;
                bw.write("<keyword> " + t + " </keyword>\n");
            } else {
                if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    String add = "";
                    if (symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                            symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                        add = " (class, used)";
                    }

                    type = jackTokenizer.identifier();
                    bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
                }
            }

            jackTokenizer.advance();

            //Checks variable name
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                name = jackTokenizer.identifier();

                //If varName used is not found in table, its a valid name for a variable
                if (symbolTable.indexOf(name) == -1) {
                    symbolTable.define(name, type, kind);
                }

                String add = " (" + symbolTable.kindOf(name) + ", " + def + ", " + symbolTable.indexOf(name) + ")";
                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }

            jackTokenizer.advance();

            //Checks for ','
            while (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                bw.write("<symbol> , </symbol>\n");

                jackTokenizer.advance();

                if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    name = jackTokenizer.identifier();
                    if (symbolTable.indexOf(name) == -1) {
                        symbolTable.define(name, type, kind);
                    }

                    String add = " (" + symbolTable.kindOf(name) + ", " + def + ", " + symbolTable.indexOf(name) + ")";
                    bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
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

    //Compiles a complete method, function, or constructor
    public void CompileSubroutine(){
        try {
            bw.write("<subroutineDec>\n");

            String name, symbolType, kind, subType = "";
            String def = "defined";
            String functionName = "";


            //Checks for constructor, function, or method
            String kw = jackTokenizer.keyword();
            if (kw != null && (kw.equals("constructor") || kw.equals("function") || kw.equals("method"))) {
                subType = kw;
                bw.write("<keyword> " + kw + " </keyword>\n");
            }


            jackTokenizer.advance();
            kw = jackTokenizer.keyword();

            //Checks for void or type
            if (kw != null && (kw.equals("void") | kw.equals("int") | kw.equals("char") | kw.equals("boolean"))) {
                bw.write("<keyword> " + kw + " </keyword>\n");
            } else {
                if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    String add = "";
                    if (symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                            symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                        add = " (class, used)";
                    }

                    bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
                }
            }

            //subroutineName
            jackTokenizer.advance();
            if (jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                String add = "";
                if (symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                        symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                    add = " (subroutine, defined)";
                }


                functionName = jackTokenizer.identifier();
                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }

            //Checks for '('
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

            //Checks for a varDec
            jackTokenizer.advance();
            while (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("var")) {
                compileVarDec();
                jackTokenizer.advance();
            }

            vw.writeFunction(className + "." + functionName, symbolTable.varCount(SymbolTable.Var));

            if (subType.equals("constructor")) {
                vw.writePush("CONST", symbolTable.varCount(SymbolTable.Field));
                vw.writeCall("Memory.alloc", 1);
                vw.writePop("POINTER", 0);
            } else {
                if (subType.equals("method")) {
                    vw.writePush(SymbolTable.Arg, 0);
                    vw.writePop("POINTER", 0);
                }
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


            String name, kind, def = "";
            kind = SymbolTable.Arg;
            def = "defined";
            String symbolType = "";

            String kw = jackTokenizer.keyword();
            String tt = jackTokenizer.tokenType();

            if ((kw != null && tt != null && (tt.equals(JackTokenizer.keyword)) ||
                    tt.equals(JackTokenizer.identifier))){

                //Checks for type
                if (tt.equals(JackTokenizer.keyword) && (kw.equals("int") | kw.equals("char") | kw.equals("boolean"))) {
                    symbolType = kw;
                    bw.write("<keyword> " + kw + "</keyword>\n");
                } else {

                    if (tt.equals(JackTokenizer.identifier)) {
                        symbolType = jackTokenizer.identifier();
                        String add = "";

                        if (symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                                symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                            add = " (class, used)";
                        }

                        bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
                    }
                }

                jackTokenizer.advance();

                //Checks variable name
                if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                    name = jackTokenizer.identifier();

                    if (symbolTable.indexOf(name) == -1) {
                        symbolTable.define(name, symbolType, kind);
                    }

                    String add = " (" + symbolTable.kindOf(name) + ", " + def + ", " + symbolTable.indexOf(name) + ")";
                    bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
                }

                jackTokenizer.advance();

                while (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                    bw.write("<symbol> , </symbol>\n");

                    jackTokenizer.advance();

                    //Checks for type
                    kw = jackTokenizer.keyword();
                    if (kw != null && (kw.equals("int") | kw.equals("char") | kw.equals("boolean"))) {
                        symbolType = kw;
                        bw.write("<keyword> " + kw + "</keyword>\n");
                    } else {

                        if (jackTokenizer.tokenType() != null &&
                                jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                            symbolType = jackTokenizer.identifier();
                            String add = " (class, used)";
                            bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
                        }
                    }

                    jackTokenizer.advance();

                    if (jackTokenizer.tokenType() != null &&
                            jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                        name = jackTokenizer.identifier();

                        if (symbolTable.indexOf(name) == -1) {
                            symbolTable.define(name, symbolType, kind);
                        }

                        String add = " (" +symbolTable.kindOf(name) + ", " + def + ", " + symbolTable.indexOf(name)+")";
                        bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
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
    public void compileVarDec() throws IOException {
        bw.write("<varDec>\n");

        String name, kind, def = "";
        String type = "";
        kind = SymbolTable.Var;
        def = "defined";

        //Checks for variable
        if(jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("var")) {
            bw.write("<keyword> var </keyword>\n");
        }

        jackTokenizer.advance();
        String t = jackTokenizer.keyword();

        //Checks for primitive type
        if(t != null && (t.equals("int") | t.equals("char") | t.equals("boolean"))) {
            type = t;
            bw.write("<keyword> " + t + " </keyword>\n");
        } else {

            if(jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                type = jackTokenizer.identifier();
                String add = "";

                if(symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                        symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                    add = " (class, used)";
                }

                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }
        }

        jackTokenizer.advance();

        //Checks variable name
        if(jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
            name = jackTokenizer.identifier();

            if(symbolTable.indexOf(name) == -1) {
                symbolTable.define(name, type, kind);
            }

            String add = " (" + symbolTable.kindOf(name) + ", " + def + ", " + symbolTable.indexOf(name) + ")";
            bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
        }

        jackTokenizer.advance();

        //Checks for multiple variable declarations on the same line
        while(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
            bw.write("<symbol> , </symbol>\n");

            jackTokenizer.advance();

            if(jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                name = jackTokenizer.identifier();

                if(symbolTable.indexOf(name) == -1) {
                    symbolTable.define(name, type, kind);
                }

                String add = " (" + symbolTable.kindOf(name) + ", " + def + ", " + symbolTable.indexOf(name) + ")";
                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }

            jackTokenizer.advance();
        }

        if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
            bw.write("<symbol> ; </symbol>\n");
        }

        bw.write("</varDec>\n");
    }

    //Compiles a sequence of statements, not including the enclosing "{}"
    public void compileStatements() {
        try {
            bw.write("<statements>\n");

            String keyword = jackTokenizer.keyword();
            while (keyword != null && (keyword.equals("let") || keyword.equals("if") ||
                    keyword.equals("while") || keyword.equals("do") || keyword.equals("return"))) {

                switch (keyword) {
                    case "let":
                        compileLet();
                        jackTokenizer.advance();
                        break;
                    case "if":
                        compileIf();
                        break;
                    case "while":
                        compileWhile();
                        jackTokenizer.advance();
                        break;
                    case "do":
                        compileDo();
                        jackTokenizer.advance();
                        break;
                    case "return":
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
    public void compileDo() {
        try{
            bw.write("<doStatement>\n");
            String def = "used";
            buffer = "";

            //Checks for 'do'
            if(jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("do")) {
                buffer += "do ";
                bw.write("<keyword> do </keyword>\n");
            }

            String vmSubName = "";
            jackTokenizer.advance();

            //subroutineName > className > varName
            if(jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                String add = "";

                if(symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                        symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                    add = " (subroutine or class, used) ";
                } else {
                    add = " (" + symbolTable.kindOf(jackTokenizer.identifier()) + ", " + def + ", " +
                            symbolTable.indexOf(jackTokenizer.identifier()) + ") ";
                }

                vmSubName = jackTokenizer.identifier();
                buffer += jackTokenizer.identifier();
                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }

            jackTokenizer.advance();

            if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                buffer += "(";
                bw.write("<symbol> ( </symbol>\n");
            } else {

                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '.') {
                    String obj = vmSubName;
                    vmSubName += ".";
                    buffer += ".";
                    bw.write("<symbol> . </symbol>\n");

                    //If obj is found in SymbolTableValues, the subroutine is a method object calling to be pushed as arg 0
                    if (symbolTable.indexOf(obj) != -1) {
                        vw.writePush(symbolTable.kindOf(obj), symbolTable.indexOf(obj));
                        parameters++;
                        vmSubName = symbolTable.typeOf(obj) + ".";
                    }

                    jackTokenizer.advance();

                    //subroutineName
                    if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                        String add = "";

                        if (symbolTable.indexOf(jackTokenizer.identifier()) == -1 &&
                                symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                            add = " (subroutine, used)";
                        }

                        vmSubName += jackTokenizer.identifier();
                        buffer += jackTokenizer.identifier();
                        bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
                    }

                    jackTokenizer.advance();

                    if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                        buffer += "(";
                        bw.write("<symbol> ( </symbol>\n");
                    }
                }
            }

            //If not in aaa.bbb() form, its a method call with a 'this' object
            int subIndex = vmSubName.indexOf('.');
            if (subIndex == -1) {
                vw.writePush("POINTER", 0);
                vmSubName = className + "." + vmSubName;
                parameters++;
            }

            jackTokenizer.advance();
            compileExpressionList();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                buffer += ")";
                bw.write("<symbol> ) </symbol>\n");
            }

            vw.writeCall(vmSubName, parameters);
            vw.writePop("TEMP", 0);
            parameters = 0;

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

            String def = "used";
            buffer = "";
            boolean array = false;

            //Checks for a 'let'
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("let")) {
                buffer += "let ";
                bw.write("<keyword> let </keyword>\n");
            }

            String vName = "";

            //varName
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                String add = "";

                if (symbolTable.indexOf(jackTokenizer.identifier()) != -1 &&
                        !symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                    add = " (" + symbolTable.kindOf(jackTokenizer.identifier()) + ", " + def + ", "
                            + symbolTable.indexOf(jackTokenizer.identifier()) + ")";
                }

                vName = jackTokenizer.identifier();
                buffer += vName + " ";

                bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");
            }

            //Checks for an array
            jackTokenizer.advance();
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '[') {
                array = true;

                //Open bracket
                buffer += "[";
                bw.write("<symbol> [ </symbol>\n");
                vw.writePush(symbolTable.kindOf(vName), symbolTable.indexOf(vName));

                //Expression
                jackTokenizer.advance();
                compileExpression();

                vw.writeArithmetic("ADD");

                //Close bracket
                if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ']') {
                    buffer += "]";
                    bw.write("<symbol> ] </symbol>\n");
                }

                jackTokenizer.advance();
            }

            //Checks for '='
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '=') {
                buffer += "= ";

                if (array) {
                    //expression
                    jackTokenizer.advance();
                    compileExpression();
                    vw.writePop("TEMP", 0);
                    vw.writePop("POINTER", 1);
                    vw.writePush("TEMP", 0);
                } else {
                    jackTokenizer.advance();
                    compileExpression();
                }

                bw.write("<symbol> = </symbol>\n");
            }

            //Assumes the value on the right was already pushed to stack, a lot less code
            if (!symbolTable.kindOf(vName).equals("NONE")) {
                if (array) {
                    vw.writePop("THAT", 0);
                } else {
                    vw.writePop(symbolTable.kindOf(vName), symbolTable.indexOf(vName));
                }
            }

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
            buffer = "";

            //Checks for while
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("while")) {
                buffer += "while";
                bw.write("<keyword> while </keyword>\n");
            }

            vw.writeLabel(whileExp + whileCtr);

            //Open (
            jackTokenizer.advance();
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                buffer += "(";
                bw.write("<symbol> ( </symbol>\n");
            }

            //Expression
            jackTokenizer.advance();
            compileExpression();

            //Close )
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                buffer += ")";
                bw.write("<symbol> ) </symbol>\n");
            }

            vw.writeArithmetic("NOT");
            jackTokenizer.advance();

            //Open {
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            vw.writeIf(whileEnd + whileCtr);
            int whctr2 = whileCtr++;

            //Statements
            jackTokenizer.advance();
            compileStatements();

            vw.writeGoto(whileExp + whctr2);

            //Close }
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            vw.writeLabel(whileEnd + whctr2);
            bw.write("</whileStatement>\n");
        } catch (Exception e){
            System.out.println("Error in compileWhile: " +e);
        }
    }

    //Compiles a return statement
    public void compileReturn() throws IOException {
        try {
            bw.write("<returnStatement>\n");

            buffer = "";

            //Check for return
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("return")) {
                buffer += "return ";
                bw.write("<keyword> return </keyword>\n");
            }

            jackTokenizer.advance();
            String t = jackTokenizer.tokenType();

            if (t != null && (t.equals(JackTokenizer.int_const) || t.equals(JackTokenizer.string_const) ||
                    t.equals(JackTokenizer.keyword) || t.equals(JackTokenizer.identifier)) || (t.equals(JackTokenizer.symbol)) &&
                    (jackTokenizer.symbol() == '(' || jackTokenizer.symbol() == '-' || jackTokenizer.symbol() == '~')) {
                compileExpression();
                vw.writeReturn();

            } else {
                //Catches a void subRoutine case
                vw.writePush("CONST", 0);
                vw.writeReturn();
            }

            //jackTokenizer.advance();
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ';') {
                bw.write("<symbol> ; </symbol>\n");
            }

            bw.write("</returnStatement>\n");
        } catch (Exception e){
            System.out.println("Error in compileReturn: " +e);
        }
    }

    //Compiles an if statement, possibly with a trailing else
    public void compileIf(){
        try {
            bw.write("<ifStatement>\n");
            buffer = "";

            //Checks for an if
            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("if")) {
                buffer += "if";
                bw.write("<keyword> if </keyword>\n");
            }

            jackTokenizer.advance();

            //Open (
            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                buffer += "(";
                bw.write("<symbol> ( </symbol>\n");
            }


            //Needs to check all expression cases
            jackTokenizer.advance();
            compileExpression();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                buffer += ")";
                bw.write("<symbol> ) </symbol>\n");
            }

            vw.writeIf(ifTrue + ifCtr);
            vw.writeGoto(ifFalse + ifCtr);

            //Helps handle nested if-else
            int ifctr2 = ifCtr++;
            jackTokenizer.advance();

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '{') {
                bw.write("<symbol> { </symbol>\n");
            }

            vw.writeLabel(ifTrue + ifctr2);
            jackTokenizer.advance();
            compileStatements();

            vw.writeGoto(ifEnd + ifctr2);
            vw.writeLabel(ifFalse + ifctr2);

            if (jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '}') {
                bw.write("<symbol> } </symbol>\n");
            }

            jackTokenizer.advance();

            if (jackTokenizer.keyword() != null && jackTokenizer.keyword().equals("else")) {
                buffer = "else";
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

                jackTokenizer.advance();
            }

            vw.writeLabel(ifEnd + ifctr2);
            bw.write("</ifStatement>\n");
        } catch (Exception e) {
            System.out.println("Error in compileIf: " +e);
        }
    }

    //Compiles an expression
    public void compileExpression() throws IOException {
        bw.write("<expression>\n");

        compileTerm();
        char symbol = jackTokenizer.symbol();

        while (symbol != '#' && (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' ||
                symbol == '&' || symbol == '|' || symbol == '<' || symbol == '>' || symbol == '=')) {
            String task = "";

            switch (symbol) {
                case '<':
                    task = "&lt;";
                    break;
                case '>':
                    task = "&gt;";
                    break;
                case '&':
                    task = "&amp;";
                    break;
                default:
                    task = symbol + "";
                    break;
            }

            buffer += symbol;
            bw.write("<symbol> " + task + " </symbol>\n");

            jackTokenizer.advance();
            compileTerm();

            //Assumes everythings been pushed
            switch(symbol) {
                case '+':vw.writeArithmetic("ADD");
                    break;
                case '-':vw.writeArithmetic("SUB");
                    break;
                case '&':vw.writeArithmetic("AND");
                    break;
                case '|':vw.writeArithmetic("OR");
                    break;
                case '<':vw.writeArithmetic("LT");
                    break;
                case '>':vw.writeArithmetic("GT");
                    break;
                case '=':{vw.writeArithmetic("EQ");
                    break;}
                case '*':vw.writeCall("Math.multiply", 2);
                    break;
                case '/':vw.writeCall("Math.divide", 2);
                    break;
            }

            symbol = jackTokenizer.symbol();
        }

        bw.write("</expression>\n");
    }

    //Compiles a term
    public void compileTerm() throws IOException {
        bw.write("<term>\n");
        String def = "used";
        String type = jackTokenizer.tokenType();

        if(type != null) {

            //integerConstant
            if(type.equals(JackTokenizer.int_const)) {
                buffer += jackTokenizer.intVal();
                bw.write("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>\n");
                vw.writePush("CONST", jackTokenizer.intVal());

                jackTokenizer.advance();
            } else {

                //stringConstant
                if(type.equals(JackTokenizer.string_const)) {
                    String str = jackTokenizer.stringVal();
                    buffer += "\"" + jackTokenizer.stringVal() + "\"";
                    bw.write("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>\n");

                    jackTokenizer.advance();

                    vw.writePush("CONST", str.length());
                    vw.writeCall("String.new", 1);
                    for(int i=0; i < str.length(); i++) {
                        vw.writePush("CONST", (int)str.charAt(i));
                        vw.writeCall("String.appendChar", 2);
                    }
                } else {

                    //keywordConstant
                    if(type.equals(JackTokenizer.keyword)) {
                        buffer += jackTokenizer.keyword();
                        bw.write("<keyword> " + jackTokenizer.keyword() + " </keyword>\n");

                        if(jackTokenizer.keyword().equals("false"))
                            vw.writePush("CONST", 0);
                        else if(jackTokenizer.keyword().equals("true")) {
                            vw.writePush("CONST", 0);
                            vw.writeArithmetic("NOT");
                        }
                        else if(jackTokenizer.keyword().equals("null"))
                            vw.writePush("CONST", 0);
                        else if(jackTokenizer.keyword().equals("this"))
                            vw.writePush("POINTER", 0);

                        jackTokenizer.advance();
                    } else {

                        //varName > subroutineName > className
                        if(type.equals(JackTokenizer.identifier)) {
                            boolean array = false;

                            String vmSubName = "";
                            String obj = jackTokenizer.identifier();
                            String add = "";

                            if(symbolTable.indexOf(jackTokenizer.identifier()) == -1 && symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                                add = " (class or subroutine, " + def + ")";
                            } else {
                                add = " (" + symbolTable.kindOf(jackTokenizer.identifier()) + ", " + def + ", " +
                                        symbolTable.indexOf(jackTokenizer.identifier()) + ")";
                            }

                            //just varName
                            bw.write("<identifier> " + jackTokenizer.identifier() + add + " </identifier>\n");

                            vw.writePush(symbolTable.kindOf(jackTokenizer.identifier()), symbolTable.indexOf(jackTokenizer.identifier()));
                            vmSubName = jackTokenizer.identifier();
                            buffer += jackTokenizer.identifier();

                            jackTokenizer.advance();

                            //varName AND expression
                            if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '[') {
                                array = true;
                                buffer += "[";
                                bw.write("<symbol> [ </symbol>\n");

                                jackTokenizer.advance();
                                compileExpression();

                                vw.writeArithmetic("ADD");

                                if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ']') {
                                    buffer += "]";
                                    bw.write("<symbol> ] </symbol>\n");
                                }


                                jackTokenizer.advance();

                                vw.writePop("POINTER", 1);
                                vw.writePush("THAT", 0);
                            } else {

                                //subroutineCall > subroutineName (expressions) . subroutineName (expressions)
                                if(jackTokenizer.symbol() != '#' && (jackTokenizer.symbol() == '(' || jackTokenizer.symbol() == '.')) {

                                    if(jackTokenizer.symbol() == '(') {
                                        buffer += "(";
                                        bw.write("<symbol> ( </symbol>\n");
                                    } else {

                                        if(jackTokenizer.symbol() == '.') {
                                            buffer += ".";

                                            if(symbolTable.indexOf(vmSubName) != -1) {
                                                parameters++;
                                                vmSubName = symbolTable.typeOf(vmSubName);
                                            }

                                            vmSubName += ".";
                                            bw.write("<symbol> . </symbol>\n");

                                            jackTokenizer.advance();

                                            //subroutineName
                                            if(jackTokenizer.tokenType() != null && jackTokenizer.tokenType().equals(JackTokenizer.identifier)) {
                                                String add2 = "";

                                                if(symbolTable.indexOf(jackTokenizer.identifier()) == -1
                                                        && symbolTable.kindOf(jackTokenizer.identifier()).equals("NONE")) {
                                                    add2 = " (subroutine, used)";
                                                }

                                                vmSubName += jackTokenizer.identifier();
                                                buffer += jackTokenizer.identifier();
                                                bw.write("<identifier> " + jackTokenizer.identifier() + add2 + " </identifier>\n");
                                            }

                                            jackTokenizer.advance();

                                            //Opening (
                                            if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == '(') {
                                                buffer += "(";
                                                bw.write("<symbol> ( </symbol>\n");
                                            }
                                        }
                                    }

                                    //Expression
                                    jackTokenizer.advance();
                                    compileExpressionList();

                                    //Closing )
                                    if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                                        buffer += ")";
                                        bw.write("<symbol> ) </symbol>\n");
                                    }

                                    //Assumes compileExpressionList pushed all parameters to stack
                                    vw.writeCall(vmSubName, parameters);
                                    parameters = 0;

                                    jackTokenizer.advance();
                                }
                            }

                        } else {

                            //() Expression
                            if(type.equals(JackTokenizer.symbol) && jackTokenizer.symbol() == '(') {
                                buffer += "(";
                                bw.write("<symbol> ( </symbol>\n");

                                jackTokenizer.advance();
                                compileExpression();

                                if(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ')') {
                                    buffer += ")";
                                    bw.write("<symbol> ) </symbol>\n");
                                }

                                jackTokenizer.advance();
                            } else {

                                //Checks for '-' or '~'
                                if(type.equals(JackTokenizer.symbol) && (jackTokenizer.symbol() == '-' ||
                                        jackTokenizer.symbol() == '~')) {
                                    buffer += jackTokenizer.symbol();
                                    bw.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");

                                    String command = "";
                                    if(jackTokenizer.symbol() == '-') {
                                        command = "NEG";
                                    } else {
                                        command = "NOT";
                                    }

                                    jackTokenizer.advance();
                                    compileTerm();

                                    vw.writeArithmetic(command);
                                }
                            }
                        }
                    }
                }
            }
        }

        bw.write("</term>\n");
    }

    //Compiles a comma-separated list of expressions
    public void compileExpressionList() throws IOException {
        bw.write("<expressionList>\n");


        String t = jackTokenizer.tokenType();
        if(t != null && (t.equals(JackTokenizer.int_const) || t.equals(JackTokenizer.string_const) ||
                t.equals(JackTokenizer.keyword) || t.equals(JackTokenizer.identifier)) ||
                (t.equals(JackTokenizer.symbol)) && (jackTokenizer.symbol() == '(' ||
                        jackTokenizer.symbol() == '-' || jackTokenizer.symbol() == '~' )) {

            compileExpression();
            parameters++;

            while(jackTokenizer.symbol() != '#' && jackTokenizer.symbol() == ',') {
                buffer += ", ";
                bw.write("<symbol> , </symbol>\n");

                jackTokenizer.advance();
                compileExpression();
                parameters++;
            }
        }

        bw.write("</expressionList>\n");
    }
}