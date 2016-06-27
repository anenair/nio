import java.io.IOException;

public class ServerHub {

	public static void main(String[] args) {
		String port = "8787";
		String hostname = "localhost";
		try {
			new Thread(new Hub(hostname, port));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
