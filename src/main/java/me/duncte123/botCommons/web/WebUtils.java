package me.duncte123.botCommons.web;

import com.afollestad.ason.Ason;
import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.request.RequestContext;
import com.github.natanbc.reliqua.request.RequestException;
import com.github.natanbc.reliqua.util.PendingRequestBuilder;
import com.github.natanbc.reliqua.util.ResponseMapper;
import me.duncte123.botCommons.BuildConfig;
import me.duncte123.botCommons.config.Config;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;


@SuppressWarnings({"unused", "WeakerAccess", "ConstantConditions"})
public final class WebUtils extends Reliqua {

    public static final WebUtils ins = new WebUtils();
    private static final String USER_AGENT = "Mozilla/5.0 (compatible; BotCommons/" + BuildConfig.VERSION + "; +https://github.com/duncte123/BotCommons;)";

    private WebUtils() {
        super(new OkHttpClient());
    }

    public PendingRequest<String> getText(String url) throws NullPointerException {
        return prepareGet(url).build(
                (response) -> response.body().string(),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<JSONObject> getJSONObject(String url) throws NullPointerException {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                (response) -> new JSONObject(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<JSONArray> getJSONArray(String url) throws NullPointerException {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                (response) -> new JSONArray(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<Ason> getAson(String url) throws NullPointerException {
        return prepareGet(url, EncodingType.APPLICATION_JSON).build(
                (response) -> new Ason(response.body().string()),
                WebUtilsErrorUtils::handleError
        );
    }

    public PendingRequest<InputStream> getInputStream(String url) throws NullPointerException {
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

    public JSONArray translate(String sourceLang, String targetLang, String input) throws NullPointerException {
        return getJSONArray(
                "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + input
        ).execute().getJSONArray(0).getJSONArray(0);
    }

    public PendingRequest<String> shortenUrl(String url, String apiKey) throws NullPointerException {
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

    public PendingRequest<String> leeks(String data) throws NullPointerException {
        Service leeks = Service.LEEKS;
        return postRawToService(leeks, data,
                (r) -> leeks.url + new JSONObject(r.body().string()).getString("key") + ".kt");
    }

    public PendingRequest<String> hastebin(String data) throws NullPointerException {
        Service hastebin = Service.HASTEBIN;
        return postRawToService(hastebin, data,
                (r) -> hastebin.url + new JSONObject(r.body().string()).getString("key") + ".kt");
    }

    public PendingRequest<String> wastebin(String data) throws NullPointerException {
        Service wastebin = Service.WASTEBIN;
        return postRawToService(wastebin, data,
                (r) -> wastebin.url + new JSONObject(r.body().string()).getString("key") + ".kt");
    }

    public enum EncodingType {
        TEXT_PLAIN("text/plain"),
        APPLICATION_JSON("application/json"),
        TEXT_HTML("text/html"),
        APPLICATION_XML("application/xml"),
        APPLICATION_URLENCODED("application/x-www-form-urlencoded");

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

    private static class WebUtilsErrorUtils {
        public static JSONObject toJSONObject(Response response) {
            return new JSONObject(new JSONTokener(getInputStream(response)));
        }

        public static InputStream getInputStream(Response response) {
            ResponseBody body = response.body();
            if(body == null) throw new IllegalStateException("Body should never be null");
            String encoding = response.header("Content-Encoding");
            if (encoding != null) {
                switch(encoding.toLowerCase()) {
                    case "gzip":
                        try {
                            return new GZIPInputStream(body.byteStream());
                        } catch(IOException e) {
                            throw new IllegalStateException("Received Content-Encoding header of gzip, but data is not valid gzip", e);
                        }
                    case "deflate":
                        return new InflaterInputStream(body.byteStream());
                }
            }
            return body.byteStream();
        }

        public static <T> void handleError(RequestContext<T> context) {
            Response response = context.getResponse();
            ResponseBody body = response.body();
            if(body == null) {
                context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + " (No body)", context.getCallStack()));
                return;
            }
            switch(response.code()) {
                case 403:
                    context.getErrorConsumer().accept(new RequestException(toJSONObject(response).getString("message"), context.getCallStack()));
                    break;
                case 404:
                    context.getSuccessConsumer().accept(null);
                    break;
                default:
                    JSONObject json = null;
                    try {
                        json = toJSONObject(response);
                    } catch(JSONException ignored) {}
                    if(json != null) {
                        context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + ": " + json.getString("message"), context.getCallStack()));
                    } else {
                        context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code(), context.getCallStack()));
                    }
            }
        }
    }
}