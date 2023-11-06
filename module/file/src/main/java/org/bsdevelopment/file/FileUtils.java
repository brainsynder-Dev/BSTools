package org.bsdevelopment.file;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File checkFile (File file) {
        if (file.exists()) return file;

        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
