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

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.EmbedUtils.embedToMessage;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MessageUtils {
    private static String errorReaction = "❌";
    private static String successReaction = "✅";

    /**
     * Returns the current error reaction
     *
     * @return The current error reaction
     *
     * @see #sendError(Message)
     */
    public static String getErrorReaction() {
        return errorReaction;
    }

    /**
     * Sets the new error reaction<br/>
     * Hint: To use a custom emote as reaction use {@link net.dv8tion.jda.api.entities.Emote#getAsMention()}
     *
     * @param errorReaction
     *     The new emoji/emote to use for error reactions.
     *
     * @see #sendError(Message)
     */
    public static void setErrorReaction(String errorReaction) {
        MessageUtils.errorReaction = errorReaction;
    }

    /**
     * Returns the current success reaction
     *
     * @return The current success reaction
     *
     * @see #sendSuccess(Message)
     */
    public static String getSuccessReaction() {
        return successReaction;
    }

    /**
     * Sets the new success reaction.<br/>
     * Hint: To use a custom emote as reaction use {@link net.dv8tion.jda.api.entities.Emote#getAsMention()}
     *
     * @param successReaction
     *     The new emoji/emote to use as success reaction
     *
     * @see #sendSuccess(Message)
     */
    public static void setSuccessReaction(String successReaction) {
        MessageUtils.successReaction = successReaction;
    }

    /**
     * This will react with a ❌ if the user doesn't have permission to run the command
     *
     * @param message
     *     the message to add the reaction to
     */
    public static void sendError(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = message.getTextChannel();

            if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)) {
                return;
            }
        }

        message.addReaction(errorReaction).queue(null, (ignored) -> {
        });
    }

    /**
     * This method uses the {@link #sendError(Message)} and {@link #sendMsg(MessageConfig)} methods
     *
     * @param message
     *     the {@link Message} for the sendError method
     * @param text
     *     the {@link String} for the sendMsg method
     */
    public static void sendErrorWithMessage(Message message, String text) {
        sendError(message);

        sendMsg(
            new MessageConfig.Builder()
                .setChannel(message.getTextChannel())
                .setMessage(text)
        );
    }

    /**
     * This will react with a ✅ if the user doesn't have permission to run the command
     *
     * @param message
     *     the message to add the reaction to
     */
    public static void sendSuccess(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            final TextChannel channel = message.getTextChannel();

            if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)) {
                message.addReaction(successReaction).queue(null, (ignored) -> {
                });
            }
        }
    }

    /**
     * This method uses the {@link #sendSuccess(Message)} and {@link #sendMsg(MessageConfig)} methods
     *
     * @param message
     *     the {@link Message} for the sendSuccess method
     * @param text
     *     the {@link String} for the sendMsg method
     */
    public static void sendSuccessWithMessage(Message message, String text) {
        sendSuccess(message);

        sendMsg(
            new MessageConfig.Builder()
                .setChannel(message.getTextChannel())
                .setMessage(text)
        );
    }

    /**
     * Shortcut for editing a message that does permission checks for embeds
     *
     * @param message
     *     The message to edit
     * @param newContent
     *     The new content of the message
     */
    public static void editMsg(Message message, Message newContent) {
        if (message == null || newContent == null) return;
        if (newContent.getEmbeds().size() > 0) {
            if (!message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                final MessageBuilder mb = new MessageBuilder()
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
     * Shortcut for sending an embed from a command context
     *
     * @param ctx
     *     The command context that holds the target channel
     * @param embed
     *     The embed to send to the channel
     *
     * @see #sendEmbed(ICommandContext, EmbedBuilder, boolean)
     */
    public static void sendEmbed(ICommandContext ctx, EmbedBuilder embed) {
        sendEmbed(ctx, embed, false);
    }

    /**
     * Shortcut for sending an embed from a command context
     *
     * @param ctx
     *     The command context that has the target channel
     * @param embed
     *     The embed to send to the channel
     * @param raw
     *     {@code true} to skip parsing of the guild-colors and other future items, default value is {@code false}
     *
     * @see #sendEmbed(ICommandContext, EmbedBuilder)
     */
    public static void sendEmbed(ICommandContext ctx, EmbedBuilder embed, boolean raw) {
        sendMsg(
            MessageConfig.Builder.fromCtx(ctx)
                .setEmbed(embed, raw)
                .build()
        );
    }

    /**
     * Shortcut for sending message from a command context
     *
     * @param ctx
     *     The command context that has the target channel
     * @param message
     *     The message to send
     */
    public static void sendMsg(ICommandContext ctx, String message) {
        sendMsg(
            MessageConfig.Builder.fromCtx(ctx)
                .setMessage(message)
                .build()
        );
    }

    /*
    Undocumented, for internal use only
     */
    public static void sendMsg(@Nullable TextChannel channel, String message) {
        if (channel == null) {
            return;
        }

        sendMsg(
            new MessageConfig.Builder()
                .setChannel(channel)
                .setMessage(message)
                .build()
        );
    }

    /*
    Undocumented, for internal use only
     */
    public static void sendEmbed(TextChannel channel, EmbedBuilder embed, boolean raw) {
        if (channel == null) {
            return;
        }

        sendMsg(
            new MessageConfig.Builder()
                .setChannel(channel)
                .setEmbed(embed, raw)
                .build()
        );
    }

    /**
     * Shortcut for the lazy that don't want to build their config before sending a message, calls {@link
     * MessageConfig.Builder#build()} underwater
     *
     * @param builder
     *     The config builder to base the message off
     */
    public static void sendMsg(MessageConfig.Builder builder) {
        sendMsg(builder.build());
    }

    /**
     * Sends a message based off the message config
     *
     * @param config
     *     The config from wha to send the message
     */
    public static void sendMsg(@Nonnull MessageConfig config) {
        final TextChannel channel = config.getChannel();
        final Guild guild = channel.getGuild();
        final TextChannel channelById = guild.getTextChannelById(channel.getIdLong());

        if (channelById == null) {
            throw new IllegalArgumentException("Channel does not seem to exist on Guild#getTextChannelById");
        }

        // we cannot talk here
        if (!channelById.canTalk()) {
            return;
        }

        final MessageBuilder messageBuilder = config.getMessageBuilder();
        final EmbedBuilder embed = config.getEmbed();

        if (embed != null) {
            if (guild.getSelfMember().hasPermission(channelById, Permission.MESSAGE_EMBED_LINKS)) {
                messageBuilder.setEmbed(embed.build());
            } else {
                messageBuilder.append(
                    embedToMessage(embed.build())
                );
            }
        }

        final Consumer<? super Throwable> failureAction = config.getFailureAction();
        final Consumer<? super Message> successAction = config.getSuccessAction();
        final Consumer<MessageAction> actionConfig = config.getActionConfig();

        // if the message is small enough we can just send it
        if (messageBuilder.length() <= Message.MAX_CONTENT_LENGTH) {
            final MessageAction messageAction = channel.sendMessage(messageBuilder.build());

            actionConfig.accept(messageAction);
            messageAction.queue(successAction, failureAction);
            return;
        }

        messageBuilder.buildAll(SplitPolicy.SPACE, SplitPolicy.NEWLINE).forEach(
            (message) -> {
                final MessageAction messageAction = channel.sendMessage(message);

                actionConfig.accept(messageAction);

                messageAction.queue(successAction, failureAction);
            }
        );
    }
}
