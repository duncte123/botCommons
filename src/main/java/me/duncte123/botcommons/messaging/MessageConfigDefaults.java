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

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MessageConfigDefaults {
    public static final Function<Long, MessageConfig.Builder> DELETE_MESSAGE_AFTER_SECONDS = (secs) -> new MessageConfig.Builder().setSuccessAction(
        (message) -> message.delete()
            .reason("automatic remove")
            .queueAfter(secs, TimeUnit.SECONDS)
    );
}
