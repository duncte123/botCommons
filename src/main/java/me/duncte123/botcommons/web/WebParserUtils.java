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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.natanbc.reliqua.request.RequestContext;
import com.github.natanbc.reliqua.request.RequestException;
import me.duncte123.botcommons.JSONHelper;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class WebParserUtils {
    // Only null when invalid json is found
    @Nullable
    public static ObjectNode toJSONObject(Response response) throws IOException {
        return toJSONObject(response, JSONHelper.createObjectMapper());
    }

    // Only null when invalid json is found
    @Nullable
    public static ObjectNode toJSONObject(Response response, ObjectMapper mapper) throws IOException {
        return (ObjectNode) mapper.readTree(getInputStream(response));
    }

    public static InputStream getInputStream(Response response) {
        final ResponseBody body = response.body();

        if (body == null) {
            throw new IllegalStateException("Body should never be null");
        }

        final String encoding = response.header("Content-Encoding");

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
        final Response response = context.getResponse();
        final ResponseBody body = response.body();

        if (body == null) {
            context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + " (No body)", context.getCallStack()));
            return;
        }

        JsonNode json = null;

        try {
            json = toJSONObject(response);
        } catch (Exception ignored) {
        }

        if (json != null) {
            context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + ": " + json, context.getCallStack()));
        } else {
            context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code(), context.getCallStack()));
        }
    }
}
