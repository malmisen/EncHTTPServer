/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverside;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
/**
 *
 * @author regularclip
 */
public class HTTPServer {
    
    private final static int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        SSLServerSocketFactory ssf;
        
        
        try{
            KeyStore ks = null;
            
            ks = KeyStore.getInstance("PKCS12");
            InputStream is = null;
            
            is = new FileInputStream(new File("/Users/regularclip/Documents/school/network_programming/cert.pfx"));
            char[] password = "regular".toCharArray();
            ks.load(is, password);
            SSLContext cntx = SSLContext.getInstance("SSL");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);
            
            cntx.init(kmf.getKeyManagers(), null, null);
            
            ssf = cntx.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket)ssf.createServerSocket(433);
           
            
            System.out.println("The server is online on port " + 433 + "...");
            int userId = 0;
            //Initialize the Games, this class keeps track of ongoing games!
            Games games = new Games();
            while(true){
                System.out.println("Accepting connections...");
                Socket connectionSocket = serverSocket.accept();
                System.out.println("New connection made");
                userId++;

                //Handle client
                Runnable ch = new ConnectionHandler(connectionSocket, games);
                Thread t = new Thread(ch);
                t.start();
             }
            
        } catch (KeyStoreException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } 
    
}
