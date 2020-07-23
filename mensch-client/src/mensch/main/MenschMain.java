package mensch.main;

import mensch.game.Game;
import mensch.ui.GameView;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MenschMain {
    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Please enter the IP of the connection server:");
        try {
            Socket connectionToServer = new Socket(ip, 35294);
            PrintWriter writer = new PrintWriter(connectionToServer.getOutputStream());
            InetAddress localAddress = InetAddress.getLocalHost();
            writer.write(localAddress.getHostAddress());
            writer.flush();
            JOptionPane.showConfirmDialog(null,"Are you ready?");
            writer.write("ready");
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
            char[] charBuffer = new char[256];
            int numChars = reader.read(charBuffer, 0, 256);
            String ips = new String(charBuffer, 0, numChars);
            System.out.println(ips);
            System.out.println("Connection to server established!");
            String[] ipList = parseIPs(ips);
            Game game = new Game();
            Thread.sleep(1000);
            game.initialize(ipList);
            GameView view = GameView.build(game);
            game.setView(view);
            view.showMessages("You are " + PLAYER_COLORS[game.getPeerId()] + ".", "Click on your die when it \nlights up in order to roll.", "Then click on a cell with \na figure to move it.");
            Thread aliveChecker = new Thread(new AliveChecker(game, 2000));
            aliveChecker.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static final String[] PLAYER_COLORS = new String[] {"red", "blue", "green", "yellow"};

    public static String[] parseIPs(String ips){
        String[] ipAddresses = ips.split("\n");
        return ipAddresses;
        /*String[] ip = new String[ipAddresses.length-1];
        for(int i=0; i<ipAddresses.length-1; i++){
            ip[i] = ipAddresses[i];
        }
        return ip;*/
    }
}
