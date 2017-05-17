package Test.Unit;

import configs.Config;
import general.IO.FileSystemManagement;
import org.junit.Test;

import java.util.List;

/**
 * Created by Gasper on 21.4.2017.
 */
public class TestFileSystemManagement {

    @Test
    public void testDirEntries(){
        FileSystemManagement fs = new FileSystemManagement();
        List<String> files = fs.getEntriesInDirectory(Config.LOCATION_IN);
        for( String s : files){
            System.out.println(s);
        }
    }
}
