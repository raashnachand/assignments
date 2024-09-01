import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Client program for NWEN243 Project 2. Compatible with MusicGuruServer.
 * Code made with help from ChatGPT.
 */
public class MusicGuruClient {
    public static void main(String[] args) {
        //Three arguments only
        if (args.length != 3) {
            System.err.println("Format: java MusicGuruClient <host> <port> <year>");
            System.exit(1);
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        
        try {
            //Creating socket and streams
            Socket socket = new Socket(host, port);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            //Read range and split into integers
            byte[] rangeBuffer = new byte[1024];
            int rangeBytesRead = input.read(rangeBuffer);
            String range = new String(rangeBuffer, 0, rangeBytesRead);
            System.out.println("Range: " + range);
            String[] splitYears = range.split("-");
            int startYear = Integer.parseInt(splitYears[0]);
            int endYear = Integer.parseInt(splitYears[1]);

            //Check if year within range and send; else send random year in range
            if (year >= startYear && year <= endYear){
                String yearString = "" + year;
                output.write(yearString.getBytes());
                output.flush();
                System.out.println("Sending year " + year);
            } else {
                Random random = new Random();
                int randomYear = random.nextInt(endYear - startYear) + startYear;
                String yearString = "" + randomYear;
                output.write(yearString.getBytes());
                output.flush();
                System.out.println("Specified year out of range; sending random year " + randomYear + " instead");
            }

            //Reading the song server sends
            byte[] songBuffer = new byte[1024];
            int songBytesRead = input.read(songBuffer);
            String song = new String(songBuffer, 0, songBytesRead);

            //Prettify the song received and print
            String[] parts = song.split(",");
            if (parts.length == 4) {
                int serverYear = Integer.parseInt(parts[0].trim());
                int chartPosition = Integer.parseInt(parts[1].trim());
                String songName = parts[2].trim();
                String artistName = parts[3].trim();
                System.out.println("In " + serverYear + " the number " + chartPosition + " song was " + songName + " by " + artistName);
            }

            //Printing the IP address
            byte[] addressBuffer = new byte[1024];
            int addressBytesRead = input.read(addressBuffer);
            String address = new String(addressBuffer, 0, addressBytesRead);
            System.out.println("(" + address + ")");
            
            // Close the socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
