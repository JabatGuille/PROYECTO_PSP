package Clases;

import Servidor.Conexiones;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Encryption {
    /**
     * Encripta la contraseña
     *
     * @param sinCifrar
     * @return
     * @throws Exception
     */
    public static String cifra(String sinCifrar) throws Exception {
        final byte[] bytes = sinCifrar.getBytes("UTF-8");
        final Cipher aes = obtieneCipher(true);
        final byte[] cifrado = aes.doFinal(bytes);

        return Base64.getEncoder().encodeToString(cifrado);
    }

    /**
     * Prepara el cipher para encriptar la contraseña
     *
     * @param paraCifrar
     * @return
     * @throws Exception
     */
    private static Cipher obtieneCipher(boolean paraCifrar) throws Exception {
        final String frase = "FraseLargaConDiferentesLetrasNumerosYCaracteresEspeciales_áÁéÉíÍóÓúÚüÜñÑ1234567890!#%$&()=%_NO_USAR_ESTA_FRASE!_";
        final MessageDigest digest = MessageDigest.getInstance("SHA");
        digest.update(frase.getBytes("UTF-8"));
        final SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

        final Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        if (paraCifrar) {
            aes.init(Cipher.ENCRYPT_MODE, key);
        } else {
            aes.init(Cipher.DECRYPT_MODE, key);
        }
        return aes;
    }

    /**
     * Encripta los datos de registro de un cliente
     *
     * @return
     */
    public static byte[] encriptarRegistroCliente() {
        try {
            Singleton singleton = Singleton.getInstance();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            //SE CREA EL PAR DE CLAVES PRIVADA Y PÚBLICA
            KeyPair par = keyGen.generateKeyPair();
            singleton.clavePrivada = par.getPrivate();
            singleton.clavePublica = par.getPublic();
            //FIRMA CON CLAVE PRIVADA EL MENSAJE
            //AL OBJETO Signature SE LE SUMINISTRAN LOS DATOS A FIRMAR
            Signature dsa = Signature.getInstance("SHA1withRSA");
            dsa.initSign(singleton.clavePrivada);
            String mensaje = Singleton.url;
            dsa.update(mensaje.getBytes());
            return dsa.sign(); //MENSAJE FIRMADO
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error en el algoritmo");
        } catch (SignatureException e) {
            System.out.println("Error Signature");
        } catch (InvalidKeyException e) {
            System.out.println("Clave invalida");
        }
        return null;
    }

    /**
     * Verifica la firma del registro del cliente
     *
     * @param firma
     * @return
     */
    public static boolean verificarRegistroCliente(byte[] firma) {
        try {
            Singleton singleton = Singleton.getInstance();
            Signature verificada = Signature.getInstance("SHA1withRSA");
            verificada.initVerify(singleton.clavePublica);
            verificada.update(Singleton.url.getBytes());
            return verificada.verify(firma);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Genera una secretKey
     */
    public static void secretKey() {
        try {
            System.out.println("Obteniendo generador de claves con cifrado AES");
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            System.out.println("Generando clave");
            keygen.init(128);//definimos tamaño de clave de 128 bits
            SecretKey key = keygen.generateKey();
            Singleton singleton = Singleton.getInstance();
            singleton.secretKey = key;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Encripta los datos de un cliente
     *
     * @param cliente
     * @param outObjeto
     */
    public static void encriptarCliente(Cliente cliente, ObjectOutputStream outObjeto) {
        Singleton singleton = Singleton.getInstance();
        try {
            Cipher desCipher = Cipher.getInstance("AES");
            // PASO 3a: Poner cifrador en modo CIFRADO
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            // CIFRADO
            outObjeto.writeObject(desCipher.doFinal(cliente.getDNI().getBytes()));
            outObjeto.writeObject(desCipher.doFinal(cliente.getNombre().getBytes()));
            outObjeto.writeObject(desCipher.doFinal(cliente.getApellido().getBytes()));
            outObjeto.writeObject(desCipher.doFinal(String.valueOf(cliente.getEdad()).getBytes()));
            outObjeto.writeObject(desCipher.doFinal(cliente.getEmail().getBytes()));
            outObjeto.writeObject(desCipher.doFinal(cliente.getContraseña().getBytes()));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta los datos de un cliente
     *
     * @param DNI
     * @param nombre
     * @param apellido
     * @param edad
     * @param email
     * @param contraseña
     */
    public static void desEncriptarCliente(byte[] DNI, byte[] nombre, byte[] apellido, byte[] edad, byte[] email, byte[] contraseña) {
        try {
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            String desDNI = new String(desCipher.doFinal(DNI));
            String desNombre = new String(desCipher.doFinal(nombre));
            String desApellido = new String(desCipher.doFinal(apellido));
            String desEdad = new String(desCipher.doFinal(edad));
            String desEmail = new String(desCipher.doFinal(email));
            String desContraseña = new String(desCipher.doFinal(contraseña));
            Cliente cliente = new Cliente(desDNI, desNombre, desApellido, Integer.parseInt(desEdad), desEmail, desContraseña);
            Conexiones.registro(cliente);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encripta los datos necesarios para el login
     *
     * @param DNI
     * @param contraseña
     * @param outObjeto
     */
    public static void encriptarLogin(String DNI, String contraseña, ObjectOutputStream outObjeto) {
        Singleton singleton = Singleton.getInstance();
        try {
            Cipher desCipher = Cipher.getInstance("AES");
            // PASO 3a: Poner cifrador en modo CIFRADO
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            // CIFRADO
            outObjeto.writeObject(desCipher.doFinal(DNI.getBytes()));
            outObjeto.writeObject(desCipher.doFinal(contraseña.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta los datos necesarios para el login
     *
     * @param DNI
     * @param contraseña
     * @param outObjeto
     */
    public static void desEncriparLogin(byte[] DNI, byte[] contraseña, ObjectOutputStream outObjeto) {
        try {
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            String desDNI = new String(desCipher.doFinal(DNI));
            String desContraseña = new String(desCipher.doFinal(contraseña));
            outObjeto.writeObject(Conexiones.login(desDNI, desContraseña));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Encripta los datos necesarios para realizar una trasferencia
     *
     * @param transaccion
     * @param outObjeto
     */
    public static void encriparHacerTransferencia(Transaccion transaccion, ObjectOutputStream outObjeto) {
        Singleton singleton = Singleton.getInstance();
        try {
            Cipher desCipher = Cipher.getInstance("AES");
            // PASO 3a: Poner cifrador en modo CIFRADO
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            // CIFRADO
            outObjeto.writeObject(desCipher.doFinal(transaccion.getDNI_cliente().getBytes()));
            outObjeto.writeObject(desCipher.doFinal(String.valueOf(transaccion.getIBAN_ORIGEN()).getBytes()));
            outObjeto.writeObject(desCipher.doFinal(String.valueOf(transaccion.getIBAN_DESTINO()).getBytes()));
            outObjeto.writeObject(desCipher.doFinal(String.valueOf(transaccion.getPrecio()).getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta los datos necesarios para hacer una transferencia
     *
     * @param DNI
     * @param IBAN_ORIGEN
     * @param IBAN_DESTINO
     * @param precio
     */
    public static void desEncriparHacerTransferencia(byte[] DNI, byte[] IBAN_ORIGEN, byte[] IBAN_DESTINO, byte[] precio) {
        try {
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            String desDNI = new String(desCipher.doFinal(DNI));
            int desIBANORIGEN = Integer.parseInt(new String(desCipher.doFinal(IBAN_ORIGEN)));
            int desIBANDESTINO = Integer.parseInt(new String(desCipher.doFinal(IBAN_DESTINO)));
            double desPrecio = Double.parseDouble(new String(desCipher.doFinal(precio)));
            Transaccion transaccion = new Transaccion(desDNI, desIBANORIGEN, desIBANDESTINO, desPrecio);
            Conexiones.hacerTransferencia(transaccion);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encripta la lista de cuentas de un cliente
     *
     * @param cuentas
     * @param outObjeto
     */
    public static void encriparListaCuentas(ArrayList<Cuenta> cuentas, ObjectOutputStream outObjeto) {
        Singleton singleton = Singleton.getInstance();
        try {
            Cipher desCipher = Cipher.getInstance("AES");
            // PASO 3a: Poner cifrador en modo CIFRADO
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            // CIFRADO
            for (int i = 0; i < cuentas.size(); i++) {
                outObjeto.writeObject(desCipher.doFinal(String.valueOf(cuentas.get(i).getIBAN()).getBytes()));
                outObjeto.writeObject(desCipher.doFinal(String.valueOf(cuentas.get(i).getDinero()).getBytes()));

            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta la lista de cuentas de un cliente
     *
     * @param totalCuentas
     * @param inObjeto
     * @return
     */
    public static ArrayList<Cuenta> desEncriparListaCuentas(int totalCuentas, ObjectInputStream inObjeto) {
        try {
            ArrayList<Cuenta> cuentas = new ArrayList<>();
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            for (int i = 0; i < totalCuentas; i++) {
                int desIBAN = Integer.parseInt(new String(desCipher.doFinal((byte[]) inObjeto.readObject())));
                double desDinero = Double.parseDouble(new String(desCipher.doFinal((byte[]) inObjeto.readObject())));
                cuentas.add(new Cuenta(desIBAN, desDinero));
            }
            return cuentas;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encripta un DNI
     *
     * @param outObjeto
     */
    public static void encriparDNI(ObjectOutputStream outObjeto) {
        Singleton singleton = Singleton.getInstance();
        try {
            Cipher desCipher = Cipher.getInstance("AES");
            // PASO 3a: Poner cifrador en modo CIFRADO
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            // CIFRADO
            outObjeto.writeObject(desCipher.doFinal(singleton.DNI.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta un DNI
     *
     * @param DNI
     * @return
     */
    public static String desEncriptarDNI(byte[] DNI) {
        try {
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            return new String(desCipher.doFinal(DNI));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void encriparVerTransferencias(ArrayList<Transaccion> transaccions, ObjectOutputStream outObjeto) {
        Singleton singleton = Singleton.getInstance();
        try {
            Cipher desCipher = Cipher.getInstance("AES");
            // PASO 3a: Poner cifrador en modo CIFRADO
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            // CIFRADO
            for (int i = 0; i < transaccions.size(); i++) {
                outObjeto.writeObject(desCipher.doFinal(String.valueOf(transaccions.get(i).getIBAN_ORIGEN()).getBytes()));
                outObjeto.writeObject(desCipher.doFinal(String.valueOf(transaccions.get(i).getIBAN_DESTINO()).getBytes()));
                outObjeto.writeObject(desCipher.doFinal(String.valueOf(transaccions.get(i).getPrecio()).getBytes()));

            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta los datos de las trasferencias que se quieren ver
     *
     * @param totalTransaccion
     * @param inObjeto
     * @return
     */
    public static ArrayList<Transaccion> desEncriparVerTransferencia(int totalTransaccion, ObjectInputStream inObjeto) {
        try {
            ArrayList<Transaccion> transaccions = new ArrayList<>();
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            for (int i = 0; i < totalTransaccion; i++) {
                int desIBANORIGEN = Integer.parseInt(new String(desCipher.doFinal((byte[]) inObjeto.readObject())));
                int desIBANDESTINO = Integer.parseInt(new String(desCipher.doFinal((byte[]) inObjeto.readObject())));
                double desDinero = Double.parseDouble(new String(desCipher.doFinal((byte[]) inObjeto.readObject())));
                transaccions.add(new Transaccion("", desIBANORIGEN, desIBANDESTINO, desDinero));
            }
            return transaccions;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encripta el codigo de confirmacion de hacer una transferencia
     *
     * @param codigo
     * @param outObjeto
     */
    public static void encriptarCodigo(String codigo, ObjectOutputStream outObjeto) {
        try {
            System.out.println("El codigo es " + codigo);
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.ENCRYPT_MODE, singleton.secretKey);
            outObjeto.writeObject(desCipher.doFinal(codigo.getBytes()));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desencripta el codigo de confirmacion de una transferencia
     *
     * @param codigo
     * @return
     */
    public static int desEncriptarCodigo(byte[] codigo) {
        try {
            Singleton singleton = Singleton.getInstance();
            Cipher desCipher = Cipher.getInstance("AES");
            desCipher.init(Cipher.DECRYPT_MODE, singleton.secretKey);
            return Integer.parseInt(new String(desCipher.doFinal(codigo)));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

    }
}
