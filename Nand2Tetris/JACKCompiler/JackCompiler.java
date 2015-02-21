import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class JackCompiler {

    public static void main(String[] args) {
        String jack = "";
        String path = "";
        //String directory = "/Users/ak/Desktop/nand2tetris/projects/11/Pong/";
        String directory;

        if (args.length == 0) {
            System.err.println("No input found");
            return;
        } else {
            directory = args[0];
        }

        List<File> jFiles = new ArrayList<>();
        File file = new File(directory);
        File[] listOfFiles = file.listFiles();
        CompilationEngine ce, ce2;

        for(File files : listOfFiles) {
            String name = files.getName();
            String ext = "";

            int c;
            if((c = name.indexOf('.')) != -1)
                ext = name.substring(c, name.length());
            if(ext.equals(".jack"))
                jFiles.add(files);
        }

        try {
            for (File f : jFiles) {
                System.out.println(f.getName());
                jack = directory + f.getName();
                path = directory + f.getName().substring(0, f.getName().indexOf('.'));
                ce = new CompilationEngine(jack, path);
            }

            System.out.println("\nFiles written to current directory");
        } catch(Exception e) {
            System.out.println("Error locating files: " +e);
        }
    }
}

/*
               .-.
              /   \
            , b   d ,
        ;-./ '| V |' \.-;
      .-| ,`\; \)/ ;/`. |-.
    .-'._\ ' |/((|\| . /_.'-.
   .-\'. `;.'`  \) `'.;`.' /-.
   '._;---/           \---;_.'
    <  .- | /       \ | -.  >
     '---'| |       | |'---'
         /_/         \_\
           |'._   _.'|
           \_ _/"\_ _/
             ||   ||
            _||_ _||_
           (((     )))

          Gobble Gobble!

*/