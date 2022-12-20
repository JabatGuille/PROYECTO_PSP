package Servidor;

import Clases.Cuenta;
import Clases.Encryption;
import Clases.Singleton;
import Clases.Transaccion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHilo extends Thread {
    Socket cliente;

    public ServerHilo(Socket cliente) {
        this.cliente = cliente;
    }

    /**
     * Se encarga de toda la logica de recibir y enviar datos entre cliente y servidor
     */
    @Override
    public void run() {
        super.run();
        try {
            Encryption.secretKey();
            Singleton singleton = Singleton.getInstance();
            ObjectOutputStream outObjeto = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream inObjeto = new ObjectInputStream(cliente.getInputStream());
            String peticion = (String) inObjeto.readObject();
            switch (peticion) {
                case "LOGIN": {
                    System.out.println("LOGIN");
                    outObjeto.writeObject(singleton.secretKey);
                    byte[] DNI = (byte[]) inObjeto.readObject();
                    byte[] contraseña = (byte[]) inObjeto.readObject();
                    Encryption.desEncriparLogin(DNI, contraseña, outObjeto);
                    break;
                }
                case "REGISTRO": {
                    byte[] mensajeFirmado = Encryption.encriptarRegistroCliente();
                    if (mensajeFirmado != null) {
                        outObjeto.writeObject(singleton.clavePublica);
                        outObjeto.writeObject(mensajeFirmado);
                        boolean comprobacion = (boolean) inObjeto.readObject();
                        if (comprobacion) {
                            outObjeto.writeObject(singleton.secretKey);
                            byte[] DNI = (byte[]) inObjeto.readObject();
                            byte[] nombre = (byte[]) inObjeto.readObject();
                            byte[] apellido = (byte[]) inObjeto.readObject();
                            byte[] edad = (byte[]) inObjeto.readObject();
                            byte[] email = (byte[]) inObjeto.readObject();
                            byte[] contraseña = (byte[]) inObjeto.readObject();
                            Encryption.desEncriptarCliente(DNI, nombre, apellido, edad, email, contraseña);
                        }
                    } else {
                        System.out.println("Error con la firma");
                    }
                    break;
                }
                case "LISTARCUENTAS": {
                    outObjeto.writeObject(singleton.secretKey);
                    byte[] DNI = (byte[]) inObjeto.readObject();
                    ArrayList<Cuenta> cuentas = Conexiones.recuperarCuentas(Encryption.desEncriptarDNI(DNI));
                    outObjeto.writeObject(cuentas.size());
                    Encryption.encriparListaCuentas(cuentas, outObjeto);
                    break;
                }
                case "CREARCUENTA": {
                    outObjeto.writeObject(singleton.secretKey);
                    byte[] DNI = (byte[]) inObjeto.readObject();
                    Conexiones.crearCuenta(Encryption.desEncriptarDNI(DNI));
                    break;
                }
                case "VERTRASFERENCIAS": {
                    outObjeto.writeObject(singleton.secretKey);
                    byte[] DNI = (byte[]) inObjeto.readObject();
                    ArrayList<Transaccion> transaccions = Conexiones.recuperarTransacciones(Encryption.desEncriptarDNI(DNI));
                    outObjeto.writeObject(transaccions.size());
                    Encryption.encriparVerTransferencias(transaccions, outObjeto);
                    break;
                }
                case "HACER_TRANSFERENCIA": {
                    outObjeto.writeObject(singleton.secretKey);
                    byte[] DNI = (byte[]) inObjeto.readObject();
                    byte[] IBAN_ORIGEN = (byte[]) inObjeto.readObject();
                    byte[] IBAN_DESTINO = (byte[]) inObjeto.readObject();
                    byte[] precio = (byte[]) inObjeto.readObject();
                    double fiveDigits = 10000 + Math.random() * 90000;
                    int codigo = (int) fiveDigits;
                    Encryption.encriptarCodigo(String.valueOf(codigo), outObjeto);
                    byte[] cEncriptado = (byte[]) inObjeto.readObject();
                    int codigoRecibido = Encryption.desEncriptarCodigo(cEncriptado);
                    if (codigo == codigoRecibido) {
                        outObjeto.writeObject(true);
                        Encryption.desEncriparHacerTransferencia(DNI, IBAN_ORIGEN, IBAN_DESTINO, precio);
                    } else {
                        outObjeto.writeObject(false);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error hilo");
        } catch (ClassNotFoundException e) {
            System.out.println("Error tipeo de datos");
        }


    }
}
