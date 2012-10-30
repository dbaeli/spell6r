package org.spell6r;

import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipFile;

public class DictionaryUtils {

  /**
   * @param language
   * @param stream   stream on the zip file
   * @return
   */
  public static OpenOfficeSpellDictionary readDictionnary(String language, InputStream stream) {
    try {
      return new OpenOfficeSpellDictionary(stream);
    } catch (Exception e) {
      java.util.logging.Logger.getAnonymousLogger().severe("Dictionnary : " + language + " error during loading : " + e.getMessage());
    }
    return null;
  }

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
    URL resource = DictionaryUtils.class.getResource(filename);
    if (resource == null) {
      resource = DictionaryUtils.class.getClassLoader().getResource(filename);
    }
    java.io.InputStream stream = null;
    try {
      if (resource != null) {
        stream = resource.openStream();
      } else {
        File localFile = new File(filename);
        if (localFile.exists()) {
          try {
            stream = new FileInputStream(localFile.getAbsolutePath());
          } catch (FileNotFoundException e) {
          }
        } else {
          // No file found
        }
      }
      //Read the dictionnary from the zip file stream (will be managed as ZipInputStream internally)
      if (stream != null) {
        return readDictionnary(language, stream);
      }
    } catch (IOException e) {
      System.err.print("Failed to load dictionnary : " + e.getMessage());
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
