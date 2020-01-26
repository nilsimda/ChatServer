package pgdp.threads;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatServer{
    private List<Socket> clients;
    private ServerSocket serverSocket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public ChatServer(ServerSocket serverSocket, List<Socket> clients){
        this.serverSocket = serverSocket;
        this.clients = clients;
    }
    public void waitForConnections(){
        System.out.println("Waiting to start Server...");
        Scanner openServerReq = new Scanner(System.in);
        String s = openServerReq.nextLine();
        if (s.length() < 15) {
            try {
                throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                System.out.print("Input was too small.");
                return;
            }
        }String firstPart = s.substring(0, 15);
        if (!s.equals("java ChatServer") && firstPart.equals("java ChatServer")) {
            try {
                if (s.charAt(15) != ' ')
                    throw new IllegalArgumentException();
                int port = Integer.parseInt(s.substring(16));
                ServerSocket serverSocket = new ServerSocket(port);
                int count = 0;
                while(count < 50){
                    System.out.println("Server waiting on port " + port +"...");
                    serverSocket.accept();
                    count++;
                }
            } catch (NumberFormatException e) {
                System.out.println("Port must be a number.");
            } catch (IllegalArgumentException e){
                System.out.print("Input must be of Form java ChatServer <port>");
            } catch(IndexOutOfBoundsException e) {
                System.out.println("Illegal input.");
            } catch (IOException e){
                System.out.println("Failed to start server, try again.");
            }
        }
        else if (s.equals("java ChatServer")) {
            try {
                serverSocket = new ServerSocket(3000);
                int count = 0;
                while (count < 50) {
                    System.out.println("Server is waiting on port 3000...");
                    Socket socket = serverSocket.accept();
                    clients.add(socket);
                    count++;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    String username = reader.readLine();
                    System.out.println("*** " + username + " has joined the Chatroom. ***");
                    writer.println("*** "+ username +" has joined the ChatRoom. ***");
                    sendToAll("Hello from Server!");
                }
            } catch (IOException e) {
                System.out.print("Failed to start Server, try again.");
            }
        } else {
            throw new IllegalArgumentException("Input was invalid.");
        }
    }
    public void sendToAll(String msg){
            for (Socket s : clients) {
                try {
                    PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
                    writer.println(msg);
                }catch (IOException e){
                    System.out.println("Couldnt get Outputstream.");
                }
            }
        }
    public static void main(String[] args) {
        try {
            ChatServer chatServer = new ChatServer(new ServerSocket(), new ArrayList<>());
            chatServer.waitForConnections();
        } catch (IOException e) {
            System.out.println("IO-Exception.");
        }

    }

}
    
