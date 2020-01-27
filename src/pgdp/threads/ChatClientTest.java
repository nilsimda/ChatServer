package pgdp.threads;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChatClientTest {
    private ByteArrayOutputStream consoleOutPut = new ByteArrayOutputStream();
    private ChatServer testServer = new ChatServer(new ServerSocket(), new ArrayList<>());
    private ChatClient testClient = new ChatClient(new Socket());

    ChatClientTest() throws IOException {
    }

    @Test
    public void startServerWithInvalidInputTest() {
        String serverInput = "java ChatServe\n";
        System.setIn(new ByteArrayInputStream(serverInput.getBytes()));
        System.setOut(new PrintStream(consoleOutPut));
        testServer.waitForConnections();
        String expected = "Which server would you like to start?\r\n" + "Input was too small.";
        assertEquals(expected, consoleOutPut.toString());
    }

    @Test
    public void startClientInvalidTest() {
        String input = "java ChatClien\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(consoleOutPut));
        testClient.connectToServer();
        String expected = "Which ChatServer would you like to connect to?\r\n" + "Input was too small.";
        assertEquals(expected, consoleOutPut.toString());
    }

    @Test
    public void startingClientWithoutServer() {
        String input = "java ChatClient\n" + "tester";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(consoleOutPut));
        testClient.connectToServer();
        String expected = "Which ChatServer would you like to connect to?\r\n" + "Connection failed.\r\n";
        assertEquals(expected, consoleOutPut.toString());
    }

    @Test
    public void testingNonIntergerPortClient() {
        String input = "java ChatClient notInt localhost";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(consoleOutPut));
        testClient.connectToServer();
        String expected = "Which ChatServer would you like to connect to?\r\n" + "Port was invalid, try again.\r\n";
        assertEquals(expected, consoleOutPut.toString());
    }
    @Test
    public void testingNonIntegerPortServer(){
        String input = "java ChatServer port";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(consoleOutPut));
        testServer.waitForConnections();
        String expected = "Which server would you like to start?\r\n" + "Port must be a number.\r\n";
        assertEquals(expected, consoleOutPut.toString());
    }
}
