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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.util.PendingRequestBuilder;
import com.github.natanbc.reliqua.util.ResponseMapper;
import me.duncte123.botcommons.CommonsInfo;
import me.duncte123.botcommons.StringUtils;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
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
        return prepareGet(url, EncodingType.TEXT_HTML).build(
            (response) -> Jsoup.parse(response.body().string()),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<ObjectNode> getJSONObject(String url) {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
            (res) -> WebParserUtils.toJSONObject(res, mapper),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<ArrayNode> getJSONArray(String url) {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
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

    public PendingRequestBuilder prepareGet(String url, EncodingType accept) {
        return createRequest(defaultRequest()
            .url(url)
            .addHeader("Accept", accept.getType()));
    }

    public PendingRequestBuilder prepareGet(String url) {
        return prepareGet(url, EncodingType.TEXT_HTML);
    }

    public PendingRequest<String> preparePost(String url, Map<String, Object> postFields) {
        return preparePost(url, postFields, EncodingType.APPLICATION_URLENCODED).build(
            (response) -> response.body().string(),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<String> preparePost(String url, EncodingType accept) {
        return preparePost(url, new HashMap<>(), accept).build(
            (response) -> response.body().string(),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<String> preparePost(String url) {
        return preparePost(url, new HashMap<>(), EncodingType.APPLICATION_URLENCODED).build(
            (response) -> response.body().string(),
            WebParserUtils::handleError
        );
    }

    public PendingRequestBuilder preparePost(String url, Map<String, Object> postFields, EncodingType accept) {
        final StringBuilder postParams = new StringBuilder();

        for (final Map.Entry<String, Object> entry : postFields.entrySet()) {
            postParams.append(entry.getKey()).append("=").append(urlEncode(String.valueOf(entry.getValue()))).append("&");
        }

        return createRequest(defaultRequest()
            .url(url)
            .post(
                RequestBody.create(
                    EncodingType.APPLICATION_URLENCODED.toMediaType(),
                    StringUtils.replaceLast(postParams.toString(), "\\&", "")
                )
            )
            .addHeader("Accept", accept.getType()));
    }

    public <T> PendingRequest<T> postJSON(String url, JsonNode data, ResponseMapper<T> responseMapper) {
        try {
            return postJSON(url, mapper.writeValueAsString(data), responseMapper);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return null;
        }
    }

    public <T> PendingRequest<T> postJSON(String url, String data, ResponseMapper<T> mapper) {
        return createRequest(defaultRequest()
            .url(url)
            .post(RequestBody.create(EncodingType.APPLICATION_JSON.toMediaType(), data)))
            .build(
                mapper,
                WebParserUtils::handleError
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

        return postJSON(
            "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + apiKey,
            json,
            (r) -> toJSONObject(r, mapper).get("shortLink").asText()
        );
    }

    public PendingRequest<String> shortenUrl(String url, String apiKey) {
        return shortenUrl(url, "lnk.dunctebot.com", apiKey);
    }

    public PendingRequest<String> shortenUrl(String url, String domain, String apiKey) {
        return shortenUrl(url, domain, apiKey, GoogleLinkLength.SHORT);
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

    private String urlEncode(String input) {
        try {
            // We're on java 8 intellij
            //noinspection CharsetObjectCanBeUsed
            return URLEncoder.encode(input, "UTF-8");
        }
        catch (UnsupportedEncodingException ignored) {
            return ""; // Should never happen as we are using UTF-8
        }
    }

    public enum EncodingType {
        APPLICATION_JSON("application/json"),
        APPLICATION_XML("application/xml"),
        APPLICATION_URLENCODED("application/x-www-form-urlencoded"),
        TEXT_PLAIN("text/plain"),
        TEXT_HTML("text/html"),
        APPLICATION_OCTET_STREAM("application/octet-stream");

        private String type;

        EncodingType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public MediaType toMediaType() {
            return MediaType.parse(type);
        }
    }
}
