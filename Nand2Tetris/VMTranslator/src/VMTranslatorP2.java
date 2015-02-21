
import java.io.*;

class VMTranslatorP2 {

    //Variables that contain vm commands in hack string form
    //Taken from worksheet in class
    final String add =
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=D+M\n";

    final String sub =
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=M-D\n";

    final String neg =
            "@SP\n" +
            "A=M-1\n" +
            "M=-M\n";

    final String and =
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=D&M\n";

    final String or =
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=D|M\n";

    final String not =
            "@SP\n" +
            "A=M-1\n" +
            "M=!M\n";

    final String push =
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            "@SP\n" +
            "M=M+1\n";

    final String pop =
            "@R13\n" +
            "M=D\n" +
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "@R13\n" +
            "A=M\n" +
            "M=D\n";

    final String Return =
            "@LCL\n" +
            "D=M\n" +
            "@5\n" +
            "A=D-A\n" +
            "D=M\n" +
            "@R13\n" +
            "M=D\n" +

            "@SP\n" +
            "A=M-1\n" +
            "D=M\n" +
            "@ARG\n" +
            "A=M\n" +
            "M=D \n" +

            "D=A+1\n" +
            "@SP\n" +
            "M=D\n" +

            "@LCL\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "@THAT\n" +
            "M=D\n" +

            "@LCL\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "@THIS\n" +
            "M=D\n" +

            "@LCL\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "@ARG\n" +
            "M=D\n" +

            "@LCL\n" +
            "A=M-1\n" +
            "D=M\n" +
            "@LCL\n" +
            "M=D\n" +

            "@R13\n" +
            "A=M\n" +
            "0;JMP\n";

    int total = 0;
    int fileIndex = 0;

    String[] input;
    String currentFile;
    String functionName;

    BufferedReader br;

    //Constructor that iterates through all the files within a folder, will need in future
    public VMTranslatorP2(String[] files) {
        for (String file : files) {
            System.out.println("Input: " + file);
        }

        this.input = files;
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("No input found");
            return;
        }

        FileWriter fw;
//        String[] temp = new String[2];
//        temp[0] = ("/Users/ak/Desktop/PointerTest.vm");
//        temp[1] = ("/Users/ak/Desktop/StackTest.vm");

        VMTranslatorP2 vmTranslator = new VMTranslatorP2(args);

        String init =
                "@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n" +
                vmTranslator.call("Sys.init", "0") +
                "0;JMP\n";

        String line;

