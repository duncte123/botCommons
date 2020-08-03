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

    public TextChannel getChannel() {
        return channel;
    }

    public MessageBuilder getMessageBuilder() {
        return messageBuilder;
    }

    public EmbedBuilder getEmbed() {
        return embed;
    }

    public Consumer<? super Throwable> getFailureAction() {
        return failureAction;
    }

    public Consumer<? super Message> getSuccessAction() {
        return successAction;
    }

    public Consumer<MessageAction> getActionConfig() {
        return actionConfig;
    }

    public static void setNonceSupplier(Function<TextChannel, String> nonceSupplier) {
        Checks.notNull(nonceSupplier, "nonceSupplier");

        MessageConfig.nonceSupplier = nonceSupplier;
    }

    public static class Builder {
        private TextChannel channel;
        private MessageBuilder messageBuilder;
        private EmbedBuilder embed;

        private Consumer<? super Throwable> failureAction = RestAction.getDefaultFailure();
        private Consumer<? super Message> successAction = RestAction.getDefaultSuccess();
        private Consumer<MessageAction> actionConfig = (a) -> {};

        public Builder setChannel(@Nonnull TextChannel channel) {
            Checks.notNull(channel, "channel");

            this.channel = channel;
            return this;
        }

        public Builder setMessage(Message message) {
            this.messageBuilder = new MessageBuilder(message);
            return this;
        }

        public Builder setMessage(String message) {
            this.messageBuilder = new MessageBuilder().append(StringUtils.abbreviate(message, Message.MAX_CONTENT_LENGTH));
            return this;
        }

        public Builder setMessageFormat(String message, Object... args) {
            this.messageBuilder = new MessageBuilder().appendFormat(message, args);
            return this;
        }

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

        public Builder setEmbed(@Nullable EmbedBuilder embed) {
            return this.setEmbed(embed, false);
        }

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

        public MessageBuilder getMessageBuilder() {
            return messageBuilder;
        }

        public Builder setFailureAction(Consumer<? super Throwable> failureAction) {
            this.failureAction = failureAction;
            return this;
        }

        public Builder setSuccessAction(Consumer<? super Message> successAction) {
            this.successAction = successAction;
            return this;
        }

        public Builder setActionConfig(@Nonnull Consumer<MessageAction> actionConfig) {
            Checks.notNull(actionConfig, "actionConfig");

            this.actionConfig = actionConfig;
            return this;
        }

        public MessageConfig build() {
            if (this.channel == null) {
                throw new IllegalArgumentException("No text channel has been set, set this with setChannel");
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

        public static Builder fromCtx(ICommandContext ctx) {
            return new Builder().setChannel(ctx.getChannel());
        }

        public static Builder fromEvent(GuildMessageReceivedEvent event) {
            return new Builder().setChannel(event.getChannel());
        }
    }
}
