package Clases;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public final class Singleton {
    private static Singleton instance;
    public String DNI;
    public PrivateKey clavePrivada;
    public PublicKey clavePublica;

    public SecretKey secretKey;
    public static String url = "https://www.egibide.org/privacidad/#:~:text=EGIBIDE%20facilita%20a%20las%20personas,la%20limitaci%C3%B3n%20de%20su%20tratamiento.";

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

