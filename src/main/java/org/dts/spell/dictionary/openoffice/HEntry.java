/*
 * Created on 27/12/2004
 */
package org.dts.spell.dictionary.openoffice;

/**
 * @author DreamTangerine
 */
public class HEntry {
  public HEntry(String word, String astr) {
    this.word = word;
    this.astr = astr;
  }

  public String word;
  public String astr;
  public boolean custom;
}
