package org.spell6r;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;

public class DictionaryUtils {

  public static OpenOfficeSpellDictionary readDictionnary(String language, String path) {
    try {
      ZipFile dict = new ZipFile(path);
      return new OpenOfficeSpellDictionary(dict, new File("dict"));
    } catch (Exception e) {
      java.util.logging.Logger.getAnonymousLogger().severe("Dictionnary : " + language + " error during loading : " + e.getMessage());
    }
    return null;
  }

  public static OpenOfficeSpellDictionary fetchDictionary(String language) {
    String filename = "dictionaries/" + language + ".zip";
    URL resource = Spell6rChecker.class.getResource(filename);
    if (resource == null) {
      resource = Spell6rChecker.class.getClassLoader().getResource(filename);
    }
    String path = null;
    if (resource != null) {
      path = resource.getPath();
    } else {
      File localFile = new File(filename);
      if (localFile.exists()) {
        path = localFile.getAbsolutePath();
      } else {
        // No file found
      }
    }
    if (path != null) {
      return readDictionnary(language, path);
    }
    return null;
  }

  static void scanResourceForDictionnary(Map<String, OpenOfficeSpellDictionary> dicts, String path) {
    File dictDir = new File(path);
    if (dictDir.exists() && dictDir.isDirectory()) {
      File[] listFiles = dictDir.listFiles();
      for (File file : listFiles) {
        String name = file.getName();
        if (file.isFile() && name.endsWith(".zip")) {
          name = name.replaceAll("\\.zip", "");
          Locale[] availableLocales = Locale.getAvailableLocales();
          for (Locale locale : availableLocales) {
            String language = locale.toString();
            if (language.equals(name)) {
              OpenOfficeSpellDictionary dic = readDictionnary(language, file.getAbsolutePath());
              System.out.println("Found spell dictionnary for : " + language);
              if (dic != null) {
                dicts.put(language, dic);
              }
            }
          }
        }
      }
    }
  }

  public static Map<String, OpenOfficeSpellDictionary> fetchDictionaries() {
    Map<String, OpenOfficeSpellDictionary> dicts = new HashMap<String, OpenOfficeSpellDictionary>();
    URL resource = Spell6rChecker.class.getResource("dictionaries/");
    if (resource != null) {
      scanResourceForDictionnary(dicts, resource.getPath());
    }
    resource = Spell6rChecker.class.getClassLoader().getResource("dictionaries/");
    if (resource != null) {
      scanResourceForDictionnary(dicts, resource.getPath());
    }
    scanResourceForDictionnary(dicts, "dictionaries/");
    return dicts;
  }

}
