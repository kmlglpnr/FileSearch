import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSearchApp {

    String path;
    String regex;
    String zipFileName;
    // OPTIMIZATION
    // Using the pattern class we ccan use our regular
    // expression once and use as many time as we want without compiling
    Pattern pattern;

    List<File> zipFiles = new ArrayList<File>();


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
        this.pattern = Pattern.compile(regex);
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }


    public void walkDirectory(String path) throws IOException{
        walkDirectoryJava6(path);
        zipFilesJava7();
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
                walkDirectory(file.getAbsolutePath());
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

    public boolean searchText(String text) {

        // if regex commandline parameter was not optional that would be the solution
        // but keep in that mind regex is optional
      //  return text.contains(this.getRegex());

        // therefore need to code defendsively
        // this code below can be written in one line

//        if (this.getRegex() == null) {
//            return true;
//        } else {
//            return
//        }
        return (this.getRegex() == null) ? true : this.pattern.matcher(text).matches();

        // FULL MATCH
        // Pattern.compile("X").matcher("example").matches() = false;
        // PARTIAL MATCH
        // Pattern.compile("X").mathcer("example").find() = true;
    }

    public void addFileToZip(File file){
        if(getZipFileName() != null){
            zipFiles.add(file);
        }
    }

    public void zipFilesJava7() throws IOException{

        // polishing up: try-with-resources allowed to skip finally clause

        try(
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getZipFileName()))
                ){
            File baseDir = new File(getPath());

            for(File file: zipFiles){
                // fileName must be a relative oath, not an absolute one.
                String fileName = getRelativeFileName(file, baseDir);

                // add a Zip entry to ZipOutputStream
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);

                // does the all buffers and copying of file to a zipEntry
                Files.copy(file.toPath(), out);

                out.closeEntry();

            }
        }
    }

    // replacing all the "\" with the "/" slashes
    // and making sure that we do not have any front slashes at the very beginning.
    public String getRelativeFileName(File file, File baseDir){
        String fileName = file.getAbsolutePath().substring(baseDir.getAbsolutePath().length());

        // IMPORTANT: the ZipEntry file name must use "/", not "\".
        fileName = fileName.replace("\\", "/");

        while (fileName.startsWith("/")){
            fileName = fileName.substring(1);
        }

        return fileName;

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
