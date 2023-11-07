package org.bsdevelopment.file;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.File;

public abstract class TomlFile {
    private final File file;
    private final Config defaultConfig;
    private FileConfig fileConfig;

    public TomlFile(File file) {
        this(file, true);
    }

    public TomlFile(File file, boolean loadDefaults) {
        this.file = FileUtils.checkFile(file);
        defaultConfig = Config.inMemory();
        if (loadDefaults) reload();
    }

    public abstract void loadDefaults();

    public void reload () {
        fileConfig = FileConfig.of(this.file, TomlFormat.instance());
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            boolean defaultsLoaded = false;
            if ((!file.exists()) || (file.length() == 0)) {
                loadDefaults();
                defaultsLoaded = true;
                fileConfig.putAll(defaultConfig);
                fileConfig.save();
            }
            if (!defaultsLoaded) {
                loadDefaults();
            }
            fileConfig.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDefault (String path, Object value) {
        defaultConfig.set(path, value);
    }

    public boolean contains (String path, boolean includeDefaults) {
        if (!includeDefaults) return fileConfig.contains(path);
        return fileConfig.contains(path) || defaultConfig.contains(path);
    }

    public void set (String path, Object value) {
        fileConfig.set(path, value);
        fileConfig.save();
    }

    public <T> T getValue (String path) {
        if (!fileConfig.contains(path)) return defaultConfig.get(path);
        return fileConfig.get(path);
    }

    public <T> T getValue (String path, T defaultValue) {
        if (!fileConfig.contains(path)) return defaultValue;
        return fileConfig.get(path);
    }

    public String getName() {
        return file.getName().replace(".toml", "");
    }

    public File getFile() {
        return file;
    }
}
