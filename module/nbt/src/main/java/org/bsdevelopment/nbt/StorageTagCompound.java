package org.bsdevelopment.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bsdevelopment.nbt.other.NBTSizeTracker;
import org.bsdevelopment.nbt.other.StorageColorType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class StorageTagCompound extends StorageBase {
    private static final Logger LOGGER = LogManager.getLogger(StorageTagCompound.class);
    private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
    private final Map<String, StorageBase> tagMap = Maps.newHashMap();
    private final List<String> booleans = new ArrayList<>();

    private static void writeEntry(String name, StorageBase data, DataOutput output) throws IOException {
        output.writeByte(data.getId());

        if (data.getId() != 0) {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readByte();
    }

    private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readUTF();
    }

    static StorageBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        StorageBase nbtbase = StorageBase.createNewByType(id);

        try {
            nbtbase.read(input, depth, sizeTracker);
            return nbtbase;
        } catch (IOException ioexception) {
            throw new IOException(ioexception);
        }
    }

    protected static String match(String s) {
        return PATTERN.matcher(s).matches() ? s : StorageTagString.configure(s);
    }

    static void escape(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    continue;
                case '\n':
                    sb.append("\\n");
                    continue;
                case '\f':
                    sb.append("\\f");
                    continue;
                case '\r':
                    sb.append("\\r");
                    continue;
                case '"':
                    sb.append("\\\"");
                    continue;
                case '/':
                    sb.append("\\/");
                    continue;
                case '\\':
                    sb.append("\\\\");
                    continue;
            }

            if (ch >= 0 && ch <= 31 || ch >= 127 && ch <= 159 || ch >= 8192 && ch <= 8447) {
                String ss = Integer.toHexString(ch);
                sb.append("\\u");

                for (int k = 0; k < 4 - ss.length(); ++k) {
                    sb.append('0');
                }

                sb.append(ss.toUpperCase());
            } else {
                sb.append(ch);
            }
        }

    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        for (String s : this.tagMap.keySet()) {
            StorageBase nbtbase = this.tagMap.get(s);
            writeEntry(s, nbtbase, output);
        }

        output.writeByte(0);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(384L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagMap.clear();
            byte b0;

            while ((b0 = readType(input, sizeTracker)) != 0) {
                String s = readKey(input, sizeTracker);
                sizeTracker.read(224 + 16L * s.length());
                StorageBase nbtbase = readNBT(b0, s, input, depth + 1, sizeTracker);

                if (this.tagMap.put(s, nbtbase) != null) {
                    sizeTracker.read(288L);
                }
            }
        }
    }

    public Set<String> getKeySet() {
        return this.tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 10;
    }

    public int getSize() {
        return this.tagMap.size();
    }

    public StorageTagCompound set(String key, Object object) {
        if (object instanceof Boolean) {
            setBoolean(key, (Boolean) object);
        } else if (object instanceof Integer) {
            setInteger(key, (Integer) object);
        } else if (object instanceof Byte) {
            setByte(key, (Byte) object);
        } else if (object instanceof Float) {
            setFloat(key, (Float) object);
        } else if (object instanceof Double) {
            setDouble(key, (Double) object);
        } else if (object instanceof Long) {
            setLong(key, (Long) object);
        } else if (object instanceof Short) {
            setShort(key, (Short) object);
        } else if (object instanceof Location) {
            setLocation(key, (Location) object);
        } else if (object instanceof Enum) {
            setEnum(key, (Enum) object);
        } else if (object instanceof Color) {
            setColor(key, (Color) object);
        } else if (object instanceof byte[]) {
            setByteArray(key, (byte[]) object);
        } else if (object instanceof int[]) {
            setIntArray(key, (int[]) object);
        } else if (object instanceof String) {
            setString(key, (String) object);
        } else if (object instanceof UUID) {
            setUniqueId(key, (UUID) object);
        }
        return this;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public StorageTagCompound setTag(String key, StorageBase value) {
        this.tagMap.put(key, value);
        return this;
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public StorageTagCompound setByte(String key, byte value) {
        this.tagMap.put(key, new StorageTagByte(value));
        return this;
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public StorageTagCompound setShort(String key, short value) {
        this.tagMap.put(key, new StorageTagShort(value));
        return this;
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public StorageTagCompound setInteger(String key, int value) {
        this.tagMap.put(key, new StorageTagInt(value));
        return this;
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public StorageTagCompound setLong(String key, long value) {
        this.tagMap.put(key, new StorageTagLong(value));
        return this;
    }

    public StorageTagCompound setUniqueId(String key, UUID value) {
        setString(key, value.toString());
        return this;
    }

    public UUID getUniqueId(String key) {
        if (this.hasKey(key + "Most", 99) && this.hasKey(key + "Least", 99))
            return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
        if (hasKey(key)) {
            String raw = getString(key);
            try {
                return UUID.fromString(raw);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return UUID.randomUUID();
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public StorageTagCompound setFloat(String key, float value) {
        this.tagMap.put(key, new StorageTagFloat(value));
        return this;
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public StorageTagCompound setDouble(String key, double value) {
        this.tagMap.put(key, new StorageTagDouble(value));
        return this;
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public StorageTagCompound setString(String key, String value) {
        this.tagMap.put(key, new StorageTagString(value));
        return this;
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public StorageTagCompound setByteArray(String key, byte[] value) {
        this.tagMap.put(key, new StorageTagByteArray(value));
        return this;
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public StorageTagCompound setIntArray(String key, int[] value) {
        this.tagMap.put(key, new StorageTagIntArray(value));
        return this;
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public StorageTagCompound setBoolean(String key, boolean value) {
        tagMap.put(key, new StorageTagByte((byte) ((value) ? 1 : 0)));
        booleans.add(key);
        return this;
    }

    public boolean isBoolean(String key) {
        return booleans.contains(key);
    }

    /**
     * gets a generic tag with the specified name
     */
    public StorageBase getTag(String key) {
        return this.tagMap.get(key);
    }

    /**
     * Gets the ID byte for the given tag key
     */
    public byte getTagId(String key) {
        StorageBase nbtbase = this.tagMap.get(key);
        return nbtbase == null ? 0 : nbtbase.getId();
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean hasKey(String key) {
        return this.tagMap.containsKey(key);
    }

    /**
     * Returns whether the given string has been previously stored as a key in this tag compound as a particular type,
     * denoted by a parameter in the form of an ordinal. If the provided ordinal is 99, this method will match tag types
     * representing numbers.
     */
    public boolean hasKey(String key, int type) {
        int i = this.getTagId(key);

        if (i == type) {
            return true;
        } else if (type != 99) {
            return false;
        } else {
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
        }
    }

    public StorageTagCompound setLocation(String key, Location location) {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setString("world", location.getWorld().getName());
        compound.setDouble("x", location.getX());
        compound.setDouble("y", location.getY());
        compound.setDouble("z", location.getZ());
        compound.setFloat("yaw", location.getYaw());
        compound.setFloat("pitch", location.getPitch());
        setTag(key, compound);
        return this;
    }

    public Location getLocation(String key) {
        StorageTagCompound compound = getCompoundTag(key);
        World world = Bukkit.getWorld(compound.getString("world", "world"));
        double x = compound.getDouble("x", 0);
        double y = compound.getDouble("y", 0);
        double z = compound.getDouble("z", 0);
        float yaw = compound.getFloat("yaw", 0f);
        float pitch = compound.getFloat("pitch", 0f);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Location getLocation(String key, Location fallback) {
        return (hasKey(key) ? getLocation(key) : fallback);
    }

    public StorageTagCompound setColor(String key, Color color) {
        return setColor(key, color, StorageColorType.COMPOUND);
    }

    public StorageTagCompound setColor(String key, Color color, StorageColorType type) {
        switch (type) {
            case HEX:
                setString(key, toHex(color.getRed(), color.getGreen(), color.getBlue()));
                break;
            case INT:
                setInteger(key, color.asRGB());
                break;
            case STRING:
                setString(key, color.getRed() + "," + color.getGreen() + "," + color.getBlue());
                break;
            case COMPOUND:
                StorageTagCompound compound = new StorageTagCompound();
                compound.setInteger("r", color.getRed());
                compound.setInteger("g", color.getGreen());
                compound.setInteger("b", color.getBlue());
                setTag(key, compound);
                break;
        }
        return this;
    }

    public Color getColor(String key) {
        return getColor(key, Color.RED);
    }

    public Color getColor(String key, Color fallback) {
        if (!hasKey(key)) return fallback;

        StorageBase base = getTag(key);
        // Was saved as a Compound {R,G,B}
        if (base instanceof StorageTagCompound) {
            StorageTagCompound compound = getCompoundTag(key);
            int r = compound.getInteger("r", 0);
            if (r > 255) r = 255;
            if (r < 0) r = 0;

            int g = compound.getInteger("g", 0);
            if (g > 255) r = 255;
            if (g < 0) r = 0;

            int b = compound.getInteger("b", 0);
            if (b > 255) r = 255;
            if (b < 0) r = 0;

            return Color.fromRGB(r, g, b);
        }

        // Was saved as an int
        if (base instanceof StorageTagInt) return Color.fromRGB(((StorageTagInt) base).getInt());

        if (base instanceof StorageTagString tagString) {
            String string = tagString.getString();

            // String is a HEX code
            if (string.startsWith("#")) {
                return Color.fromRGB(
                        Integer.valueOf(string.substring(1, 3), 16),
                        Integer.valueOf(string.substring(3, 5), 16),
                        Integer.valueOf(string.substring(5, 7), 16)
                );
            }

            // String is a split R,G,B
            if (string.contains(",")) {
                String[] args = string.split(",");
                int r = 255, g = 255, b = 255;
                if (args.length >= 3) {
                    r = Integer.parseInt(args[0].trim());
                    g = Integer.parseInt(args[1].trim());
                    b = Integer.parseInt(args[2].trim());
                }
                return Color.fromRGB(r, g, b);
            }

            try {
                // String is a rgb integer, someone added some quotes
                int rgb = Integer.parseInt(string);
                return Color.fromRGB(rgb);
            } catch (NumberFormatException e) {
                return fallback;
            }
        }

        return fallback;
    }

    public StorageTagCompound setEnum(String key, Enum anEnum) {
        setString(key, anEnum.name());
        return this;
    }

    public <E extends Enum> E getEnum(String key, Class<E> type) {
        return getEnum(key, type, null);
    }

    public <E extends Enum> E getEnum(String key, Class<E> type, E fallback) {
        if (!hasKey(key)) return fallback;
        return (E) E.valueOf(type, getString(key));
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(String key) {
        StorageBase storage = this.tagMap.get(key);
        if (storage.getId() == 1) {
            return ((StorageTagByte) storage).getByte();
        }
        return 0;
    }

    public byte getByte(String key, byte fallback) {
        return (hasKey(key) ? getByte(key) : fallback);
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(String key) {
        return Short.parseShort(getValue(key));
    }

    public short getShort(String key, short fallback) {
        return (hasKey(key) ? getShort(key) : fallback);
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(String key) {
        return Integer.parseInt(getValue(key));
    }

    public int getInteger(String key, int fallback) {
        return (hasKey(key) ? getInteger(key) : fallback);
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(String key) {
        return Long.parseLong(getValue(key));
    }

    public long getLong(String key, long fallback) {
        return (hasKey(key) ? getLong(key) : fallback);
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(String key) {
        return Float.parseFloat(getValue(key));
    }

    public float getFloat(String key, float fallback) {
        return (hasKey(key) ? getFloat(key) : fallback);
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(String key) {
        return Double.parseDouble(getValue(key));
    }

    public double getDouble(String key, double fallback) {
        return (hasKey(key) ? getDouble(key) : fallback);
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(String key) {
        return getValue(key);
    }

    public String getString(String key, String fallback) {
        return (hasKey(key) ? getValue(key) : fallback);
    }

    public String getValue(String key) {
        try {
            if (this.hasKey(key)) {
                return fetchValue(tagMap.get(key));
            }
        } catch (ClassCastException ignored) {
        }
        return "";
    }

    private String fetchValue(StorageBase base) {
        if (base instanceof StorageTagByte) {
            byte tagByte = ((StorageTagByte) base).getByte();
            if ((tagByte == 0) || (tagByte == 1))
                return String.valueOf(tagByte == 1);
            return String.valueOf(tagByte);
        }
        if (base instanceof StorageTagByteArray)
            return Arrays.toString(((StorageTagByteArray) base).getByteArray());
        if (base instanceof StorageTagDouble)
            return String.valueOf(((StorageTagDouble) base).getDouble());
        if (base instanceof StorageTagFloat)
            return String.valueOf(((StorageTagFloat) base).getFloat());
        if (base instanceof StorageTagInt)
            return String.valueOf(((StorageTagInt) base).getInt());
        if (base instanceof StorageTagIntArray)
            return Arrays.toString(((StorageTagIntArray) base).getIntArray());
        if (base instanceof StorageTagLong)
            return String.valueOf(((StorageTagLong) base).getLong());
        if (base instanceof StorageTagShort)
            return String.valueOf(((StorageTagShort) base).getShort());
        if (base instanceof StorageTagString)
            return String.valueOf(base.getString());
        if (base instanceof StorageTagList list) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.tagCount(); i++) {
                builder.append(fetchValue(list.get(i)));
            }
            return builder.toString();
        }

        return base.getString();
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(String key) {
        try {
            if (this.hasKey(key, 7)) {
                return ((StorageTagByteArray) this.tagMap.get(key)).getList();
            }
        } catch (ClassCastException ignored) {
        }

        return new byte[0];
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(String key) {
        try {
            if (this.hasKey(key, 11)) {
                return ((StorageTagIntArray) this.tagMap.get(key)).getList();
            }
        } catch (ClassCastException ignored) {
        }

        return new int[0];
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public StorageTagCompound getCompoundTag(String key) {
        try {
            if (this.hasKey(key, 10)) {
                return (StorageTagCompound) this.tagMap.get(key);
            }
        } catch (ClassCastException ignored) {
        }

        return new StorageTagCompound();
    }

    /**
     * Gets the NBTTagList object with the given name.
     */
    public StorageTagList getTagList(String key, int type) {
        try {
            if (this.getTagId(key) == 9) {
                StorageTagList nbttaglist = (StorageTagList) this.tagMap.get(key);

                if (!nbttaglist.hasNoTags() && nbttaglist.getTagType() != type) {
                    return new StorageTagList();
                }

                return nbttaglist;
            }
        } catch (ClassCastException ignored) {
        }

        return new StorageTagList();
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }

    public boolean getBoolean(String key, boolean fallback) {
        return (hasKey(key) ? getBoolean(key) : fallback);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        Collection<String> collection = this.tagMap.keySet();

        if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(this.tagMap.keySet());
            Collections.sort(list);
            collection = list;
        }

        for (String s : collection) {
            if (stringbuilder.length() != 1) {
                stringbuilder.append(',');
            }

            stringbuilder.append(match(s)).append(':').append(this.tagMap.get(s));
        }

        return stringbuilder.append('}').toString();
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagCompound copy() {
        StorageTagCompound nbttagcompound = new StorageTagCompound();

        for (String s : this.tagMap.keySet()) {
            nbttagcompound.setTag(s, this.tagMap.get(s).copy());
        }

        return nbttagcompound;
    }

    public boolean equals(Object instance) {
        return super.equals(instance) && Objects.equals(this.tagMap.entrySet(), ((StorageTagCompound) instance).tagMap.entrySet());
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    /**
     * Remove the specified tag.
     */
    public StorageTagCompound remove(String key) {
        if (hasKey(key)) tagMap.remove(key);
        booleans.remove(key);
        return this;
    }

    /**
     * Merges this NBTTagCompound with the given compound. Any sub-compounds are merged using the same methods, other
     * types of tags are overwritten from the given compound.
     */
    public StorageTagCompound merge(StorageTagCompound other) {
        for (String s : other.tagMap.keySet()) {
            StorageBase nbtbase = other.tagMap.get(s);

            if (nbtbase.getId() == 10) {
                if (this.hasKey(s, 10)) {
                    StorageTagCompound nbttagcompound = this.getCompoundTag(s);
                    nbttagcompound.merge((StorageTagCompound) nbtbase);
                } else {
                    this.setTag(s, nbtbase.copy());
                }
            } else {
                this.setTag(s, nbtbase.copy());
            }
        }
        return this;
    }


    /**
     * It takes three integers, converts them to hexadecimal, and returns a string
     *
     * @param r The red value of the color (from 0-255)
     * @param g The green value of the color (from 0-255)
     * @param b The blue value of the color (0-255)
     * @return A string in the format of #RRGGBB
     */
    private String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    /**
     * Convert the given number to a two digit hexadecimal string, padding with a leading zero if necessary.
     *
     * @param number The number to convert to hex.
     * @return A string of the hex value of the number.
     */
    private String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }
}