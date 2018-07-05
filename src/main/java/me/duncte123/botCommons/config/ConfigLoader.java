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

import com.afollestad.ason.Ason;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.text.translate.UnicodeUnescaper;

import java.io.*;

public class ConfigLoader {

    /**
     * This will attempt to load the config and create it if it is not there
     *
     * @param file the file to load
     * @return the loaded config
     * @throws IOException if something goes wrong
     */
    public static Config getConfig(final File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            final FileWriter writer = new FileWriter(file);
            writer.write("{}");
            writer.close();
        }
        return new MainConfig(file);
    }

    public static class MainConfig extends Config {

        private final File configFile;

        MainConfig(final File file) throws IOException {
            super(null, new Ason(Files.asCharSource(file, Charsets.UTF_8).read()));
            this.configFile = file;
        }

        @Override
        public File getConfigFile() {
            return this.configFile;
        }

        @Override
        public void save() throws Exception {
            try {
                final BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(this.configFile), "UTF-8"));
                new UnicodeUnescaper().translate(
                        this.config.toString(4), writer);
                writer.close();
            } catch (final IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}