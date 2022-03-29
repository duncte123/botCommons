/*
 *    Copyright 2020 Duncan "duncte123" Sterken
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

import me.duncte123.botcommons.StringUtils;
import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageConfig {
    private static Function<MessageChannel, String> nonceSupplier = (c) -> c.getId() + System.currentTimeMillis();

    private final MessageChannel channel;
    private final MessageBuilder messageBuilder;
    private final List<EmbedBuilder> embeds;
    private final long replyToId;
    private final boolean mentionRepliedUser;

    private final Consumer<? super Throwable> failureAction;
    private final Consumer<? super Message> successAction;
    private final Consumer<MessageAction> actionConfig;

    /**
     * Constructs a new instacne of the message config, it is better to use the {@link Builder builder}
     *
     * @param channel
     *     The text channel that the message will be sent to
     * @param messageBuilder
     *     The message builder that holds the content for the message
     * @param embeds
     *     The embeds to send along with the message
     * @param replyToId
     *     A message id to reply to, set to {@code 0} to disable
     * @param mentionRepliedUser
     *     {@code false} to not ping the user in the reply (Default: {@code true})
     * @param failureAction
     *     The action that will be invoked when the message sending fails
     * @param successAction
     *     The action that will be called when the message sending succeeds
     * @param actionConfig
     *     Gets called before the message is sent, allows for more configuration on the message action
     *
     * @see Builder
     */
    public MessageConfig(MessageChannel channel, MessageBuilder messageBuilder, Collection<? extends EmbedBuilder> embeds, long replyToId,
                         boolean mentionRepliedUser, Consumer<? super Throwable> failureAction,
                         Consumer<? super Message> successAction, Consumer<MessageAction> actionConfig) {

        Checks.notNull(channel, "channel");
        Checks.notNull(messageBuilder, "messageBuilder");
        Checks.notNull(actionConfig, "actionConfig");
        Checks.notNull(embeds, "embeds");

        this.channel = channel;
        this.messageBuilder = messageBuilder;
        this.embeds = new ArrayList<>(embeds);
        this.replyToId = replyToId;
        this.mentionRepliedUser = mentionRepliedUser;
        this.failureAction = failureAction;
        this.successAction = successAction;
        this.actionConfig = actionConfig;

        // Set the nonce for the message
        this.messageBuilder.setNonce(nonceSupplier.apply(channel));
    }

    /**
     * Returns the text channel that the message will be sent in
     *
     * @return The text channel that the message will be sent in
     */
    public MessageChannel getChannel() {
        return this.channel;
    }

    /**
     * Returns the message builder that holds the contents for the message<br/>
     * The reason that we are using a message builder is so that we can easily attach the embed and set the nonce
     *
     * @return The message builder that holds the contents for the message
     */
    public MessageBuilder getMessageBuilder() {
        return this.messageBuilder;
    }

    /**
     * Returns the list of embeds that should go under the message
     *
     * @return A possibly empty list of embeds that should go under the message
     */
    @Nonnull
    public List<EmbedBuilder> getEmbeds() {
        return this.embeds;
    }

    /**
     * Returns the id of the message to reply to
     *
     * @return the message id that we want to reply to
     */
    public long getReplyToId() {
        return replyToId;
    }

    /**
     * Returns true if we should mention the user we reply to, false otherwise
     *
     * @return true if we should mention the user we reply to, false otherwise
     */
    public boolean isMentionRepliedUser() {
        return mentionRepliedUser;
    }

    /**
     * Returns the action that is called when the {@link RestAction} fails
     *
     * @return The action that is called when the {@link RestAction} fails
     */
    public Consumer<? super Throwable> getFailureAction() {
        return this.failureAction;
    }

    /**
     * Returns the action that is called when the {@link RestAction} succeeds
     *
     * @return The action that is called when the {@link RestAction} succeeds
     */
    public Consumer<? super Message> getSuccessAction() {
        return this.successAction;
    }

    /**
     * Returns the {@link MessageAction} for you to configure (eg append some content or override the nonce)
     *
     * @return The {@link MessageAction} for you to configure (eg append some content or override the nonce)
     *
     * @see MessageAction#append(CharSequence)
     * @see MessageAction#nonce(String)
     */
    public Consumer<MessageAction> getActionConfig() {
        return this.actionConfig;
    }

    /**
     * Sets the supplier for the nonce.<br/>
     * A nonce can be used to verify if the message you recieve is the message you want
     *
     * @param nonceSupplier
     *     A function that returns the nonce, by default this is the {@link MessageChannel#getId() channel id} combined
     *     with the {@link System#currentTimeMillis() current time in milliseconds}
     */
    public static void setNonceSupplier(Function<MessageChannel, String> nonceSupplier) {
        Checks.notNull(nonceSupplier, "nonceSupplier");

        MessageConfig.nonceSupplier = nonceSupplier;
    }

    /**
     * Builder class for the message config
     */
    // TODO: addEmbed and addEmbeds
    public static class Builder {
        private final List<EmbedBuilder> embeds = new ArrayList<>();
        private MessageBuilder messageBuilder = new MessageBuilder();
        private long replyToId;
        private boolean mentionRepliedUser = MessageAction.isDefaultMentionRepliedUser();
        private MessageChannel channel;
        private Consumer<? super Throwable> failureAction = RestAction.getDefaultFailure();
        private Consumer<? super Message> successAction = RestAction.getDefaultSuccess();
        private Consumer<MessageAction> actionConfig = (a) -> {
        };

        /**
         * Sets the channel that the message will be sent to
         *
         * @param channel
         *     the channel that the message will be sent to
         *
         * @return The builder instance, useful for chaining
         */
        public Builder setChannel(@Nonnull MessageChannel channel) {
            Checks.notNull(channel, "channel");

            this.channel = channel;
            return this;
        }

        /**
         * Sets the message content for the message that will be sent.<br/>
         * <b>THIS WILL REPLACE THE CURRENT MESSAGE BUILDER</b>
         * <p>This method will also attempt to extract the channel and any possible embeds if this information is
         * present.</p>
         *
         * @param message
         *     The message to extract the content from
         *
         * @return The builder instance, useful for chaining
         *
         * @see #setMessage(String)
         * @see #setMessageFormat(String, Object...)
         */
        public Builder setMessage(Message message) {
            this.messageBuilder = new MessageBuilder(message);
            // clear the embeds
            this.messageBuilder.setEmbeds();

            // set the channel if we have one
            if (message.getType() == MessageType.DEFAULT && message.isFromGuild()) {
                this.setChannel(message.getTextChannel());
            }

            // set the embeds on our own config, this will always use the raw preset
            if (!message.getEmbeds().isEmpty()) {
                this.setEmbeds(message.getEmbeds());
            }

            return this;
        }

        /**
         * Sets the content of the message that will be sent
         *
         * @param message
         *     The content for the message
         *
         * @return The builder instance, useful for chaining
         *
         * @see #setMessage(Message)
         * @see #setMessageFormat(String, Object...)
         */
        public Builder setMessage(String message) {
            this.messageBuilder.setContent(StringUtils.abbreviate(message, Message.MAX_CONTENT_LENGTH));
            return this;
        }

        /**
         * Sets the content for the message and applies a format, this is a shortcut for using {@link String#format}
         * with {@link #setMessage(String)}
         *
         * @param message
         *     The content for the message
         * @param args
         *     the arguments to format the message with
         *
         * @return The builder instance, useful for chaining
         *
         * @see #setMessage(Message)
         * @see #setMessage(String)
         */
        public Builder setMessageFormat(String message, Object... args) {
            this.messageBuilder.setContent(String.format(message, args));
            return this;
        }

        /**
         * Sets the embed for the message
         *
         * @param embeds
         *     The embeds to set on the message
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(EmbedBuilder)
         * @see #addEmbed(boolean, EmbedBuilder)
         * @see #setEmbeds(boolean, EmbedBuilder...)
         * @see #setEmbeds(Collection)
         * @see #setEmbeds(boolean, Collection)
         */
        public Builder setEmbeds(@Nonnull EmbedBuilder... embeds) {
            return this.setEmbeds(false, embeds);
        }

        /**
         * Sets the embed for the message.<br/>
         * <b>NOTE:</b> Parsing of colors will never happen if the text channel is null at the time of calling this
         * method
         *
         * @param raw
         *     {@code true} to skip parsing of the guild-colors and other future items, default value is {@code false}
         * @param embeds
         *     The embeds on the message
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(EmbedBuilder)
         * @see #addEmbed(boolean, EmbedBuilder)
         * @see #setEmbeds(EmbedBuilder...)
         * @see #setEmbeds(Collection)
         * @see #setEmbeds(boolean, Collection)
         */
        public Builder setEmbeds(boolean raw, @Nonnull EmbedBuilder... embeds) {
            Checks.noneNull(embeds, "MessageEmbeds");

            return this.setEmbeds(raw, Arrays.asList(embeds));
        }

        /**
         * Sets the embeds that the message should have
         *
         * @param embeds
         *     The embeds to attach to the message
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(EmbedBuilder)
         * @see #addEmbed(boolean, EmbedBuilder)
         * @see #setEmbeds(EmbedBuilder...)
         * @see #setEmbeds(boolean, EmbedBuilder...)
         * @see #setEmbeds(boolean, Collection)
         */
        public Builder setEmbeds(@Nonnull Collection<? extends EmbedBuilder> embeds) {
            return this.setEmbeds(false, embeds);
        }

        /**
         * Please don't use this method
         *
         * @param embeds
         *     The embeds to set
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(EmbedBuilder)
         * @see #addEmbed(boolean, EmbedBuilder)
         * @see #setEmbeds(EmbedBuilder...)
         * @see #setEmbeds(boolean, EmbedBuilder...)
         * @see #setEmbeds(Collection)
         * @see #setEmbeds(boolean, Collection)
         */
        public Builder setEmbeds(@Nonnull List<MessageEmbed> embeds) {
            return this.setEmbeds(
                true,
                embeds.stream().map(EmbedBuilder::new).collect(Collectors.toList())
            );
        }

        /**
         * Sets the embeds for the message
         *
         * @param raw
         *     {@code true} to skip parsing of the guild-colors and other future items, default value is {@code false}
         * @param embeds
         *     The embeds to set on the message
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(EmbedBuilder)
         * @see #addEmbed(boolean, EmbedBuilder)
         * @see #setEmbeds(EmbedBuilder...)
         * @see #setEmbeds(boolean, EmbedBuilder...)
         * @see #setEmbeds(Collection)
         */
        public Builder setEmbeds(boolean raw, @Nonnull Collection<? extends EmbedBuilder> embeds) {
            Checks.noneNull(embeds, "MessageEmbeds");

            Checks.check(embeds.size() <= 10, "Cannot have more than 10 embeds in a message!");

            // Use raw to skip this parsing
            if (!raw && this.channel != null && this.channel instanceof GuildMessageChannel) {
                final long guild = ((GuildMessageChannel) this.channel).getGuild().getIdLong();

                for (final EmbedBuilder embedBuilder : embeds) {
                    embedBuilder.setColor(EmbedUtils.getColorOrDefault(guild));
                }
            }

            this.embeds.clear();
            this.embeds.addAll(embeds);

            return this;
        }

        /**
         * Adds a single embed to the current embed list
         *
         * @param embed
         *     The embed to add
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(boolean, EmbedBuilder)
         * @see #setEmbeds(EmbedBuilder...)
         * @see #setEmbeds(boolean, EmbedBuilder...)
         * @see #setEmbeds(Collection)
         * @see #setEmbeds(boolean, Collection)
         */
        public Builder addEmbed(@Nonnull EmbedBuilder embed) {
            return this.addEmbed(false, embed);
        }

        /**
         * Adds a single embed to the current embed list
         *
         * @param raw
         *     {@code true} to skip parsing of the guild-colors and other future items, default value is {@code false}
         * @param embed
         *     The embed to add
         *
         * @return The builder instance, useful for chaining
         *
         * @see #addEmbed(EmbedBuilder)
         * @see #setEmbeds(EmbedBuilder...)
         * @see #setEmbeds(boolean, EmbedBuilder...)
         * @see #setEmbeds(Collection)
         * @see #setEmbeds(boolean, Collection)
         */
        public Builder addEmbed(boolean raw, @Nonnull EmbedBuilder embed) {
            Checks.notNull(embed, "embed");
            Checks.check(this.embeds.size() <= 10, "Cannot have more than 10 embeds in a message!");

            // Use raw to skip this parsing
            if (!raw && this.channel != null && this.channel instanceof GuildMessageChannel) {
                final long guild = ((GuildMessageChannel) this.channel).getGuild().getIdLong();

                embed.setColor(EmbedUtils.getColorOrDefault(guild));
            }

            this.embeds.add(embed);

            return this;
        }

        /**
         * Returns the current message builder instance for you to modify
         *
         * @return The message builder instance that you can modify
         *
         * @see #configureMessageBuilder(Consumer)
         */
        public MessageBuilder getMessageBuilder() {
            return this.messageBuilder;
        }

        /**
         * Applies a configuration to the message builder
         *
         * @param consumer
         *     the builder that you can modify
         *
         * @return The builder instance, useful for chaining
         *
         * @see #getMessageBuilder()
         */
        public Builder configureMessageBuilder(@Nonnull Consumer<MessageBuilder> consumer) {
            Checks.notNull(consumer, "consumer");

            consumer.accept(this.messageBuilder);
            return this;
        }

        /**
         * Sets the action that is called when the {@link RestAction} fails
         *
         * @param failureAction
         *     the action that is called when the {@link RestAction} fails, Defaults to {@link
         *     RestAction#getDefaultFailure()}
         *
         * @return The builder instance, useful for chaining
         */
        public Builder setFailureAction(Consumer<? super Throwable> failureAction) {
            this.failureAction = failureAction;
            return this;
        }

        /**
         * Sets the action that is called when the {@link RestAction} succeeds
         *
         * @param successAction
         *     the action that is called when the {@link RestAction} succeeds, Defaults to {@link
         *     RestAction#getDefaultSuccess()}
         *
         * @return The builder instance, useful for chaining
         */
        public Builder setSuccessAction(Consumer<? super Message> successAction) {
            this.successAction = successAction;
            return this;
        }

        /**
         * Sets the {@link MessageAction} for you to configure (eg append some content or override the nonce)
         *
         * @param actionConfig
         *     the {@link MessageAction} for you to configure (eg append some content or override the nonce)
         *
         * @return The builder instance, useful for chaining
         *
         * @see MessageAction#append(CharSequence)
         * @see MessageAction#nonce(String)
         * @see MessageAction
         */
        public Builder setActionConfig(@Nonnull Consumer<MessageAction> actionConfig) {
            Checks.notNull(actionConfig, "actionConfig");

            this.actionConfig = actionConfig;
            return this;
        }

        /**
         * Replies to the given {@link Message}<br>
         * <b>THIS WILL ONLY WORK IF THE BOT HAS READ HISTORY PERMISSION IN THE CHANNEL</b>
         *
         * @param message
         *     The {@link Message} on discord that you want to reply to, or {@code null} to disable
         *
         * @return The builder instance, useful for chaining
         *
         * @see #replyTo(long)
         * @see #replyTo(long, boolean)
         * @see #replyTo(Message, boolean)
         */
        public Builder replyTo(@Nullable Message message) {
            if (message == null) {
                this.replyToId = 0;
            } else {
                this.replyToId = message.getIdLong();
            }

            return this;
        }

        /**
         * Replies to the given {@link Message}<br>
         * <b>THIS WILL ONLY WORK IF THE BOT HAS READ HISTORY PERMISSION IN THE CHANNEL</b>
         *
         * @param message
         *     The {@link Message} on discord that you want to reply to, or {@code null} to disable
         * @param mentionRepliedUser
         *     Set to {@code false} to not ping the user in the reply (Default: {@link
         *     MessageAction#isDefaultMentionRepliedUser()})
         *
         * @return The builder instance, useful for chaining
         *
         * @see #replyTo(long)
         * @see #replyTo(long, boolean)
         * @see #replyTo(Message)
         */
        public Builder replyTo(@Nullable Message message, boolean mentionRepliedUser) {
            if (message == null) {
                this.replyToId = 0;
            } else {
                this.replyToId = message.getIdLong();
            }

            this.mentionRepliedUser = mentionRepliedUser;
            return this;
        }

        /**
         * Replies to the given message with the specified id<br>
         * <b>THIS WILL ONLY WORK IF THE BOT HAS READ HISTORY PERMISSION IN THE CHANNEL</b>
         *
         * @param messageId
         *     The message id from a message on discord, set to {@code 0} to disable
         *
         * @return The builder instance, useful for chaining
         *
         * @see #replyTo(long, boolean)
         * @see #replyTo(Message)
         * @see #replyTo(Message, boolean)
         */
        public Builder replyTo(long messageId) {
            this.replyToId = messageId;
            return this;
        }

        /**
         * Replies to the given message with the specified id<br>
         * <b>THIS WILL ONLY WORK IF THE BOT HAS READ HISTORY PERMISSION IN THE CHANNEL</b>
         *
         * @param messageId
         *     The message id from a message on discord, set to {@code 0} to disable
         * @param mentionRepliedUser
         *     Set to {@code false} to not ping the user in the reply (Default: {@link
         *     MessageAction#isDefaultMentionRepliedUser()})
         *
         * @return The builder instance, useful for chaining
         *
         * @see #replyTo(long)
         * @see #replyTo(Message)
         * @see #replyTo(Message, boolean)
         */
        public Builder replyTo(long messageId, boolean mentionRepliedUser) {
            this.replyToId = messageId;
            this.mentionRepliedUser = mentionRepliedUser;
            return this;
        }

        /**
         * Builds the message config and returns it.
         * <p><b>NOTE:</b> This method will return null when the text channel is null</p>
         *
         * @return a message config instance
         */
        @Nonnull
        public MessageConfig build() {
            if (this.channel == null) {
                throw new IllegalArgumentException("No text channel has been set, set this with setChannel");
            }

            // we can send messages with just an embed
            if (this.messageBuilder.isEmpty() && this.embeds.isEmpty()) {
                throw new IllegalArgumentException("This message has no content, please add some content with setMessage or setEmbeds");
            }

            Checks.check(this.embeds.size() <= 10, "Cannot have more than 10 embeds in a message!");

            return new MessageConfig(
                this.channel,
                this.messageBuilder,
                this.embeds,
                this.replyToId,
                this.mentionRepliedUser,
                this.failureAction,
                this.successAction,
                this.actionConfig
            );
        }

        /**
         * Creates a config builder instance from a command context
         *
         * @param ctx
         *     a command context instance to get the text channel from
         *
         * @return A builder instance that was created from a command context
         *
         * @see me.duncte123.botcommons.commands.DefaultCommandContext
         */
        public static Builder fromCtx(ICommandContext ctx) {
            return new Builder().setChannel(ctx.getChannel()).replyTo(ctx.getMessage());
        }

        /**
         * Creates a config builder instance from a JDA guild message received event
         *
         * @param event
         *     A {@link MessageReceivedEvent} from JDA to get the text channel from
         *
         * @return A builder instance that was created from a {@link MessageReceivedEvent}
         */
        public static Builder fromEvent(MessageReceivedEvent event) {
            return new Builder().setChannel(event.getChannel()).replyTo(event.getMessage());
        }
    }
}
