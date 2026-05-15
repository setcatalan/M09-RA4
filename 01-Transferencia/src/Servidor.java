import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void connectar(){
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
            System.out.println("Esperant connexio...");
            clientSocket = serverSocket.accept();
            System.out.println("Connexio acceptada: " + clientSocket.getInetAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarFitxers(){

    }

    public static void main(String[] args) {
        
    }
}