/*
 * This file is part of muCommander, http://www.mucommander.com
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mucommander.text;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.mock;

/**
 * A test case for {@link Translator}
 *
 * @author Nicolas Filotto (nicolas.filotto@gmail.com)
 */
public class TranslatorTest {

    /**
     * Mock non-null parameters for testing purpose
     */
    ResourceBundle dictionaryBundle = mock(ResourceBundle.class);
    ResourceBundle languagesBundle = mock(ResourceBundle.class);
    List<Locale> availableLanguages = mock(List.class);

    /**
     * Test the init() method with full coverage
     */
    @Test
    public void testInit() {
        // The global variables should be set correctly, no matter what the input is
        Translator.init(dictionaryBundle, languagesBundle, availableLanguages);
        assert Translator.dictionaryBundle.equals(dictionaryBundle);
        assert Translator.languagesBundle.equals(languagesBundle);
        assert Translator.availableLanguages.equals(availableLanguages);
    }

    /**
     * Test the test() method with full coverage
     */
    @Test
    public void testValidation() {
        // An illegal state exception should be thrown if one of the global variables are null
        Translator.init(null, languagesBundle, availableLanguages);
        Assert.assertThrows(IllegalStateException.class, Translator::test);
        Translator.init(dictionaryBundle, null, availableLanguages);
        Assert.assertThrows(IllegalStateException.class, Translator::test);
        Translator.init(dictionaryBundle, languagesBundle, null);
        Assert.assertThrows(IllegalStateException.class, Translator::test);

        // The validation method should be passed if nothing is null
        Translator.init(dictionaryBundle, languagesBundle, availableLanguages);
        Translator.test();
    }

    /**
     * Test thr getAvailableLanguages() method with full coverage
     */
    @Test
    public void testGetAvailableLanguages() {
        // The returned value of the available languages should be the same as the one we set
        Translator.init(dictionaryBundle, languagesBundle, availableLanguages);
        List<Locale> result = Translator.getAvailableLanguages();
        assert result.equals(availableLanguages);
    }

    /**
     * Test the hasValue() method with full coverage
     */
    @Test
    public void testHasValue() {
        // Loads the dictionary properties and languages list in resources
        // The dictionary bundle loaded includes 3 keys: key1, key2, and key3
        // The languages list loaded includes 1 key: en
        Locale locale = Activator.loadLocale();
        Translator.init(Activator.getDictionaryBundle(locale), Activator.getLanguageBundle(locale), Collections.emptyList());

        // The method should return true if the key is included in the dictionary loaded
        // No matter whether the default language is used
        assert Translator.hasValue("key1", true);
        assert Translator.hasValue("key1", false);

        // The method should return false if the key is not included in the dictionary loaded
        // No matter whether the default language is used
        assert !Translator.hasValue("Test", true);
        assert !Translator.hasValue("Test", false);
    }

    /**
     * Test the get() method with full coverage
     */
    @Test
    public void testGet() {
        // Loads the dictionary properties and languages list in resources
        // The dictionary bundle loaded includes 3 keys: key1, key2, and key3
        // The languages list loaded includes 1 key: en
        Locale locale = Activator.loadLocale();
        Translator.init(Activator.getDictionaryBundle(locale), Activator.getLanguageBundle(locale), Collections.emptyList());

        // The key is in the dictionary and format parameters are empty
        assert Translator.get("key1").equals("ELEC5618");
        // The key is in the dictionary and format parameters are given
        assert Translator.get("key1", "1").equals("ELEC5618");
        // The key is in the languages list and format parameters are empty
        assert Translator.get("en").equals("English");
        // The key is in the languages list and format parameters are given
        assert Translator.get("en", "1").equals("English");
        // The key is not in the dictionary, not in the languages list, and format parameters are empty
        assert Translator.get("key4").equals("key4");
        // The key is not in the dictionary, not in the languages list, and format parameters are given
        assert Translator.get("key4", "1").equals("key4");
    }
}
