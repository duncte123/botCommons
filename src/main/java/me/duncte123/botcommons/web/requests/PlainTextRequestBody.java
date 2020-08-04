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

import me.duncte123.botcommons.web.ContentType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

public class PlainTextRequestBody implements IRequestBody {
    private final StringBuilder body = new StringBuilder();

    @Override
    public @NotNull ContentType getContentType() {
        return ContentType.TEXT_PLAIN;
    }

    public PlainTextRequestBody setContent(@NotNull String content) {
        this.body.setLength(0);
        this.body.append(content);

        return this;
    }

    public PlainTextRequestBody appendContent(@NotNull String content) {
        this.body.append(content);

        return this;
    }

    public StringBuilder getBuilder() {
        return body;
    }

    @Override
    public @NotNull RequestBody toRequestBody() {
        return RequestBody.create(null, this.body.toString().getBytes());
    }
}
