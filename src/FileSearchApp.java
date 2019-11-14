import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class FileSearchApp {

    String path;
    String regex;
    String zipFileName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    public void walkDirectoryJava6(String path) throws IOException{
        // create a first directory or path with given @argument = path
        File dir = new File(path);
        // list each files - or directories (directories are also files)
        // and put it in a arrayList
        File[] files = dir.listFiles();

        for(File file : files){
            if(file.isDirectory()){
                // if it is a directory do a recursive call
                walkDirectoryJava6(file.getAbsolutePath());
            } else {
                processFile(file);
            }
        }
    }

    public void processFile(File file){

        try {
            // search for a file
            if (searchFile(file)) {
                // add to a zip provided they match the regulare expression
                addFileToZip(file);
            }
        } catch(FileNotFoundException | UncheckedIOException e){
            System.out.println("Error processing file: " + file + " : " + e);
        }
    }

    public boolean searchFile(File file) throws FileNotFoundException {
        return searchFileJava6(file);
    }

    public boolean searchFileJava6(File file) throws FileNotFoundException {
        boolean found = false;
        Scanner scanner = new Scanner(file, "UTF-8");
        // using the scanner read each line of the file
        while(scanner.hasNextLine()){
            // check each file to see if it has a regular expression
            // we are searching for.
            found = searchText(scanner.nextLine());
            if(found){break;} // break out of the loop first time find a match
        }
        scanner.close();
        return found;
    }

    public boolean searchFileJava7(File file) throws IOException{
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for(String line : lines){
            if(searchText(line)){
                return true;
            }
        }
        return false;
    }

    public boolean searchText(String test){
        return true;
    }
    public void addFileToZip(File file){
        System.out.println("addFileToZip: " + file);
    }


    public static void main(String[] args) {

        FileSearchApp app = new FileSearchApp();

        switch(Math.min(args.length, 3)){
            case 0:
                System.out.println("USAGE: FileSearchApp path [regex] [zipfile]");
                return;
            case 3: app.setZipFileName(args[2]);
            case 2: app.setRegex(args[1]);
            case 1: app.setPath(args[0]);
        }

        try{
            app.walkDirectoryJava6(app.getPath());
        } catch (Exception e){
            e.printStackTrace();
        }



    }
}
