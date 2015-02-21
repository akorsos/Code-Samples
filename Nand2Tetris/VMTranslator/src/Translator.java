import java.io.FileWriter;
import java.util.HashMap;

public class Translator {

    //var currentFunction: Option[String] = None  ARRAY?
    String filename = ""
    int numLabels = 0

    public static void main(String[] args){
        HashMap symbolRegisters = new HashMap();
        symbolRegisters.put("local", "LCL");
        symbolRegisters.put("argument", "ARG");
        symbolRegisters.put("this", "THIS");
        symbolRegisters.put("that", "THAT");



    }



}
