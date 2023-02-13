import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Encryptor {
//
    public String encryptString(String pass,String email,String salt) throws NoSuchAlgorithmException{


        MessageDigest sha = MessageDigest.getInstance("SHA-256");


        byte[] messageDigest = sha.digest(((salt+pass+email)).getBytes());

       // email+pass+user
        BigInteger bigInt = new BigInteger(1,messageDigest);


        //System.out.println(salt+" "+pass+" "+ email +" "+bigInt.toString(16));

        return bigInt.toString(16);

    }

    public static void main(String[] args) throws  NoSuchAlgorithmException{
        Encryptor encryptor = new Encryptor();


        //create manually an admin and get hash
        System.out.println(encryptor.encryptString("adminpass2","admin2","UVDu!@vsf2"));

    }



}
