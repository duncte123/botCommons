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

import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class MessageConfigDefaults {
    public static final BiConsumer<? super Message, Long> DELETE_MESSAGE_AFTER_SECONDS = (message, secs) -> message.delete()
        .reason("automatic remove")
        .queueAfter(secs, TimeUnit.SECONDS);
}
