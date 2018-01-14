package form3test;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * Created by rhall on 12/01/2018.
 */
public class TestUtil {

    public static String readFileAsString( String fileName ){

        ClassPathResource resource = new ClassPathResource(fileName);
        File f;
        try {
            f = resource.getFile();
            return FileUtils.readFileToString( f);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
