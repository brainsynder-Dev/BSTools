package org.bsdevelopment.strings;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.tellraw.Part;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colorize {
    // A regex pattern that is used to find hex color codes in a string.
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");

    /**
     * If the hex code is valid, return the color, otherwise return white.
     *
     * @param hex The hex code of the color you want to fetch.
     * @return The color of the hex code.
     */
    public static ChatColor fetchColor(String hex) {
        return fetchColor(hex, ChatColor.WHITE);
    }

    /**
     * It takes a hex color code, and returns a ChatColor object
     *
     * @param hex      The hex code to fetch the color from.
     * @param fallback The color to return if the hex code is invalid.
     * @return A ChatColor object.
     */
    public static ChatColor fetchColor(String hex, ChatColor fallback) {
        if ((hex == null) || hex.isEmpty()) return fallback;
        if (hex.startsWith("&#")) hex = hex.replace("&", "");
        if (!hex.startsWith("#")) hex = "#" + hex;
        return ChatColor.of(hex);
    }

    /**
     * It takes a color and returns a ChatColor
     *
     * @param color The color you want to convert to a ChatColor.
     * @return The ChatColor that is closest to the color that is passed in.
     */
    public static ChatColor fetchColor(Color color) {
        if (color == null) return ChatColor.WHITE;
        return fetchColor(toHex(color.getRed(), color.getGreen(), color.getBlue()));
    }

    /**
     * It takes a string, replaces all instances of the color character with an ampersand (&), then replaces all instances of a
     * hex color code (&#FFFFFF) with the corresponding color code
     *
     * @param text The text to translate
     * @return The translated text.
     */
    public static String translateBungeeHex(String text) {
        if ((text == null) || text.isEmpty()) return text;
        text = text.replace(ChatColor.COLOR_CHAR, '&');
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = String.valueOf(ChatColor.of("#" + matcher.group(1)));
            matcher.appendReplacement(buffer, replacement).toString();
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    /**
     * It takes a string, and replaces all instances of '&' with the Minecraft color code character
     *
     * @param text The text to translate
     * @return The text is being returned.
     */
    public static String translateBungee(String text) {
        if ((text == null) || text.isEmpty()) return text;
        return ChatColor.translateAlternateColorCodes('&', text);
    }


    /**
     * It takes a string, and replaces all instances of '&' with the Minecraft color code character
     *
     * @param text The text to translate.
     * @return The text is being returned.
     */
    public static String translateBukkit(String text) {
        if ((text == null) || text.isEmpty()) return text;
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * It checks if a string contains any hex colors (&#FFFFFF)
     *
     * @param text The text to check for hex colors.
     * @return A boolean value.
     */
    public static boolean containsHexColors(String text) {
        if ((text == null) || text.isEmpty()) return false;
        text = text.replace(ChatColor.COLOR_CHAR, '&');
        Matcher matcher = HEX_PATTERN.matcher(text);
        return matcher.find();
    }

    /**
     * It replaces all hex colors (&x&e&3&a&a&4&f) in a string with the '&#' format (&#e3aa4f)
     *
     * @param text The text to remove the hex colors from
     * @return The text with the hex colors removed.
     */
    public static String removeHexColor(String text) {
        // String is empty
        if ((text == null) || text.isEmpty()) return text;

        // The String does not contain any valid hex
        //if (!containsHexColors(text)) return text;

        // Replaces the COLOR_CHAR('§') to '&'
        text = text.replace(ChatColor.COLOR_CHAR, '&');
        Pattern word = Pattern.compile("&x");
        Matcher matcher = word.matcher(text);

        char[] chars = text.toCharArray();
        while (matcher.find()) {
            StringBuilder builder = new StringBuilder();
            int start = matcher.start();
            int end = (start + 13);

            // If the '&x' is at the end of the string ignore it
            if (end > text.length()) continue;
            for (int i = start; i < end; i++) builder.append(chars[i]);

            String hex = builder.toString();
            hex = hex.replace("&x", "").replace("&", "");
            text = text.replace(builder.toString(), "&#" + hex);
        }

        return text;
    }

    /**
     * It takes a list of parts and returns a json object that contains the parts
     * <p>
     * Example: {"text":"","extra":[{"text","color"}]}
     *
     * @param parts The list of parts to convert to JSON.
     * @return A JsonObject
     */
    public static JsonObject convertParts2Json(List<Part> parts) {
        JsonObject json = new JsonObject();
        json.add("text", "");

        JsonArray extra = new JsonArray();
        for (Part part : parts) {
            extra.add(part.toJson());
        }
        json.add("extra", extra);
        return json;
    }

    /**
     * It splits a string into parts, each part containing a color and text
     *
     * @param value The string you want to split
     * @return A list of parts.
     */
    public static List<Part> splitMessageToParts(String value) {
        List<Part> parts = new ArrayList<>();
        // String is empty
        if ((value == null) || value.isEmpty()) return parts;
        value = value.replace(org.bukkit.ChatColor.COLOR_CHAR, '&');

        if (value.contains("&")) {
            String[] args = value.split("&");

            for (String string : args) {
                if (string == null) continue;
                if (string.isEmpty()) continue;

                // Has hex color

                Part part = new Part();
                if (string.startsWith("#")) {
                    // 0-6

                    StringBuilder HEX = new StringBuilder();
                    int end = 6;
                    // If the '&x' is at the end of the string ignore it
                    for (int i = -1; i < end; i++) HEX.append(string.charAt(i + 1));
                    part.text = string.replace(HEX.toString(), "");
                    part.customColor = hex2Color(HEX.toString());
                } else {
                    org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(string.charAt(0));
                    if (color == null) {
                        part.text = "&" + string;
                    } else {
                        part.text = string.replaceFirst(String.valueOf(string.charAt(0)), "");
                        part.color = color;
                    }
                }
                parts.add(part);
            }
        } else {
            // 'value' does not contain '&'
            parts.add(new Part(value));
        }
        return parts;
    }

    /**
     * It takes two hex colors and a string, and returns the string with each character colored with a color that fades
     * from the first hex color to the second hex color
     *
     * @param firstHex  The first hex color to start with.
     * @param secondHex The hex color you want to end on.
     * @param text      The text to be colored
     * @return A string with the text in it, with the colors fading from the first color to the second color.
     */
    public static String colorFadeText(String firstHex, String secondHex, String text) {
        int[] start = hexToRGB(firstHex);
        int[] last = hexToRGB(secondHex);

        StringBuilder builder = new StringBuilder();

        int dR = numberFade(start[0], last[0], text.length());
        int dG = numberFade(start[1], last[1], text.length());
        int dB = numberFade(start[2], last[2], text.length());

        for (int i = 0; i < text.length(); i++) {
            builder
                    .append(ChatColor.of(new java.awt.Color(start[0] + dR * i, start[1] + dG * i, start[2] + dB * i)))
                    .append(text.charAt(i));
        }
        return builder.toString();
    }

    /**
     * It takes a hexadecimal string, converts it to an integer, and then converts that integer to a color
     *
     * @param hex The hexadecimal color code.
     * @return A color object
     */
    public static Color hex2Color(String hex) {
        return Color.fromRGB(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }


    /**
     * It takes three integers, converts them to hexadecimal, and returns a string
     *
     * @param r The red value of the color (from 0-255)
     * @param g The green value of the color (from 0-255)
     * @param b The blue value of the color (0-255)
     * @return A string in the format of #RRGGBB
     */
    public static String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    /**
     * Convert the given number to a two digit hexadecimal string, padding with a leading zero if necessary.
     *
     * @param number The number to convert to hex.
     * @return A string of the hex value of the number.
     */
    private static String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    /**
     * It takes a string of 6 hexadecimal characters and returns an array of 3 integers
     *
     * @param hex The RGB value to convert to an int array.
     * @return An array of integers.
     */
    public static int[] hexToRGB(String hex) {
        int[] ret = new int[3];
        for (int i = 0; i < 3; i++) {
            ret[i] = hexToInt(hex.charAt(i * 2), hex.charAt(i * 2 + 1));
        }
        return ret;
    }

    /**
     * It converts a hexadecimal character to an integer
     *
     * @param a The first character of the hexadecimal number.
     * @param b The byte array to convert to hex
     * @return The hexadecimal value of the two characters.
     */
    private static int hexToInt(char a, char b) {
        int x = a < 65 ? a - 48 : a - 55;
        int y = b < 65 ? b - 48 : b - 55;
        return x * 16 + y;
    }

    /**
     * Given an initial value, a final value, and a number of steps, return the amount to increment the value by for each
     * step.
     *
     * @param i initial value
     * @param f final value
     * @param n The number of frames you want to fade between the two numbers.
     * @return The difference between the final and initial values divided by the number of steps minus one.
     */
    private static Integer numberFade(int i, int f, int n) {
        return (f - i) / (n - 1);
    }
}
