
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

//Emits VM commands into a file, using the VM command syntax
public class VMWriter {

    BufferedWriter bw;

    //Creates a new file and prepares it for writing
    public VMWriter(String output){
        File file = new File(output);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
        } catch (Exception e){
            System.out.println("Error creating file in constructor: "+e);
        }
    }

    //Writes a VM push command
    public void writePush(String segment, int index) {
        String identifier = "";
        switch(segment) {
            case "CONST":identifier = "constant";
                break;
            case "ARG":identifier = "argument";
                break;
            case "LOCAL":identifier = "local";
                break;
            case "VAR":identifier = "local";
                break;
            case "STATIC":identifier = "static";
                break;
            case "FIELD":identifier = "this";
                break;
            case "THIS":identifier = "this";
                break;
            case "THAT":identifier = "that";
                break;
            case "POINTER":identifier = "pointer";
                break;
            case "TEMP":identifier = "temp";
                break;
        }

        try {
            if (!identifier.isEmpty())
                bw.write("push " + identifier + " " + index + "\n");
        } catch (Exception e){
            System.out.println("Error writing in writePush: " +e);
        }
    }

    //Writes a VM pop command
    public void writePop(String segment, int index){
        String identifier = "";
        switch(segment) {
            case "CONST":identifier = "constant";
                break;
            case SymbolTable.Arg:identifier = "argument";
                break;
            case "LOCAL":identifier = "local";
                break;
            case SymbolTable.Var:identifier = "local";
                break;
            case SymbolTable.Static:identifier = "static";
                break;
            case SymbolTable.Field:identifier = "this";
                break;
            case "THIS":identifier = "this";
                break;
            case "THAT":identifier = "that";
                break;
            case "POINTER":identifier = "pointer";
                break;
            case "TEMP":identifier = "temp";
                break;
        }

        try {
            if (!identifier.isEmpty())
                bw.write("pop " + identifier + " " + index + "\n");
        } catch (Exception e){
            System.out.println("Error writing in writePop: " +e);
        }
    }

    //Writes a VM arithmetic command
    public void writeArithmetic(String command) {
        try {
            if (command.equals("ADD") || command.equals("SUB") || command.equals("NEG") ||
                    command.equals("EQ") || command.equals("GT") || command.equals("LT") ||
                    command.equals("AND") || command.equals("OR") || command.equals("NOT")) {
                command = command.toLowerCase(); //may not be necessary
                bw.write(command + "\n");
            }
        } catch (Exception e){
            System.out.println("Error writing in writeArithmetic: " +e);
        }

    }

    //Writes a VM label command
    public void writeLabel(String label) {
        try {
            bw.write("label " + label + "\n");
        } catch (Exception e){
            System.out.println("Error in writeLabel");
        }
    }

    //Writes a VM goto command
    public void writeGoto(String label){
        try {
            bw.write("goto " + label + "\n");
        } catch (Exception e){
            System.out.println("writeGoto: " +e);
        }
    }

    //Writes a VM If-goto command
    public void writeIf(String label){
        try {
            bw.write("if-goto " + label + "\n");
        } catch (Exception e){
            System.out.println("Error in writeIf: " +e);
        }
    }

    //Writes a VM call command
    public void writeCall(String name, int nArgs){
        try {
            bw.write("call " + name + " " + nArgs + "\n");
        } catch (Exception e){
            System.out.println("Error in writeCall: " +e);
        }
    }

    //Writes a VM function command
    public void writeFunction(String name, int nLocals){
        try {
            bw.write("function " + name + " " + nLocals + "\n");
        } catch (Exception e){
            System.out.println("Error in writeFunction: " +e);
        }
    }

    //Writes a VM return command
    public void writeReturn(){
        try {
            bw.write("return\n");
        } catch (Exception e){
            System.out.println("Error in writeReturn: " +e);
        }
    }

    //Closes the output file
    public void close(){
        try {
            bw.close();
        } catch (Exception e){
            System.out.println("Error closing writer: " +e);
        }
    }
}