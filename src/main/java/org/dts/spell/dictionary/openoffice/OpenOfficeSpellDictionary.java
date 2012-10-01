package org.dts.spell.dictionary.openoffice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import com.swabunga.spell.engine.SpellDictionary;

/**
 * @author DreamTangerine
 */
public class OpenOfficeSpellDictionary implements SpellDictionary {

  private OpenOfficeDictionarySupport dictionaryEngine;
  private PersonalDictionary personalDictionary;
  private List listeners = new ArrayList();

  public OpenOfficeSpellDictionary(String zipFileName) throws IOException {
    initFromZipFile(new ZipFile(zipFileName), null);
  }

  public OpenOfficeSpellDictionary(ZipFile zipFile) throws IOException {
    initFromZipFile(zipFile, null);
  }

  public OpenOfficeSpellDictionary(InputStream inputStream) throws IOException {
    initFromStream(inputStream);
  }

  public OpenOfficeSpellDictionary(ZipFile zipFile, File personalFileRootDir) throws IOException {
    initFromZipFile(zipFile, personalFileRootDir);
  }

  public OpenOfficeSpellDictionary(File dictFile, File affFile) throws IOException {
    initFromFiles(dictFile, affFile, null);
  }

  /**
   * @param stream Stream on the zip file
   * @throws IOException
   */
  private void initFromStream(InputStream stream) throws IOException {
    dictionaryEngine = new OpenOfficeDictionarySupport(stream);
  }

  private void initFromZipFile(ZipFile zipFile, File personalFileRootDir) throws IOException {
    long t = System.currentTimeMillis();
    dictionaryEngine = new OpenOfficeDictionarySupport(zipFile);
    File personalDictionaryFile = computePersonalWordFile(zipFile.getName(), personalFileRootDir);
    initPersonalWordsSupport(personalDictionaryFile);
  }

  private void initFromFiles(File dictFile, File affFile, File personalFileRootDir) throws IOException {
    dictionaryEngine = new OpenOfficeDictionarySupport(affFile.getPath(), dictFile.getPath());
    File personalDictionaryFile = computePersonalWordFile(dictFile.getPath(), personalFileRootDir);
    initPersonalWordsSupport(personalDictionaryFile);
  }

  /**
   * Accessor to manage PersonalDictionary
   */
  public PersonalDictionary getPersonalDictionary() {
    return personalDictionary;
  }

  /**
   * Accessor to manage PersonalDictionary
   */
  public void deletePersonalDictionary() {
    personalDictionary.delete();
    dictionaryEngine.clearCustomWords();
    fireDictionaryConfigurationChanged();
  }

  public void addDictionaryListener(IDictionaryListener listener) {
    listeners.add(listener);
  }

  public void removeDictionaryListener(IDictionaryListener listener) {
    listeners.remove(listener);
  }

  /**
   * Accessor to manage PersonalDictionary
   */
  public void fireDictionaryConfigurationChanged() {
    // Sync CustomWords with the Engine
    dictionaryEngine.syncCustomWords(personalDictionary.getWords());
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      IDictionaryListener listener = (IDictionaryListener) iter.next();
      if (listener != null) {
        listener.dictionaryConfigurationChanged();
      }
    }
  }

  // Should notify that spelling configuration changed
  public void addWord(String word) {
    if (word != null && word.length() > 0) {
      dictionaryEngine.addCustomWord(word);
      // Will call fireDictionaryChanged by side-effect
      personalDictionary.addWord(word);
    }
  }

  public boolean isCorrect(String word) {
    return dictionaryEngine.spell(word);
  }

  public List getSuggestions(String sourceWord, int scoreThreshold, int[][] matrix) {
    return getSuggestions(sourceWord);
  }

  public List getSuggestions(String sourceWord, int scoreThreshold) {
    return getSuggestions(sourceWord);
  }

  public List getSuggestions(String word) {
    return dictionaryEngine.suggest(word);
  }

  /**
   * @param dictionaryPath
   * @param personalFileRootDir
   */
  private File computePersonalWordFile(String dictionaryPath, File personalFileRootDir) {
    int indexDot = dictionaryPath.lastIndexOf('.');
    int indexDirSep = dictionaryPath.lastIndexOf(File.separatorChar);
    if (personalFileRootDir != null) {
      personalFileRootDir.mkdirs();
      if (indexDot > -1) {
        String baseName = dictionaryPath.substring(indexDirSep > -1 ? indexDirSep : 0, indexDot);
        return new File(personalFileRootDir, baseName + ".per");
      } else {
        String baseName = dictionaryPath.substring(indexDirSep > -1 ? indexDirSep : 0);
        return new File(personalFileRootDir, baseName + ".per");
      }
    } else {
      String baseName = (indexDot > -1) ? dictionaryPath.substring(0, indexDot) : dictionaryPath;
      return new File(baseName + ".per");
    }
  }

  public void initPersonalWordsSupport(File personalFile) throws IOException {

    personalDictionary = new PersonalDictionary(personalFile, dictionaryEngine.get_dic_encoding()) {
      /**
       * ensure that any direct change to the PersonalDictionary fires an event to the main dictionary
       */
      protected void dictionaryChanged() {
        super.dictionaryChanged();
        fireDictionaryConfigurationChanged();
      }
    };

    // Fill-in the dictionary engine
    List words = personalDictionary.getWords();
    for (Iterator iter = words.iterator(); iter.hasNext();) {
      String customWord = (String) iter.next();
      dictionaryEngine.addCustomWord(customWord);
    }
  }
}
