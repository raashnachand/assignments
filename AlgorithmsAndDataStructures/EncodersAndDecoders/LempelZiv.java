
import java.util.*;

public class LempelZiv {
    private static final int WINDOW_SIZE = 100;
    private static final int LOOKAHEAD_BUFFER_SIZE = 8;
    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */

    public static String compress(String input) {
        StringBuilder compressed = new StringBuilder();
        int cursor = 0;

        while (cursor < input.length()) {
            int matchOffset = 0;
            int matchLength = 0;
            char nextChar = input.charAt(cursor + matchLength);

            // Search for the longest match within the window
            for (int offset = 1; offset <= WINDOW_SIZE && cursor - offset >= 0; offset++) {
                int length = 0;
                while (length < LOOKAHEAD_BUFFER_SIZE && cursor + length < input.length()
                        && input.charAt(cursor - offset + length) == input.charAt(cursor + length)) {
                    length++;
                }
                if (length >= matchLength) {
                    matchOffset = offset;
                    matchLength = length;
                    nextChar = (cursor + length < input.length()) ? input.charAt(cursor + length) : '\0';
                }
            }

            // Append the tuple to the compressed output
            compressed.append('[')
                    .append(matchOffset)
                    .append('|')
                    .append(matchLength)
                    .append('|')
                    .append(nextChar)
                    .append(']');

            cursor += Math.max(1, matchLength);
        }

        return compressed.toString();
    }

    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        StringBuilder decompressed = new StringBuilder();
        int cursor = 0;

        while (cursor < compressed.length()) {
            if (compressed.charAt(cursor) == '[') {
                int offsetStart = cursor + 1;
                int offsetEnd = compressed.indexOf('|', offsetStart);
                int lengthStart = offsetEnd + 1;
                int lengthEnd = compressed.indexOf('|', lengthStart);
                int charStart = lengthEnd + 1;
                int charEnd = compressed.indexOf(']', charStart);

                int offset = Integer.parseInt(compressed.substring(offsetStart, offsetEnd));
                int length = Integer.parseInt(compressed.substring(lengthStart, lengthEnd));
                char character = compressed.charAt(charStart);

                int startPos = decompressed.length() - offset;
                for (int i = 0; i < length; i++) {
                    char repeatedChar = decompressed.charAt(startPos + i);
                    decompressed.append(repeatedChar);
                }
                decompressed.append(character);

                cursor = charEnd + 1;
            } else {
                decompressed.append(compressed.charAt(cursor));
                cursor++;
            }
        }

        return decompressed.toString();
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}
