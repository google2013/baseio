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
package com.generallycloud.nio.container.implementation;

import com.generallycloud.nio.acceptor.ChannelAcceptor;
import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.LoggerUtil;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.container.service.FutureAcceptorService;
import com.generallycloud.nio.protocol.ReadFuture;

public class SystemStopServerServlet extends FutureAcceptorService {

	private Logger logger = LoggerFactory.getLogger(SystemStopServerServlet.class);

	public SystemStopServerServlet() {
		this.setServiceName("/system-stop-server.auth");
	}

	@Override
	public void accept(SocketSession session, ReadFuture future) throws Exception {

		SocketChannelContext context = session.getContext();

		future.write("server is stopping");

		session.flush(future);

		new Thread(new StopServer(context)).start();
	}

	private class StopServer implements Runnable {

		private SocketChannelContext context = null;

		public StopServer(SocketChannelContext context) {
			this.context = context;
		}

		@Override
		public void run() {

			ThreadUtil.sleep(500);

			LoggerUtil.prettyNIOServerLog(logger, "execute stop service");
			
			String[] words = new String[] { "5", "4", "3", "2", "1" };

			for (int i = 0; i < 5; i++) {

				LoggerUtil.prettyNIOServerLog(logger,"service will stop after {} seconds", words[i]);

				ThreadUtil.sleep(1000);
			}

			CloseUtil.unbind((ChannelAcceptor) context.getChannelService());
		}
	}
}
