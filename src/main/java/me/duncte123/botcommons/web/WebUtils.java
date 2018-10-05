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

import com.afollestad.ason.Ason;
import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.util.PendingRequestBuilder;
import com.github.natanbc.reliqua.util.ResponseMapper;
import me.duncte123.botcommons.CommonsInfo;
import me.duncte123.botcommons.StringUtils;
import net.dv8tion.jda.core.utils.IOUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static me.duncte123.botcommons.web.WebUtilsErrorUtils.toJSONObject;


@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public final class WebUtils extends Reliqua {

    public static final WebUtils ins = new WebUtils();
    private static String USER_AGENT = "Mozilla/5.0 (compatible; BotCommons/" + CommonsInfo.VERSION + "; +https://github.com/duncte123/BotCommons;)";

    private WebUtils() {
        super(new OkHttpClient());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> getClient().connectionPool().evictAll()));
    }

    public PendingRequest<String> getText(String url) {
        return prepareGet(url).build(
                (response) -> response.body().string(),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<Document> scrapeWebPage(String url) {
        return prepareGet(url, EncodingType.TEXT_HTML).build(
                (response) -> Jsoup.parse(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<JSONObject> getJSONObject(String url) {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                WebUtilsErrorUtils::toJSONObject,
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<JSONArray> getJSONArray(String url) {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                (response) -> new JSONArray(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<InputStream> getInputStream(String url) {
        return prepareGet(url).build(
                (response) -> response.body().byteStream(),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<byte[]> getByteStream(String url) {
        return prepareGet(url).build(
            (response) -> IOUtil.readFully(response.body().byteStream()),
            WebUtilsErrorUtils::handleError
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
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<String> preparePost(String url, EncodingType accept) {
        return preparePost(url, new HashMap<>(), accept).build(
                (response) -> response.body().string(),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<String> preparePost(String url) {
        return preparePost(url, new HashMap<>(), EncodingType.APPLICATION_URLENCODED).build(
                (response) -> response.body().string(),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequestBuilder preparePost(String url, Map<String, Object> postFields, EncodingType accept) {
        StringBuilder postParams = new StringBuilder();

        for (Map.Entry<String, Object> entry : postFields.entrySet()) {
            postParams.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        return createRequest(defaultRequest()
                .url(url)
                .post(RequestBody.create(EncodingType.APPLICATION_URLENCODED.toMediaType(),
                        StringUtils.replaceLast(postParams.toString(), "\\&", "")))
                .addHeader("Accept", accept.getType()));
    }

    public <T> PendingRequest<T> postJSON(String url, JSONObject data, ResponseMapper<T> mapper) {
        return createRequest(defaultRequest()
                .url(url)
                .post(RequestBody.create(EncodingType.APPLICATION_JSON.toMediaType(), data.toString())))
                .build(
                        mapper,
                        WebUtilsErrorUtils::handleError
                );
    }

    public JSONArray translate(String sourceLang, String targetLang, String input) {
        return getJSONArray(
                "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + input
        ).execute().getJSONArray(0).getJSONArray(0);
    }

    public PendingRequest<String> shortenUrl(String url, String apiKey) {
        return postJSON(
                "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" +
                        apiKey,
                new JSONObject()
                        .put("dynamicLinkInfo", new JSONObject()
                                .put("dynamicLinkDomain", "g57v2.app.goo.gl").put("link", url))
                        .put("suffix", new JSONObject("{\"option\": \"UNGUESSABLE\"}"))
                ,
                (r) -> toJSONObject(r).getString("shortLink"));
    }

    public <T> PendingRequest<T> prepareRaw(Request request, ResponseMapper<T> mapper) {
        return createRequest(request).build(mapper, WebUtilsErrorUtils::handleError);
    }

    private PendingRequest<String> postRawToService(Service s, String raw) {
        return createRequest(defaultRequest()
                .post(RequestBody.create(EncodingType.TEXT_PLAIN.toMediaType(), raw))
                .url(s.url + "documents")).build(
                (r) -> s.url + toJSONObject(r).getString("key") + ".kt"
                , WebUtilsErrorUtils::handleError);
    }

    public PendingRequest<String> leeks(String data) {
        return postRawToService(Service.LEEKS, data);
    }

    public PendingRequest<String> hastebin(String data) {
        return postRawToService(Service.HASTEBIN, data);
    }

    public PendingRequest<String> wastebin(String data) {
        return postRawToService(Service.WASTEBIN, data);
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

    public enum Service {
        //        HASTEBIN("https://hastebin.com/"),
        HASTEBIN("https://hasteb.in/"),
        WASTEBIN("https://wastebin.party/"),
        LEEKS("https://haste.leeksapp.com/");

        private final String url;

        Service(String u) {
            this.url = u;
        }

        public String getUrl() {
            return url;
        }
    }

}
