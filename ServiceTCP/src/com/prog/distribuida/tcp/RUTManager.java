/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog.distribuida.tcp;

import java.util.Vector;

/**
 *
 * @author maxim
 */
public class RUTManager extends Thread{
    
    Vector<ClientSocketManager> clients;
    long startTime;
    
    public RUTManager(Vector<ClientSocketManager> clients) {
        this.clients = clients;
        this.start();
    }
    
    @Override
    public void run(){
        startTime = System.currentTimeMillis();
        while(true){
            if (System.currentTimeMillis() - startTime > 30000) {
                for(ClientSocketManager clientSocketManager : clients){
                    if (clientSocketManager != null) {
                        clientSocketManager.SendMessage("ping");
                    }
                }
                startTime = System.currentTimeMillis();
            }
        }
    }

    public void setClients(Vector<ClientSocketManager> clients) {
        this.clients = clients;
    }
    
}
