package Clases;

import java.io.Serializable;
import java.util.ArrayList;

public class Cliente implements Serializable {
    private String DNI;
    private String Nombre;
    private String Apellido;
    private int Edad;
    private String Email;
    private String contraseña;

    private ArrayList<Cuenta> cuentas = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String DNI, String nombre, String apellido, int edad, String email, String contraseña) {
        this.DNI = DNI;
        this.Nombre = nombre;
        Apellido = apellido;
        Edad = edad;
        Email = email;
        this.contraseña = contraseña;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public int getEdad() {
        return Edad;
    }

    public void setEdad(int edad) {
        Edad = edad;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "DNI='" + DNI + '\'' +
                ", Nombre='" + Nombre + '\'' +
                ", Apellido='" + Apellido + '\'' +
                ", Edad=" + Edad +
                ", Email='" + Email + '\'' +
                ", contraseña='" + contraseña + '\'' +
                ", cuentas=" + cuentas +
                '}';
    }
}
