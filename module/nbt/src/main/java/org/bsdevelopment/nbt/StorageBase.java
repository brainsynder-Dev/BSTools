package org.bsdevelopment.nbt;

import org.bsdevelopment.nbt.other.NBTSizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class StorageBase {
    public static final String[] NBT_TYPES = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};

    /**
     * Creates a new NBTBase object that corresponds with the passed in id.
     */
    protected static StorageBase createNewByType(byte id) {
        return switch (id) {
            case 0 -> new StorageTagEnd();
            case 1 -> new StorageTagByte();
            case 2 -> new StorageTagShort();
            case 3 -> new StorageTagInt();
            case 4 -> new StorageTagLong();
            case 5 -> new StorageTagFloat();
            case 6 -> new StorageTagDouble();
            case 7 -> new StorageTagByteArray();
            case 8 -> new StorageTagString();
            case 9 -> new StorageTagList();
            case 10 -> new StorageTagCompound();
            case 11 -> new StorageTagIntArray();
            case 12 -> new StorageTagLongArray();
            default -> null;
        };
    }

    public static String getName(int id) {
        return switch (id) {
            case 0 -> "TAG_End";
            case 1 -> "TAG_Byte";
            case 2 -> "TAG_Short";
            case 3 -> "TAG_Int";
            case 4 -> "TAG_Long";
            case 5 -> "TAG_Float";
            case 6 -> "TAG_Double";
            case 7 -> "TAG_Byte_Array";
            case 8 -> "TAG_String";
            case 9 -> "TAG_List";
            case 10 -> "TAG_Compound";
            case 11 -> "TAG_Int_Array";
            case 12 -> "TAG_Long_Array";
            case 99 -> "Any Numeric Tag";
            default -> "UNKNOWN";
        };
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    abstract void write(DataOutput output) throws IOException;

    abstract void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException;

    public abstract String toString();

    /**
     * Gets the type byte for the tag.
     */
    public abstract byte getId();

    /**
     * Creates a clone of the tag.
     */
    public abstract StorageBase copy();

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return false;
    }

    public boolean equals(Object instance) {
        return instance instanceof StorageBase && this.getId() == ((StorageBase) instance).getId();
    }

    public int hashCode() {
        return this.getId();
    }

    protected String getString() {
        return this.toString();
    }
}