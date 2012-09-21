package org.spell6r;

import java.util.List;

public class SpellingError {
  String source;
  String word;
  private List suggestions;

  public SpellingError(String source, String word, List suggestions) {
    super();
    this.source = source;
    this.word = word;
    this.suggestions = suggestions;
  }

  public String getSource() {
    return source;
  }

  public String getWord() {
    return word;
  }

  public List getSuggestions() {
    return suggestions;
  }

}
