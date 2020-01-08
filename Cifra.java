import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.PrintWriter;

public class Cifra{

    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public static byte[] sha256(byte[] text) throws NoSuchAlgorithmException{

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text);
            return digest;    
    }
    public static String aesCbc(String message, byte[] Key, byte[] iv){
        try{
            IvParameterSpec Vetor = new IvParameterSpec(iv);
            SecretKeySpec Chave = new SecretKeySpec(Key,"AES");

            Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cifra.init(Cipher.ENCRYPT_MODE, Chave, Vetor);

            byte[] cifrado = cifra.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(cifrado);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Erro ao cifrar mensagem");
        }
        return null;
    }

    public static void main(String[] args){

        //USER IMPUT BEGIN
        System.out.println("Qual a pergunta?");
        Scanner scanner = new Scanner(System.in);
        String question = scanner.nextLine();

        System.out.println("Qual a resposta?");
        String answer = scanner.nextLine();
        answer = answer.toLowerCase();

        System.out.println("Escrever mensagem a enviar: ");
        String message = scanner.nextLine();
        scanner.close();
        //USER IMPUT END

        //GERAR NÚMERO ALEATÓRIO DE 128 BITS (16 BYTES)
        SecureRandom random = new SecureRandom();
        BigInteger randN = (new BigInteger(random.generateSeed(16))).abs();

        //CONCATENAÇÃO PASS+ALEATÓRIO
        byte[] randPassConcat = (answer+randN).getBytes();

        //CHAMADA DO MÉTODO DE HASHING
        int iteracoes = 0;
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis()-start<15000){
            try{
                randPassConcat = sha256(randPassConcat); 
                iteracoes++;                    
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Erro ao calcular hash");
            }                 
        }
        byte[] iv = random.generateSeed(16);

        String cripto = aesCbc(message,randPassConcat,iv);


        String key = bytesToHex(randPassConcat);
        System.out.println("KEY: "+key);

        //PRINT IV AS HEX
        System.out.println("IV: "+bytesToHex(iv));

        String base64question = Base64.getEncoder().encodeToString(question.getBytes());
        //String base64cripto   = Base64.getEncoder().encodeToString(cripto.getBytes());
        String base64iv       = Base64.getEncoder().encodeToString(iv);
        try{
            PrintWriter writer = new PrintWriter("file.txt","UTF-8");
            writer.println(iteracoes); // NÚMERO ITERAÇÕES (int)
            writer.println(base64question); // PERGUNTA (string -> base64 string)
            writer.println(randN); // NÚMERO ALEATÓRIO (bigInt)
            writer.println(cripto); // CRIPTOGRAMA (string -> base64 string)
            writer.println(base64iv); // VECTOR INICIALIZAÇÃO (byte[] -> base64 string)
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }   
}