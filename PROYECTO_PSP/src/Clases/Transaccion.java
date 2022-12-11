package Clases;

import java.io.Serializable;

public class Transaccion implements Serializable {
    private int IBAN_ORIGEN;
    private int IBAN_DESTINO;
    private double precio;

    public Transaccion() {
    }

    public Transaccion(int IBAN_ORIGEN, int IBAN_DESTINO, double precio) {
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
}
