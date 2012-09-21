package org.dts.spell.dictionary.openoffice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Personal Dictionary Support Write the file on the filesystem at each change.
 * 
 * @author db
 */
public class PersonalDictionary {

  protected Set wordsList = new HashSet();
  protected String encoding;
  protected File personalFile;

  public PersonalDictionary(File personalFile, String encoding) {
    super();
    this.encoding = encoding;
    this.personalFile = personalFile;
    try {
      loadDictionary();
    } catch (IOException e) {
      System.err.println("PersonalDictionary not initialized " + personalFile.getAbsolutePath());
    }
  }

  protected void loadDictionary() throws IOException {

    BufferedReader rd = null;
    try {
      if (null != personalFile && personalFile.exists() && !personalFile.isDirectory()) {
        rd = new BufferedReader(new InputStreamReader(new FileInputStream(personalFile), encoding));
        String line = rd.readLine();
        while (line != null) {
          wordsList.add(line.trim());
          line = rd.readLine();
        }
      }
    } finally {
      if (rd != null) {
        rd.close();
      }
    }
  }

  public void delete() {
    wordsList.clear();
    personalFile.delete();
  }

  public void addWord(String word) {
    if (word != null && wordsList.add(word)) {
      // System.out.println("Add personal word:" + word);
      dictionaryChanged();
    }
  }

  public void addWords(List words) {
    if (words != null && words.addAll(words)) {
      dictionaryChanged();
    }
  }

  public void removeWord(String word) {
    if (wordsList.remove(word)) {
      dictionaryChanged();
    }
  }

  public void removeWords(List words) {
    if (words != null && wordsList.removeAll(words)) {
      dictionaryChanged();
    }
  }

  public List getWords() {
    Object[] arrayWords = wordsList.toArray();
    Arrays.sort(arrayWords);
    List theWords = new ArrayList();
    for (int i = 0; i < arrayWords.length; i++) {
      String value = (String) arrayWords[i];
      if (value != null && value.trim().length() > 0) {
        theWords.add(value.trim());
      }
    }
    return theWords;
  }

  protected void dictionaryChanged() {
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(personalFile), encoding));
      Object[] arrayWords = wordsList.toArray();
      Arrays.sort(arrayWords);
      for (int i = 0; i < arrayWords.length; i++) {
        pw.println(arrayWords[i]);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        Utils.close(pw);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
