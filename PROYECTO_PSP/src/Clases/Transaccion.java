package Clases;

import java.io.Serializable;

public class Transaccion implements Serializable {
    private String DNI_cliente;
    private int IBAN_ORIGEN;
    private int IBAN_DESTINO;
    private double precio;

    public Transaccion() {
    }

    public Transaccion(String DNI_cliente,int IBAN_ORIGEN, int IBAN_DESTINO, double precio) {
        this.DNI_cliente = DNI_cliente;
        this.IBAN_ORIGEN = IBAN_ORIGEN;
        this.IBAN_DESTINO = IBAN_DESTINO;
        this.precio = precio;
    }

    public int getIBAN_ORIGEN() {
        return IBAN_ORIGEN;
    }

    public void setIBAN_ORIGEN(int IBAN_ORIGEN) {
        this.IBAN_ORIGEN = IBAN_ORIGEN;
    }

    public int getIBAN_DESTINO() {
        return IBAN_DESTINO;
    }

    public void setIBAN_DESTINO(int IBAN_DESTINO) {
        this.IBAN_DESTINO = IBAN_DESTINO;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDNI_cliente() {
        return DNI_cliente;
    }

    public void setDNI_cliente(String DNI_cliente) {
        this.DNI_cliente = DNI_cliente;
    }
}
