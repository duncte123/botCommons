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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.duncte123.botcommons.web.WebUtils;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebTest {

    @Test
    public void testWebUtilsCanSetUserAgentAndWillSendCorrectUserAgent() throws JsonProcessingException {
        String userAgent = "Mozilla/5.0 botCommons test";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.set(
            "data",
            mapper.createObjectNode().put("user-agent", userAgent)
        );
        String parsed = mapper.writeValueAsString(body);

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(parsed)
        );

        WebUtils.setUserAgent(userAgent);

        assertEquals(userAgent, WebUtils.getUserAgent());

        HttpUrl baseUrl = server.url("/user-agent");
        ObjectNode json = WebUtils.ins.getJSONObject(baseUrl.toString()).execute();

        assertEquals(userAgent, json.get("data").get("user-agent").asText());
    }

    @Test
    public void testAsyncWebRequest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.set(
            "data",
            mapper.createObjectNode().put("file", "Hi there")
        );
        String parsed = mapper.writeValueAsString(body);

        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(parsed)
        );

        HttpUrl baseUrl = server.url("/llama");

        WebUtils.ins.getJSONObject(baseUrl.toString())
            .async(
                json -> assertNotNull(json.get("data").get("file").asText())
            );
    }

}
