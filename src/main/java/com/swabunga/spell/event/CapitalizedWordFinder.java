package com.swabunga.spell.event;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Extension of the basic word finder, which searches text for sequences of letters. Split words on upper case
 * char : BusinessKeyword, PartnerAgreement are both considered as 2 words.
 */
public class CapitalizedWordFinder extends DefaultWordFinder {

    /**
     * Creates a new DefaultWordFinder object.
     * 
     * @param inText the String to search
     */
    public CapitalizedWordFinder(String inText) {
        super(inText);
    }

    /**
     * Creates a new DefaultWordFinder object.
     */
    public CapitalizedWordFinder() {
        super();
    }

    private static Pattern PATTERN_SPLIT_SPACES = Pattern.compile("\\s");

    /**
     * Returns the position in the string <em>after</em> the end of the next word. Note that this return
     * value should not be used as an index into the string without checking first that it is in range, since
     * it is possible for the value <code>text.length()</code> to be returned by this method.
     */
    protected int getNextWordEnd(String text, int startPos) {
        // Override super.getNextWordEnd(text, startPos);

        // If we're dealing with a possible 'internet word' we need to provide
        // some special handling
        if (SpellChecker.isINETWord(text.substring(startPos))) {
            for (int i = startPos; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (Character.isLetterOrDigit(ch))
                    continue;

                if (ch == '\r' || ch == '\n')
                    return i;
                // Chop off any characters that might be enclosing the 'internet word'. eg ',",),]
                if (Character.isSpaceChar(ch))
                    if (i > 0 && Character.isLetterOrDigit(text.charAt(i - 1)))
                        return i;
                    else
                        return i - 1;
            }
            return text.length();
        } else {
            String firstToken = PATTERN_SPLIT_SPACES.split(text.substring(startPos))[0];
            boolean[] isUpperList = new boolean[firstToken.length()];
            boolean[] isDigitList = new boolean[firstToken.length()];
            for (int i = 0; i < firstToken.length(); i++) {
                // char ch = text.charAt(i);
                if (!isWordChar(i + startPos)) {
                    return i + startPos;
                } else if (i >= 0 && i + 2 < firstToken.length()) {
                    // Split words on upper case char : BusinessKeyword, PartnerAgreement are considered as 2
                    // words.
                    char next = firstToken.charAt(i + 2);
                    char curr = firstToken.charAt(i + 1);
                    char prev = firstToken.charAt(i);

                    isDigitList[i] = Character.isDigit(prev);
                    isDigitList[i + 1] = Character.isDigit(curr);
                    if (isDigitList[i] != isDigitList[i + 1])
                        return i + 1 + startPos;

                    if (isWordChar(i + startPos) && isWordChar(i + 1 + startPos) && isWordChar(i + 2 + startPos)) {
                        // Pattern xBa => x/Ba
                        isUpperList[i] = isCharUpperCase(prev, i - 1 < 0 ? false : isUpperList[i - 1]);
                        isUpperList[i + 1] = isCharUpperCase(curr, isUpperList[i]);
                        isUpperList[i + 2] = isCharUpperCase(next, isUpperList[i + 1]);
                        if (!isUpperList[i] && isUpperList[i + 1]) {
                            return i + 1 + startPos;
                        }
                        // Pattern NRa => N/Ra
                        if (isUpperList[i] && isUpperList[i + 1] && !isUpperList[i + 2]) {
                            return i + 1 + startPos;
                        }
                        // Pattern aR$ => a/R
                    }
                } else if (i >= 0 && i + 1 < firstToken.length()) {
                    // Pattern aR$ => a/R, and a1$ =>a/1
                    char prev = firstToken.charAt(i);
                    char curr = firstToken.charAt(i + 1);
                    
                    isDigitList[i] = Character.isDigit(prev);
                    isDigitList[i + 1] = Character.isDigit(curr);
                    if (isDigitList[i] != isDigitList[i + 1])
                        return i + 1 + startPos;

                    if (isWordChar(i + startPos) && isWordChar(i + 1 + startPos)) {
                        isUpperList[i] = isCharUpperCase(prev, i - 1 < 0 ? false : isUpperList[i - 1]);
                        isUpperList[i + 1] = isCharUpperCase(curr, isUpperList[i]);

                        if (!isUpperList[i] && isUpperList[i + 1]) {
                            return i + 1 + startPos;
                        }
                    }
                }
            }
            return firstToken.length() + startPos;
        }
    }

    private boolean isUpper(List list, int index) {
        if (index < 0)
            return false;
        return ((Boolean) list.get(index)).booleanValue();
    }

    /**
     * do _not_ separate digits and letters.
     */
    protected boolean isWordChar(int posn) {
        char curr = text.charAt(posn);
        return Character.isLetterOrDigit(curr);
    }

    protected boolean isCharUpperCase(char curr, boolean isPreviousCharUpperCase) {
        if (Character.isLetter(curr))
            return Character.isUpperCase(curr);
        else
            // return true;
            // digit case (considered as same as previous char)
            return isPreviousCharUpperCase;
    }

    protected boolean isCharLowerCase(char curr, boolean isPreviousCharUpperCase) {
        if (Character.isLetter(curr))
            return Character.isLowerCase(curr);
        else
            // return false;
            // digit case (considered as same as previous char)
            return !isPreviousCharUpperCase;
    }

}
