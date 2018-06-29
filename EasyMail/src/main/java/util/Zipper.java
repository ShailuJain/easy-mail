package util;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private static File tmpFile = null;
    private static ZipOutputStream zipOutputStream;
    private static String curZippingDir = "";

    static {
        tmpFile = new File("files.zip");
    }
    public static File zip(List<File> files) throws IOException {
        zipOutputStream = new ZipOutputStream(new FileOutputStream(tmpFile));
        File f = zipAll(files);
        zipOutputStream.close();
        return f;
    }
    public static void delete(){
        tmpFile.delete();
    }

    private static File zipAll(List<File> files) throws IOException {
        for (File file : files) {
            String fileName = file.getName();
            if(file.isDirectory()){
                curZippingDir += file.getName() + "/";
                zipOutputStream.putNextEntry(new ZipEntry(curZippingDir));
                zipOutputStream.closeEntry();
                zipAll(Arrays.asList(file.listFiles()));
                curZippingDir = curZippingDir.substring(0,curZippingDir.length() - (fileName.length() + 1));
            }else {
                FileInputStream fileInputStream = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(curZippingDir + fileName);
                zipOutputStream.putNextEntry(zipEntry);
                byte[] tmp = new byte[4 * 1024];
                int size = 0;
                while ((size = fileInputStream.read(tmp)) != -1) {
                    zipOutputStream.write(tmp, 0, size);
                }
                zipOutputStream.flush();
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
        }
        return tmpFile;
    }
}
