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
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.EmbedUtils.embedToMessage;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MessageUtils {

    private static String errorReaction = "❌";
    private static String successReaction = "✅";

    public static String getErrorReaction() {
        return errorReaction;
    }

    public static void setErrorReaction(String errorReaction) {
        MessageUtils.errorReaction = errorReaction;
    }

    public static String getSuccessReaction() {
        return successReaction;
    }

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
     * This method uses the sendError and sendMsg methods
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
            TextChannel channel = message.getTextChannel();
            if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)) {
                message.addReaction(successReaction).queue(null, (ignored) -> {
                });
            }
        }
    }

    /**
     * This method uses the sendSuccess and sendMsg methods
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

    public static void sendMsg(MessageConfig.Builder builder) {
        sendMsg(builder.build());
    }

    public static void sendMsg(MessageConfig config) {
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
