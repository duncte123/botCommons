/*
 *    Copyright 2019 Duncan "duncte123" Sterken
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

package me.duncte123.botcommons.commands;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Dummy class that holds the basics for a command context
 */
public interface ICommandContext {

    /**
     * Returns the {@link net.dv8tion.jda.api.entities.Guild} for the current command/event
     *
     * @return the {@link net.dv8tion.jda.api.entities.Guild} for this command/event
     */
    Guild getGuild();

    /**
     * Returns the {@link net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent message event} that was received for this instance
     *
     * @return the {@link net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent message event} that was received for this instance
     */
    GuildMessageReceivedEvent getEvent();

    /**
     * Returns the {@link net.dv8tion.jda.api.entities.TextChannel channel} that the message for this event was send in
     *
     * @return the {@link net.dv8tion.jda.api.entities.TextChannel channel} that the message for this event was send in
     */
    default TextChannel getChannel() {
        return this.getEvent().getChannel();
    }

    /**
     * Returns the {@link net.dv8tion.jda.api.entities.Message message} that triggered this event
     *
     * @return the {@link net.dv8tion.jda.api.entities.Message message} that triggered this event
     */
    default Message getMessage() {
        return this.getEvent().getMessage();
    }

    /**
     * Returns the {@link net.dv8tion.jda.api.entities.User author} of the message as user
     *
     * @return the {@link net.dv8tion.jda.api.entities.User author} of the message as user
     */
    default User getAuthor() {
        return this.getEvent().getAuthor();
    }
    /**
     * Returns the {@link net.dv8tion.jda.api.entities.Member author} of the message as member
     *
     * @return the {@link net.dv8tion.jda.api.entities.Member author} of the message as member
     */
    default Member getMember() {
        return this.getEvent().getMember();
    }

    /**
     * Returns the current {@link net.dv8tion.jda.api.JDA jda} instance
     *
     * @return the current {@link net.dv8tion.jda.api.JDA jda} instance
     */
    default JDA getJDA() {
        return this.getEvent().getJDA();
    }

    /**
     * Returns the current {@link net.dv8tion.jda.api.sharding.ShardManager} instance
     *
     * @return the current {@link net.dv8tion.jda.api.sharding.ShardManager} instance
     */
    default ShardManager getShardManager() {
        return this.getJDA().getShardManager();
    }

    /**
     * Returns the {@link net.dv8tion.jda.api.entities.User user} for the currently logged in account
     *
     * @return the {@link net.dv8tion.jda.api.entities.User user} for the currently logged in account
     */
    default User getSelfUser() {
        return this.getJDA().getSelfUser();
    }

    /**
     * Returns the {@link net.dv8tion.jda.api.entities.Member member} in the guild for the currently logged in account
     *
     * @return the {@link net.dv8tion.jda.api.entities.Member member} in the guild for the currently logged in account
     */
    default Member getSelfMember() {
        return this.getGuild().getSelfMember();
    }

}
