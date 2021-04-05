package com.marcwoodyard.RaspberryPiThermostat.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class PasswordTools {

    // Strong password >= 7
    public static int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() < 8)
            return 0;
        else if (password.length() >= 10)
            strength += 2;
        else
            strength += 1;

        //Contains one special character
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
            strength += 2;

        //Contains one digit
        if (password.matches("(?=.*[0-9]).*"))
            strength += 2;

        //Contains one lower case letter
        if (password.matches("(?=.*[a-z]).*"))
            strength += 2;

        //Contains one upper case letter
        if (password.matches("(?=.*[A-Z]).*"))
            strength += 2;

        return strength;
    }

    public static String generateSecureString(int length, boolean includeSymbols) {
        char[] possibleCharacters;

        if (includeSymbols)
            possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?").toCharArray();
        else
            possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789").toCharArray();

        return RandomStringUtils.random(length, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
    }

}