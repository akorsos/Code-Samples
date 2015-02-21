import java.io.File;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JackAnalyzer {

    public static void main(String[] args) {
        String jack = "";
        String path = "";
        String directory = "/Users/ak/Desktop/nand2tetris/projects/10/ArrayTest/";

        BufferedWriter bufferedWriter;
        JackTokenizer jackTokenizer;
        CompilationEngine compilationEngine;

        List<File> jFiles = new ArrayList<>();
        File dirFile = new File(directory);
        File[] listOfFiles = dirFile.listFiles();

        for(File files : listOfFiles) {
            String name = files.getName();
            String extension = "";

            int f;
            if((f = name.indexOf('.')) != -1)
                extension = name.substring(f, name.length());
            if(extension.equals(".jack"))
                jFiles.add(files);
        }

        try {
            for(File f : jFiles) {
                System.out.println(f.getName());
                jack = directory + f.getName();
                path = directory + "AK--" + f.getName().substring(0, f.getName().indexOf('.')) + "T.xml";
                compilationEngine = new CompilationEngine(jack, path);
            }
        }
        catch(Exception e) {
            System.out.println("Error locating files: " +e);
        }
    }
}