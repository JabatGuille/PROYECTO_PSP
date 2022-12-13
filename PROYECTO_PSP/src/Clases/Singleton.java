package Clases;

import java.security.PrivateKey;
import java.security.PublicKey;

public final class Singleton {
    private static Singleton instance;
    public String DNI;
    public PrivateKey clavePrivada;
    public PublicKey clavePublica;

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

