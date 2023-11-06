package org.bsdevelopment.nbt;

import org.bsdevelopment.nbt.other.NBTSizeTracker;
import org.bukkit.Color;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StorageTagString extends StorageBase {
    /**
     * The string value for the tag (cannot be empty).
     */
    private String data;

    public StorageTagString() {
        this("");
    }

    public StorageTagString(String data) {
        Objects.requireNonNull(data, "Null string not allowed");
        this.data = data;
    }

    public StorageTagString(Color color) {
        Objects.requireNonNull(color, "Null color not allowed");
        this.data = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    static String configure(String data) {
        StringBuilder stringbuilder = new StringBuilder("\"");

        for (int i = 0; i < data.length(); ++i) {
            char c0 = data.charAt(i);

            if (c0 == '\\' || c0 == '"') {
                stringbuilder.append('\\');
            }

            stringbuilder.append(c0);
        }

        return stringbuilder.append('"').toString();
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        output.writeUTF(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(288L);
        this.data = input.readUTF();
        sizeTracker.read(16L * this.data.length());
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 8;
    }

    public String toString() {
        return configure(this.data);
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagString copy() {
        return new StorageTagString(this.data);
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.data.isEmpty();
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        } else {
            StorageTagString nbttagstring = (StorageTagString) o;
            return this.data == null && nbttagstring.data == null || Objects.equals(this.data, nbttagstring.data);
        }
    }

    public Color getAsColor() {
        if (!data.contains(",")) return null;
        String[] args = data.replace(" ", "").split(",");
        if (args.length != 3) return null;
        int r = Integer.parseInt(args[0]);
        if (r > 255) r = 255;
        if (r < 0) r = 0;

        int g = Integer.parseInt(args[1]);
        if (g > 255) r = 255;
        if (g < 0) r = 0;

        int b = Integer.parseInt(args[2]);
        if (b > 255) r = 255;
        if (b < 0) r = 0;

        return Color.fromRGB(r, g, b);

    }

    public int hashCode() {
        return super.hashCode() ^ this.data.hashCode();
    }

    public String getString() {
        return this.data;
    }


    public long getLong() {
        try {
            return Long.parseLong(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getInt() {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public short getShort() {
        try {
            return Short.parseShort(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public byte getByte() {
        try {
            return Byte.parseByte(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getDouble() {
        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public float getFloat() {
        try {
            return Float.parseFloat(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}