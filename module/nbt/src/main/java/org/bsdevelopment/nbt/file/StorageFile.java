package org.bsdevelopment.nbt.file;

import org.bsdevelopment.nbt.CompressedStreamTools;
import org.bsdevelopment.nbt.StorageTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageFile extends StorageTagCompound {
    private final File file;

    public StorageFile(File file) {
        this.file = checkFile(file);

        try { // Will fix the data not being read from the file
            FileInputStream stream = new FileInputStream(this.file);
            StorageTagCompound compound = CompressedStreamTools.readCompressed(stream);
            compound.getKeySet().forEach(key -> {
                setTag(key, compound.getTag(key));
            });
            stream.close();
        } catch (IOException ignored) {}
    }

    static File checkFile (File file) {
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

    public StorageFile setDefault (StorageTagCompound compound) {
        compound.getKeySet().forEach(key -> {
            setTag(key, compound.getTag(key));
        });
        return this;
    }

    public void save () {
        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(this, stream);
            stream.close();
        }catch (Exception ignored){}
    }

    public boolean move(String oldKey, String newKey) {
        if (hasKey(oldKey)) {
            setTag(newKey, getTag(oldKey));
            remove(oldKey);
            save();
            return true;
        }
        return false;
    }

    public File getFile() {
        return file;
    }
}