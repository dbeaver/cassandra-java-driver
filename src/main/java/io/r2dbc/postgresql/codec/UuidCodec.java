/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.postgresql.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.postgresql.client.Parameter;
import io.r2dbc.postgresql.message.Format;
import io.r2dbc.postgresql.type.PostgresqlObjectId;
import io.r2dbc.postgresql.util.Assert;
import io.r2dbc.postgresql.util.ByteBufUtils;
import reactor.core.publisher.Flux;
import reactor.util.annotation.Nullable;

import java.util.UUID;

import static io.r2dbc.postgresql.message.Format.FORMAT_BINARY;
import static io.r2dbc.postgresql.message.Format.FORMAT_TEXT;

final class UuidCodec extends AbstractCodec<UUID> {

    private final ByteBufAllocator byteBufAllocator;

    UuidCodec(ByteBufAllocator byteBufAllocator) {
        super(UUID.class);
        this.byteBufAllocator = Assert.requireNonNull(byteBufAllocator, "byteBufAllocator must not be null");
    }

    @Override
    public Parameter doEncode(UUID value) {
        Assert.requireNonNull(value, "value must not be null");

        ByteBuf encoded = ByteBufUtils.encode(this.byteBufAllocator, value.toString());
        return create(FORMAT_TEXT, PostgresqlObjectId.UUID, Flux.just(encoded));
    }

    @Override
    public Parameter encodeNull() {
        return createNull(FORMAT_TEXT, PostgresqlObjectId.UUID);
    }

    @Override
    boolean doCanDecode(Format format, PostgresqlObjectId type) {
        Assert.requireNonNull(format, "format must not be null");
        Assert.requireNonNull(type, "type must not be null");

        return PostgresqlObjectId.UUID == type;
    }

    @Override
    UUID doDecode(ByteBuf byteBuf, @Nullable Format format, @Nullable Class<? extends UUID> type) {
        Assert.requireNonNull(byteBuf, "byteBuf must not be null");

        if (format == FORMAT_BINARY) {
            return new UUID(byteBuf.readLong(), byteBuf.readLong());
        }

        String str = ByteBufUtils.decode(byteBuf);
        return UUID.fromString(str);
    }

}