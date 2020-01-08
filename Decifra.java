import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decifra{

    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public static String aesCbcDecrypt(String cripto, byte[] Key, byte[] iv) {
        try {
            IvParameterSpec Vetor = new IvParameterSpec(iv);
            SecretKeySpec Chave = new SecretKeySpec(Key, "AES");
     
            Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cifra.init(Cipher.DECRYPT_MODE, Chave, Vetor);

            byte[] original = cifra.doFinal(cripto.getBytes()); 
            return new String(original);
        } catch (Exception ex) {
            System.out.println("Erro ao decifrar mensagem");
            ex.printStackTrace();
        }
     
        return null;
    }

    public static byte[] sha256(byte[] text) throws NoSuchAlgorithmException{

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(text);
        return digest;    
}

    public static void main(String [] args){
        //LER FICHEIRO LINHA-A-LINHA
        String array[] = new String[5];       
        try {
            int i = 0;
            Scanner scanner = new Scanner(new File(args[0])); //NOME DADO COMO PARÂMETRO
            while(scanner.hasNextLine()){
                 array[i]=(scanner.nextLine());
                 i++;         
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int iteracoes = Integer.parseInt(array[0]);
        String question = new String(Base64.getDecoder().decode(array[1]));
        BigInteger randN = new BigInteger(array[2]);
        String cripto = new String(Base64.getDecoder().decode(array[3]));
        byte[] iv = (Base64.getDecoder().decode(array[4]));

        System.out.println(question);
        System.out.print("Introduzir resposta: ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        answer = answer.toLowerCase();
        scanner.close();

        byte[] randPassConcat = (answer+randN).getBytes();

        for(int i=0;i<iteracoes;i++){
            try {
                randPassConcat = sha256(randPassConcat);
            } catch (Exception e) {
                System.out.println("Erro ao calcular Hash");
                e.printStackTrace();
            }
        }
        String key = bytesToHex(randPassConcat);
        System.out.println("KEY: "+key);

        //PRINT IV AS HEX
        System.out.println("IV: "+bytesToHex(iv));

        System.out.println(aesCbcDecrypt(cripto, randPassConcat, iv)); 
    }
}
