import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.unity.message.RequestHandler;
import com.unity.utils.SocketChannelUtils;

/** Selecting thread */
public final class Hub implements Runnable{

	// to accept connections
	private ServerSocketChannel serverSocketChannel = null;

	private AtomicLong clientId = new AtomicLong(0);

	private Selector selector = null;

	private Map<SocketChannel, RequestHandler> socketChannelClientMap = new ConcurrentHashMap<SocketChannel, RequestHandler>();

	private Map<String, SocketChannel> clientSocketChannelMap = new ConcurrentHashMap<String, SocketChannel>();

	public Hub(String hostname, String port) throws IOException {
		this(new InetSocketAddress(hostname, Integer.valueOf(port)));
	}

	private Hub(InetSocketAddress inetSocketAddress) throws IOException {

		initializeSelector(inetSocketAddress);

	}

	private void initializeSelector(InetSocketAddress inetSocketAddress) throws IOException {

		// create selector
		selector = Selector.open();

		// create server socket channel
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		// bind socket server to defined host and ip
		serverSocketChannel.bind(inetSocketAddress);

		// associate socket channel to selector
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void run() {

		// wait for events from selector
		while (true) {

			System.out.println("waiting for client connections ... ");

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
					if (selectionKey.isAcceptable()) {
						acceptSocketConnection(selectionKey);
					} else if (selectionKey.isReadable()) {

						String message = SocketChannelUtils.readFromChannelIntoBuffer(selectionKey);
						System.out.println("Message Received :  " + message);
						ReadProcessor.process(selectionKey, socketChannelClientMap, message,clientSocketChannelMap);
//						ReadProcessor.process(message, selectionKey, clientSocketChannelMap,selector);
					} else if (selectionKey.isWritable()) {

						System.out.println("Client : " + socketChannelClientMap.get((SocketChannel)selectionKey.channel()).getClientId());
						String[] response = WriteProcessor.process(selectionKey, socketChannelClientMap);
						SocketChannelUtils.writeToChannelFromBuffer(selectionKey, response);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void acceptSocketConnection(SelectionKey selectionKey) throws IOException {
		System.out.println("accepting connection");
		// get the client socket channel on accepting the request
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
		SocketChannel clientSocketChannel = serverSocketChannel.accept();
		long id = clientId.incrementAndGet();
		socketChannelClientMap.put(clientSocketChannel, new RequestHandler(id));
		clientSocketChannelMap.put(id + "", clientSocketChannel);
		clientSocketChannel.configureBlocking(false);

		// associate socket channel to selector. record it for read operation
		clientSocketChannel.register(selector, SelectionKey.OP_READ);

	}
}
