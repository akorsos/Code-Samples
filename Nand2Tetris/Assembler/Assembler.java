package ak.hack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assembler {
    Map<String, Integer> map = new HashMap<>(60);
    BufferedReader br;

    //Command symbols @, (, etc
    char A0 = 0;
    char C1 = 1;
    char L2 = 2;

    int currentAddress = 16;
    int currentLine = 0;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No input found");
            System.exit(0);
        }

        Assembler assembler = new Assembler(args[0]);
        //Assembler assembler = new Assembler("/Users/ak/Desktop/nand2tetris/projects/04/mult/Mult.asm");

        String line;

        try {
            FileWriter fw = new FileWriter("out.hack");
            while(true) {
                line = assembler.parse();
                if (line == null) {
                    fw.close();
                    return;
                } else {
                    fw.write(line);
                    fw.write("\n");
                    //System.out.println(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not assemble file: " + e);
        }
    }

    public Assembler(String file) {
        try {
            //Make a new Map to house our symbol library
            br = new BufferedReader(new FileReader(file));
            map.put("SP", 0);
            map.put("LCL", 1);
            map.put("ARG", 2);
            map.put("THIS", 3);
            map.put("THAT", 4);
            map.put("SCREEN", 16384);
            map.put("KBD", 24576);
            map.put("R0", 0);
            map.put("R1", 1);
            map.put("R2", 2);
            map.put("R3", 3);
            map.put("R4", 4);
            map.put("R5", 5);
            map.put("R6", 6);
            map.put("R7", 7);
            map.put("R8", 8);
            map.put("R9", 9);
            map.put("R10", 10);
            map.put("R11", 11);
            map.put("R12", 12);
            map.put("R13", 13);
            map.put("R14", 14);
            map.put("R15", 15);

            firstPass();
            closeReader();

            br = new BufferedReader(new FileReader(file));

        } catch (Exception e) {
            System.out.println("Mapping issue: " +e);
        }
    }

    //Big switch case that converts commands to their binary equivalent
    public char commandToBinary(String line) throws Exception {
        //Trim line
        String command = line.replaceAll(".*=", "");
        command = command.replaceAll(";.*", "");

        switch (command) {
            //Declaring binary strings like this is only legal in 7.0+
            case "0": return 0b0101010;
            case "1": return 0b0111111;
            case "-1": return 0b0111010;
            case "D": return 0b0001100;
            case "A": return 0b0110000;
            case "!D": return 0b0001101;
            case "!A": return 0b0110001;
            case "-D": return 0b0001111;
            case "-A": return 0b0110011;

            case "D+1": return 0b0011111;
            case "A+1": return 0b0110111;
            case "D-1": return 0b0001110;
            case "A-1": return 0b0110010;
            case "D+A": return 0b0000010;
            case "D-A": return 0b0010011;
            case "A-D": return 0b0000111;
            case "D&A": return 0b0000000;
            case "D|A": return 0b0010101;

            case "M": return 0b1110000;
            case "!M": return 0b1110001;
            case "-M": return 0b1110011;
            case "M+1": return 0b1110111;
            case "M-1": return 0b1110010;
            case "D+M": return 0b1000010;
            case "D-M": return 0b1010011;
            case "M-D": return 0b1000111;
            case "D&M": return 0b1000000;
            case "D|M": return 0b1010101;
            default: throw new Exception("Command could not be converted");
        }
    }

    //Switch case that converts jumps to their binary equivalent
    public char jump(String line) {
        if (line.indexOf(';') == -1) {
            return 0;
        }

        //Trim line
        String jump = line.replaceAll(".*;", "");

        switch (jump) {
            case "JGT": return 0b001;
            case "JEQ": return 0b010;
            case "JGE": return 0b011;
            case "JLT": return 0b100;
            case "JNE": return 0b101;
            case "JLE": return 0b110;
            case "JMP": return 0b111;
            default: return 0;
        }
    }

    public void firstPass() throws Exception {
        String current = next();

        //On the first pass we check for symbols and make sure not to double up
        while(current != null) {
            if (type(current) == L2) {
                if (map.put(current.substring(1, current.length() - 1), currentLine) != null){
                    //The given symbol was already accounted for so we do nothing and return
                    return;
                }
            } else {
                currentLine += 1;
            }

            current = next();
        }
    }

    //Trims and returns next line
    public String next() throws IOException {
        String current;

        while(true) {
            current = br.readLine();

            //If the current line is null, we've reached the end
            if (current == null) {
                closeReader();
                return null;
            }

            //Trim line
            current = current.replaceAll("\\s","");
            current = current.replaceAll("//.*", "");

            //Accounts for lines with a length of zero, prevents OutOfBoundsExceptions
            if (current.length() == 0) {
                continue;
            }

            return current;
        }
    }

    //Recognizes and handles a symbol in a given line
    public char type(String line) {
        if (line.charAt(0) == '@') {
            return A0;
        } else {
            if (line.charAt(0) == '(') {
                return L2;
            } else {
                return C1;
            }
        }
    }

    //Accounts for and handles the =A, =D, and =M lines
    public char to(String line) {
        if (line.indexOf('=') == -1) {
            return 0;
        }

        //Trim line
        String newLine = line.replaceAll("=.*", "");
        char result = 0;

        //These have to be three separate if statements to catch MD, AD, DA, etc
        if (newLine.indexOf('A') != -1) {
            result |= 4;
        }

        //'|=' is a nifty inclusive operator I just found that equates to a sort of bitwise '+='
        if (newLine.indexOf('D') != -1) {
            result |= 2;
        }

        if (newLine.indexOf('M') != -1) {
            result |= 1;
        }

        return result;
    }

    public String parse() throws Exception {
        String current = next();

        //Watch for labels
        while(current != null && type(current) == L2) {
            current = next();
        }

        //Watch for empty lines
        if (current == null) {
            return null;
        }

        //Handling the '@' operator
        if (type(current) == A0) {
            //Sets point to start reading to the character after '@'
            current = current.substring(1);
            //Checks if current is a command
            if (current.charAt(0) < '0' || current.charAt(0) > '9') {
                //If it's a command, pull its info from the Map
                Integer address = map.get(current);
                //If not it's not already in Map, add it to memory after R15
                if (address == null) {
                    address = currentAddress;
                    map.put(current, address);
                    currentAddress += 1;
                }
                return String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
            } else {
                return String.format("%16s", Integer.toBinaryString(Integer.parseInt(current))).replace(' ', '0');
            }
        }

        //If it's not an assignment, do this instead. Use shifts to make the binary sit properly
        int bin = 0b1110000000000000 + (commandToBinary(current) << 6) + (to(current) << 3) + jump(current);

        return String.format("%16s", Integer.toBinaryString(bin)).replace(' ', '0');
    }

    //Closes BufferedReader
    public void closeReader() {
        try {
            //Makes sure BufferedReader doesn't close prematurely
            if (br != null) {
                br.close();
            }

        } catch (IOException ex) {
            System.out.println("Could not close Reader: " +ex);
        }
    }
}