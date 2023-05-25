package com.mucommander.ui.chooser;

import com.mucommander.text.Activator;
import com.mucommander.text.Translator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;

public class PreviewLabelTest {
    static PreviewLabel previewLabel;

    @BeforeClass
    public static void setup() {
        // Initialise the object being tested
        previewLabel = new PreviewLabel();
        // Import the Locale data used in test cases
        Locale locale = Activator.loadLocale();
        Translator.init(Activator.getDictionaryBundle(locale), Activator.getLanguageBundle(locale), Collections.emptyList());
    }

    /**
     * Test the setTextPainted() method with full coverage.
     * This test is an integration test and the real methods used by the setTextPainted() method will also be called.
     */
    @Test
    public void testSetTextPainted() {
        // The text should be set to the corresponding language if the input parameter is true
        // My system language is English so the output should be the English text
        previewLabel.setTextPainted(true);
        assert previewLabel.getText().equals("Sample text");

        // The text should be set to a space if the input parameter is false
        previewLabel.setTextPainted(false);
        assert previewLabel.getText().equals(" ");
    }
}
