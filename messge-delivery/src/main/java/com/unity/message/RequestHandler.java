package com.unity.message;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestHandler {

	private long clientId;
	private Queue<String> queue = new LinkedBlockingQueue<String>();

	public RequestHandler(long clientId) {
		this.clientId = clientId;
	}

	public long getClientId() {
		return clientId;
	}

	public void process(final Map<SocketChannel, Long> socketChannelClientMap) {
		
		

	}

	public void addMessage(String message) {
		queue.add(message);
	}

	public Queue<String> getQueue() {
		return queue;
	}
}
