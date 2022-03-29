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

package me.duncte123.botcommons.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.List;

/**
 * Provides a basic command context that should be sufficient for most bots
 */
public class DefaultCommandContext implements ICommandContext {
    private final MessageReceivedEvent event;
    private final List<String> args;

    public DefaultCommandContext(List<String> args, MessageReceivedEvent event) {
        Checks.notNull(event, "event");
        Checks.notNull(args, "args");

        this.args = args;
        this.event = event;
    }

    public List<String> getArgs() {
        return this.args;
    }

    @Override
    public MessageReceivedEvent getEvent() {
        return this.event;
    }
}
