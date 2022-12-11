package Clases;

public final class Singleton {
    private static Singleton instance;
    public String DNI;

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

