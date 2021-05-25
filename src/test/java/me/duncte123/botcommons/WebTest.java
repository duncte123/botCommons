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
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.util.StatusCodeValidator;
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

        System.out.println("Before");
        WebUtils.ins.getJSONObject(baseUrl.toString())
            .async(json -> {
                    assertNotNull(json.get("data").get("file").asText());
                    System.out.println("During");
            });
        System.out.println("After");
    }

    @Test
    public void testPendingRequestFunction() { // Not that I expect it to go wrong
        final PendingRequest<ObjectNode> pendingRequest = WebUtils.ins.getJSONObject("https://example.com/",
            (b) -> b.setStatusCodeValidator(StatusCodeValidator.ACCEPT_2XX)
        );

        assertEquals(StatusCodeValidator.ACCEPT_2XX, pendingRequest.getStatusCodeValidator());
    }

    @Test
    public void testRateLimiting() {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("X-RateLimit-Remaining", 1)
            .addHeader("X-RateLimit-Limit", 1)
            .addHeader("X-RateLimit-Reset-After", 5)
            .setBody("My cool body")
        );

        HttpUrl urlOne = server.url("/bla-request-one");
        final String s1 = WebUtils.ins.getText(urlOne.toString()).execute();

        System.out.println(s1);
        assertEquals("My cool body", s1);

        server.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("X-RateLimit-Remaining", 1)
            .addHeader("X-RateLimit-Limit", 1)
            .addHeader("X-RateLimit-Reset-After", 5)
            .setBody("My cool body 2")
        );

        final double curr = Math.floor(System.currentTimeMillis() / 1000D);

        final String s2 = WebUtils.ins.getText(urlOne.toString()).execute();

        System.out.println(s2);
        assertEquals("My cool body 2", s2);

        final double now = Math.floor(System.currentTimeMillis() / 1000D);

        // should have waited for 5 seconds
        assertEquals(5D, now - curr, 0.5D);
    }

}
