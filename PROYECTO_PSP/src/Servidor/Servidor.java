package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5555);
            while (true) {
                Socket cliente = server.accept();
                ServerHilo serverHilo = new ServerHilo(cliente);
                serverHilo.start();
            }
        } catch (IOException e) {
            System.out.println("Error desconocido");
        }
    }
}
