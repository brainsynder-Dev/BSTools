package org.bsdevelopment.nbt;

import org.bsdevelopment.nbt.other.NBTSizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StorageTagByte extends StoragePrimitive {
    /**
     * The byte value for the tag.
     */
    private byte data;

    StorageTagByte() {
    }

    public StorageTagByte(byte data) {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        output.writeByte(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(72L);
        this.data = input.readByte();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 1;
    }

    public String toString() {
        return this.data + "b";
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagByte copy() {
        return new StorageTagByte(this.data);
    }

    public boolean equals(Object p_equals_1_) {
        return super.equals(p_equals_1_) && this.data == ((StorageTagByte) p_equals_1_).data;
    }

    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public long getLong() {
        return this.data;
    }

    public int getInt() {
        return this.data;
    }

    public short getShort() {
        return this.data;
    }

    public byte getByte() {
        return this.data;
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return this.data;
    }
}