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
     *         the text to replace
     * @param search
     *         The string to search for
     * @param replacement
     *         what to replace it with
     *
     * @return the replaced string
     *
     * @throws IllegalArgumentException when text or search are blank or when any of the arguments are null
     */
    public static String replaceLast(String text, String search, String replacement) {
        Checks.notNull(text, "The text parameter may not be null");
        Checks.notNull(search, "The search parameter may not be null");
        Checks.notNull(replacement, "The replacement parameter may not be null");

        if (text.isEmpty() || search.isEmpty()) {
            throw new IllegalArgumentException("Text and search may not be blank");
        }

        final int index = text.lastIndexOf(search);

        // Search not found
        if (index == -1) {
            return text;
        }

        final String firstPart = text.substring(0, index);
        final String lastPart = text.substring(index + search.length());

        return firstPart + replacement + lastPart;
    }

}
