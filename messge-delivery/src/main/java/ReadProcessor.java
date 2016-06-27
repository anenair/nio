import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.unity.message.MessageType;
import com.unity.message.RequestHandler;

public class ReadProcessor {

	public static void process(SelectionKey selectionKey,
			final Map<SocketChannel, RequestHandler> socketChannelClientMap, String message,
			Map<String, SocketChannel> clientSocketChannelMap) throws IOException {

		SocketChannel key = (SocketChannel) selectionKey.channel();
		RequestHandler requestHandler = socketChannelClientMap.get(key);

		if (MessageType.WHO_AM_I.name().equals(message) || MessageType.WHO_IS_HERE.name().equals(message)) {

			requestHandler.addMessage(message);
		} else {

			int index = message.indexOf("###");
			String messagePart = message.substring(0, index);
			System.out.println("Message to be sent : " + messagePart);
			String[] clientIds = message.substring(index + 3, message.length()).split(",");
			for (String id : clientIds) {
				System.out.println("Send message to ID : " + id);
				SocketChannel socketChannel = clientSocketChannelMap.get(id.trim());
				socketChannelClientMap.get(socketChannel).addMessage(messagePart);
				SelectionKey selectionKey1 = socketChannel.keyFor(selectionKey.selector());
				System.out.println("Selection Key : " + selectionKey1);
				selectionKey1.interestOps(SelectionKey.OP_WRITE);
				// SocketChannelUtils.writeToChannelFromBuffer(selectionKey1,
				// new String[] { messagePart });
				System.out.println("done");
				selectionKey.selector().wakeup();
			}

		}

	}

}
