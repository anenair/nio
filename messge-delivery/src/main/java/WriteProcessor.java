import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.unity.message.MessageType;
import com.unity.message.RequestHandler;

public class WriteProcessor {

	public static String[] process(SelectionKey selectionKey,
			final Map<SocketChannel, RequestHandler> socketChannelClientMap) {

		SocketChannel key = (SocketChannel) selectionKey.channel();
		RequestHandler requestHandler = socketChannelClientMap.get(key);
		String[] messages = getMessages(socketChannelClientMap, key, requestHandler);

		return messages;
	}

	private static String[] getMessages(Map<SocketChannel, RequestHandler> socketChannelClientMap, SocketChannel key,
			RequestHandler requestHandler) {

		Queue<String> queue = requestHandler.getQueue();
		List<String> messages = new ArrayList<String>(queue.size() * 2);

		while (!queue.isEmpty()) {
			String requestMessage = queue.remove();
			if (MessageType.WHO_AM_I.name().equals(requestMessage)) {
				messages.add("" + requestHandler.getClientId());
			} 
			else if (MessageType.WHO_IS_HERE.name().equals(requestMessage)) {
				addOtherMembers(key, socketChannelClientMap, messages);
			}
			else {
				System.out.println("adding message to queue : " + requestMessage);
				messages.add(requestMessage);
			}
		}
		return messages.toArray(new String[messages.size()]);
	}

	private static void addOtherMembers(SocketChannel key, Map<SocketChannel, RequestHandler> socketChannelClientMap,
			List<String> messages) {

		for (SocketChannel socketChannel : socketChannelClientMap.keySet()) {
			if (!key.equals(socketChannel)) {
				messages.add("" + socketChannelClientMap.get(socketChannel).getClientId());
			}
		}
	}

}
