package pgdp.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {
        System.out.println("Write the ChatServer you would like to connect to.");
        Scanner clientInput = new Scanner(System.in);
        String input = clientInput.nextLine();
        if(input.equals("java ChatClient")){
            try {
                Socket client = new Socket("localhost", 3000);
                System.out.println("Enter UserName:");
                String userName = clientInput.nextLine();
                System.out.print("Hello " + userName + "! Welcome to the Chatroom. Instructions:\n" +
                        "1. Simply type the message to send broadcast to all active clients.\n" +
                        "2. Type '@username<space>yourmessage' without quotes to send message directly to desired client.\n" +
                        "3. Type 'WHOIS' without quotes to see list of active clients.\n" +
                        "4. Type 'LOGOUT without quotes to log off.\n"+
                        "5. Type 'PENGU' without quotes to request a random penguin fact.\n");
                PrintWriter writer = new PrintWriter(
                        client.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer.println(userName);
                System.out.println(reader.readLine());
                while(true){
                    String msg = reader.readLine();
                    if(msg.equals("LOGOUT")){
                        client.close();
                        break;
                    }else
                        System.out.println(msg);
                }

            } catch (IOException e){
                System.out.println("Connection failed.");
            }
        }
    }
}
