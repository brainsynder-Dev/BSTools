package org.bsdevelopment.file;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public abstract class JsonFile {
    private final Charset ENCODE = Charsets.UTF_8;
    private final Gson GSON = new Gson();

    private JsonObject json;
    protected JsonObject defaults = new JsonObject();
    private final File file;

    public JsonFile(File file) {
        this(file, true);
    }

    public JsonFile(File file, boolean loadDefaults) {
        this.file = FileUtils.checkFile(file);
        if (loadDefaults) reload();
    }

    /**
     * This is an abstract method that loads default values.
     */
    public abstract void loadDefaults();

    /**
     * This function reloads a JSON file, creating it with default values if it doesn't exist, and then loads the JSON data
     * into a JsonObject.
     */
    public void reload() {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            boolean defaultsLoaded = false;
            if ((!file.exists()) || (file.length() == 0)) {
                loadDefaults();
                defaultsLoaded = true;
                OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
                pw.write(defaults.toString(WriterConfig.PRETTY_PRINT).replace("\u0026", "&"));
                pw.flush();
                pw.close();
            }
            json = (JsonObject) Json.parse(new InputStreamReader(new FileInputStream(file), ENCODE));
            if (!defaultsLoaded) {
                loadDefaults();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The function "save" returns a boolean value and calls another function "save" with a boolean parameter set to false.
     *
     * @return The `save()` method is being called with a boolean parameter `false`, and it is returning a boolean value.
     * The value being returned is the result of calling the `save()` method with the `false` parameter.
     */
    public boolean save() {
        return save(false);
    }

    /**
     * This function saves a JSON object to a file, optionally clearing the file before writing to it.
     *
     * @param clearFile A boolean value that determines whether the file should be cleared of existing text before saving
     * new text to it. If set to true, the file will be cleared before saving. If set to false, the new text will be
     * appended to the existing text in the file.
     * @return The method returns a boolean value. It returns `true` if the save operation was successful and `false` if it
     * failed.
     */
    public boolean save(boolean clearFile) {
        String text = json.toString(WriterConfig.PRETTY_PRINT).replace("\u0026", "&");
        OutputStreamWriter fw;

        // Will clear the file of existing text to prevent text duplication
        // Checking if a file exists and if a boolean variable `clearFile` is true. If both conditions
        // are true, it deletes the file and creates a new empty file with the same name.
        if (file.exists() && clearFile) {
            file.delete();
            try {
                file.createNewFile();
            } catch (Exception ignored) {
            }
        }


        try {
            fw = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
            try {
                fw.write(text);
            } finally {
                fw.flush();
                fw.close();
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * This function checks if a given key exists in a map or if it matches a default key.
     *
     * @param key The parameter "key" is a String representing the key that is being checked for existence in a data
     * structure.
     * @return The method `containsKey` is returning a boolean value. It returns `true` if the input `key` is found either
     * in the main keys or in the default keys, and `false` otherwise.
     */
    public boolean containsKey(String key) {
        return hasKey(key) || hasDefaultKey(key);
    }

    /**
     * This function checks if a given key exists in a JSON object.
     *
     * @param key The parameter "key" is a String representing the key that we want to check for existence in a JSON
     * object. The method "hasKey" returns a boolean value indicating whether the JSON object contains the specified key or
     * not.
     * @return The method `hasKey` is returning a boolean value. It returns `true` if the `json` object contains the
     * specified `key`, and `false` otherwise.
     */
    public boolean hasKey(String key) {
        return json.names().contains(key);
    }

    /**
     * This function checks if a given key exists in a set of default values.
     *
     * @param key The key is a string parameter that represents the key to be checked for existence in the defaults object.
     * The method checks if the defaults object contains the specified key and returns a boolean value indicating whether
     * the key exists or not.
     * @return The method `hasDefaultKey` is returning a boolean value. It returns `true` if the `defaults` object has a
     * key that matches the input `key`, and `false` otherwise.
     */
    private boolean hasDefaultKey(String key) {
        return defaults.names().contains(key);
    }

    /* -- GETTER Methods -- */

    /**
     * This function returns a list of keys from a JSON object.
     *
     * @return A list of keys from a JSON object.
     */
    public List<String> getKeys() {
        return json.names();
    }

    /**
     * This function returns the name of a file without the ".json" extension.
     *
     * @return The method `getName()` is returning a `String` that represents the name of a file without the ".json"
     * extension.
     */
    public String getName() {
        return file.getName().replace(".json", "");
    }

    /**
     * The function returns a File object.
     *
     * @return The method `getFile()` is returning a `File` object.
     */
    public File getFile() {
        return file;
    }

    public JsonValue getValue(String key) {
        JsonValue value = null;
        if (hasKey(key)) value = json.get(key);
        if ((value == null) && hasDefaultKey(key)) value = defaults.get(key);
        return value;
    }

    public JsonValue getDefaultValue(String key) {
        if (!hasDefaultKey(key)) return null;
        return defaults.get(key);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        return value.asString();
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        if (value.isString()) return Double.parseDouble(value.asString());
        return value.asDouble();
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(String key, byte fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        if (value.isString()) return Byte.parseByte(value.asString());
        return (byte) value.asInt();
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        try {
            if (value.isString()) return Boolean.parseBoolean(value.asString());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return fallback;
        }
        return value.asBoolean();
    }

    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(String key, short fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        if (value.isString()) return Short.parseShort(value.asString());
        return (short) value.asLong();
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        if (value.isString()) return Float.parseFloat(value.asString());
        return value.asFloat();
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        if (value.isString()) return Long.parseLong(value.asString());
        return value.asLong();
    }

    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    public int getInteger(String key, int fallback) {
        JsonValue value = getValue(key);
        if (value == null) return fallback;
        if (value.isString()) return Integer.parseInt(value.asString());
        return value.asInt();
    }


    /* -- SETTER Methods -- */

    public void set(String key, int value) {
        json.set(key, value);
    }

    public void set(String key, long value) {
        json.set(key, value);
    }

    public void set(String key, float value) {
        json.set(key, value);
    }

    public void set(String key, short value) {
        json.set(key, value);
    }

    public void set(String key, byte value) {
        json.set(key, value);
    }

    public void set(String key, double value) {
        json.set(key, value);
    }

    public void set(String key, boolean value) {
        json.set(key, value);
    }

    public void set(String key, ItemStack value) {
        String raw = GSON.toJson(value);
        json.set(key, Json.parse(raw));
    }

    public void set(String key, String value) {
        json.set(key, value);
    }

    public void set(String key, JsonValue value) {
        json.set(key, value);
    }

    public void remove(String key) {
        boolean update = false;
        if (defaults.names().contains(key)) {
            update = true;
            defaults.remove(key);
        }
        if (json.names().contains(key)) {
            update = true;
            json.remove(key);
        }
        if (update) save();
    }


    /* -- DEFAULT Methods -- */

    public void setDefault(String key, int value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, long value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, float value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, short value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, byte value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, double value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, boolean value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, String value) {
        defaults.add(key, value);
    }

    public void setDefault(String key, JsonValue value) {
        defaults.add(key, value);
    }

    /**
     * This function moves a value from an old key to a new key in a JSON object and saves the changes.
     *
     * @param oldKey The old key is a String parameter that represents the key of the JSON object that needs to be moved.
     * @param newKey The new key is the key that the value associated with the old key will be moved to.
     * @return The method is returning a boolean value. If the condition `hasKey(oldKey)` is true, then the method will
     * execute the code inside the if statement, and return `true`. Otherwise, it will return `false`.
     */
    public boolean move(String oldKey, String newKey) {
        if (hasKey(oldKey)) {
            json.set(newKey, getValue(oldKey));
            json.remove(oldKey);
            save();
            return true;
        }
        return false;
    }
}