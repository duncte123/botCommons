/*
 *    Copyright 2019 Duncan "duncte123" Sterken
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

package me.duncte123.botcommons.web.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.web.ContentType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

public class JSONRequestBody implements IRequestBody {

    private final byte[] json;

    private JSONRequestBody(byte[] json) {
        this.json = json;
    }

    public static JSONRequestBody fromJSONObject(@NotNull org.json.JSONObject jsonObject) {
        return new JSONRequestBody(jsonObject.toString().getBytes());
    }

    public static JSONRequestBody fromJSONArray(@NotNull org.json.JSONArray jsonObject) {
        return new JSONRequestBody(jsonObject.toString().getBytes());
    }

    public static JSONRequestBody fromJackson(@NotNull JsonNode jsonNode) throws JsonProcessingException {
        return new JSONRequestBody(new ObjectMapper().writeValueAsBytes(jsonNode));
    }

    @Override
    public @NotNull String getContentType() {
        return ContentType.JSON.getType();
    }

    @Override
    public @NotNull RequestBody toRequestBody() {
        return RequestBody.create(json);
    }
}
