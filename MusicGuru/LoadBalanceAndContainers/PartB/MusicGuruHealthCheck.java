import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Health Check for the server. Accepts a connection and immediately drops it.
 * Only works on port 5001, which has been selected in the AWS instance 
 * security group.
 */
public class MusicGuruHealthCheck {

    //takes 1 argument, port
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
