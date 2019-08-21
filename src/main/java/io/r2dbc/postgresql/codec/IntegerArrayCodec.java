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
import reactor.core.publisher.Flux;

import static io.r2dbc.postgresql.message.Format.FORMAT_TEXT;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.INT4_ARRAY;

final class IntegerArrayCodec extends AbstractArrayCodec<Integer> {

    IntegerArrayCodec(ByteBufAllocator byteBufAllocator) {
        super(byteBufAllocator, Integer.class);
    }

    @Override
    public Parameter encodeNull() {
        return createNull(FORMAT_TEXT, INT4_ARRAY);
    }

    @Override
    Integer decodeItem(ByteBuf byteBuf) {
        return byteBuf.readInt();
    }

    @Override
    Integer decodeItem(String strValue) {
        return Integer.parseInt(strValue);
    }

    @Override
    boolean doCanDecode(Format format, PostgresqlObjectId type) {
        Assert.requireNonNull(type, "type must not be null");

        return INT4_ARRAY == type;
    }

    @Override
    Parameter encodeArray(ByteBuf byteBuf) {
        Assert.requireNonNull(byteBuf, "byteBuf must not be null");

        return create(FORMAT_TEXT, INT4_ARRAY, Flux.just(byteBuf));
    }

    @Override
    String encodeItem(Integer value) {
        Assert.requireNonNull(value, "value must not be null");

        return value.toString();
    }

}