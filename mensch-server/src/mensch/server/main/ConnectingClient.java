package mensch.server.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class ConnectingClient implements Runnable {

    private MenschServer server;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Thread thread;

    private char[] charBuffer = new char[256];

    private boolean isReady = false;
    private boolean isDead = false;

    private String remoteIP;

    public ConnectingClient(MenschServer server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void write(String s) {
        writer.write(s);
        writer.flush();
    }

    public String getIP() {
        return remoteIP.isEmpty() ? socket.getInetAddress().getHostAddress() : remoteIP;
    }

    public String read() throws IOException {
        int numChars = reader.read(charBuffer, 0, 256);
        return new String(charBuffer, 0, numChars);
    }

    public boolean isReady() {
        return this.isReady;
    }

    public boolean isDead() {
        return this.isDead;
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            String msg = null;
            try {
                msg = read();
            } catch (SocketException e) {
                System.err.println("Client connection was closed abruptly.");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Message from client: " + msg);
            if(msg != null) {
                if(msg.equals("ready")) {
                    this.isReady = true;
                    if (server.allClientsReady()) {
                        server.startGame();
                    }
                } else {
                    this.remoteIP = msg;
                }
            }
        }
        this.isDead = true;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Thread getThread() {
        return this.thread;
    }
}
