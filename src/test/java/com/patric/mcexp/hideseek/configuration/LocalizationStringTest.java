package com.patric.mcexp.hideseek.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalizationStringTest {

    @Test
    void replacesPlayerAndAmountPlaceholders() {
        LocalizationString localizationString =
                new LocalizationString("Hello {PLAYER}, you have {AMOUNT} coins");

        String result = localizationString
                .addPlayer("Alex")
                .addAmount(5)
                .toString();

        assertEquals("Hello Alex, you have 5 coins", result);
    }

    @Test
    void replacesFirstOccurrenceOnlyEachCall() {
        LocalizationString localizationString =
                new LocalizationString("{PLAYER} vs {PLAYER} - {AMOUNT}:{AMOUNT}");

        String result = localizationString
                .addPlayer("Alice")
                .addPlayer("Bob")
                .addAmount("1")
                .addAmount("0")
                .toString();

        assertEquals("Alice vs Bob - 1:0", result);
    }
}
