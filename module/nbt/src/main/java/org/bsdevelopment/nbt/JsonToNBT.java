package org.bsdevelopment.nbt;

import com.google.common.collect.Lists;
import org.bsdevelopment.nbt.other.NBTException;

import java.util.List;
import java.util.regex.Pattern;

public class JsonToNBT {
    private static final Pattern DOUBLE_PATTERN2 = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    private JsonToNBT(StringReader reader) {
        this.reader = reader;
    }

    public static JsonToNBT parse(String string) {
        return new JsonToNBT(new StringReader(string));
    }

    public static StorageTagCompound getTagFromJson(String string) throws NBTException {
        return parse(string).toCompound();
    }

    public StorageTagCompound toCompound() throws NBTException {
        StorageTagCompound compoundTag = this.parseCompoundTag();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw this.reader.throwError("Trailing data found");
        } else {
            return compoundTag;
        }
    }

    public StorageTagList toList() throws NBTException {
        StorageTagList compoundTag = (StorageTagList) this.list();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw this.reader.throwError("Trailing data found");
        } else {
            return compoundTag;
        }
    }

    private String readString() throws NBTException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw this.reader.throwError("Expected key");
        } else {
            return this.reader.readString();
        }
    }

    protected StorageBase parseTagPrimitive() throws NBTException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();
        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return new StorageTagString(this.reader.readQuotedString());
        } else {
            String string = this.reader.readUnquotedString();
            if (string.isEmpty()) {
                this.reader.setCursor(i);
                throw this.reader.throwError("Expected value");
            } else {
                return this.parsePrimitive(string);
            }
        }
    }

    private StorageBase parsePrimitive(String value) {
        try {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                return new StorageTagByte((byte) (Boolean.getBoolean(value) ? 1 : 0));
            }

            if (FLOAT_PATTERN.matcher(value).matches()) {
                return new StorageTagFloat(Float.parseFloat(value.substring(0, value.length() - 1)));
            }

            if (BYTE_PATTERN.matcher(value).matches()) {
                return new StorageTagByte(Byte.parseByte(value.substring(0, value.length() - 1)));
            }

            if (LONG_PATTERN.matcher(value).matches()) {
                return new StorageTagLong(Long.parseLong(value.substring(0, value.length() - 1)));
            }

            if (SHORT_PATTERN.matcher(value).matches()) {
                return new StorageTagShort(Short.parseShort(value.substring(0, value.length() - 1)));
            }

            if (INT_PATTERN.matcher(value).matches()) {
                return new StorageTagInt(Integer.parseInt(value));
            }

            if (DOUBLE_PATTERN.matcher(value).matches()) {
                return new StorageTagDouble(Double.parseDouble(value.substring(0, value.length() - 1)));
            }

            if (DOUBLE_PATTERN2.matcher(value).matches()) { //Without the d (EG: 1.0d -> 1.0)
                return new StorageTagDouble(Double.parseDouble(value));
            }
        } catch (NumberFormatException ignored) {
        }

        return new StorageTagString(value);
    }

    private StorageBase parseTag() throws NBTException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw this.reader.throwError("Expected value");
        } else {
            char c = this.reader.peek();
            if (c == '{') {
                return this.parseCompoundTag();
            } else {
                return c == '[' ? this.parseTagArray() : this.parseTagPrimitive();
            }
        }
    }

    private StorageBase parseTagArray() throws NBTException {
        return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.arrays() : this.list();
    }

    private StorageTagCompound parseCompoundTag() throws NBTException {
        this.expect('{');
        StorageTagCompound compoundTag = new StorageTagCompound();
        this.reader.skipWhitespace();

        while (this.reader.canRead() && this.reader.peek() != '}') {
            int i = this.reader.getCursor();
            String string = this.readString();
            if (string.isEmpty()) {
                this.reader.setCursor(i);
                throw this.reader.throwError("Expected non-empty key");
            }

            this.expect(':');
            compoundTag.setTag(string, this.parseTag());
            if (!this.readComma()) {
                break;
            }

            if (!this.reader.canRead()) {
                throw this.reader.throwError("Expected key");
            }
        }

        this.expect('}');
        return compoundTag;
    }

    private StorageBase list() throws NBTException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw this.reader.throwError("Expected value");
        } else {
            StorageTagList listTag = new StorageTagList();
            int reader = -1;

            while (this.reader.peek() != ']') {
                int i = this.reader.getCursor();
                StorageBase tag = this.parseTag();
                int reader2 = tag.getId();
                if (reader == -1) {
                    reader = reader2;
                } else if (reader2 != reader) {
                    this.reader.setCursor(i);
                    throw this.reader.throwError("Unable to insert " + StorageBase.getName(reader2) + " into ListTag of type " + StorageBase.getName(reader));
                }

                listTag.appendTag(tag);
                if (!this.readComma()) {
                    break;
                }

                if (!this.reader.canRead()) {
                    throw this.reader.throwError("Expected value");
                }
            }

            this.expect(']');
            return listTag;
        }
    }

    private StorageBase arrays() throws NBTException {
        this.expect('[');
        int i = this.reader.getCursor();
        char c = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw this.reader.throwError("Expected value");
        } else if (c == 'B') {
            return new StorageTagByteArray(this.readArray((byte) 7, (byte) 1));
        } else if (c == 'L') {
            return new StorageTagLongArray(this.readArray((byte) 12, (byte) 4));
        } else if (c == 'I') {
            return new StorageTagIntArray(this.readArray((byte) 11, (byte) 3));
        } else {
            this.reader.setCursor(i);
            throw this.reader.throwError("Invalid array type '" + c + "' found");
        }
    }

    private <T extends Number> List<T> readArray(byte b, byte b1) throws NBTException {
        List<T> list = Lists.newArrayList();

        while (true) {
            if (this.reader.peek() != ']') {
                int i = this.reader.getCursor();
                StorageBase tag = this.parseTag();
                int reader = tag.getId();

                if (reader != b1) {
                    throw this.reader.throwError("Unable to insert " + StorageBase.getName(i) + " into " + StorageBase.getName(b));
                }

                if (b1 == 1) {
                    list.add((T) Byte.valueOf(((StoragePrimitive) tag).getByte()));
                } else if (b1 == 4) {
                    list.add((T) Long.valueOf(((StoragePrimitive) tag).getLong()));
                } else {
                    list.add((T) Integer.valueOf(((StoragePrimitive) tag).getInt()));
                }

                if (this.readComma()) {
                    if (!this.reader.canRead()) {
                        throw this.reader.throwError("Expected value");
                    }
                    continue;
                }
            }

            this.expect(']');
            return list;
        }
    }

    private void expect(char c) throws NBTException {
        this.reader.skipWhitespace();
        this.reader.expect(c);
    }

    private boolean checkChar(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+';
    }

    private boolean readComma() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }
}