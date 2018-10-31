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

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmbedUtilsTest {

    @Test
    public void testCanSetEmbedSupplier() {

        EmbedUtils.setEmbedBuilder(
            () -> new EmbedBuilder().setAuthor("test")
        );

        MessageEmbed embedWithCustomAuthor = EmbedUtils.defaultEmbed()
            .setAuthor("Kaas").setDescription("Hello world2").build();

        MessageEmbed normalEmbed = EmbedUtils.embedMessage("Hello World");

        assertEquals("Kaas", embedWithCustomAuthor.getAuthor().getName());
        assertEquals("test", normalEmbed.getAuthor().getName());
    }


    @Test
    public void testCanSetCustomColors() {
        int color = 0xFF00FF;

        EmbedUtils.addColor(3L, color);

        MessageEmbed embed = EmbedUtils.defaultEmbed(3L).build();

        assertEquals(color, embed.getColorRaw());
    }
}
