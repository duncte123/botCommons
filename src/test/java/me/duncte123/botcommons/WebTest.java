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

package me.duncte123.botcommons;

import me.duncte123.botcommons.web.WebUtils;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebTest {

    @Test
    public void testWebUtilsCanSetUserAgentAndWillSendCorrectUserAgent() {

        String userAgent = "Mozilla/5.0 botCommons test";

        WebUtils.setUserAgent(userAgent);

        assertEquals(userAgent, WebUtils.getUserAgent());

        JSONObject json = WebUtils.ins.getJSONObject("https://apis.duncte123.me/user-agent").execute();

        assertEquals(userAgent, json.getString("user-agent"));
    }

    @Test
    public void testAsyncWebRequest() {
        WebUtils.ins.getJSONObject("https://apis.duncte123.me/llama")
                .async(
                        json -> assertNotNull(json.getString("file"))
                );
    }

}
