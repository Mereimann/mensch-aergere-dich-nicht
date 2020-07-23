package mensch.server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MenschServer {

    ServerSocket server;

    List<ConnectingClient> clientList = new ArrayList<>(4);

    private boolean isRunning = true;

    public MenschServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public void acceptClients() {
        System.out.println("Server started, waiting for client connections...");
        while(this.isRunning) {
            try {
                Socket client = server.accept();
                clientList.add(new ConnectingClient(this, client));
                System.out.println("Client connected! " + client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean allClientsReady() {
        for (ConnectingClient client:
             clientList) {
            if(!(client.isReady() || client.isDead())) {
                return false;
            }
        }
        return true;
    }

    public void startGame() {
        System.out.println("Starting game!");
        //this.isRunning = false;

        for (ConnectingClient client:
             clientList) {
            String ipMessage = "";
            try {
                for (ConnectingClient client2:
                        clientList) {
                    if(client2 != client) {
                        ipMessage += client2.getIP() + "\n";
                    } else {
                        ipMessage += "self" + "\n";
                    }
                }
                client.write(ipMessage);
                client.getSocket().close();
            } catch (IOException e) {
                System.err.println("Could not close socket...");
            }
        }

        clientList.clear();
    }

    public static void main(String[] args) {
        int port = 35294;
        MenschServer server = null;
        try {
            server = new MenschServer(port);
        } catch (IOException exception) {
            System.err.println("Could not open server, IOException: " + exception.getMessage());
        }

        if(server == null) {
            return;
        }

        server.acceptClients();
    }
}
