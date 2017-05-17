package general.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gasper on 21.4.2017.
 */
public class FileSystemManagement {

    public static List<String> getEntriesInDirectory(String dir){
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        List ret = new ArrayList();
        for(File f : listOfFiles){
            if (f.isFile())
                ret.add(f.getName());
        }
        return ret;
    }
}
