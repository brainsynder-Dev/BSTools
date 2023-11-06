package org.bsdevelopment.nbt;

import org.bsdevelopment.nbt.other.NBTSizeTracker;

import java.io.DataInput;
import java.io.DataOutput;

public class StorageTagEnd extends StorageBase {
    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) {
        sizeTracker.read(64L);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) {
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 0;
    }

    public String toString() {
        return "END";
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagEnd copy() {
        return new StorageTagEnd();
    }
}