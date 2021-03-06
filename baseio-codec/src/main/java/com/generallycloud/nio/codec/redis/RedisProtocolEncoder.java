/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package com.generallycloud.nio.codec.redis;

import java.io.IOException;

import com.generallycloud.nio.buffer.ByteBuf;
import com.generallycloud.nio.buffer.ByteBufAllocator;
import com.generallycloud.nio.codec.redis.future.RedisReadFuture;
import com.generallycloud.nio.component.BufferedOutputStream;
import com.generallycloud.nio.protocol.ChannelReadFuture;
import com.generallycloud.nio.protocol.ChannelWriteFuture;
import com.generallycloud.nio.protocol.ChannelWriteFutureImpl;
import com.generallycloud.nio.protocol.ProtocolEncoder;

public class RedisProtocolEncoder implements ProtocolEncoder {

	@Override
	public ChannelWriteFuture encode(ByteBufAllocator allocator, ChannelReadFuture future) throws IOException {
		
		RedisReadFuture f = (RedisReadFuture) future;

		BufferedOutputStream os = f.getBufferedOutputStream();
		
		int size = os.size();
		
		if (size == 0) {
			throw new IOException("null write text");
		}

		ByteBuf buf = allocator.allocate(size);

		buf.put(os.array(), 0, size);

		return new ChannelWriteFutureImpl(future, buf.flip());
		
	}
	
}
