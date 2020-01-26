package pgdp.threads;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;

public class ChatServer {
    private List<Socket> clients;
    private ServerSocket serverSocket;
    private static Map<String, Socket> mapOfUsers = new HashMap<>();
    private static Map<String, LocalTime> mapOfTimeStayed = new HashMap<>();

    public ChatServer(ServerSocket serverSocket, List<Socket> clients) {
        this.serverSocket = serverSocket;
        this.clients = clients;
    }

    public void waitForConnections() {
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
        }
        String firstPart = s.substring(0, 15);
        if (!s.equals("java ChatServer") && firstPart.equals("java ChatServer")) {
            try {
                if (s.charAt(15) != ' ')
                    throw new IllegalArgumentException();
                int port = Integer.parseInt(s.substring(16));
                ServerSocket serverSocket = new ServerSocket(port);
                int count = 0;
                while (clients.size() < 50) {
                    System.out.println("Server waiting on port " + port + "...");
                    Socket socket = serverSocket.accept();
                    count++;
                    Thread t = new Thread(() -> {
                        clients.add(socket);
                        communication(socket);
                    });
                    t.start();
                }
            } catch (NumberFormatException e) {
                System.out.println("Port must be a number.");
            } catch (IllegalArgumentException e) {
                System.out.print("Input must be of Form java ChatServer <port>");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Illegal input.");
            } catch (IOException e) {
                System.out.println("Failed to start server, try again.");
            }
        } else if (s.equals("java ChatServer")) {
            try {
                serverSocket = new ServerSocket(3000);
                System.out.println("Server is waiting on port 3000...");
                int count = 0;
                while (count < 50) {
                    Socket socket = serverSocket.accept();
                    count++;
                    Thread t = new Thread(() -> {
                        clients.add(socket);
                        communication(socket);
                    });
                    t.start();
                }
            } catch (IOException e) {
                System.out.print("Failed to start Server, try again.");
            }
        } else {
            throw new IllegalArgumentException("Input was invalid.");
        }
    }

    public void sendToAll(String msg) {
        for (Socket s : clients) {
            try {
                PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
                writer.println(msg);
            } catch (IOException e) {
                System.out.println("Couldnt get Outputstream.");
            }
        }
    }

    private void communication(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String username = reader.readLine();
            System.out.println("*** " + username + " has joined the Chatroom. ***\n" +
                    "Server is waiting on port 3000...");
            sendToAll("*** " + username + " has joined the ChatRoom. ***");
            synchronized (ChatServer.class) {
                mapOfTimeStayed.put(username, LocalTime.now());
                mapOfUsers.put(username, socket);
            }
            while (true) {
                String msg = reader.readLine();
                if (msg.equals("PENGU")) {
                    sendToAll("Cool Penguin Fact!");
                } else if (msg.equals("LOGOUT")) {
                    synchronized (ChatServer.class) {
                        try {
                            clients.remove(socket);
                            mapOfUsers.remove(username);
                            mapOfTimeStayed.remove(username);
                            socket.close();
                            break;
                        } catch (IOException e) {
                            System.out.println("Could not close socket.");
                        }
                    }
                } else if(msg.charAt(0) == '@') {
                    String userName = msg.substring(1, msg.indexOf(" "));
                    if(!userName.isEmpty() && mapOfUsers.containsKey(userName)){
                        Socket s = mapOfUsers.get(userName);
                        PrintWriter wr = new PrintWriter(s.getOutputStream(), true);
                        wr.println(msg);
                    }
                    else
                        writer.println("This user is not in the Chatroom.");
                }else if(msg.equals("WHOIS")){
                    StringBuilder builder = new StringBuilder();
                    mapOfTimeStayed.entrySet().stream().forEach(entry ->{
                        writer.println(entry.getKey() +" since " + entry.getValue());
                    });
                }
                else
                    sendToAll(msg);
            }
        } catch (IOException e) {
            System.out.println("IO-Exception during communication, socket will be terminated.");
            clients.remove(socket);
            mapOfUsers.values().remove(socket);
            //mapOfTimeStayed.
            try {
                socket.close();
            } catch (IOException f){
                System.out.println("Could not close socket.");
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
    
