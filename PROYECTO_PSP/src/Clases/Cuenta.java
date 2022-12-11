package Clases;

public class Cuenta {

    private int IBAN;
    private double dinero;

    public Cuenta() {
    }

    public Cuenta(int IBAN, double dinero) {
        this.IBAN = IBAN;
        this.dinero = dinero;
    }

    public int getIBAN() {
        return IBAN;
    }

    public void setIBAN(int IBAN) {
        this.IBAN = IBAN;
    }

    public double getDinero() {
        return dinero;
    }

    public void setDinero(double dinero) {
        this.dinero = dinero;
    }
}
