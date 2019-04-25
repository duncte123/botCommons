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
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebTest {

    @Test
    public void testWebUtilsCanSetUserAgentAndWillSendCorrectUserAgent() {
        String userAgent = "Mozilla/5.0 botCommons test";

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(new JSONObject().put("data", new JSONObject().put("user-agent", userAgent)).toString())
        );

        WebUtils.setUserAgent(userAgent);

        assertEquals(userAgent, WebUtils.getUserAgent());

        HttpUrl baseUrl = server.url("/user-agent");

        JSONObject json = WebUtils.ins.getJSONObject(baseUrl.toString()).execute();

        assertEquals(userAgent, json.getJSONObject("data").getString("user-agent"));
    }

    @Test
    public void testAsyncWebRequest() {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(new JSONObject().put("data", new JSONObject().put("file", "Hi there")).toString())
        );

        HttpUrl baseUrl = server.url("/llama");

        WebUtils.ins.getJSONObject(baseUrl.toString())
                .async(
                        json -> assertNotNull(json.getJSONObject("data").getString("file"))
                );
    }

}
