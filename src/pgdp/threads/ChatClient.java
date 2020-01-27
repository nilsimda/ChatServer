package pgdp.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ChatClient {
    private Socket client;

    public ChatClient(Socket client) {
        this.client = client;
    }

    public void connectToServer() {
        System.out.println("Which ChatServer would you like to connect to?");
        Scanner clientInput = new Scanner(System.in);
        String input = clientInput.nextLine();
        if(input.length() < 15) {
            System.out.print("Input was too small.");
            return;
        }
        String firstPart = input.substring(0, 15);
        if(!input.equals("java ChatClient") && firstPart.equals("java ChatClient")){
            String[] arr = input.split(" ");
            if(arr.length == 4){
                try {
                    //String serverAdress = arr[3];
                    int portNumber = Integer.parseInt(arr[2]);
                }catch (NumberFormatException e){
                    System.out.println("Port was invalid, try again.");
                    return;
                }
            } else {
                System.out.println("Wrong Format, try again.");
                return;
            }
            try {
                client = new Socket(arr[3], Integer.parseInt(arr[2]));
                System.out.println("Enter UserName:");
                String userName = clientInput.nextLine(); //Duplicate name possible
                /*while(checkDuplicateName(userName)){ uncomment this and method in l.132 to check for duplicate names (might have problems with cmd)
                    System.out.println("This name is already in use, enter a new one:");
                    userName = clientInput.nextLine();
                }*/
                System.out.print("Hello " + userName + "! Welcome to the Chatroom. Instructions:\n" +
                        "1. Simply type the message to send broadcast to all active clients.\n" +
                        "2. Type '@username<space>yourmessage' without quotes to send message directly to desired client.\n" +
                        "3. Type 'WHOIS' without quotes to see list of active clients.\n" +
                        "4. Type 'LOGOUT without quotes to log off.\n" +
                        "5. Type 'PENGU' without quotes to request a random penguin fact.\n");
                PrintWriter writer = new PrintWriter(
                        client.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer.println(userName);
                System.out.println(reader.readLine());
                Thread read = new Thread(() -> {
                    read(client);
                });
                Thread write = new Thread(() -> {
                    write(client);
                });
                read.start();
                write.start();
            } catch (IOException e) {
                System.out.println("Connection failed.");
            }
        }
        if (input.equals("java ChatClient")) {
            try {
                client = new Socket("localhost", 3000);
                System.out.println("Enter UserName:");

                String userName = clientInput.nextLine();

                System.out.print("Hello " + userName + "! Welcome to the Chatroom. Instructions:\n" +
                        "1. Simply type the message to send broadcast to all active clients.\n" +
                        "2. Type '@username<space>yourmessage' without quotes to send message directly to desired client.\n" +
                        "3. Type 'WHOIS' without quotes to see list of active clients.\n" +
                        "4. Type 'LOGOUT without quotes to log off.\n" +
                        "5. Type 'PENGU' without quotes to request a random penguin fact.\n");
                PrintWriter writer = new PrintWriter(
                        client.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer.println(userName);
                System.out.println(reader.readLine());
                Thread read = new Thread(() -> {
                    read(client);
                });
                Thread write = new Thread(() -> {
                    write(client);
                });
                read.start();
                write.start();
            } catch (IOException e) {
                System.out.println("Connection failed.");
            }
        }
    }

    public void read(Socket client) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (!client.isClosed()) {
                System.out.println(reader.readLine());
            }
        } catch (IOException e) {
            return;
        }
    }

    public void write(Socket client) {
        try {
            PrintWriter writer = new PrintWriter(client.getOutputStream(),true);
            Scanner userInput = new Scanner(System.in);
            while (true) {
                String msg = userInput.nextLine();
                if(msg.equals("LOGOUT")){
                    writer.println(msg);
                    client.close();
                    break;
                }
                else
                    writer.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Error while writing as client.");
        }
    }
    /*public boolean checkDuplicateName(String username){ uncomment this and the while loop in l.44, if duplicate names are invalid
       return ChatServer.mapOfUsers.keySet().stream().anyMatch(s -> s.equals(username));
    }*/

    public static void main(String[] args) {
        ChatClient client = new ChatClient(new Socket());
        client.connectToServer();

    }
}
