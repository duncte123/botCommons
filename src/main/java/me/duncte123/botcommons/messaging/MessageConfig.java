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

import gnu.trove.map.TLongIntMap;
import me.duncte123.botcommons.StringUtils;
import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class MessageConfig {
    private static Function<TextChannel, String> nonceSupplier = (c) -> c.getId() + System.currentTimeMillis();

    private final TextChannel channel;
    private final MessageBuilder messageBuilder;
    private final EmbedBuilder embed;

    private final Consumer<? super Throwable> failureAction;
    private final Consumer<? super Message> successAction;
    private final Consumer<MessageAction> actionConfig;

    /**
     * Constructs a new instacne of the message config, it is better to use the {@link Builder builder}
     *
     * @param channel The text channel that the message will be sent to
     * @param messageBuilder The message builder that holds the content for the message
     * @param embed An optional embed to send with the message
     * @param failureAction The action that will be invoked when the message sending fails
     * @param successAction The action that will be called when the message sending succeeds
     * @param actionConfig Gets called before the message is sent, allows for more configuration on the message action
     *
     * @see Builder
     */
    public MessageConfig(TextChannel channel, MessageBuilder messageBuilder, EmbedBuilder embed, Consumer<? super Throwable> failureAction,
                         Consumer<? super Message> successAction, Consumer<MessageAction> actionConfig) {

        Checks.notNull(channel, "channel");
        Checks.notNull(messageBuilder, "messageBuilder");
        Checks.notNull(actionConfig, "actionConfig");

        this.channel = channel;
        this.messageBuilder = messageBuilder;
        this.embed = embed;
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
    public TextChannel getChannel() {
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
     * Returns the embed that should go under the message
     *
     * @return A possibly null embed that should go under the message
     */
    @Nullable
    public EmbedBuilder getEmbed() {
        return this.embed;
    }

    /**
     *
     * @return
     */
    public Consumer<? super Throwable> getFailureAction() {
        return this.failureAction;
    }

    /**
     *
     * @return
     */
    public Consumer<? super Message> getSuccessAction() {
        return this.successAction;
    }

    /**
     *
     * @return
     */
    public Consumer<MessageAction> getActionConfig() {
        return this.actionConfig;
    }

    /**
     *
     * @param nonceSupplier
     */
    public static void setNonceSupplier(Function<TextChannel, String> nonceSupplier) {
        Checks.notNull(nonceSupplier, "nonceSupplier");

        MessageConfig.nonceSupplier = nonceSupplier;
    }

    /**
     * Builder class for the message config
     */
    public static class Builder {
        private TextChannel channel;
        private final MessageBuilder messageBuilder = new MessageBuilder();
        private EmbedBuilder embed;

        private Consumer<? super Throwable> failureAction = RestAction.getDefaultFailure();
        private Consumer<? super Message> successAction = RestAction.getDefaultSuccess();
        private Consumer<MessageAction> actionConfig = (a) -> {};

        /**
         *
         * @param channel
         * @return
         */
        public Builder setChannel(@Nonnull TextChannel channel) {
            Checks.notNull(channel, "channel");

            this.channel = channel;
            return this;
        }

        /**
         *
         * @param message
         * @return
         */
        public Builder setMessage(Message message) {
            this.messageBuilder.setContent(message.getContentRaw());
            return this;
        }

        /**
         *
         * @param message
         * @return
         */
        public Builder setMessage(String message) {
            this.messageBuilder.setContent(StringUtils.abbreviate(message, Message.MAX_CONTENT_LENGTH));
            return this;
        }

        /**
         *
         * @param message
         * @param args
         * @return
         */
        public Builder setMessageFormat(String message, Object... args) {
            this.messageBuilder.setContent(String.format(message, args));
            return this;
        }

        /**
         *
         * @param embed
         * @return
         * @deprecated Use the method that takes in an embed builder instead
         */
        @Deprecated
        @ForRemoval(deadline = "2.0.1")
        public Builder setEmbed(MessageEmbed embed) {
            TLongIntMap colors = EmbedUtils.customColors;
            long guild = channel.getGuild().getIdLong();
            final EmbedBuilder builder = new EmbedBuilder(embed);

            if (colors.containsKey(guild)) {
                builder.setColor(colors.get(guild));
            }

            this.embed = builder;
            return this;
        }

        /**
         *
         * @param embed
         * @return
         */
        public Builder setEmbed(@Nullable EmbedBuilder embed) {
            return this.setEmbed(embed, false);
        }

        /**
         *
         * @param embed
         * @param raw
         * @return
         */
        public Builder setEmbed(@Nullable EmbedBuilder embed, boolean raw) {
            // Use raw to skip this parsing
            if (embed != null && !raw) {
                final TLongIntMap colors = EmbedUtils.customColors;
                final long guild = channel.getGuild().getIdLong();

                if (colors.containsKey(guild)) {
                    embed.setColor(colors.get(guild));
                }
            }

            this.embed = embed;
            return this;
        }

        /**
         *
         * @return
         */
        public MessageBuilder getMessageBuilder() {
            return messageBuilder;
        }

        /**
         *
         * @param failureAction
         * @return
         */
        public Builder setFailureAction(Consumer<? super Throwable> failureAction) {
            this.failureAction = failureAction;
            return this;
        }

        /**
         *
         * @param successAction
         * @return
         */
        public Builder setSuccessAction(Consumer<? super Message> successAction) {
            this.successAction = successAction;
            return this;
        }

        /**
         *
         * @param actionConfig
         * @return
         */
        public Builder setActionConfig(@Nonnull Consumer<MessageAction> actionConfig) {
            Checks.notNull(actionConfig, "actionConfig");

            this.actionConfig = actionConfig;
            return this;
        }

        /**
         * Builds the message config and returns it
         *
         * @return a message config instance
         */
        public MessageConfig build() {
            if (this.channel == null) {
                throw new IllegalArgumentException("No text channel has been set, set this with setChannel");
            }

            // we can send messages with just an embed
            if (this.messageBuilder.isEmpty() && this.embed == null) {
                throw new IllegalArgumentException("This message has no content, please add some content with setMessage or setEmbed");
            }

            return new MessageConfig(
                this.channel,
                this.messageBuilder,
                this.embed,
                this.failureAction,
                this.successAction,
                this.actionConfig
            );
        }

        /**
         * Creates a config builder instance from a command context
         *
         * @param ctx a command context instance to get the text channel from
         *
         * @return A builder instance that was created from a command context
         *
         * @see me.duncte123.botcommons.commands.DefaultCommandContext
         */
        public static Builder fromCtx(ICommandContext ctx) {
            return new Builder().setChannel(ctx.getChannel());
        }

        /**
         * Creates a config builder instance from a JDA guild message received event
         *
         * @param event A {@link GuildMessageReceivedEvent} from JDA to get the text channel from
         *
         * @return A builder instance that was created from a {@link GuildMessageReceivedEvent}
         */
        public static Builder fromEvent(GuildMessageReceivedEvent event) {
            return new Builder().setChannel(event.getChannel());
        }
    }
}
