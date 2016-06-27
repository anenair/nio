package com.unity.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketChannelUtils {

	private SocketChannelUtils() {

	}

	public static String readFromChannelIntoBuffer(SelectionKey selectionKey) throws IOException {
		System.out.println("Reading from channel to buffer");
		// retrieve the client socket channel from key
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		readBuffer.clear();
		// read from socket channel into buffer
		int numRead = socketChannel.read(readBuffer);
		System.out.println("Number of bytes read : " + numRead);

		String message = new String(readBuffer.array()).trim();

		// perform some operation on the received message
		System.out.println("Message Received : " + message);

		selectionKey.interestOps(SelectionKey.OP_WRITE);

		return message;
	}

	public static void writeToChannelFromBuffer(SelectionKey selectionKey, String[] messages) throws IOException {
		
		System.out.println("writing to channel from buffer");
		// retrieve the client socket channel held by the key
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

		for (String message : messages) {
			System.out.println("sending message : " + message);
			byte[] msg = message.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(msg);

			// Writes a sequence of bytes to this channel from the given buffer.
			socketChannel.write(buffer);
			buffer.clear();
		}

		selectionKey.interestOps(SelectionKey.OP_READ);

	}
}
