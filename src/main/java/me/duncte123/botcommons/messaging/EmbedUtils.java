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

package me.duncte123.botcommons.messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Util class to help send embeds
 */
public class EmbedUtils {
    private static Supplier<EmbedBuilder> embedBuilderSupplier = EmbedBuilder::new;
    private static int defaultColor = Role.DEFAULT_COLOR_RAW;
    static Function<Long, Integer> embedColorSupplier = (__) -> defaultColor;

    /**
     * Sets the embed builder for the util method
     *
     * @param embedBuilderSupplier
     *     the default embed layout
     */
    public static void setEmbedBuilder(@Nonnull Supplier<EmbedBuilder> embedBuilderSupplier) {
        Checks.notNull(embedBuilderSupplier, "embedBuilderSupplier");

        EmbedUtils.embedBuilderSupplier = embedBuilderSupplier;
    }

    /**
     * Sets the supplier that gets embed colors
     *
     * @param supplier
     *     the supplier for getting embed colors, the parameter is the guild id
     */
    public static void setEmbedColorSupplier(@Nonnull Function<Long, Integer> supplier) {
        Checks.notNull(supplier, "supplier");

        EmbedUtils.embedColorSupplier = supplier;
    }

    /**
     * Gets a color for an id
     *
     * @param key
     *     the id to find the color for
     *
     * @return The color for this key or "0"
     */
    public static int getColor(long key) {
        return embedColorSupplier.apply(key);
    }

    /**
     * Gets a color for an id
     *
     * @param key
     *     the id to find the color for
     *
     * @return The color for this key or the default value
     *
     * @see #setDefaultColor(int)
     * @see #getDefaultColor()
     */
    public static int getColorOrDefault(long key) {
        final int color = getColor(key);

        if (color <= 0) {
            return defaultColor;
        }

        return color;
    }

    /**
     * Returns the default color of all embeds
     *
     * @return the default color of all embeds
     */
    public static int getDefaultColor() {
        return defaultColor;
    }

    /**
     * Sets the default color of all embeds
     *
     * @param defaultColor
     *     The default color of all embeds
     */
    public static void setDefaultColor(int defaultColor) {
        EmbedUtils.defaultColor = defaultColor;
    }

    /**
     * The default way to send a embedded message to the channel with a field in it
     *
     * @param title
     *     The title of the field
     * @param message
     *     The message to display
     *
     * @return The {@link EmbedBuilder} for this action
     */
    public static EmbedBuilder embedField(String title, String message) {
        return getDefaultEmbed().addField(title, message, false);
    }

    /**
     * The default way to display a nice embedded message
     *
     * @param message
     *     The message to display
     *
     * @return The {@link EmbedBuilder} for this action
     */
    public static EmbedBuilder embedMessage(String message) {
        return getDefaultEmbed().setDescription(message);
    }

    /**
     * The default way to display a nice embedded message
     *
     * @param message
     *     The message to display
     * @param title
     *     The title for the embed
     *
     * @return The {@link EmbedBuilder} for this action
     */
    public static EmbedBuilder embedMessageWithTitle(String title, String message) {
        return getDefaultEmbed().setTitle(title).setDescription(message);
    }

    /**
     * The default way to send a embedded image to the channel
     *
     * @param imageURL
     *     The url from the image
     *
     * @return The {@link EmbedBuilder} for this action
     */
    public static EmbedBuilder embedImage(String imageURL) {
        return getDefaultEmbed().setImage(imageURL);
    }

    /**
     * Creates an embed that has bot a title and an image
     *
     * @param title
     *     The title of the embed
     * @param url
     *     The url that the title links to
     * @param image
     *     The image that the embed shows
     *
     * @return The {@link EmbedBuilder} for this action
     */
    public static EmbedBuilder embedImageWithTitle(String title, String url, String image) {
        return getDefaultEmbed().setTitle(title, url).setImage(image);
    }

    /**
     * Returns the default {@link EmbedBuilder embed} set in {@link #setEmbedBuilder(Supplier)}
     *
     * @return The default {@link EmbedBuilder embed} set in {@link #setEmbedBuilder(Supplier)}
     */
    public static EmbedBuilder getDefaultEmbed() {
        return embedBuilderSupplier.get();
    }

    /**
     * Returns the default {@link EmbedBuilder embed} set in {@link #setEmbedBuilder(Supplier)}
     *
     * @param guildId
     *     The guild id that has a color stored (or the defalt color)
     *
     * @return The default {@link EmbedBuilder embed} set in {@link #setEmbedBuilder(Supplier)} with the color value set
     * in {@link #setEmbedColorSupplier(Function)}
     */
    public static EmbedBuilder getDefaultEmbed(long guildId) {
        return embedBuilderSupplier.get()
            .setColor(getColorOrDefault(guildId));
    }

    /**
     * This will convert our embeds for if the bot is not able to send embeds
     *
     * @param embed
     *     the {@link MessageEmbed} that we are trying to send
     *
     * @return the converted embed
     */
    static String embedToMessage(MessageEmbed embed) {
        final StringBuilder msg = new StringBuilder();

        if (embed.getAuthor() != null) {
            msg.append("***").append(embed.getAuthor().getName()).append("***\n\n");
        }

        if (embed.getDescription() != null) {
            msg.append("_").append(embed.getDescription()
                // Reformat
                .replaceAll("\\[(.+)]\\((.+)\\)", "$1 (Link: $2)")
            ).append("_\n\n");
        }

        for (MessageEmbed.Field f : embed.getFields()) {
            msg.append("__").append(f.getName()).append("__\n").append(
                f.getValue()
                    // Reformat
                    .replaceAll("\\[(.+)]\\((.+)\\)", "$1 (Link: $2)")
            ).append("\n\n");
        }

        if (embed.getImage() != null) {
            msg.append(embed.getImage().getUrl()).append("\n");
        }

        if (embed.getFooter() != null) {
            msg.append(embed.getFooter().getText());
        }

        if (embed.getTimestamp() != null) {
            msg.append(" | ").append(embed.getTimestamp());
        }

        return msg.toString();
    }

    /*public static Queue<Message> embedToCodeBlock(MessageEmbed embed) {
        return new MessageBuilder().appendCodeBlock(embedToMessage(embed), "java").buildAll(SplitPolicy.NEWLINE);
    }*/
}
