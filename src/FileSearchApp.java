import java.io.File;

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

    public void walkDirectory(String path){
        System.out.println("walkDirectory: " + path);
        searchFile(null);
        addFile(null);
    }

    public void searchFile(File file){
        System.out.println("searchFile: " + file);
    }
    public void addFile(File file){
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
            app.walkDirectory(app.getPath());
        } catch (Exception e){
            e.printStackTrace();
        }



    }
}
