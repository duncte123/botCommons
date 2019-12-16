/*
 *    Copyright 2019 Duncan "duncte123" Sterken
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

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testReplaceLastReplacesCorrectly() {
        String input = "this , that";
        String expected = "this and that";

        String result = StringUtils.replaceLast(input, ",", "and");

        assertEquals(expected, result);
    }

    @Test
    public void testReplaceLastDoesNothingWhenSearchIsNotPresent() {
        String input = "this | that";
        String expected = "this and that";

        String result = StringUtils.replaceLast(input, ",", "and");

        assertEquals(input, result);
        assertNotEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplaceLastThrowsWhenArgumentsAreEmpty() {
        StringUtils.replaceLast("", ",", "and");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplaceLastThrowsWhenArgumentsAreNull() {
        StringUtils.replaceLast("hello world", null, "and");
    }

    @Test
    public void testNormalStringAbbreviations() {
        String res1 = StringUtils.abbreviate("Hello world, this is a very long string", 10);
        String res2 = StringUtils.abbreviate("Hello", 10);

        assertEquals("Hello w...", res1);
        assertEquals("Hello", res2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailingStringAbbreviations() {
        StringUtils.abbreviate("", 10);
        StringUtils.abbreviate(null, 10);
        StringUtils.abbreviate("bla bla bla", 0);
        StringUtils.abbreviate("bla bla bla", -1);
    }
}
