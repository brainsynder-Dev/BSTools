package org.bsdevelopment.nbt.other;

public class NBTException extends Exception {
    public static final int CONTEXT_AMOUNT = 10;
    private final String input;
    private final String message;
    private final int cursor;

    public NBTException(String message, String input, int cursor) {
        super(message, null, true, true);
        this.cursor = cursor;
        this.message = message;
        this.input = input;
    }

    @Override
    public String getMessage() {
        String message = this.message;
        String context = getContext();
        if (context != null) message += " at position " + cursor + ": " + context;
        return message;
    }

    private String getContext() {
        StringBuilder stringbuilder = new StringBuilder();
        int cursor = Math.min(this.input.length(), this.cursor);
        if (cursor > CONTEXT_AMOUNT) stringbuilder.append("...");
        stringbuilder.append(this.input, Math.max(0, cursor - CONTEXT_AMOUNT), cursor);
        stringbuilder.append("<--[HERE]");
        return stringbuilder.toString();
    }
}