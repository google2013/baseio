package com.gifisan.nio.component;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.gifisan.nio.server.session.InnerSession;

public interface EndPoint extends Closeable {

	public abstract void endConnect();

	public abstract boolean isWriting(byte sessionID);

	public abstract void setWriting(byte sessionID);

	public abstract String getLocalAddr();

	public abstract String getLocalHost();

	public abstract int getLocalPort();

	public abstract int getMaxIdleTime();

	public abstract String getRemoteAddr();

	public abstract String getRemoteHost();

	public abstract int getRemotePort();

	public abstract boolean isBlocking();

	public abstract boolean isOpened();

	public abstract int sessionSize();

	public abstract int read(ByteBuffer buffer) throws IOException;
	
	public abstract ByteBuffer read(int limit) throws IOException;

	public abstract int write(ByteBuffer buffer) throws IOException;

	public abstract void setSchedule(SlowlyNetworkReader accept);

	public abstract SlowlyNetworkReader getSchedule();

	public abstract boolean isEndConnect();

	public abstract InnerSession getCurrentSession();

	public abstract void setCurrentSession(InnerSession session);


}
