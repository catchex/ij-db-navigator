package com.dci.intellij.dbn.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringUtil extends com.intellij.openapi.util.text.StringUtil {
    @NotNull
    public static List<String> tokenize(@NotNull String string, @NotNull String separator) {
        List<String> tokens = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, separator);
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken().trim());
        }
        return tokens;
    }

    public static String concatenate(List<String> tokens, String separator) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            buffer.append(token);
            if (i < tokens.size() - 1) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    public static boolean containsOneOf(String string, String... tokens) {
        for (String token : tokens) {
            if (string.contains(token)) return true;
        }
        return false;
    }

    public static boolean isMixedCase(String string) {
        boolean upperCaseFound = false;
        boolean lowerCaseFound = false;
        for (int i=0; i<string.length(); i++) {
            char chr = string.charAt(i);

            if (!upperCaseFound && Character.isUpperCase(chr)) {
                upperCaseFound = true;
            } else if (!lowerCaseFound && Character.isLowerCase(chr)) {
                lowerCaseFound = true;
            }

            if (upperCaseFound && lowerCaseFound) return true;
        }

        return upperCaseFound == lowerCaseFound;
    }


    public static boolean isEmptyOrSpaces(String string) {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }

    public static boolean isNotEmptyOrSpaces(String string) {
        return !isEmptyOrSpaces(string);
    }

    public static boolean isOneOf(String string, String ... values) {
        for (String value : values) {
            if (value.equals(string)) return true;
        }
        return false;
    }

    public static boolean isOneOfIgnoreCase(String string, String ... values) {
        for (String value : values) {
            if (value.equalsIgnoreCase(string)) return true;
        }
        return false;
    }



    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isWord(String name) {
        boolean containsLetters = false;
        for (char c : name.toCharArray()) {
            boolean isLetter = Character.isLetter(c) || c == '_';
            containsLetters = containsLetters || isLetter;
            if (!isLetter && !Character.isDigit(c)) {
                return false;
            }
        }
        return containsLetters;
    }

    public static String removeCharacter(String content, char c) {
        int index = content.indexOf(c);
        if (index > -1) {
            int beginIndex = 0;
            int endIndex = index;
            StringBuilder buffer = new StringBuilder();
            while (endIndex > -1) {
                if (beginIndex < endIndex) buffer.append(content.substring(beginIndex, endIndex));
                beginIndex = endIndex + 1;
                endIndex = content.indexOf(c, beginIndex);
            }
            if (beginIndex < content.length() - 1) {
                buffer.append(content.substring(beginIndex));
            }
            return buffer.toString();
        }
        return content;
    }

    public static String toUnicodeRepresentation(String str) {
        if (str == null) return str;
        StringBuilder unicodeString = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if ((ch >= 0x0020) && (ch <= 0x007e)) {
                // no need to convert to unicode
                unicodeString.append(ch);
            } else {
                // standard unicode format.
                unicodeString.append("\\u");

                // Get hex value of the char.
                String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);

                // Prepend zeros because unicode requires 4 digits
                for (int j = 0; j < 4 - hex.length(); j++)
                    unicodeString.append("0");
                unicodeString.append(hex.toLowerCase());        // standard unicode format.
                //ostr.append(hex.toLowerCase(Locale.ENGLISH));
            }
        }

        return unicodeString.toString();
    }

    public static @NotNull String trim(@Nullable String message) {
        return isEmptyOrSpaces(message) ? "" : message.trim();
    }

    public static String wrap(String string, int maxRowLength, String wrapCharacters) {
        StringBuilder builder = new StringBuilder();
        if (string != null) {
            StringTokenizer tokenizer = new StringTokenizer(string, wrapCharacters, true);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int wrapIndex = builder.lastIndexOf("\n") + 1;
                if (wrapCharacters.contains(token)) {
                    builder.append(token);
                } else {
                    int tokenLength = token.length();
                    if (tokenLength >= maxRowLength) {
                        if (wrapIndex != builder.length()) {
                            builder.append("\n");
                        }
                        builder.append(token.trim());
                    } else {
                        if (builder.length() - wrapIndex + tokenLength > maxRowLength) {
                            builder.append("\n");
                        }
                        builder.append(token);
                    }
                }
            }
        }
        return builder.toString().trim();
    }

    public static int computeMaxRowLength(String string) {
        int offset = 0;
        int maxLength = 0;
        while (true) {
            int index = string.indexOf('\n', offset);
            if (index == -1) {
                maxLength = Math.max(maxLength, string.length() - offset);
                break;
            } else {
                int length = index - offset;
                maxLength = Math.max(maxLength, length);
                offset = index + 1;
            }

        }
        return maxLength;
    }

    // TODO IJ9 compatibility
    public static boolean equals(@Nullable CharSequence s1, @Nullable CharSequence s2) {
        if (s1 == null ^ s2 == null) {
            return false;
        }

        if (s1 == null) {
            return true;
        }

        if (s1.length() != s2.length()) {
            return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        System.out.println(computeMaxRowLength("\ntest\ntest2342345345345\n234\n2324444444444444444444444444444444444\n1"));
    }

    public static boolean containsIgnoreCase(@NotNull CharSequence where, @NotNull CharSequence what){
        return indexOfIgnoreCase(where, what, 0) > -1;
    }

    public static int indexOfIgnoreCase(@NotNull CharSequence where, @NotNull CharSequence what, int fromIndex) {
        int targetCount = what.length();
        int sourceCount = where.length();

        if (fromIndex >= sourceCount) {
            return targetCount == 0 ? sourceCount : -1;
        }

        if (fromIndex < 0) {
            fromIndex = 0;
        }

        if (targetCount == 0) {
            return fromIndex;
        }

        char first = what.charAt(0);
        int max = sourceCount - targetCount;

        for (int i = fromIndex; i <= max; i++) {
      /* Look for first character. */
            if (!charsEqualIgnoreCase(where.charAt(i), first)) {
                while (++i <= max && !charsEqualIgnoreCase(where.charAt(i), first)) ;
            }

      /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = 1; j < end && charsEqualIgnoreCase(where.charAt(j), what.charAt(k)); j++, k++) ;

                if (j == end) {
          /* Found whole string. */
                    return i;
                }
            }
        }

        return -1;
    }

    public static StringBuilder appendToUpperCase(StringBuilder builder, CharSequence s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char upCased = toUpperCase(c);
            builder.append(upCased);
        }

        return builder;
    }

    public static StringBuilder appendToLowerCase(StringBuilder builder, CharSequence s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char lowCased = toLowerCase(c);
            builder.append(lowCased);
        }

        return builder;
    }


    public static CharSequence toUpperCase(CharSequence s) {
        StringBuilder answer = null;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char upcased = toUpperCase(c);
            if (answer == null && upcased != c) {
                answer = new StringBuilder(s.length());
                answer.append(s.subSequence(0, i));
            }

            if (answer != null) {
                answer.append(upcased);
            }
        }

        return answer == null ? s : answer.toString();
    }

    public static boolean equalsIgnoreCase(@Nullable CharSequence s1, @Nullable CharSequence s2) {
        if (s1 == null ^ s2 == null) {
            return false;
        }

        if (s1 == null) {
            return true;
        }

        if (s1.length() != s2.length()) {
            return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            if (!charsEqualIgnoreCase(s1.charAt(i),s2.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String[] toStringArray(String separatedValues, String separator) {
        String[] array = null;
        if (separatedValues != null) {
            StringTokenizer tokenizer = new StringTokenizer(separatedValues, separator);
            array = new String[tokenizer.countTokens()];
            int index = 0;
            while (tokenizer.hasMoreTokens()) {
                array[index] = tokenizer.nextToken().trim();
                index++;
            }
        }
        return array;
    }
    public static List<String> toStringList(String separatedValues, String separator) {
        List<String> list = null;
        if (separatedValues != null) {
            StringTokenizer tokenizer = new StringTokenizer(separatedValues, separator);
            list = new ArrayList<String>(tokenizer.countTokens());
            while (tokenizer.hasMoreTokens()) {
                list.add(tokenizer.nextToken().trim());
            }
        }
        return list;
    }

    public static final int ALIGN_LEFT = -1;
    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_RIGHT = 1;

    public static String fillUpAligned(String value, String fill, int length, int alignment) {
        switch (alignment) {
            case ALIGN_RIGHT: {
                return fillUpRightAligned(value, fill, length);
            }
            case ALIGN_CENTER: {
                return fillUpCenterAligned(value, fill, length);
            }
            default: {
                return fillUpLeftAligned(value, fill, length);
            }
        }
    }

    public static String fillUpLeftAligned(String value, String fill, int length) {
        if (value.length() >= length) {
            return value;
        }
        while (value.length() < length) {
            value += fill;
        }
        return value;
    }

    public static String fillUpRightAligned(String value, String fill, int length) {
        if (value.length() >= length) {
            return value;
        }
        while (value.length() < length) {
            value = fill + value;
        }
        return value;
    }

    public static String fillUpCenterAligned(String value, String fill, int length) {
        if (value.length() >= length) {
            return value;
        }
        boolean left = true;
        while (value.length() < length) {
            if (left) {
                value = fillUpLeftAligned(value, fill, value.length() + 1);
            } else {
                value = fillUpRightAligned(value, fill, value.length() + 1);
            }
            left = !left;
        }
        return value;
    }

    public static String surroundValueWith(String value, String surrounding) {
        return surrounding + value + surrounding;
    }
}

