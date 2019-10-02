/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.tcp;

import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author maxim
 */
public class RUTManager extends Thread implements TCPServiceManagerCallerInterface{
    
    Vector<ClientSocketManager> clients;
    long startTime;
    long timeBeforeDeleted;
    String messageRecieved;
    
    public RUTManager(Vector<ClientSocketManager> clients) {
        this.clients = clients;
        this.start();
    }
    
    @Override
    public void run(){
        startTime = System.currentTimeMillis();
        while(true){
            if (System.currentTimeMillis() - startTime > 3000) {
                for(ClientSocketManager clientSocketManager : clients){
                    this.messageRecieved = "";
                    if (clientSocketManager != null) {
                        clientSocketManager.SendMessage("ping");
                    }
                    timeBeforeDeleted = System.currentTimeMillis();
//                    if (System.currentTimeMillis() - timeBeforeDeleted > 5000) {
//                        if(this.messageRecieved.equals("pong")){
//                            clientSocketManager.clearLastSocket();
//                        }
//                    }
                }
                startTime = System.currentTimeMillis();
            }
        }
    }

    public void setClients(Vector<ClientSocketManager> clients) {
        this.clients = clients;
    }

    @Override
    public void MessageReceiveFromClient(Socket clientSocket, byte[] data) {
        this.messageRecieved = new String(data);
    }

    @Override
    public void ErrorHasBeenThrown(Exception error) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
