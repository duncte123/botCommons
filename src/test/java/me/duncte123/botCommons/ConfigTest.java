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

package me.duncte123.botCommons;

import me.duncte123.botCommons.config.Config;
import me.duncte123.botCommons.config.ConfigLoader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigTest {

    @Test
    public void createConfigAndPut() throws IOException {
        File configFile = new File("testConfig.json");

        Files.deleteIfExists(configFile.toPath());

        Config config = ConfigLoader.getConfig(configFile);

        config.put("test.value.kaas", "kaas");
        config.put("test.value.hello", "hello");
        config.put("test.value2.hello", "hello");
    }
}
