package org.bsdevelopment.nbt;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bsdevelopment.nbt.other.NBTSizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class StorageTagList extends StorageBase {
    private static final Logger LOGGER = LogManager.getLogger(StorageTagList.class);
    private List<StorageBase> tagList = Lists.newArrayList();

    /**
     * The type byte for the tags in the list - they must all be of the same type.
     */
    private byte tagType = 0;

    public List<StorageBase> getList() {
        return tagList;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException {
        if (this.tagList.isEmpty()) {
            this.tagType = 0;
        } else {
            this.tagType = this.tagList.get(0).getId();
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (int i = 0; i < this.tagList.size(); ++i) {
            this.tagList.get(i).write(output);
        }
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(296L);

        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagType = input.readByte();
            int i = input.readInt();

            if (this.tagType == 0 && i > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                sizeTracker.read(32L * (long) i);
                this.tagList = Lists.newArrayListWithCapacity(i);

                for (int j = 0; j < i; ++j) {
                    StorageBase nbtbase = createNewByType(this.tagType);
                    nbtbase.read(input, depth + 1, sizeTracker);
                    this.tagList.add(nbtbase);
                }
            }
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return 9;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[");

        for (int i = 0; i < this.tagList.size(); ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.tagList.get(i));
        }

        return stringbuilder.append(']').toString();
    }

    public List<StorageBase> getTagList() {
        return tagList;
    }

    /**
     * Adds the provided tag to the end of the list. There is no check to verify this tag is of the same type as any
     * previous tag.
     */
    public StorageTagList appendTag(StorageBase nbt) {
        if (nbt.getId() == 0) {
            LOGGER.warn("Invalid TagEnd added to ListTag");
        } else {
            if (this.tagType == 0) {
                this.tagType = nbt.getId();
            } else if (this.tagType != nbt.getId()) {
                LOGGER.warn("Mismatching tag types to tag list (" + nbt.getClass().getSimpleName() + " != " + StorageBase.createNewByType(tagType).getClass().getSimpleName() + ")");
                return this;
            }

            this.tagList.add(nbt);
        }
        return this;
    }

    /**
     * Set the given index to the given tag
     */
    public void set(int idx, StorageBase nbt) {
        if (nbt.getId() == 0) {
            LOGGER.warn("Invalid TagEnd added to ListTag");
        } else if (idx >= 0 && idx < this.tagList.size()) {
            if (this.tagType == 0) {
                this.tagType = nbt.getId();
            } else if (this.tagType != nbt.getId()) {
                LOGGER.warn("Mismatching tag types to tag list (" + nbt.getClass().getSimpleName() + " != " + StorageBase.createNewByType(tagType).getClass().getSimpleName() + ")");
                return;
            }

            this.tagList.set(idx, nbt);
        } else {
            LOGGER.warn("index out of bounds to set tag in tag list");
        }
    }

    /**
     * Removes a tag at the given index.
     */
    public StorageBase removeTag(int i) {
        return this.tagList.remove(i);
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagList.isEmpty();
    }

    /**
     * Retrieves the NBTTagCompound at the specified index in the list
     */
    public StorageTagCompound getCompoundTagAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            StorageBase nbtbase = this.tagList.get(i);

            if (nbtbase.getId() == 10) {
                return (StorageTagCompound) nbtbase;
            }
        }

        return new StorageTagCompound();
    }

    public int getIntAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            StorageBase nbtbase = this.tagList.get(i);

            if (nbtbase.getId() == 3) {
                return ((StorageTagInt) nbtbase).getInt();
            }
        }

        return 0;
    }

    public int[] getIntArrayAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            StorageBase nbtbase = this.tagList.get(i);

            if (nbtbase.getId() == 11) {
                return ((StorageTagIntArray) nbtbase).getIntArray();
            }
        }

        return new int[0];
    }

    public double getDoubleAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            StorageBase nbtbase = this.tagList.get(i);

            if (nbtbase.getId() == 6) {
                return ((StorageTagDouble) nbtbase).getDouble();
            }
        }

        return 0.0D;
    }

    public float getFloatAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            StorageBase nbtbase = this.tagList.get(i);

            if (nbtbase.getId() == 5) {
                return ((StorageTagFloat) nbtbase).getFloat();
            }
        }

        return 0.0F;
    }

    /**
     * Retrieves the tag String value at the specified index in the list
     */
    public String getStringTagAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            StorageBase nbtbase = this.tagList.get(i);
            return nbtbase.getId() == 8 ? nbtbase.getString() : nbtbase.toString();
        } else {
            return "";
        }
    }

    /**
     * Get the tag at the given position
     */
    public StorageBase get(int idx) {
        return idx >= 0 && idx < this.tagList.size() ? this.tagList.get(idx) : new StorageTagEnd();
    }

    /**
     * Returns the number of tags in the list.
     */
    public int tagCount() {
        return this.tagList.size();
    }

    /**
     * Creates a clone of the tag.
     */
    public StorageTagList copy() {
        StorageTagList nbttaglist = new StorageTagList();
        nbttaglist.tagType = this.tagType;

        for (StorageBase nbtbase : this.tagList) {
            StorageBase nbtbase1 = nbtbase.copy();
            nbttaglist.tagList.add(nbtbase1);
        }

        return nbttaglist;
    }

    public boolean equals(Object instance) {
        if (!super.equals(instance)) {
            return false;
        } else {
            StorageTagList nbttaglist = (StorageTagList) instance;
            return this.tagType == nbttaglist.tagType && Objects.equals(this.tagList, nbttaglist.tagList);
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagList.hashCode();
    }

    public int getTagType() {
        return this.tagType;
    }
}