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

package me.duncte123.botCommons.web;

import com.afollestad.ason.Ason;
import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.util.PendingRequestBuilder;
import com.github.natanbc.reliqua.util.ResponseMapper;
import me.duncte123.botCommons.BuildConfig;
import me.duncte123.botCommons.config.Config;
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


@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public final class WebUtils extends Reliqua {

    public static final WebUtils ins = new WebUtils();
    private static String USER_AGENT = "Mozilla/5.0 (compatible; BotCommons/" + BuildConfig.VERSION + "; +https://github.com/duncte123/BotCommons;)";

    private WebUtils() {
        super(new OkHttpClient());
    }

    public static void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }

    public static String getUserAgent() { return USER_AGENT; }

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
                (response) -> new JSONObject(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<JSONArray> getJSONArray(String url) {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                (response) -> new JSONArray(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<Ason> getAson(String url) {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                (response) -> new Ason(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<InputStream> getInputStream(String url) {
        return prepareGet(url).build(
                (response) -> response.body().byteStream(),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequestBuilder prepareGet(String url, EncodingType accept) {
        return createRequest(
                new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("User-Agent", USER_AGENT)
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

        return createRequest(
                new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(EncodingType.APPLICATION_URLENCODED.toMediaType(),
                                Config.replaceLast(postParams.toString(), "\\&", "")))
                        .addHeader("User-Agent", USER_AGENT)
                        .addHeader("Accept", accept.getType())
                        .addHeader("cache-control", "no-cache"));
    }

    public <T> PendingRequest<T> postJSON(String url, JSONObject data, ResponseMapper<T> mapper) {
        return createRequest(
                new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(EncodingType.APPLICATION_JSON.toMediaType(), data.toString()))
                        .addHeader("User-Agent", USER_AGENT))
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
                (r) -> new JSONObject(r.body().string()).getString("shortLink"));
    }

    public <T> PendingRequest<T> prepareRaw(Request request, ResponseMapper<T> mapper) {
        return createRequest(request).build(mapper, WebUtilsErrorUtils::handleError);
    }

    private <T> PendingRequest<T> postRawToService(Service s, String raw, ResponseMapper<T> mapper) {
        return createRequest(
                new Request.Builder()
                        .post(RequestBody.create(EncodingType.TEXT_PLAIN.toMediaType(), raw))
                        .url(s.url + "documents")).build(mapper, WebUtilsErrorUtils::handleError);
    }

    public PendingRequest<String> leeks(String data) {
        Service leeks = Service.LEEKS;
        return postRawToService(leeks, data,
                (r) -> leeks.url + new JSONObject(r.body().string()).getString("key") + ".kt");
    }

    public PendingRequest<String> hastebin(String data) {
        Service hastebin = Service.HASTEBIN;
        return postRawToService(hastebin, data,
                (r) -> hastebin.url + new JSONObject(r.body().string()).getString("key") + ".kt");
    }

    public PendingRequest<String> wastebin(String data) {
        Service wastebin = Service.WASTEBIN;
        return postRawToService(wastebin, data,
                (r) -> wastebin.url + new JSONObject(r.body().string()).getString("key") + ".kt");
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
        HASTEBIN("https://hastebin.com/"),
        WASTEBIN("https://wastebin.party/"),
        LEEKS("https://haste.leeks.life/");

        private final String url;

        Service(String u) {
            this.url = u;
        }

        public String getUrl() {
            return url;
        }
    }

}