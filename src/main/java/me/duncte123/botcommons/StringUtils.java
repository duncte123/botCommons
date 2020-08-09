/*
 *    Copyright 2018 Duncan "duncte123" Sterken
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.duncte123.botcommons;

import net.dv8tion.jda.internal.utils.Checks;

public class StringUtils {

    /**
     * Replaces the last thing in a string
     *
     * @param text
     *     the text to replace
     * @param search
     *     The string to search for
     * @param replacement
     *     what to replace it with
     *
     * @return the replaced string
     *
     * @throws IllegalArgumentException
     *     when text or search are blank or when any of the arguments are null
     */
    public static String replaceLast(String text, String search, String replacement) {
        Checks.notBlank(text, "text");
        Checks.notBlank(search, "search");
        Checks.notNull(replacement, "replacement");

        final int index = text.lastIndexOf(search);

        // Search not found
        if (index == -1) {
            return text;
        }

        final String firstPart = text.substring(0, index);
        final String lastPart = text.substring(index + search.length());

        return firstPart + replacement + lastPart;
    }

    /**
     * Abbreviates the string to your desired length
     *
     * @param string
     *     The string to abbreviate
     * @param maxLength
     *     the maximum length of the returned string
     *
     * @return The abbreviated string
     *
     * @throws IllegalArgumentException
     *     when the string is blank or null or when maxLength is less than 0
     */
    public static String abbreviate(String string, int maxLength) {
        Checks.notNull(string, "string");

        if (string.isEmpty()) {
            return "";
        }

        Checks.positive(maxLength, "maxLength");

        final String marker = "...";
        final int markerLength = marker.length();

        if (string.length() < maxLength - markerLength) {
            return string;
        }

        return string.substring(0, maxLength - markerLength) + marker;
    }

    /**
     * Capitalizes a string (this is NOT the same as String#toUpperCase)
     *
     * @param str
     *     the string to capitalize
     *
     * @return the capitalized string
     */
    public static String capitalizeFully(String str) {
        Checks.notBlank(str, "str");

        final String[] words = str.toLowerCase().split("\\s+");
        final StringBuilder builder = new StringBuilder();

        for (String word : words) {
            builder.append(Character.toUpperCase(word.charAt(0)))
                .append(word.substring(1));
        }

        return builder.toString();
    }

}
