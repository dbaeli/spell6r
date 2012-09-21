/*
 * Copyright (C) by Prima Solutions, All Rights Reserved.
 * 
 * THIS MATERIAL IS CONSIDERED A TRADE SECRET BY PRIMA SOLUTIONS. UNAUTHORIZED ACCESS, USE, MODIFICATION, REPRODUCTION
 * OR DISTRIBUTION IS PROHIBITED.
 */
package org.spell6r;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;

import com.swabunga.spell.event.CapitalizedWordFinder;
import com.swabunga.spell.event.DefaultWordFinder;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class SpellCheckerHelper {

  // public static String DEFAULT_DICTIONARIES_ROOT_DIR = "dict/";
  public static int COUNT_DICT_FOUND = 0;

  public static final Map<SpellCheckLanguage, String[]> DICTIONARIES = new HashMap<SpellCheckLanguage, String[]>();

  static {
    // TODO Extension mecanism for Dictionaries (Property file in dict directory)
    DICTIONARIES.put(SpellCheckLanguage.ENGLISH, new String[] {"en_US.zip", "IBCS_en_US.zip"});
    DICTIONARIES.put(SpellCheckLanguage.FRENCH, new String[] {"fr_FR.zip", "IBCS_fr_FR.zip"});
  }

  private static Map<SpellCheckLanguage, SpellChecker> spellCheckersLanguageMap = new HashMap<SpellCheckLanguage, SpellChecker>();

  public static void initDictionaries(String rootDir) {
    File folderDictionaries = new File(rootDir);

    for (SpellCheckLanguage lang : DICTIONARIES.keySet()) {
      try {
        SpellChecker checker = new SpellChecker();
        String[] dictionaries = DICTIONARIES.get(lang);

        // Load the dictionary
        for (int j = 0; j < dictionaries.length; j++) {
          String dictionary = dictionaries[j];
          if (new File(rootDir + "/" + dictionary).exists()) {
            ZipFile zipFile = new ZipFile(rootDir + "/" + dictionary);
            OpenOfficeSpellDictionary openOfficeSpellDictionary = new OpenOfficeSpellDictionary(zipFile, folderDictionaries);
            checker.loadDictionary(openOfficeSpellDictionary);
            COUNT_DICT_FOUND++;
          }
        }
        if (COUNT_DICT_FOUND > 0) {
          spellCheckersLanguageMap.put(lang, checker);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

    }
  }

  public static SpellResult checkSentenceEnglish(String sentence, String sourceQualifiedName, Map<SpellCheckLanguage, Set<SpellingErrorWithSource>> spellingErrorsMap) {
    return checkSentenceLocalized(SpellCheckLanguage.ENGLISH, sentence, sourceQualifiedName, spellingErrorsMap);
  }

  public static SpellResult checkSentenceLocalized(SpellCheckLanguage language, String sentence, String sourceQualifiedName, Map<SpellCheckLanguage, Set<SpellingErrorWithSource>> spellingErrorsMap) {
    String result = checkSpellingForLanguage(language, sentence, sourceQualifiedName, spellingErrorsMap);
    return new SpellResult(sentence, result);
  }

  /**
   * 
   * @param language
   * @param sentence
   * @param source
   * @param spellingErrorsMap
   * @return string containing all words that have issues.
   */
  private static String checkSpellingForLanguage(SpellCheckLanguage language, String sentence, String source, Map<SpellCheckLanguage, Set<SpellingErrorWithSource>> spellingErrorsMap) {
    Set<String> missSpeltWords = new HashSet<String>();

    SpellChecker checker = spellCheckersLanguageMap.get(language);
    if (checker == null) {
      // No spell checker available for this language.
      return "";
    }

    Set<SpellingErrorWithSource> spellingErrorsSet = spellingErrorsMap.get(language);
    if (spellingErrorsSet == null) {
      spellingErrorsSet = new HashSet<SpellingErrorWithSource>();
      spellingErrorsMap.put(language, spellingErrorsSet);
    }
    DefaultWordFinder textTok = new CapitalizedWordFinder(sentence);
    StringWordTokenizer swt = new StringWordTokenizer(textTok);
    int result = checker.checkSpelling(swt, missSpeltWords, false);
    if (result > 0) {
      StringBuffer spellingErrors = new StringBuffer();
      // spellingErrors.append("misspelt (" + LANGUAGES[languageIndex] + ")=");
      // Aggregate errors
      for (String spellError : missSpeltWords) {
        spellingErrorsSet.add(new SpellingErrorWithSource(spellError, source, sentence));
      }

      boolean first = true;
      for (String word : missSpeltWords) {
        if (!first)
          spellingErrors.append(", ");
        else
          first = false;
        spellingErrors.append(word);
      }
      return spellingErrors.toString();
    }
    return "";
  }

  public static class SpellResult {
    private String source;
    private List<String> errorMessages = new ArrayList<String>();

    public SpellResult(String source) {
      super();
      this.source = source;
    }

    public SpellResult(String source, String errorMessage) {
      super();
      this.source = source;
      if (errorMessage != null && errorMessage.length() > 0)
        addErrorMessage(errorMessage);
    }

    public String getErrorMessage() {
      if (errorMessages.size() == 0) {
        return "";
      }
      StringBuffer errorMessage = new StringBuffer();
      for (Iterator<String> iter = errorMessages.iterator(); iter.hasNext();) {
        errorMessage.append(iter.next());
        if (iter.hasNext()) {
          errorMessage.append(" / ");
        }
      }
      return errorMessage.toString();
    }

    public void addErrorMessage(String errorMessage) {
      if (errorMessage != null && errorMessage.length() > 0) {
        this.errorMessages.add(errorMessage);
      }
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public boolean isErrored() {
      return errorMessages.size() > 0;
    }

  }

  /**
   * Spelling with source, only spelling is used for comparison
   */
  public static class SpellingErrorWithSource implements Comparable<SpellingErrorWithSource> {
    String error;
    String source;
    String content;

    public SpellingErrorWithSource(String error, String source, String content) {
      super();
      this.error = error;
      this.source = source;
      this.content = content;
    }

    public String getError() {
      return error;
    }

    public String getSource() {
      return source;
    }

    public String getContent() {
      return content;
    }

    @Override
    public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((error == null) ? 0 : error.hashCode());
      result = PRIME * result + ((source == null) ? 0 : source.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final SpellingErrorWithSource other = (SpellingErrorWithSource) obj;
      if (error == null) {
        if (other.error != null)
          return false;
      } else if (!error.equals(other.error))
        return false;
      if (source == null) {
        if (other.source != null)
          return false;
      } else if (!source.equals(other.source))
        return false;
      return true;
    }

    public int compareTo(SpellingErrorWithSource object) {
      SpellingErrorWithSource w2 = object;
      int compare = error.compareToIgnoreCase(w2.getError());
      if (compare == 0) {
        compare = source.compareToIgnoreCase(w2.getSource());
      }
      return compare;
    }

  }
}
