package test;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class WideCharacterFormatter {

    public static void main(String[] args) {
        String sampleString = "你好，世界！"; // Sample string with wide characters.
        int visibleLength = calculateVisibleLength(sampleString);

        // Example of formatting a string to a fixed width using the calculated visible length.
        String formattedString = String.format("%-" + visibleLength + "s", sampleString);
        System.out.println(formattedString);
    }

    /**
     * Calculates the visible length of a string considering wide and narrow characters.
     * @param str The string to calculate the length for.
     * @return The visible length of the string.
     */
    public static int calculateVisibleLength(String str) {
        int length = 0;
        StringCharacterIterator iterator = new StringCharacterIterator(str);
        for (char ch = iterator.first(); ch != CharacterIterator.DONE; ch = iterator.next()) {
            if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                    || Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                    || Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)) {
                // For CJK characters and symbols, count as two.
                length += 2;
            } else if (Character.isSurrogate(ch)) {
                // For surrogate pairs, count as one.
                length += 1;
            } else {
                // For other characters, count as one.
                length += 1;
            }
        }
        return length;
    }
}