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

import gnu.trove.map.TLongIntMap;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.EmbedUtils.embedToMessage;

public class MessageUtils {

    /**
     * This will react with a ❌ if the user doesn't have permission to run the command
     *
     * @param message
     *         the message to add the reaction to
     */
    public static void sendError(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = message.getTextChannel();

            if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)) {
                return;
            }
        }
        message.addReaction("❌").queue(null, (ignored) -> {
        });
    }

    /**
     * This method uses the sendError and sendMsg methods
     *
     * @param message
     *         the {@link Message} for the sendError method
     * @param text
     *         the {@link String} for the sendMsg method
     */
    public static void sendErrorWithMessage(Message message, String text) {
        sendError(message);
        new MessageBuilder().append(text).buildAll(SplitPolicy.NEWLINE).forEach(message1 ->
            sendMsg(message.getTextChannel(), message1)
        );
    }

    /**
     * This will react with a ✅ if the user doesn't have permission to run the command
     *
     * @param message
     *         the message to add the reaction to
     */
    public static void sendSuccess(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = message.getTextChannel();
            if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)) {
                message.addReaction("✅").queue(null, (ignored) -> {
                });
            }
        }
    }

    /**
     * This method uses the sendSuccess and sendMsg methods
     *
     * @param message
     *         the {@link Message} for the sendSuccess method
     * @param text
     *         the {@link String} for the sendMsg method
     */
    public static void sendSuccessWithMessage(Message message, String text) {
        sendSuccess(message);
        sendMsg(message.getTextChannel(), text);
    }

    /**
     * This is a shortcut for sending formatted messages to a channel which also deletes it after delay unit
     *
     * @param event
     *         an instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param delay
     *         the {@link Long} that is our delay
     * @param unit
     *         the {@link TimeUnit} that is our unit that uses the delay parameter
     * @param msg
     *         the message format to send
     */
    public static void sendMsgAndDeleteAfter(GuildMessageReceivedEvent event, long delay, TimeUnit unit, String msg) {
        sendMsgFormatAndDeleteAfter(event.getChannel(), delay, unit, StringUtils.abbreviate(msg, 2000), "");
    }

    /**
     * This is a shortcut for sending formatted messages to a channel which also deletes it after delay unit
     *
     * @param tc
     *         an instance of {@link TextChannel TextChannel}
     * @param delay
     *         the {@link Long} that is our delay
     * @param unit
     *         the {@link TimeUnit} that is our unit that uses the delay parameter
     * @param msg
     *         the message format to send
     */
    public static void sendMsgAndDeleteAfter(TextChannel tc, long delay, TimeUnit unit, String msg) {
        sendMsgFormatAndDeleteAfter(tc, delay, unit, StringUtils.abbreviate(msg, 2000), "");
    }

    /**
     * This is a shortcut for sending formatted messages to a channel which also deletes it after delay unit
     *
     * @param event
     *         an instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param delay
     *         the {@link Long} that is our delay
     * @param unit
     *         the {@link TimeUnit} that is our unit that uses the delay parameter
     * @param msg
     *         the message format to send
     * @param args
     *         the arguments that should be used in the msg parameter
     */
    public static void sendMsgFormatAndDeleteAfter(GuildMessageReceivedEvent event, long delay, TimeUnit unit, String msg, Object... args) {
        sendMsgFormatAndDeleteAfter(event.getChannel(), delay, unit, StringUtils.abbreviate(msg, 2000), args);
    }

    /**
     * This is a shortcut for sending formatted messages to a channel which also deletes it after delay unit
     *
     * @param channel
     *         the {@link TextChannel TextChannel} that we want to send our message to
     * @param delay
     *         the {@link Long} that is our delay
     * @param unit
     *         the {@link TimeUnit} that is our unit that uses the delay parameter
     * @param msg
     *         the message format to send
     * @param args
     *         the arguments that should be used in the msg parameter
     */
    public static void sendMsgFormatAndDeleteAfter(TextChannel channel, long delay, TimeUnit unit, String msg, Object... args) {

        sendMsg(channel, new MessageBuilder().append(String.format(StringUtils.abbreviate(msg, 2000), args)).build(),
            it -> it.delete().reason("automatic remove").queueAfter(delay, unit)
        );
    }

    /**
     * This is a shortcut for sending formatted messages to a channel
     *
     * @param event
     *         an instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message format to send
     * @param args
     *         the arguments that should be used in the msg parameter
     */
    public static void sendMsgFormat(GuildMessageReceivedEvent event, String msg, Object... args) {
        sendMsg(event.getChannel(), (new MessageBuilder().append(String.format(StringUtils.abbreviate(msg, 2000), args)).build()));
    }

    /**
     * This is a shortcut for sending formatted messages to a channel
     *
     * @param channel
     *         the {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message format to send
     * @param args
     *         the arguments that should be used in the msg parameter
     */
    public static void sendMsgFormat(TextChannel channel, String msg, Object... args) {
        sendMsg(channel, (new MessageBuilder().append(String.format(StringUtils.abbreviate(msg, 2000), args)).build()));
    }

    /**
     * This will check if we can send a embed and convert it to a message if we can't send embeds
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param embed
     *         The embed to send
     */
    public static void sendEmbed(GuildMessageReceivedEvent event, MessageEmbed embed) {
        sendEmbed(event.getChannel(), embed, null);
    }

    public static void sendEmbed(GuildMessageReceivedEvent event, EmbedBuilder embed) {
        sendEmbed(event.getChannel(), embed, null);
    }

    public static void sendEmbed(TextChannel channel, EmbedBuilder embed) {
        sendEmbed(channel, embed, null);
    }

    public static void sendEmbed(TextChannel channel, EmbedBuilder embed, Consumer<Message> success) {

        if (channel == null) {
            return;
        }

        TLongIntMap colors = EmbedUtils.customColors;
        long guild = channel.getGuild().getIdLong();

        if (colors.containsKey(guild)) {
            embed.setColor(colors.get(guild));
        }

        sendEmbedRaw(channel, embed.build(), success);
    }

    public static void sendEmbed(GuildMessageReceivedEvent event, MessageEmbed embed, Consumer<Message> success) {
        sendEmbed(event.getChannel(), embed, success);
    }

    public static void sendEmbed(TextChannel channel, MessageEmbed embed) {
        sendEmbed(channel, embed, null);
    }

    /**
     * This will check if we can send a embed and convert it to a message if we can't send embeds
     *
     * @param channel
     *         the {@link TextChannel TextChannel} that we want to send the embed to
     * @param embed
     *         The embed to send
     */
    public static void sendEmbed(TextChannel channel, MessageEmbed embed, Consumer<Message> success) {
        if (channel == null) {
            return;
        }

        TLongIntMap colors = EmbedUtils.customColors;
        long guild = channel.getGuild().getIdLong();

        if (colors.containsKey(guild)) {
            embed = new EmbedBuilder(embed).setColor(colors.get(guild)).build();
        }

        sendEmbedRaw(channel, embed, success);

    }

    public static void sendEmbedRaw(TextChannel channel, MessageEmbed embed, Consumer<Message> success) {
        if (channel == null) {
            return;
        }

        if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
            (new MessageBuilder()).append(embedToMessage(embed))
                .buildAll(SplitPolicy.NEWLINE)
                .forEach(it -> MessageUtils.sendMsg(channel, it, success));
//                sendMsg(channel, EmbedUtils.embedToMessage(embed));

            return;
        }


        sendMsg(channel, embed, success);
    }

    public static void editMsg(Message message, Message newContent) {
        if (message == null || newContent == null) return;
        if (newContent.getEmbeds().size() > 0) {
            if (!message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {

                MessageBuilder mb = new MessageBuilder()
                    .append(newContent.getContentRaw())
                    .append('\n');

                newContent.getEmbeds().forEach(
                    messageEmbed -> mb.append(embedToMessage(messageEmbed))
                );

                message.editMessage(mb.build()).queue();

                return;
            }
        }

        message.editMessage(newContent).queue();
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     *
     * @ Deprecated Use {@link #sendEmbed(TextChannel, MessageEmbed, Consumer)}
     */
    @ReplaceWith("MessageUtils#sendEmbed")
    private static void sendMsg(TextChannel channel, MessageEmbed msg, Consumer<Message> success) {
        //Check if the channel exists
        if ((channel != null && channel.getGuild().getTextChannelById(channel.getId()) != null) &&
            channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)) {
            Message m = new MessageBuilder().setEmbed(msg).build();
            //Only send a message if we can talk
            channel.sendMessage(m).queue(success);
        }
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message to send
     */
    public static void sendMsg(GuildMessageReceivedEvent event, String msg) {
        sendMsg(event.getChannel(), (new MessageBuilder()).append(StringUtils.abbreviate(msg, 2000)).build());
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     */
    public static void sendMsg(GuildMessageReceivedEvent event, String msg, Consumer<Message> success) {
        sendMsg(event.getChannel(), (new MessageBuilder()).append(StringUtils.abbreviate(msg, 2000)).build(), success);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     * @param failure
     *         the failure consumer
     */
    public static void sendMsg(GuildMessageReceivedEvent event, String msg, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMsg(event.getChannel(), (new MessageBuilder()).append(StringUtils.abbreviate(msg, 2000)).build(), success, failure);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     */
    public static void sendMsg(TextChannel channel, String msg) {
        sendMsg(channel, (new MessageBuilder()).append(StringUtils.abbreviate(msg, 2000)).build());
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     */
    public static void sendMsg(TextChannel channel, String msg, Consumer<Message> success) {
        sendMsg(channel, (new MessageBuilder()).append(StringUtils.abbreviate(msg, 2000)).build(), success);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     * @param failure
     *         the failure consumer
     */
    public static void sendMsg(TextChannel channel, String msg, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMsg(channel, (new MessageBuilder()).append(StringUtils.abbreviate(msg, 2000)).build(), success, failure);

    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     *
     * @deprecated Use  #sendEmbed(TextChannel, MessageEmbed)
     */
    @Deprecated
    public static void sendMsg(TextChannel channel, MessageEmbed msg) {
        sendMsg(channel, (new MessageBuilder()).setEmbed(msg).build());
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message to send
     */
    public static void sendMsg(GuildMessageReceivedEvent event, Message msg) {
        sendMsg(event.getChannel(), msg);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     */
    public static void sendMsg(GuildMessageReceivedEvent event, Message msg, Consumer<Message> success) {
        sendMsg(event.getChannel(), msg, success);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param event
     *         a instance of {@link GuildMessageReceivedEvent GuildMessageReceivedEvent}
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     * @param failure
     *         the failure consumer
     */
    public static void sendMsg(GuildMessageReceivedEvent event, Message msg, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMsg(event.getChannel(), msg, success, failure);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     */
    public static void sendMsg(TextChannel channel, Message msg) {
        sendMsg(channel, msg, null);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     */
    public static void sendMsg(TextChannel channel, Message msg, Consumer<Message> success) {
        sendMsg(channel, msg, success, null);
    }

    /**
     * This is a shortcut for sending messages to a channel
     *
     * @param channel
     *         he {@link TextChannel TextChannel} that we want to send our message to
     * @param msg
     *         the message to send
     * @param success
     *         The success consumer
     * @param failure
     *         the failure consumer
     */
    public static void sendMsg(TextChannel channel, Message msg, Consumer<Message> success, Consumer<Throwable> failure) {
        //Check if the channel exists and we can talk
        if ((channel != null && channel.getGuild().getTextChannelById(channel.getId()) != null) && channel.canTalk()) {
            MessageBuilder builder = new MessageBuilder(msg.getContentRaw());

            if (!msg.getEmbeds().isEmpty()) {
                if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
                    builder.setEmbed(msg.getEmbeds().get(0));
                } else {
                    msg.getEmbeds().forEach(
                        messageEmbed -> builder.append(embedToMessage(messageEmbed))
                    );
                }
            }

            builder.buildAll(SplitPolicy.SPACE, SplitPolicy.NEWLINE).forEach(
                (message) -> channel.sendMessage(message).queue(success, failure)
            );
        }
    }
}
