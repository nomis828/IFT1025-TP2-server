package clients.client_simple;

public class ClientLauncher {

    public static void main(String[] args) {
        LigneDeCommandeClient client;
        try {
            client = new LigneDeCommandeClient();
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
