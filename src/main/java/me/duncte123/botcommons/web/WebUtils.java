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
import me.duncte123.botcommons.BotCommons;
import me.duncte123.botcommons.JSONHelper;
import me.duncte123.botcommons.web.requests.IRequestBody;
import me.duncte123.botcommons.web.requests.JSONRequestBody;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static me.duncte123.botcommons.web.WebParserUtils.toJSONObject;

@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public final class WebUtils extends Reliqua {

    public static final WebUtils ins = new WebUtils();
    private static String USER_AGENT = "Mozilla/5.0 (compatible; BotCommons/" + BotCommons.VERSION + "; +https://github.com/duncte123/BotCommons;)";
    private final ObjectMapper mapper = JSONHelper.createObjectMapper();

    private WebUtils() {
        super(
            new OkHttpClient.Builder()
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .build(),
            null,
            true
        );
    }

    /**
     * Retrieves text from a webpage
     *
     * @param url
     *     The url to retrieve the text from
     *
     * @return A {@link PendingRequest PendingRequest} that is pending execution via {@link PendingRequest#async()
     * PendingRequest#async()}, {@link PendingRequest#submit() PendingRequest#submit()} or {@link
     * PendingRequest#execute() PendingRequest#execute()}
     *
     * @see #getText(String, PendingRequestFunction)
     * @see #getText(String, PendingRequestFunction, RequestBuilderFunction)
     * @see PendingRequest
     */
    public PendingRequest<String> getText(String url) {
        return getText(url, null);
    }

    public PendingRequest<String> getText(String url, @Nullable PendingRequestFunction pendingBuilder) {
        return getText(url, pendingBuilder, null);
    }

    public PendingRequest<String> getText(String url, @Nullable PendingRequestFunction pendingBuilder, @Nullable RequestBuilderFunction requestBuilder) {
        final Request.Builder builder = prepareGet(url);
        final PendingRequestBuilder pendingRequestBuilder = applyFunctions(builder, pendingBuilder, requestBuilder);

        return pendingRequestBuilder.build(
            (response) -> response.body().string(),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<Document> scrapeWebPage(String url) {
        return scrapeWebPage(url, null);
    }

    public PendingRequest<Document> scrapeWebPage(String url, @Nullable PendingRequestFunction pendingBuilder) {
        return scrapeWebPage(url, pendingBuilder, null);
    }

    public PendingRequest<Document> scrapeWebPage(String url, @Nullable PendingRequestFunction pendingBuilder, @Nullable RequestBuilderFunction requestBuilder) {
        final Request.Builder builder = prepareGet(url, ContentType.TEXT_HTML);
        final PendingRequestBuilder pendingRequestBuilder = applyFunctions(builder, pendingBuilder, requestBuilder);

        return pendingRequestBuilder.build(
            (response) -> Jsoup.parse(response.body().string()),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<ObjectNode> getJSONObject(String url) {
        return getJSONObject(url, null);
    }

    public PendingRequest<ObjectNode> getJSONObject(String url, @Nullable PendingRequestFunction pendingBuilder) {
        return getJSONObject(url, pendingBuilder, null);
    }

    public PendingRequest<ObjectNode> getJSONObject(String url, @Nullable PendingRequestFunction pendingBuilder, @Nullable RequestBuilderFunction requestBuilder) {
        final Request.Builder builder = prepareGet(url, ContentType.JSON);
        final PendingRequestBuilder pendingRequestBuilder = applyFunctions(builder, pendingBuilder, requestBuilder);

        return pendingRequestBuilder.build(
            (res) -> WebParserUtils.toJSONObject(res, mapper),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<ArrayNode> getJSONArray(String url) {
        return getJSONArray(url, null);
    }

    public PendingRequest<ArrayNode> getJSONArray(String url, @Nullable PendingRequestFunction pendingBuilder) {
        return getJSONArray(url, pendingBuilder, null);
    }

    public PendingRequest<ArrayNode> getJSONArray(String url, @Nullable PendingRequestFunction pendingBuilder, @Nullable RequestBuilderFunction requestBuilder) {
        final Request.Builder builder = prepareGet(url, ContentType.JSON);
        final PendingRequestBuilder pendingRequestBuilder = applyFunctions(builder, pendingBuilder, requestBuilder);

        return pendingRequestBuilder.build(
            (res) -> (ArrayNode) mapper.readTree(WebParserUtils.getInputStream(res)),
            WebParserUtils::handleError
        );
    }

    public PendingRequest<InputStream> getInputStream(String url) {
        return getInputStream(url, null);
    }

    public PendingRequest<InputStream> getInputStream(String url, @Nullable PendingRequestFunction pendingBuilder) {
        return getInputStream(url, pendingBuilder, null);
    }

    public PendingRequest<InputStream> getInputStream(String url, @Nullable PendingRequestFunction pendingBuilder, @Nullable RequestBuilderFunction requestBuilder) {
        final Request.Builder builder = prepareGet(url);
        final PendingRequestBuilder pendingRequestBuilder = applyFunctions(builder, pendingBuilder, requestBuilder);

        return pendingRequestBuilder.build(
            WebParserUtils::getInputStream,
            WebParserUtils::handleError
        );
    }

    public PendingRequest<byte[]> getByteStream(String url) {
        return getByteStream(url, null);
    }

    public PendingRequest<byte[]> getByteStream(String url, @Nullable PendingRequestFunction pendingBuilder) {
        return getByteStream(url, pendingBuilder, null);
    }

    public PendingRequest<byte[]> getByteStream(String url, @Nullable PendingRequestFunction pendingBuilder, @Nullable RequestBuilderFunction requestBuilder) {
        final Request.Builder builder = prepareGet(url);
        final PendingRequestBuilder pendingRequestBuilder = applyFunctions(builder, pendingBuilder, requestBuilder);

        return pendingRequestBuilder.build(
            (res) -> IOUtil.readFully(WebParserUtils.getInputStream(res)),
            WebParserUtils::handleError
        );
    }

    public Request.Builder prepareGet(String url) {
        return prepareGet(url, ContentType.ANY);
    }

    public Request.Builder prepareGet(String url, ContentType accept) {
        return
            defaultRequest()
                .url(url)
                .addHeader("Accept", accept.getType());
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

    private PendingRequestBuilder applyFunctions(Request.Builder builder, @Nullable PendingRequestFunction fn1, @Nullable RequestBuilderFunction fn2) {
        if (fn2 != null) {
            builder = fn2.apply(builder);
        }

        PendingRequestBuilder pendingRequestBuilder = createRequest(builder);

        if (fn1 != null) {
            pendingRequestBuilder = fn1.apply(pendingRequestBuilder);
        }

        return pendingRequestBuilder;
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
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return ""; // Should never happen as we are using UTF-8
        }
    }
}
