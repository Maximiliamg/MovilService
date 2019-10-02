package com.prog.distribuida.tcp;

import com.sun.org.apache.xalan.internal.templates.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import sun.net.www.http.HttpClient;

public class ClientSocketManager extends Thread {

    Socket clientSocket;

    BufferedReader reader;
    PrintWriter writer;
    boolean isEnabled = true;
    private TCPServiceManagerCallerInterface caller;
    private String serverIpAddress;
    private int port;
    final Object mutex = new Object();

    public void waitForAWhile() {
        try {
            synchronized (mutex) {
                mutex.wait();
            }
        } catch (Exception ex) {
        }
    }

    public void notifyMutex() {
        try {
            synchronized (mutex) {
                mutex.notify();
            }
        } catch (Exception ex) {
        }
    }

    public ClientSocketManager(TCPServiceManagerCallerInterface caller) {
        this.caller = caller;
        this.start();
    }

    public ClientSocketManager(Socket clientSocket,
            TCPServiceManagerCallerInterface caller) {
        this.clientSocket = clientSocket;
        this.start();
        this.caller = caller;
    }

    public ClientSocketManager(String serverIpAddress,
            int port,
            TCPServiceManagerCallerInterface caller) {
        this.serverIpAddress = serverIpAddress;
        this.port = port;
        this.caller = caller;
        this.start();
    }

    public void assignSocketToThisThread(Socket socket) {
        this.clientSocket = socket;
        this.notifyMutex();
    }

    public boolean initializeSocket() {
        try {
            this.clientSocket = new Socket(serverIpAddress, port);
            return true;
        } catch (Exception ex) {

        }
        return false;
    }

    public boolean initializeStreams() {
        try {
            if (clientSocket == null) {
                if (!initializeSocket()) {
                    return false;
                }
            }
            reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()), true);
            return true;
        } catch (Exception ex) {
            caller.ErrorHasBeenThrown(ex);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            while (isEnabled) {
                if (clientSocket == null) {
                    this.waitForAWhile();
                }
                if (initializeStreams()) {
                    String newMessage = null;
                    while ((newMessage = this.reader.readLine()) != null) {
                        System.out.println(newMessage);
                        caller.MessageReceiveFromClient(clientSocket, newMessage.getBytes());
                    }
                }
                clearLastSocket();
            }
        } catch (Exception ex) {

        }
    }

    public void SendMessage(String message) {
        try {
            if (clientSocket.isConnected()) {
                writer.write(message + "\n");
                writer.flush();
            }
        } catch (Exception ex) {
            caller.ErrorHasBeenThrown(ex);
        }
    }

    public void clearLastSocket() {
//        caller.MessageReceiveFromClient(clientSocket, "El que tenia aqui se desconectó".getBytes());
        try{
            System.out.println("[SOCKET_MANAGER] HTTP REQUEST BEING SENT");
            System.out.println("Client Socket Inet: "+ clientSocket.getInetAddress());
            if (Pattern.matches("/[0-9.]+", clientSocket.getInetAddress().toString())) {
                System.out.println(clientSocket.getInetAddress().toString());
                httpRequest("" + clientSocket.getInetAddress().toString());
            }
        } catch (Exception ex){
            System.out.println("[SOCKET_MANAGER] Message could not be sent to server");
        }
        try {
            writer.close();
        } catch (Exception ex) {

        }
        try {
            reader.close();
        } catch (Exception ex) {

        }
        try {
            clientSocket.close();
        } catch (Exception ex) {

        }
        clientSocket = null;
    }

    public boolean isThisThreadBusy() {
        return clientSocket != null;
    }

    public void httpRequest(String ip) throws IOException {
        String USER_AGENT = "Mozilla/5.0";
        String url = "http://192.168.0.18:8080/MovilAPI/api/users/toggleStatus"+ip;
        System.out.println(url);
        
        org.apache.http.client.HttpClient client = HttpClients.createDefault();
        HttpPut request = new HttpPut(url);

        // add request header
        request.addHeader(
                "User-Agent", USER_AGENT);
        request.setEntity( new StringEntity( "{\"data\": \"offline\"}", ContentType.APPLICATION_JSON ) );

        HttpResponse response = client.execute(request);

        if (response.getStatusLine()
                .getStatusCode() == 404) {
            System.out.println("Error 404 Not Found");
        }

        Scanner sc = new Scanner(response.getEntity().getContent());
        String data = "";

        while (sc.hasNext()) {
            data += sc.nextLine();
        }
        System.out.println(data);
    }

}
