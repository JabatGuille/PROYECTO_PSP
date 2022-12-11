package Servidor;

import Clases.Cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerHilo extends Thread {
    Socket cliente;

    public ServerHilo(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        super.run();
        try {
            ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
            String peticion = (String) inObjeto.readObject();
            switch (peticion) {
                case "LOGIN": {
                    System.out.println("LOGIN");
                    String DNI = (String) inObjeto.readObject();
                    String contraseña = (String) inObjeto.readObject();
                    outObjeto.writeObject(Conexiones.login(DNI, contraseña));
                    break;
                }
                case "REGISTRO": {
                    Cliente cliente = (Cliente) inObjeto.readObject();
                    Conexiones.registro(cliente);
                    break;
                }
                case "LISTARCUENTAS": {
                    String DNI = (String) inObjeto.readObject();
                    outObjeto.writeObject(Conexiones.recuperarCuentas(DNI));
                    break;
                }
                case "VERTRASFERENCIAS": {
                    String DNI = (String) inObjeto.readObject();
                    break;
                }
                case "HACER_TRANSFERENCIA": {
                    
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Error tipeo de datos");
        }


    }
}
