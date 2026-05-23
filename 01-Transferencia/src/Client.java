import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp";
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private Socket socket;

    public void connectar() throws IOException {
        System.out.println("Connectant a -> " + HOST + ":" + PORT);
        socket = new Socket(HOST, PORT);
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public void rebreFitxers() throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine().trim();

            if (nomFitxer.equalsIgnoreCase("sortir")) {
                System.out.println("Sortint...");
                sortida.writeObject("sortir");
                sortida.flush();
                continuar = false;
            } else {
                sortida.writeObject(nomFitxer);
                sortida.flush();

                byte[] contingut = null;
                try {
                    contingut = (byte[]) entrada.readObject();
                } catch (ClassNotFoundException e) {
                    System.out.println("Error rebent el fitxer: " + e.getMessage());
                }

                if (contingut != null) {
                    String nomSense = new File(nomFitxer).getName();
                    String rutaDesti = DIR_ARRIBADA + "/" + nomSense;
                    System.out.println("Nom del fitxer a guardar: " + rutaDesti);
                    Files.write(Paths.get(rutaDesti), contingut);
                    System.out.println("Fitxer rebut i guardat com: " + rutaDesti);
                } else {
                    System.out.println("El servidor no ha pogut enviar el fitxer.");
                }
            }
        }

        scanner.close();
    }

    public void tancarConnexio() throws IOException {
        System.out.println("Connexio tancada.");
        if (sortida != null) sortida.close();
        if (entrada != null) entrada.close();
        if (socket != null && !socket.isClosed()) socket.close();
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxers();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                client.tancarConnexio();
            } catch (IOException e) {
                System.out.println("Error tancant connexió: " + e.getMessage());
            }
        }
    }
}