package org.bsdevelopment.strings;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class StringUtilities {

    /**
     * <p>Check if a String starts with a specified prefix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.startsWith(null, null)      = true
     * StringUtils.startsWith(null, "abc")     = false
     * StringUtils.startsWith("abcdef", null)  = false
     * StringUtils.startsWith("abcdef", "abc") = true
     * StringUtils.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @param str    the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case sensitive, or
     * both <code>null</code>
     * @see java.lang.String#startsWith(String)
     * @since 2.4
     */
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * <p>Case insensitive check if a String starts with a specified prefix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * StringUtils.startsWithIgnoreCase(null, null)      = true
     * StringUtils.startsWithIgnoreCase(null, "abc")     = false
     * StringUtils.startsWithIgnoreCase("abcdef", null)  = false
     * StringUtils.startsWithIgnoreCase("abcdef", "abc") = true
     * StringUtils.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     *
     * @param str    the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case insensitive, or
     * both <code>null</code>
     * @see java.lang.String#startsWith(String)
     * @since 2.4
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * <p>Check if a String starts with a specified prefix (optionally case insensitive).</p>
     *
     * @param str        the String to check, may be null
     * @param prefix     the prefix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *                   (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     * both <code>null</code>
     * @see java.lang.String#startsWith(String)
     */
    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * <p>Check if a String starts with any of an array of specified strings.</p>
     *
     * <pre>
     * StringUtils.startsWithAny(null, null)      = false
     * StringUtils.startsWithAny(null, new String[] {"abc"})  = false
     * StringUtils.startsWithAny("abcxyz", null)     = false
     * StringUtils.startsWithAny("abcxyz", new String[] {""}) = false
     * StringUtils.startsWithAny("abcxyz", new String[] {"abc"}) = true
     * StringUtils.startsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @param string        the String to check, may be null
     * @param searchStrings the Strings to find, may be null or empty
     * @return <code>true</code> if the String starts with any of the the prefixes, case insensitive, or
     * both <code>null</code>
     * @see #startsWith(String, String)
     * @since 2.5
     */
    public static boolean startsWithAny(String string, String[] searchStrings) {
        if (isEmpty(string) || ArrayUtils.isEmpty(searchStrings)) {
            return false;
        }
        for (int i = 0; i < searchStrings.length; i++) {
            String searchString = searchStrings[i];
            if (StringUtils.startsWith(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    // endsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String ends with a specified suffix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.endsWith(null, null)      = true
     * StringUtils.endsWith(null, "def")     = false
     * StringUtils.endsWith("abcdef", null)  = false
     * StringUtils.endsWith("abcdef", "def") = true
     * StringUtils.endsWith("ABCDEF", "def") = false
     * StringUtils.endsWith("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str    the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case sensitive, or
     * both <code>null</code>
     * @see java.lang.String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * <p>Case insensitive check if a String ends with a specified suffix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * StringUtils.endsWithIgnoreCase(null, null)      = true
     * StringUtils.endsWithIgnoreCase(null, "def")     = false
     * StringUtils.endsWithIgnoreCase("abcdef", null)  = false
     * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str    the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case insensitive, or
     * both <code>null</code>
     * @see java.lang.String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>Check if a String ends with a specified suffix (optionally case insensitive).</p>
     *
     * @param str        the String to check, may be null
     * @param suffix     the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *                   (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     * both <code>null</code>
     * @see java.lang.String#endsWith(String)
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(CharSequence charSequence) {
        int length = length(charSequence);
        if (length == 0) return true;

        for (int i = 0; i < length; ++i) {
            if (!Character.isWhitespace(charSequence.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Combines a String array into a single message
     *
     * @param args  The array that needs to be merged.
     * @param start The index of where the combined string starts.
     * @return The Combined String
     */
    public static String mergeStringArray(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * It takes a String, a character to pad with, the maximum padding, and the alignment of the text, and returns a padded
     * String
     *
     * @param text        The text to be padded.
     * @param paddingChar The character to use for padding.
     * @param maxPadding  The maximum length of the string.
     * @param alignText   This is an enum that can be used to align the text to the left, right or center.
     * @return A string with the text padded to the left, right, or center.
     */
    public static String getPaddedString(String text, char paddingChar, int maxPadding, StringUtilities.AlignText alignText) {
        if (text == null) {
            throw new NullPointerException("Can not add padding in null String!");
        }

        int length = text.length();
        int padding = (maxPadding - length) / 2;//decide left and right padding
        if (padding <= 0) {
            return text;// return actual String if padding is less than or equal to 0
        }

        String empty = "", hash = "#";//hash is used as a place holder

        // extra character in case of String with even length
        int extra = (length % 2 == 0) ? 1 : 0;

        String leftPadding = "%" + padding + "s";
        String rightPadding = "%" + (padding - extra) + "s";

        // Will align the text to the selected side
        switch (alignText) {
            case LEFT:
                leftPadding = "%s";
                rightPadding = "%" + (padding + (padding - extra)) + "s";
                break;
            case RIGHT:
                rightPadding = "%s";
                leftPadding = "%" + (padding + (padding - extra)) + "s";
                break;
        }

        String strFormat = leftPadding + "%s" + rightPadding;
        String formattedString = String.format(strFormat, empty, hash, empty);

        //Replace space with * and hash with provided String
        String paddedString = formattedString.replace(' ', paddingChar).replace(hash, text);
        return paddedString;
    }

    /**
     * Replaces the last occurrence of the target string with the replacement string
     *
     * @param target      the string to be replaced
     * @param replacement The string to replace the target with.
     * @param haystack    The string to search in
     * @return The last occurrence of the target string is replaced with the replacement string.
     */
    public static String replaceLast(String target, String replacement, String haystack) {
        if (!haystack.contains(target)) return haystack;
        int pos = haystack.lastIndexOf(target);
        if (pos > -1) {
            return haystack.substring(0, pos)
                    + replacement
                    + haystack.substring(pos + target.length());
        } else {
            return haystack;
        }
    }

    /**
     * It returns true if the string contains the character the specified number of times
     *
     * @param string    The string to search in
     * @param character The character to search for.
     * @param count     The number of times the character should appear in the string. If this is -1, then the method will
     *                  return true if the character appears at least once.
     * @return The method returns a boolean value.
     */
    public static boolean contains(String string, char character, int count) {
        if (count == -1) return string.contains(String.valueOf(character));

        int i = 0;
        for (char c : string.toCharArray()) {
            if (c == character) i++;
        }

        return (i == count);
    }

    /**
     * Return the part of the string after the first occurrence of the needle.
     *
     * @param needle   The string to search for
     * @param haystack The string to search in
     * @return The string after the needle in the haystack.
     */
    public static String after(String needle, String haystack) {
        return haystack.substring((haystack.indexOf(needle) + needle.length()));
    }

    /**
     * Return the part of the string after the last occurrence of the needle.
     *
     * @param needle   The string to search for
     * @param haystack The string to search in
     * @return The substring of haystack after the last occurrence of needle.
     */
    public static String afterLast(String needle, String haystack) {
        return haystack.substring(reversePos(needle, haystack) + needle.length());
    }

    /**
     * Return the part of the string before the first occurrence of the needle.
     *
     * @param needle   The string to search for
     * @param haystack The string to search in
     * @return The string before the needle.
     */
    public static String before(String needle, String haystack) {
        return haystack.substring(0, haystack.indexOf(needle));
    }

    /**
     * Return the part of the haystack before the last occurrence of the needle.
     *
     * @param needle   The string to search for
     * @param haystack The string to search in
     * @return The substring of haystack from the beginning to the position of the last occurrence of needle.
     */
    public static String beforeLast(String needle, String haystack) {
        return haystack.substring(0, reversePos(needle, haystack));
    }

    /**
     * Return the string between the first and last strings, inclusive of the first string but not the last string.
     *
     * @param first    The string that comes before the string you want to extract.
     * @param last     The string that will be used to find the end of the string.
     * @param haystack The string to search in
     * @return The string between the first and last strings.
     */
    public static String between(String first, String last, String haystack) {
        return before(last, after(first, haystack));
    }

    /**
     * Return the text between the last occurrence of the first string and the last occurrence of the second string.
     *
     * @param first    The first string to search for.
     * @param last     The last string to search for
     * @param haystack The string to search in
     * @return The string between the last two occurrences of the first and last strings.
     */
    public static String betweenLast(String first, String last, String haystack) {
        return afterLast(first, beforeLast(last, haystack));
    }


    /**
     * Returns the last position of the needle in the original haystack.
     *
     * @param needle   The string to search for
     * @param haystack the string to search in
     * @return The position of the last occurrence of the needle in the haystack.
     */
    public static int reversePos(String needle, String haystack) {
        int pos = reverse(haystack).indexOf(reverse(needle));
        return haystack.length() - pos - needle.length();
    }

    /**
     * It reverses the letters in a string
     *
     * @param input a string
     * @return A string that is the reverse of the input string.
     */
    public static String reverse(String input) {
        char[] chars = input.toCharArray();
        List<Character> characters = new ArrayList<>();

        for (char c : chars)
            characters.add(c);

        Collections.reverse(characters);
        ListIterator iterator = characters.listIterator();
        StringBuilder builder = new StringBuilder();

        while (iterator.hasNext())
            builder.append(iterator.next());
        return builder.toString();
    }

    /**
     * It scrambles the words in a string.
     *
     * @param input The string to be scrambled.
     * @return A scrambled version of the input string.
     */
    public static String scramble(String input) {
        StringBuilder out = new StringBuilder();
        for (String part : input.split(" ")) {
            List<Character> characters = new ArrayList<>();
            for (char c : part.toCharArray()) {
                characters.add(c);
            }
            StringBuilder output = new StringBuilder(part.length());
            while (characters.size() != 0) {
                int rndm = (int) (Math.random() * characters.size());
                output.append(characters.remove(rndm));
            }
            out.append(output).append(' ');
        }
        return out.toString().trim();
    }

    /**
     * An enum that is used to align the text in the getPaddedString method.
     */
    public enum AlignText {
        LEFT, RIGHT, CENTER
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}