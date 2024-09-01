import java.io.IOException;
import java.net.Socket;

public class MusicGuruHealthClient {

    //Takes 2 arguments: IP address and port
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(host, port);
            while (!socket.isClosed()) {
                Thread.sleep(1000);
            }
            socket.close();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
