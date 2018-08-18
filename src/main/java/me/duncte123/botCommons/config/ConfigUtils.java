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

package me.duncte123.botCommons.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {

    private static final Gson gson = new Gson();

    public static <T> T loadFromFile(String fileName, Class<T> classOfT) throws IOException {
        return loadFromFile(new File(fileName), classOfT);
    }

    public static <T> T loadFromFile(File file, Class<T> classOfT) throws IOException {
        return gson.fromJson(Files.asCharSource(file, Charsets.UTF_8).read(), classOfT);
    }
}
