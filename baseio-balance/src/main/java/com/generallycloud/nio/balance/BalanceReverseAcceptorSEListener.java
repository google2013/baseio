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
package com.generallycloud.nio.balance;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.component.SocketSessionEventListenerAdapter;
import com.generallycloud.nio.component.SocketSession;

public class BalanceReverseAcceptorSEListener extends SocketSessionEventListenerAdapter {

	private Logger			logger	= LoggerFactory.getLogger(BalanceReverseAcceptorSEListener.class);

	private BalanceContext	context;

	public BalanceReverseAcceptorSEListener(BalanceContext context) {
		this.context = context;
	}

	@Override
	public void sessionOpened(SocketSession session) {
		logger.info("load node from [ {} ] connected.", session);
		context.getBalanceRouter().addRouterSession((BalanceReverseSocketSession) session);
	}

	@Override
	public void sessionClosed(SocketSession session) {
		logger.info("load node from [ {} ] disconnected.", session);
		context.getBalanceRouter().removeRouterSession((BalanceReverseSocketSession) session);
	}
}
