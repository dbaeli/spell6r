/*
 * Copyright (C) by Prima Solutions, All Rights Reserved.
 * 
 * THIS MATERIAL IS CONSIDERED A TRADE SECRET BY PRIMA SOLUTIONS. UNAUTHORIZED ACCESS, USE, MODIFICATION, REPRODUCTION
 * OR DISTRIBUTION IS PROHIBITED.
 */
package org.spell6r;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.*;
import org.apache.commons.io.IOUtils;
import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OpenOffice dictionnary based spellchecker
 *
 * @author dba
 */
public class Spell6rChecker extends SpellChecker {

  private String currentSource;
  private List<SpellingError> spellingErrors = new ArrayList<SpellingError>();

  public Spell6rChecker(String... languages) {

    // Preloads the language
    for (String language : languages) {
      OpenOfficeSpellDictionary loadedDictionary = DictionaryUtils.fetchDictionary(language);
      if (loadedDictionary != null) {
        this.loadDictionary(loadedDictionary);
      }
    }

    addSpellCheckListener(new SpellCheckListener() {
      public void spellingError(SpellCheckEvent event) {
        String invalidWord = event.getInvalidWord();
        spellingErrors.add(new SpellingError(currentSource, invalidWord, event.getSuggestions()));
      }
    });

    //Setup extended dictionary
    final SpellDictionaryHashMap personalDictionary;
    try {
      personalDictionary = new SpellDictionaryHashMap();
      setUserDictionary(personalDictionary);
    } catch (IOException e) {
      //No file, no error.
    }
  }

  public void reset() {
    spellingErrors.clear();
    currentSource = null;
    super.reset();
  }

  public String getCurrentSource() {
    return currentSource;
  }

  public void setCurrentSource(String currentSource) {
    this.currentSource = currentSource;
  }

  public int getSpellingIssuesCount() {
    return spellingErrors.size();
  }

  public String dumpSpellReport() {
    StringBuffer sb = new StringBuffer();
    dumpSpellReport(sb);
    return sb.toString();
  }

  public void dumpSpellReport(StringBuffer buffer) {
    for (SpellingError spellingError : spellingErrors) {
      buffer.append("Spell6r ERROR in ");
      buffer.append(spellingError.source);
      buffer.append(" MISSSPELLED ");
      buffer.append(spellingError.word);

      List suggestions = spellingError.getSuggestions();
      if (suggestions != null && !suggestions.isEmpty()) {
        buffer.append(" SUGGESTIONS ");
        buffer.append(suggestions);
      }
      buffer.append("\n");
    }
    buffer.append("Spell6r ERRORS COUNT : ");
    buffer.append(spellingErrors.size());
    buffer.append("\n");
  }

  /**
   * Scans the classpath for dictionaries
   */
  public void discoverDictionaries() {
    Map<String, OpenOfficeSpellDictionary> fetchDictionaries = DictionaryUtils.fetchDictionaries();
    for (OpenOfficeSpellDictionary dictionary : fetchDictionaries.values()) {
      loadDictionary(dictionary);
    }
  }

  public void discoverExtendedDic() {
    final InputStream resourceAsStream = Spell6rChecker.class.getResourceAsStream("/extended.dic");
    if (resourceAsStream != null) {
      try {
        final List<String> words = IOUtils.readLines(resourceAsStream);
        int wordCount = 0;
        for (String word : words) {
          if (word.trim().length() > 0) {
            addToDictionary(word);
            wordCount++;
          }
        }
        if (wordCount > 0) {
          System.out.println("SPELLCHECK : Found an extended dic with " + words.size() + " word(s)");
        }
      } catch (IOException e) {
        System.err.println("SPELLCHECK ERROR reading the personal dictionary");
      }
    } else {
      System.out.println("SPELLCHECK : No extended dictionary found for spellchecking ( /extended.dic in the classpath).");
    }
  }

  //---------------------------------------

  public static void main(String[] args) {

    if (args.length < 1) {
      System.out.println("Usage: PrimaSpellChecker <file>");
      System.exit(1);
    }

    final Spell6rChecker checker = new Spell6rChecker();
    checker.discoverDictionaries();

    for (String fileToParse : args) {
      System.out.println("Spell6r: Parsing " + fileToParse);
      checker.setCurrentSource(fileToParse);
      FileWordTokenizer fwt = new FileWordTokenizer(new File(fileToParse), new XMLWordFinder());
      checker.checkSpelling(fwt, false);
    }
    System.out.println(checker.dumpSpellReport());

  }


}
