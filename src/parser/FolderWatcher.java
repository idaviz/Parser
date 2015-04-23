/*
 * David García García
 * Acciona Airport Services 2014
 */
package parser;

/**
 * This class watches the inbound folder awaiting for any change in terms of new
 * files. In case of a new file, it is automatically processes and deleted from
 * the inbound folder.
 *
 * @author dgarcia25
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class FolderWatcher {
    Configuration c = new Configuration();
    //private String path = "C:\\SDK/IN/";
    private String path = c.getInbox_path();

//private String path = "src/Inbox/IN/";
    final File folder = new File(this.getPath());

    /**
     * Method to retrieve the current path to folder Inbox
     *
     * @return Relative path of the Inbox as String
     */
    public String getPath() {
        return path;
    }

    /**
     * Method to set a path to folder Inbox
     *
     * @param path Value of path to Inbox as String.
     */
    public void setPath(String path) {
        this.path = path;
    }
    public ArrayList<String> rawFileListMaster = new ArrayList<>();

    /**
     * Obtains the list of raw files.
     *
     * @return An arraylist that contains all the files found in the folder.
     */
    public ArrayList<String> getRawFileListMaster() {
        return rawFileListMaster;
    }

    /**
     * Lists the content of a folder.
     *
     * @param folder Target path that will be listed.
     */
    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
        }
    }

    /**
     * Creates a list with all the files in the Inbound folder and leaves it
     * empty.
     *
     * @param folder Target path that will be listed.
     * @return An ArrayList that contains all the target files of the Inbox
     * folder.
     * @throws java.io.FileNotFoundException
     */
    public ArrayList<String> loadRawFileList(final File folder) throws FileNotFoundException, IOException {

        ArrayList<String> rawFileList = new ArrayList<>();
 
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.getName().equals("ATTCH'S")) {
                try (BufferedReader br = new BufferedReader(new FileReader(this.getPath() + fileEntry.getName()))) {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append('\n');
                        line = br.readLine();
                    }
                    String everything = sb.toString();
                    rawFileList.add(everything);
                    // close buffered reader, then delete file
                    br.close();
                    // Deletion of processed file from IN folder
                    //fileEntry.delete();
                    
                    String option = c.getEmpty_after_read();
                    if (option.equals("yes")){
                        fileEntry.delete();
                    }
                }
            }
        }
        return rawFileList;
    }

    /**
     * Class constructors. Creates and object that observes and reports the
     * content of a selected folder.
     *
     * @throws IOException
     */
    public FolderWatcher() throws IOException {

        rawFileListMaster = loadRawFileList(folder);

    }
}