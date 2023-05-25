package com.mucommander.text;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;

public class DurationFormatTest {
    @BeforeClass
    public static void setup() {
        // Import the Locale data used in test cases
        Locale locale = Activator.loadLocale();
        Translator.init(Activator.getDictionaryBundle(locale), Activator.getLanguageBundle(locale), Collections.emptyList());
    }

    @Test
    public void testFormat() {
        String result;

        // This test case goes through the infinite branch
        result = DurationFormat.format(9999999999999L);
        assert result.equals("∞");

        // The following test cases go through each instruction and each branch in the format method
        result = DurationFormat.format(0L);
        assert result.equals("<1s");
        result = DurationFormat.format(1000L);
        assert result.equals("1s");
        result = DurationFormat.format(60 * 1000L);
        assert result.equals("1m");
        result = DurationFormat.format(3600 * 1000L);
        assert result.equals("1h");
        result = DurationFormat.format((1 + 3600) * 1000L);
        assert result.equals("1h 1s");
        result = DurationFormat.format((60 + 3600 + 86400) * 1000L);
        assert result.equals("1d 1h 1m");
        result = DurationFormat.format((60 + 3600 + 86400 + 31104000) * 1000L);
        assert result.equals("1y 1d 1h 1m");
    }
}