        try {
            fw = new FileWriter("out.asm");


            fw.write(init);

            while(true) {
                line = vmTranslator.parse();

                if (line == null) {
                    fw.close();
                    System.out.println("Compiled successfully");
                    return;
                } else {
                    fw.write(line);
                    fw.write("\n");
                }

                //System.out.println(s);
            }

        } catch (Exception e) {
            System.out.println("Writing error: " +e);
        }
    }

    //Function to make sure reader is open and/or can be opened
    public boolean openReader() {
        try {
            if (br == null && fileIndex != input.length) {
                br = new BufferedReader(new FileReader(input[fileIndex]));
                currentFile = input[fileIndex].replaceAll(".*/", "");
                fileIndex += 1;

                return true;
            } else
                return false;

        } catch (Exception e) {
            System.out.println("Could not open reader: " +e);
            return false;
        }
    }

    //Function to make sure reader doesn't close prematurely
    public void closeReader() {
        try {
            if (br != null)
                br.close();

        } catch (IOException ex) {
            System.out.println("Could not close reader: " +ex);
        }
    }

    //A function to keep track of the line number
    public String nextTotal() {
        total += 1;

        return Integer.toString(total);
    }

    //A function to pull the next line
    public String next() throws IOException {
        if (br == null)
            if (!openReader())
                return null;

        String command;

        while(true) {
            command = br.readLine();

            if (command == null) {
                closeReader();
                br = null;
                return next();
            }

            command = command.replaceAll("//.*", "").trim();

            //Accounts for lines with a length of zero, prevents OutOfBoundsExceptions
            if (command.length() == 0) {
                continue;
            }

            return command;
        }
    }

    //Function to replace commands with their hack language equivalent
    public String parse() throws Exception {
        String command = next();

        //Prints the original vm command to terminal for testing
        //System.out.println("//" + s);

        if (command == null)
            return null;
        switch (command) {
            case "add": {
                return add;
            }
            case "sub": {
                return sub;
            }
            case "neg": {
                return neg;
            }
            case "eq": {
                return equalTo();
            }
            case "gt": {
                return greaterThen();
            }
            case "lt": {
                return lessThen();
            }
            case "and": {
                return and;
            }
            case "or": {
                return or;
            }
            case "not": {
                return not;
            }
            default: {
                String[] split = command.split(" ");
                if (split.length == 0)
                    throw new Exception("Invalid command");
                switch (split[0]) {
                    case "push": return push(split[1], split[2]);
                    case "pop": return pop(split[1], split[2]);
                    case "label": return "(" + functionName + "$" + split[1] + ")\n";
                    case "goto": return goTo(split[1]);
                    case "if-goto": return ifGoTo(split[1]);
                    case "function": { functionName = split[1]; return function(split[1], split[2]); }
                    case "call": return call(split[1], split[2]);
                    case "return": { return Return; }
                    default: throw new Exception("Invalid command: " + split[0]);
                }
            }
        }
    }

    //Function to replace memory locations with their hack language equivalent on a pop
    public String pop(String command, String index) throws Exception {
        switch (command) {
            case "local": {
                return
                "@LCL\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                pop;
            }
            case "argument": {
                return
                "@ARG\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                pop;
            }
            case "this": {
                return
                "@THIS\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                pop;
            }
            case "that": {
                return
                "@THAT\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                pop;
            }
            case "pointer": {
                if (index.equals("0")){
                    return
                    "@THIS\n" +
                    "D=A\n" +
                    pop;
                } else {
                    return
                    "@THAT\n" +
                    "D=A\n" +
                    pop;
                }
            }
            case "static": {
                return
                "@" + currentFile + "." + index + "\n" +
                "D=A\n" +
                pop;
            }
            case "temp": {
                return
                "@R5\n" +
                "D=A\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                pop;
            }
            default: throw new Exception("Invalid command");
        }
    }

    //Function to replace memory locations with their hack language equivalent on a push
    public String push(String command, String index) throws Exception {
        switch (command) {
            case "local": {
                return
                "@LCL\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                push;
            }
            case "argument": {
                return
                "@ARG\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                push;
            }
            case "this": {
                return
                "@THIS\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                push;
            }
            case "that": {
                return
                "@THAT\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                push;
            }
            case "pointer": {
                if (index.equals("0")){
                    return
                    "@THIS\n" +
                    "D=M\n" +
                    push;
                } else {
                    return
                    "@THAT\n" +
                    "D=M\n" +
                    push;
                }
            }
            case "constant": {
                return
                "@" + index + "\n" +
                "D=A\n" +
                push;
            }
            case "static": {
                return
                "@" + currentFile + "." + index + "\n" +
                "D=M\n" +
                push;
            }
            case "temp": {
                return
                "@R5\n" +
                "D=A\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                push;
            }
            default: throw new Exception("Invalid command");
        }
    }

    //Function that returns a hack string equivalent for EQ
    public String equalTo() {
        String line = nextTotal();
        String command =
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@EQ.true." + line + "\n" +
                "D;JEQ\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@EQ.after." + line + "\n" +
                "0;JMP\n" +
                "(EQ.true." + line + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(EQ.after." + line + ")\n";
        return command;
    }

    //Function that returns a hack string equivalent for GT
    public String greaterThen() {
        String line = nextTotal();
        String command =
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@GT.true." + line + "\n" +
                "\nD;JGT\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@GT.after." + line + "\n" +
                "0;JMP\n" +
                "(GT.true." + line + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(GT.after." + line + ")\n";
        return command;
    }

    //Function that returns a hack string equivalent for LT
    public String lessThen() {
        String line = nextTotal();
        String command =
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@LT.true." + line + "\n" +
                "D;JLT\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@LT.after." + line + "\n" +
                "0;JMP\n" +
                "(LT.true." + line + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(LT.after." + line + ")\n";
        return command;
    }

    public String goTo(String name) {
        String line =
                "@" + functionName + "$" + name + "\n" +
                "0;JMP\n";
        return line;
    }

    public String ifGoTo(String name) {
        String line =
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@" + functionName + "$" + name + "\n" +
                "D;JNE\n";
        return line;
    }

    public String function(String a, String b) {
        String line =
                "(" + a + ")\n" +
                "@SP\n" +
                "A=M\n";
        int kk = Integer.parseInt(b);
        for (int i = 0; i < kk; i += 1) {
            line += "M=0\n" + "A=A+1\n";
        }
        return line +
            "   D=A\n" +
                "@SP\n" +
                "M=D\n";
    }

    public String call(String a, String b) {
        String count = nextTotal();
        return
                "@SP\n" +
                "D=M\n" +
                "@R13\n" +
                "M=D\n" +

                "@RET." + count + "\n" +
                "D=A\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +

                "@SP\n" +
                "M=M+1\n" +

                "@LCL\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                // SP++
                "@SP\n" +
                "M=M+1\n" +

                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +

                "@SP\n" +
                "M=M+1\n" +

                "@THIS\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +

                "@SP\n" +
                "M=M+1\n" +

                "@THAT\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +

                "@SP\n" +
                "M=M+1\n" +

                "@R13\n" +
                "D=M\n" +
                "@" + b + "\n" +
                "D=D-A\n" +
                "@ARG\n" +
                "M=D\n" +

                "@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +
                "@" + a + "\n" +
                "0;JMP\n" +
                "(RET." + count + ")\n";
    }
}