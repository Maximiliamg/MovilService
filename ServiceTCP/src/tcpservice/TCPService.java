/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpservice;

import com.prog.distribuida.tcp.TCPServiceManager;
import com.prog.distribuida.tcp.TCPServiceManagerCallerInterface;
import java.net.Socket;

/**
 *
 * @author Administrador
 */
public class TCPService implements TCPServiceManagerCallerInterface{

    public TCPService(){
        new TCPServiceManager(9090,this);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TCPService();
    }

    @Override
    public void MessageReceiveFromClient(Socket clientSocket, byte[] data) {
        System.out.println(clientSocket.getInetAddress().getHostName()
                +":"+clientSocket.getPort()+": "+new String(data));
        
    }

    @Override
    public void ErrorHasBeenThrown(Exception error) {
        
    }
    
}
