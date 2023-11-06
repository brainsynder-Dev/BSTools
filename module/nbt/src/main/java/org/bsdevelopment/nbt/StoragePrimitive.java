package org.bsdevelopment.nbt;

abstract class StoragePrimitive extends StorageBase {
    public abstract long getLong();

    public abstract int getInt();

    public abstract short getShort();

    public abstract byte getByte();

    public abstract double getDouble();

    public abstract float getFloat();


    int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }
}