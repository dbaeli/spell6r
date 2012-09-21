/*
 * Copyright (C) by Prima Solutions, All Rights Reserved.
 * 
 * THIS MATERIAL IS CONSIDERED A TRADE SECRET BY PRIMA SOLUTIONS. UNAUTHORIZED ACCESS, USE, MODIFICATION,
 * REPRODUCTION OR DISTRIBUTION IS PROHIBITED.
 */
package org.spell6r;

public class SpellCheckLanguage {
    private String languageKey;
    private String languageLabel;

    public static String LABEL_EN = "English";
    public static String LABEL_FR = "French";

    public static final SpellCheckLanguage ENGLISH = new SpellCheckLanguage("En", LABEL_EN);
    public static final SpellCheckLanguage FRENCH = new SpellCheckLanguage("Fr", LABEL_FR);

    private SpellCheckLanguage(String lang, String label) {
        this.languageKey = lang;
        this.languageLabel = label;
    }

    public String getKey() {
        return this.languageKey;
    }

    public String getLabel() {
        return this.languageLabel;
    }

    public static SpellCheckLanguage getRelatedLanguage(String langString) {
        if (FRENCH.getKey().equalsIgnoreCase(langString.trim()))
            return FRENCH;

        return ENGLISH;
    }
}
