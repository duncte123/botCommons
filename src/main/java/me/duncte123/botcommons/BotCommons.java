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

package me.duncte123.botcommons;

import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

public class BotCommons {

    public static final String VERSION = "@version@";

    /**
     * Kills all the threads that BotCommons uses internally, allowing your bot to shut own without using System.exit
     *
     * @param manager Your {@link ShardManager ShardManager} instance for killing the threads that JDA does not shut down and keep your bot up
     */
    public static void shutdown(ShardManager manager) {
        manager.getShardCache().forEach((jda) -> {
            jda.getHttpClient().connectionPool().evictAll();
            jda.getHttpClient().dispatcher().executorService().shutdown();
        });
        manager.shutdown();
        shutdown();
    }

    /**
     * Kills all the threads that BotCommons uses internally, allowing your bot to shut own without using System.exit
     *
     * @param jda Your {@link JDA JDA} instance for killing the threads that JDA does not shut down and keep your bot up
     */
    public static void shutdown(JDA jda) {
        jda.getHttpClient().connectionPool().evictAll();
        jda.getHttpClient().dispatcher().executorService().shutdown();
        jda.shutdown();
        shutdown();
    }

    /**
     * Kills all the threads that BotCommons uses internally, allowing your bot to shut own without using System.exit
     */
    public static void shutdown() {
        WebUtils.ins.getClient().connectionPool().evictAll();
        WebUtils.ins.getClient().dispatcher().executorService().shutdown();
    }
}
