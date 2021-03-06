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
package com.generallycloud.nio.component;

import java.math.BigDecimal;

import com.generallycloud.nio.Linkable;
import com.generallycloud.nio.common.LifeCycleUtil;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.LoggerUtil;
import com.generallycloud.nio.component.SocketSessionManager.SocketSessionManagerEvent;
import com.generallycloud.nio.component.concurrent.ExecutorEventLoopGroup;
import com.generallycloud.nio.component.concurrent.LineEventLoopGroup;
import com.generallycloud.nio.component.concurrent.ThreadEventLoopGroup;
import com.generallycloud.nio.component.ssl.SslContext;
import com.generallycloud.nio.configuration.ServerConfiguration;
import com.generallycloud.nio.protocol.EmptyReadFuture;
import com.generallycloud.nio.protocol.ProtocolDecoder;
import com.generallycloud.nio.protocol.ProtocolEncoder;
import com.generallycloud.nio.protocol.ProtocolFactory;

public abstract class AbstractSocketChannelContext extends AbstractChannelContext
		implements SocketChannelContext {

	private IoEventHandleAdaptor						ioEventHandleAdaptor;
	private ProtocolFactory							protocolFactory;
	private BeatFutureFactory						beatFutureFactory;
	private int									sessionAttachmentSize;
	private ExecutorEventLoopGroup					executorEventLoopGroup;
	private ProtocolEncoder							protocolEncoder;
	private ProtocolDecoder							protocolDecoder;
	private SslContext								sslContext;
	private boolean								enableSSL;
	private boolean								initialized;
	private ChannelByteBufReader						channelByteBufReader;
	private ForeReadFutureAcceptor					foreReadFutureAcceptor;
	protected SocketSessionManager						sessionManager;
	private SocketSessionFactory						sessionFactory;
	private LinkableGroup<SocketSessionEventListener>		sessionEventListenerGroup	= new LinkableGroup<>();
	private LinkableGroup<SocketSessionIdleEventListener>	sessionIdleEventListenerGroup	= new LinkableGroup<>();
	private Logger									logger					= LoggerFactory
			.getLogger(AbstractSocketChannelContext.class);

	@Override
	public int getSessionAttachmentSize() {
		return sessionAttachmentSize;
	}

	@Override
	public void addSessionEventListener(SocketSessionEventListener listener) {
		sessionEventListenerGroup.addLink(new SocketSessionEventListenerWrapper(listener));
	}

	@Override
	public void addSessionIdleEventListener(SocketSessionIdleEventListener listener) {
		sessionIdleEventListenerGroup
				.addLink(new SocketSessionIdleEventListenerWrapper(listener));
	}

	@Override
	public void offerSessionMEvent(SocketSessionManagerEvent event) {
		sessionManager.offerSessionMEvent(event);
	}

	@Override
	public Linkable<SocketSessionEventListener> getSessionEventListenerLink() {
		return sessionEventListenerGroup.getRootLink();
	}

	@Override
	public Linkable<SocketSessionIdleEventListener> getSessionIdleEventListenerLink() {
		return sessionIdleEventListenerGroup.getRootLink();
	}

	@Override
	public SocketSessionManager getSessionManager() {
		return sessionManager;
	}

	@Override
	public void setSessionAttachmentSize(int sessionAttachmentSize) {
		this.sessionAttachmentSize = sessionAttachmentSize;
	}

	@Override
	public BeatFutureFactory getBeatFutureFactory() {
		return beatFutureFactory;
	}

	@Override
	public void setBeatFutureFactory(BeatFutureFactory beatFutureFactory) {
		this.beatFutureFactory = beatFutureFactory;
	}

	@Override
	public ProtocolEncoder getProtocolEncoder() {
		return protocolEncoder;
	}
	
	public ProtocolDecoder getProtocolDecoder() {
		return protocolDecoder;
	}

	public AbstractSocketChannelContext(ServerConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected void clearContext() {
		super.clearContext();
	}

	@Override
	protected void doStart() throws Exception {

		if (ioEventHandleAdaptor == null) {
			throw new IllegalArgumentException("null ioEventHandle");
		}

		if (protocolFactory == null) {
			throw new IllegalArgumentException("null protocolFactory");
		}

		if (!initialized) {

			initialized = true;

			this.serverConfiguration.initializeDefault(this);

			this.addSessionEventListener(new SocketSessionManagerSEListener());
		}

		EmptyReadFuture.initializeReadFuture(this);

		if (isEnableSSL()) {
			this.sslContext.initialize(this);
		}

		int SERVER_CORE_SIZE = serverConfiguration.getSERVER_CORE_SIZE();
		int server_port = serverConfiguration.getSERVER_PORT();
		long session_idle = serverConfiguration.getSERVER_SESSION_IDLE_TIME();
		String protocolId = protocolFactory.getProtocolId();

		this.encoding = serverConfiguration.getSERVER_ENCODING();
		this.sessionIdleTime = serverConfiguration.getSERVER_SESSION_IDLE_TIME();

		if (protocolEncoder == null) {
			this.protocolEncoder = protocolFactory.getProtocolEncoder();
			this.protocolDecoder = protocolFactory.getProtocolDecoder();
		}

		this.initializeByteBufAllocator();

		LoggerUtil.prettyNIOServerLog(logger,
				"======================================= service begin to start =======================================");
		LoggerUtil.prettyNIOServerLog(logger, "encoding              :{ {} }", encoding);
		LoggerUtil.prettyNIOServerLog(logger, "protocol              :{ {} }",protocolId);
		LoggerUtil.prettyNIOServerLog(logger, "cpu size              :{ cpu * {} }", SERVER_CORE_SIZE);
		LoggerUtil.prettyNIOServerLog(logger, "enable ssl            :{ {} }", isEnableSSL());
		LoggerUtil.prettyNIOServerLog(logger, "session idle          :{ {} }",session_idle);
		LoggerUtil.prettyNIOServerLog(logger, "listen port(tcp)      :{ {} }",server_port);

		if (serverConfiguration.isSERVER_ENABLE_MEMORY_POOL()) {

			long SERVER_MEMORY_POOL_CAPACITY = serverConfiguration
					.getSERVER_MEMORY_POOL_CAPACITY() * SERVER_CORE_SIZE;
			long SERVER_MEMORY_POOL_UNIT = serverConfiguration.getSERVER_MEMORY_POOL_UNIT();

			double MEMORY_POOL_SIZE = new BigDecimal(
					SERVER_MEMORY_POOL_CAPACITY * SERVER_MEMORY_POOL_UNIT)
							.divide(new BigDecimal(1024 * 1024), 2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();

			LoggerUtil.prettyNIOServerLog(logger, "memory pool cap       :{ {} * {} ≈ {} M }",
					new Object[] { SERVER_MEMORY_POOL_UNIT, SERVER_MEMORY_POOL_CAPACITY,
							MEMORY_POOL_SIZE });
		}

		LifeCycleUtil.start(ioEventHandleAdaptor);

		if (executorEventLoopGroup == null) {

			int eventQueueSize = serverConfiguration.getSERVER_IO_EVENT_QUEUE();

			int eventLoopSize = serverConfiguration.getSERVER_CORE_SIZE();

			if (serverConfiguration.isSERVER_ENABLE_WORK_EVENT_LOOP()) {
				this.executorEventLoopGroup = new ThreadEventLoopGroup("event-process",
						eventQueueSize, eventLoopSize);
			} else {
				this.executorEventLoopGroup = new LineEventLoopGroup("event-process",
						eventQueueSize, eventLoopSize);
			}
		}

		if (foreReadFutureAcceptor == null) {
			this.foreReadFutureAcceptor = new EventLoopReadFutureAcceptor();
		}

		if (channelByteBufReader == null) {

			this.channelByteBufReader = new IoLimitChannelByteBufReader();

			if (enableSSL) {
				getLastChannelByteBufReader(channelByteBufReader)
						.setNext(new SslChannelByteBufReader());
			}

			getLastChannelByteBufReader(channelByteBufReader)
					.setNext(new TransparentByteBufReader(this));
		}

		if (sessionManager == null) {
			initSessionManager();
		}

		if (sessionFactory == null) {
			sessionFactory = new SocketSessionFactoryImpl();
		}

		LifeCycleUtil.start(byteBufAllocatorManager);

		LifeCycleUtil.start(executorEventLoopGroup);

		doStartModule();
	}
	
	protected abstract void initSessionManager();

	protected void doStartModule() throws Exception {

	}

	protected void doStopModule() {

	}

	private ChannelByteBufReader getLastChannelByteBufReader(ChannelByteBufReader value) {

		for (;;) {

			if (value.getNext() == null) {
				return value;
			}

			value = value.getNext().getValue();
		}
	}

	@Override
	protected void doStop() throws Exception {

		LifeCycleUtil.stop(executorEventLoopGroup);

		LifeCycleUtil.stop(ioEventHandleAdaptor);

		LifeCycleUtil.stop(byteBufAllocatorManager);

		clearContext();

		doStopModule();
	}

	@Override
	public ProtocolFactory getProtocolFactory() {
		return protocolFactory;
	}

	@Override
	public IoEventHandleAdaptor getIoEventHandleAdaptor() {
		return ioEventHandleAdaptor;
	}

	@Override
	public ExecutorEventLoopGroup getExecutorEventLoopGroup() {
		return executorEventLoopGroup;
	}

	@Override
	public void setIoEventHandleAdaptor(IoEventHandleAdaptor ioEventHandleAdaptor) {
		this.ioEventHandleAdaptor = ioEventHandleAdaptor;
	}

	@Override
	public void setProtocolFactory(ProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}

	@Override
	public void setExecutorEventLoopGroup(ExecutorEventLoopGroup executorEventLoopGroup) {
		this.executorEventLoopGroup = executorEventLoopGroup;
	}

	@Override
	public SslContext getSslContext() {
		return sslContext;
	}

	@Override
	public void setSslContext(SslContext sslContext) {
		if (sslContext == null) {
			throw new IllegalArgumentException("null sslContext");
		}
		this.sslContext = sslContext;
		this.enableSSL = true;
	}

	@Override
	public boolean isEnableSSL() {
		return enableSSL;
	}

	@Override
	public SocketSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void setSocketSessionFactory(SocketSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public ChannelByteBufReader getChannelByteBufReader() {
		return channelByteBufReader;
	}

	@Override
	public ForeReadFutureAcceptor getForeReadFutureAcceptor() {
		return foreReadFutureAcceptor;
	}

	@Override
	public void setSessionManager(SocketSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

}
