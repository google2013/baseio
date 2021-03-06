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
package com.generallycloud.nio.connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.LoggerUtil;
import com.generallycloud.nio.component.AioSocketChannel;
import com.generallycloud.nio.component.AioSocketChannelContext;
import com.generallycloud.nio.component.CachedAioThread;
import com.generallycloud.nio.component.UnsafeSocketSession;

/**
 * @author wangkai
 *
 */
public class AioSocketChannelConnector extends AbstractSocketChannelConnector {

	private AioSocketChannelContext context;

	public AioSocketChannelConnector(AioSocketChannelContext context) {
		this.context = context;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void connect(InetSocketAddress socketAddress) throws IOException {

		AsynchronousChannelGroup group = context.getAsynchronousChannelGroup();

		AsynchronousSocketChannel _channel = AsynchronousSocketChannel.open(group);

		_channel.connect(socketAddress, this,
				new CompletionHandler<Void, AioSocketChannelConnector>() {

					@Override
					public void completed(Void result, AioSocketChannelConnector connector) {

						CachedAioThread aioThread = (CachedAioThread) Thread.currentThread();

						AioSocketChannel channel = new AioSocketChannel(aioThread, _channel);

						connector.finishConnect(channel.getSession(), null);

						aioThread.getReadCompletionHandler().completed(0, channel);
					}

					@Override
					public void failed(Throwable exc, AioSocketChannelConnector connector) {
						connector.finishConnect(session, exc);
					}
				});

		wait4connect();
	}

	protected void finishConnect(UnsafeSocketSession session, Throwable exception) {

		if (exception == null) {

			this.session = session;

			LoggerUtil.prettyNIOServerLog(logger, "connected to server @{}", getServerSocketAddress());

			fireSessionOpend();

			this.waiter.setPayload(null);

			if (waiter.isTimeouted()) {
				CloseUtil.close(this);
			}
		} else {

			this.waiter.setPayload(exception);
		}
	}

	@Override
	public AioSocketChannelContext getContext() {
		return context;
	}

	@Override
	protected void destroyService() {
	}

}