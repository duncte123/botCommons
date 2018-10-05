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

import com.github.natanbc.reliqua.request.RequestContext;
import com.github.natanbc.reliqua.request.RequestException;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

@SuppressWarnings("WeakerAccess")
public class WebUtilsErrorUtils {
    public static JSONObject toJSONObject(Response response) {
        return new JSONObject(new JSONTokener(getInputStream(response)));
    }

    public static InputStream getInputStream(Response response) {
        ResponseBody body = response.body();
        if (body == null) throw new IllegalStateException("Body should never be null");
        String encoding = response.header("Content-Encoding");
        if (encoding != null) {
            switch (encoding.toLowerCase()) {
                case "gzip":
                    try {
                        return new GZIPInputStream(body.byteStream());
                    } catch (IOException e) {
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
        if (body == null) {
            context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + " (No body)", context.getCallStack()));
            return;
        }
        switch (response.code()) {
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
                } catch (JSONException ignored) {
                }
                if (json != null) {
                    context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + ": " + json.getString("message"), context.getCallStack()));
                } else {
                    context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code(), context.getCallStack()));
                }
        }
    }
}
