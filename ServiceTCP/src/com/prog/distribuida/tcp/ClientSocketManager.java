package com.prog.distribuida.tcp;
import com.prog.distribuida.tcp.TCPServiceManagerCallerInterface;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientSocketManager extends Thread{
        Socket clientSocket;

        BufferedReader reader;
        PrintWriter writer;
        boolean isEnabled=true;
        private TCPServiceManagerCallerInterface caller;
        private String serverIpAddress;
        private int port;
        final Object mutex=new Object();
        
        
        public void waitForAWhile(){
            try{                
                synchronized(mutex){
                    mutex.wait();
                }
            }catch (Exception ex) {                
            }
        }
        
        public void notifyMutex(){
            try{                
                synchronized(mutex){
                    mutex.notify();
                }
            }catch (Exception ex) {                
            }
        }
        
        public ClientSocketManager(TCPServiceManagerCallerInterface caller) {
            this.caller=caller;            
            this.start();            
        }
        
        public ClientSocketManager(Socket clientSocket,
                TCPServiceManagerCallerInterface caller) {
            this.clientSocket = clientSocket;
            this.start();
            this.caller=caller;            
        }
        
        public ClientSocketManager(String serverIpAddress,
                int port,
                TCPServiceManagerCallerInterface caller) {
            this.serverIpAddress=serverIpAddress;
            this.port=port;            
            this.caller=caller;            
            this.start();
        }
        
        public void assignSocketToThisThread(Socket socket){
            this.clientSocket=socket;
            this.notifyMutex();
        }
        
        public boolean initializeSocket(){
            try{
                this.clientSocket=new Socket(serverIpAddress,port);
                return true;
            }catch (Exception ex) {
                
            }
            return false;
        }
        
        public boolean initializeStreams(){
            try{
                if(clientSocket==null){
                    if(!initializeSocket()){
                        return false;
                    }
                }
                reader=new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                writer=new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()),true);
                return true;
            }catch (Exception ex) {
                caller.ErrorHasBeenThrown(ex);
            }
            return false;
        }
        
        @Override
        public void run(){
            try{
                while(isEnabled){
                    if(clientSocket==null){
                        this.waitForAWhile();
                    }
                    if(initializeStreams()){
                        String newMessage=null;
                        while((newMessage=this.reader.readLine())!=null){
                            System.out.println("TCP SERVICE GOT SOMETHING!");
                            caller.MessageReceiveFromClient(clientSocket, newMessage.getBytes());
                        }
                    }
                    clearLastSocket();
                }
            }catch (Exception ex) {
                
            }
        }
        
        public void SendMessage(String message){
            try{
                if(clientSocket.isConnected()){
                    writer.write(message+"\n");
                    writer.flush();
                }
            }catch (Exception ex) {
                caller.ErrorHasBeenThrown(ex);
            }
        }

        private void clearLastSocket() {
            caller.MessageReceiveFromClient(clientSocket, "El que tenia aqui se desconectó".getBytes());
            System.out.println("ESTE ES EL QUE ME VA A DECIR");
            try{
                writer.close();
            }catch (Exception ex) {
                
            }
            try{
                reader.close();
            }catch (Exception ex) {
                
            }
            try{
                clientSocket.close();
            }catch (Exception ex) {
                
            }
            clientSocket=null;
        }
        
        public boolean isThisThreadBusy(){
            return clientSocket!=null;
        }        
        
    }