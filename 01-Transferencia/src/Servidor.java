import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getInetAddress().getHostAddress());
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        System.out.println("Tancant connexió amb el client: " + socket.getInetAddress().getHostAddress());
        if (sortida != null) sortida.close();
        if (entrada != null) entrada.close();
        if (socket != null && !socket.isClosed()) socket.close();
        if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
    }

    public void enviarFitxers(Socket socket) throws IOException {
        boolean continuar = true;
        while (continuar) {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = null;
            try {
                nomFitxer = (String) entrada.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Error llegint el fitxer del client: " + e.getMessage());
            }

            if (nomFitxer == null || nomFitxer.equalsIgnoreCase("sortir")) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                continuar = false;
            } else {
                System.out.println("Nomfitxer rebut: " + nomFitxer);
                try {
                    Fitxer fitxer = new Fitxer(nomFitxer);
                    byte[] contingut = fitxer.getContingut();
                    System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                    sortida.writeObject(contingut);
                    sortida.flush();
                    System.out.println("Fitxer enviat al client: " + nomFitxer);
                } catch (IOException e) {
                    System.out.println("Error llegint el fitxer del client: " + e.getMessage());
                    sortida.writeObject(null);
                    sortida.flush();
                }
            }
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = null;
        try {
            socket = servidor.connectar();
            servidor.enviarFitxers(socket);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    servidor.tancarConnexio(socket);
                } catch (IOException e) {
                    System.out.println("Error tancant connexió: " + e.getMessage());
                }
            }
        }
    }
}