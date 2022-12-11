package Servidor;

import Clases.Cliente;
import Clases.Cuenta;
import Clases.Transaccion;

import java.sql.*;
import java.util.ArrayList;

public class Conexiones {
    static String conexion_string = "jdbc:mysql://localhost/banco";
    static String usuario = "root";
    static String contraseña = "12345";

    public static boolean login(String DNI, String contraseñaCliente) {
        try {
            Connection conexion = DriverManager.getConnection(conexion_string, usuario, contraseña);
            String sql = "SELECT Count(*) FROM CLIENTE where DNI=? and contraseña=?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, DNI);
            statement.setString(2, contraseñaCliente);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            int total = 0;
            while (resultSet.next()) {
                total = resultSet.getInt("Count(*)");
            }
            conexion.close();
            return total == 1;
        } catch (SQLException r) {
            r.printStackTrace();
        }
        return false;
    }

    public static boolean registro(Cliente cliente) {
        try {
            Connection conexion = DriverManager.getConnection(conexion_string, usuario, contraseña);
            String sql = "INSERT INTO CLIENTE (DNI,Nombre,Apellido,Edad,Email,contraseña) values (?,?,?,?,?,?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, cliente.getDNI());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getApellido());
            statement.setInt(4, cliente.getEdad());
            statement.setString(5, cliente.getEmail());
            statement.setString(6, cliente.getContraseña());
            statement.executeUpdate();
            System.out.println("Cliente añadido");
            sql = "insert into cuentabancaria(dinero,DNI_cliente) values (?,?)";
            statement = conexion.prepareStatement(sql);
            statement.setDouble(1, 0);
            statement.setString(2, cliente.getDNI());
            statement.executeUpdate();
            conexion.close();
        } catch (SQLException r) {
            r.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Cuenta> recuperarCuentas(String DNI) {
        ArrayList<Cuenta> cuentas = new ArrayList<>();
        try {
            Connection conexion = DriverManager.getConnection(conexion_string, usuario, contraseña);
            String sql = "SELECT * FROM cuentabancaria where DNI_cliente=?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, DNI);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                int IBAN = resultSet.getInt("IBAN");
                double dinero = resultSet.getDouble("dinero");
                cuentas.add(new Cuenta(IBAN, dinero));
            }
            conexion.close();
        } catch (SQLException r) {
            r.printStackTrace();
        }
        return cuentas;
    }

    public static ArrayList<Transaccion> recuperarTransacciones(String DNI) {
        ArrayList<Transaccion> transaccions = new ArrayList<>();
        try {
            Connection conexion = DriverManager.getConnection(conexion_string, usuario, contraseña);
            String sql = "select * from transferencia join cuentabancaria on DNI_cliente = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, DNI);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                transaccions.add(new Transaccion(resultSet.getInt("IBAN_ORIGEN"), resultSet.getInt("IBAN_DESTINO"), resultSet.getDouble("dinero")));
            }
            conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transaccions;
    }

    public static void hacerTransferencia(Transaccion transaccion) {
        try {
            Connection conexion = DriverManager.getConnection(conexion_string, usuario, contraseña);

            String sql = "select dinero from cuentabancaria where IBAN = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, transaccion.getIBAN_ORIGEN());
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            double dinero = 0;
            while (resultSet.next()) {
                dinero = resultSet.getDouble("dinero");
            }
            if (dinero - transaccion.getPrecio() >= 0) {
                sql = "select IBAN from cuentabancaria where IBAN = ?";
                statement = conexion.prepareStatement(sql);
                statement.setInt(1, transaccion.getIBAN_DESTINO());
                statement.execute();
                resultSet = statement.getResultSet();
                int IBAN = 0;
                while (resultSet.next()) {
                    IBAN = resultSet.getInt("IBAN");
                }
                if (IBAN != 0) {
                    sql = "insert into transferencia(IBAN_ORIGEN,IBAN_DESTINO,dinero) values (?,?,?)";
                    statement = conexion.prepareStatement(sql);
                    statement.setInt(1, transaccion.getIBAN_ORIGEN());
                    statement.setInt(2, transaccion.getIBAN_DESTINO());
                    statement.setDouble(3, transaccion.getPrecio());
                    statement.execute();

                    sql = "UPDATE cuentabancaria SET dinero = ? WHERE IBAN = ?";
                    statement = conexion.prepareStatement(sql);
                    statement.setDouble(1, dinero - transaccion.getPrecio());
                    statement.setInt(2, transaccion.getIBAN_ORIGEN());
                    statement.execute();

                    sql = "select dinero from cuentabancaria where IBAN = ?";
                    statement = conexion.prepareStatement(sql);
                    statement.setInt(1, transaccion.getIBAN_DESTINO());
                    statement.execute();
                    resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        dinero = resultSet.getDouble("dinero");
                    }
                    sql = "UPDATE cuentabancaria SET dinero = ? WHERE IBAN = ?";
                    statement = conexion.prepareStatement(sql);
                    statement.setDouble(1, dinero + transaccion.getPrecio());
                    statement.setInt(2, transaccion.getIBAN_DESTINO());
                    statement.execute();
                }
            }
            conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void crearCuenta(String DNI) {
        try {
            Connection conexion = DriverManager.getConnection(conexion_string, usuario, contraseña);
            String sql = "insert into cuentabancaria(dinero,DNI_cliente) values (?,?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setDouble(1, 0);
            statement.setString(2, DNI);
            statement.executeUpdate();
            conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}