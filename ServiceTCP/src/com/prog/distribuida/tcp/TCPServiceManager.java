/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author Administrador
 */
public class TCPServiceManager extends Thread implements TCPServiceManagerCallerInterface {

    ServerSocket serverSocket;
    private int port;
    private TCPServiceManagerCallerInterface caller;
    boolean isEnabled = true;
    Vector<ClientSocketManager> clients = new Vector<ClientSocketManager>();
    final int NUMBER_OF_THREADS = 50;
    public RUTManager RUT;

    public TCPServiceManager(int port,
            TCPServiceManagerCallerInterface caller) {
        this.port = port;
        this.caller = caller;
        initializeThreads();
        this.RUT = new RUTManager(clients);
        this.start();

    }

    public void initializeThreads() {
        try {
            for (int index = 0; index < NUMBER_OF_THREADS; index++) {
                clients.add(new ClientSocketManager(this));
            }
        } catch (Exception ex) {

        }
    }

    public ClientSocketManager getNotBusyClientSocketManager() {
        try {
            for (ClientSocketManager current : this.clients) {
                if (current != null) {
                    if (!current.isThisThreadBusy()) {
                        return current;
                    }
                }
            }
        } catch (Exception ex) {

        }
        return null;
    }

    public void SendMessageToAllClients(String message) {
        for (ClientSocketManager current : clients) {
            if (current != null) {
                current.SendMessage(message);
            }
        }
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
            while (this.isEnabled) {
                //clients.add( new ClientSocketManager( 
                //      serverSocket.accept(),this));
                Socket receivedSocket = serverSocket.accept();
                ClientSocketManager freeClientSocketManager
                        = getNotBusyClientSocketManager();
                if (freeClientSocketManager != null) {
                    freeClientSocketManager.assignSocketToThisThread(receivedSocket);
                    RUT.setClients(clients);
                } else {
                    try {
                        receivedSocket.close();
                    } catch (Exception error) {

                    }
                }
            }
        } catch (Exception error) {
            this.caller.ErrorHasBeenThrown(error);
        }
    }

    @Override
    public void MessageReceiveFromClient(Socket clientSocket, byte[] data) {
        System.out.println("R: " + new String(data));
//        SendMessageToAllClients(":"+clientSocket.getInetAddress()+
//                                ":"+clientSocket.getInetAddress().getHostName()+
//                                ":"+clientSocket.getPort()
//                                +": "+new String(data));
        SendMessageToAllClients(new String(data));
    }

    @Override
    public void ErrorHasBeenThrown(Exception error) {

    }

}
