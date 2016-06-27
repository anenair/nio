import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.unity.message.MessageType;
import com.unity.utils.SocketChannelUtils;

public final class Client {

	private static Selector selector;

	private static void initializeConnection(String hostname, String port) throws IOException {

		InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, Integer.valueOf(port));

		// create a client socket channel
		SocketChannel socketChannel = SocketChannel.open();
		// non-blocking I/O
		socketChannel.configureBlocking(false);

		socketChannel.connect(inetSocketAddress);

		// create selector
		selector = Selector.open();

		// Record to selector (OP_CONNECT type)
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}

	public static void main(String[] args) throws IOException {

		initializeConnection("localhost", "8787");
		run();
	}

	public static void run() {

		// wait for events from selector
		while (true) {

			try {
				// waiting for an event
				selector.select();

				// event arrives. create keys.
				Set<SelectionKey> selectionKeySet = selector.selectedKeys();
				Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();

				// for each of the key created by selector
				while (selectionKeyIterator.hasNext()) {
					SelectionKey selectionKey = selectionKeyIterator.next();
					selectionKeyIterator.remove();

					// check the type of request
					if (selectionKey.isConnectable()) {
						// Connection OK
						System.out.println("Server Found");
						finishConnection(selectionKey);
					} else if (selectionKey.isWritable()) {

						SocketChannelUtils.writeToChannelFromBuffer(selectionKey,
								new String[] { MessageType.getMessage() });
					} else if (selectionKey.isReadable()) {
						SocketChannelUtils.readFromChannelIntoBuffer(selectionKey);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static void finishConnection(SelectionKey selectionKey) throws IOException {

		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

		// Close pending connections
		if (socketChannel.isConnectionPending()) {
			socketChannel.finishConnect();
		}
		selectionKey.interestOps(SelectionKey.OP_WRITE);
	}

}
