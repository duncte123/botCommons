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

package me.duncte123.botcommons.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.util.PendingRequestBuilder;
import com.github.natanbc.reliqua.util.ResponseMapper;
import me.duncte123.botcommons.CommonsInfo;
import me.duncte123.botcommons.web.requests.IRequestBody;
import me.duncte123.botcommons.web.requests.JSONRequestBody;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static me.duncte123.botcommons.web.WebParserUtils.toJSONObject;


@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public final class WebUtils extends Reliqua {

    public static final WebUtils ins = new WebUtils();
    private static String USER_AGENT = "Mozilla/5.0 (compatible; BotCommons/" + CommonsInfo.VERSION + "; +https://github.com/duncte123/BotCommons;)";
    private final ObjectMapper mapper = new ObjectMapper();

    private WebUtils() {
        super(
            new OkHttpClient.Builder()
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .build()
        );
    }

    public PendingRequest<String> getText(String url) {
        return prepareGet(url).build(
            (response) -> response.body().string(),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<Document> scrapeWebPage(String url) {
        return prepareGet(url, ContentType.TEXT_HTML).build(
            (response) -> Jsoup.parse(response.body().string()),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<ObjectNode> getJSONObject(String url) {
        return prepareGet(url, ContentType.JSON).build(
            (res) -> WebParserUtils.toJSONObject(res, mapper),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<ArrayNode> getJSONArray(String url) {
        return prepareGet(url, ContentType.JSON).build(
            (res) -> (ArrayNode) mapper.readTree(WebParserUtils.getInputStream(res)),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<InputStream> getInputStream(String url) {
        return prepareGet(url).build(
            WebParserUtils::getInputStream,
            WebParserUtils::handleError
        );
    }

    public PendingRequest<byte[]> getByteStream(String url) {
        return prepareGet(url).build(
            (res) -> IOUtil.readFully(WebParserUtils.getInputStream(res)),
            WebParserUtils::handleError
        );
    }

    public PendingRequestBuilder prepareGet(String url, ContentType accept) {
        return createRequest(defaultRequest()
            .url(url)
            .addHeader("Accept", accept.getType()));
    }

    public PendingRequestBuilder prepareGet(String url) {
        return prepareGet(url, ContentType.ANY);
    }

    public PendingRequestBuilder postRequest(String url, IRequestBody body) {
        return createRequest(
            defaultRequest()
                .url(url)
                .header("content-Type", body.getContentType())
                .post(body.toRequestBody())
        );
    }

    public ArrayNode translate(String sourceLang, String targetLang, String input) {
        return (ArrayNode) getJSONArray(
            "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + input
        )
            .execute()
            .get(0)
            .get(0);
    }

    public PendingRequest<String> shortenUrl(String url, String domain, String apiKey, GoogleLinkLength linkLength) {
        final ObjectNode json = mapper.createObjectNode();

        json.set("dynamicLinkInfo",
            mapper.createObjectNode()
                .put("domainUriPrefix", domain)
                .put("link", url)
        );
        json.set("suffix",
            mapper.createObjectNode()
                .put("option", linkLength.name())
        );

        try {
            return postRequest(
                "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + apiKey,
                JSONRequestBody.fromJackson(json)
            )
                .build(
                    (r) -> toJSONObject(r, mapper).get("shortLink").asText(),
                    WebParserUtils::handleError
                );
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return null;
        }
    }

    public <T> PendingRequest<T> prepareRaw(Request request, ResponseMapper<T> mapper) {
        return createRequest(request).build(mapper, WebParserUtils::handleError);
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }

    public static void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }

    public static Request.Builder defaultRequest() {
        return new Request.Builder()
            .get()
            .addHeader("User-Agent", USER_AGENT)
            .addHeader("cache-control", "no-cache");
    }

    public static String urlEncodeString(String input) {
        try {
            // We're on java 8 intellij
            //noinspection CharsetObjectCanBeUsed
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return ""; // Should never happen as we are using UTF-8
        }
    }
}
