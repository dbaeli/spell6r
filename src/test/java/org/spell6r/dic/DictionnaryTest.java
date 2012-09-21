package org.spell6r.dic;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.dts.spell.dictionary.openoffice.OpenOfficeSpellDictionary;
import org.junit.Test;
import org.spell6r.DictionaryUtils;
import org.spell6r.Spell6rChecker;

public class DictionnaryTest {

	@Test
	public void testFR() {
		OpenOfficeSpellDictionary loadDictionary = DictionaryUtils.fetchDictionary("fr_FR");
		assertNotNull("Dictionnary could not be load", loadDictionary);
		//Dictionnary
		assertTrue(loadDictionary.isCorrect("Bonjour"));
		//Reforme 1990
		assertTrue(loadDictionary.isCorrect("Plateforme"));
	}
	
	@Test
	public void testEN() {
		OpenOfficeSpellDictionary loadDictionary = DictionaryUtils.fetchDictionary("en_US");
		assertNotNull("Dictionnary could not be load", loadDictionary);
		//Dictionnary
		assertTrue(loadDictionary.isCorrect("Hello"));
		assertTrue(loadDictionary.isCorrect("Platform"));
	}
	
	public void testENFR() {
		Spell6rChecker spell6r = new Spell6rChecker("en_US", "fr_FR");
		//Dictionnary
		assertTrue(spell6r.isCorrect("Hello"));
		assertTrue(spell6r.isCorrect("Bonjour"));
	}
	
	@Test
	public void testDiscover() {
		Map<String, OpenOfficeSpellDictionary> fetchDictionaries = DictionaryUtils.fetchDictionaries();
		assertTrue(fetchDictionaries.size()>0);
	}

}
