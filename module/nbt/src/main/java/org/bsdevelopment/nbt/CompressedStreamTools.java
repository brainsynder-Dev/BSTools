package org.bsdevelopment.nbt;

import org.bsdevelopment.nbt.other.NBTSizeTracker;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools {
    /**
     * Load the gzipped compound from the inputstream.
     */
    public static StorageBase readCompressedBase(InputStream is) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));
        StorageBase base;

        try {
            base = read(datainputstream, NBTSizeTracker.INFINITE);
        } finally {
            datainputstream.close();
        }

        return base;
    }

    public static StorageTagCompound readCompressed(InputStream is) throws IOException {
        StorageBase nbtbase = readCompressedBase(is);

        if (nbtbase instanceof StorageTagCompound) {
            return (StorageTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    /**
     * Write the compound, gzipped, to the outputstream.
     */
    public static void writeCompressed(StorageBase compound, OutputStream outputStream) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

        try {
            writeTag(compound, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }
    }

    /**
     * Reads from a CompressedStream.
     */
    public static StorageBase read(DataInputStream inputStream) throws IOException {
        return read(inputStream, NBTSizeTracker.INFINITE);
    }

    /**
     * Reads the given DataInput, constructs, and returns an NBTTagCompound with the data from the DataInput
     */
    public static StorageBase read(DataInput input, NBTSizeTracker accounter) throws IOException {
        return read(input, 0, accounter);
    }

    @Deprecated
    public static void write(StorageBase compound, DataOutput output) throws IOException {
        writeTag(compound, output);
    }

    public static void writeTag(StorageBase tag, DataOutput output) throws IOException {
        output.writeByte(tag.getId());

        if (tag.getId() != 0) {
            output.writeUTF("");
            tag.write(output);
        }
    }

    private static StorageBase read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
        byte b0 = input.readByte();

        if (b0 == 0) {
            return new StorageTagEnd();
        } else {
            input.readUTF();
            StorageBase nbtbase = StorageBase.createNewByType(b0);

            try {
                nbtbase.read(input, depth, accounter);
                return nbtbase;
            } catch (IOException ioexception) {
                throw new IOException(ioexception);
            }
        }
    }
}