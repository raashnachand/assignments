import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

/**
 * Server program for NWEN243 Project 2.
 * Code made with help from ChatGPT.
 */
public class MusicGuruServer {
    //Field for storing music data.
    public static List<SongEntry> musicData;
    /**
     * Main method. Takes 1 argument: port.
    */
    public static void main(String[] args) {
        //Populate data.
        musicData = populateEntries();
        if (args.length != 1) {
            System.err.println("Format: java MusicGuruServer <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try {
            // Create a server socket.
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                // Accept, handle and close client connection.
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handling communication with client.
     * @param clientSocket 
     */
    private static void handleClient(Socket clientSocket) {
        try {
            // Get input and output streams from the client socket.
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            // Hardcoding the range and sending to client.
            String range = 1950 + "-" + 2009;
            output.write(range.getBytes());
            output.flush();
            // Receiving the year that the client requested.
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                String year = new String(buffer, 0, bytesRead);
                System.out.println("Received: " + year);

                // Send a random song from that year back to the client - function below.
                String song = songPicker(Integer.parseInt(year));
                output.write(song.getBytes());
                output.flush();
                // Send the IP address of the server.
                String serverIPAddress = InetAddress.getLocalHost().getHostAddress();
                output.write(serverIPAddress.getBytes());
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Selects all entries from the year that the client specified, picks a random song, returns song entry.
     * @param year
     * @return
     */
    private static String songPicker(int year){
        List<SongEntry> subList = new ArrayList<>();
        for (SongEntry songEntry : musicData) {
            if (songEntry.getYear() == year){
                subList.add(songEntry);
            }
        }
        Random random = new Random();
        int chartPosition = random.nextInt(10) + 1;
        SongEntry song = subList.get(chartPosition - 1);
        return song.toString();
    }

    /**
     * The method to populate the list of song entries from the musicdata.txt file.
     * @return
     */
    public static List<SongEntry> populateEntries(){
        List<SongEntry> songEntries = new ArrayList<>();

        // Scan each line, separate the parts, store it as a SongEntry object.
        try (Scanner scanner = new Scanner(new File("musicdata.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int year = Integer.parseInt(parts[0].trim());
                    int chartPosition = Integer.parseInt(parts[1].trim());
                    String songName = parts[2].trim();
                    String artistName = parts[3].trim();
                    SongEntry entry = new SongEntry(year, chartPosition, songName, artistName);
                    songEntries.add(entry);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return songEntries;
    }
}

/**
 * SongEntry class. Stores year, chart position, song name, artist name.
 */
class SongEntry {
    private int year;
    private int chartPosition;
    private String songName;
    private String artistName;

    public SongEntry(int year, int chartPosition, String songName, String artistName) {
        this.year = year;
        this.chartPosition = chartPosition;
        this.songName = songName;
        this.artistName = artistName;
    }

    @Override
    public String toString() {
        return year + ", " + chartPosition + ", " + songName + ", " + artistName;
    }
}