package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ZipperTest {
    public static void main(String[] args) throws IOException {
        ArrayList<File> files = new ArrayList<>(){
            {
                add(new File("files.zip"));
                add(new File("META-INF"));
            }
        };
        Zipper.zip(files);
    }
}
