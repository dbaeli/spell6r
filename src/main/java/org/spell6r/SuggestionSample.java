/*
 * Copyright (C) by Prima Solutions, All Rights Reserved.
 * 
 * THIS MATERIAL IS CONSIDERED A TRADE SECRET BY PRIMA SOLUTIONS. UNAUTHORIZED ACCESS, USE, MODIFICATION, REPRODUCTION
 * OR DISTRIBUTION IS PROHIBITED.
 */
package org.spell6r;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;

import com.swabunga.spell.engine.Configuration;
import com.swabunga.spell.event.SpellChecker;

public class SuggestionSample {
  public static void main(String[] args) throws IOException {

    OpenOfficeSpellDictionary dict = new OpenOfficeSpellDictionary("dictionaries/en_US.zip");
    SpellChecker spell = new SpellChecker(dict, 200);
    spell.getConfiguration().setInteger(Configuration.COST_INSERT_CHAR, 10);
    spell.getConfiguration().setInteger(Configuration.COST_REMOVE_CHAR, 10);
    final String word = "doag";
    final List<String> suggestions = dict.getSuggestions(word);
    for (Iterator<String> iterator = suggestions.iterator(); iterator.hasNext();) {
      System.out.println("Dict:" + iterator.next());
    }
    final List<String> suggestionsSpell = spell.getSuggestions(word, 200);
    for (Iterator<String> iterator = suggestionsSpell.iterator(); iterator.hasNext();) {
      System.out.println("Spell:" + iterator.next());
    }
  }
}
