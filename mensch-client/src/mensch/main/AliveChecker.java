package mensch.main;

import mensch.game.Game;
import mensch.game.RemoteGame;

import java.rmi.RemoteException;

public class AliveChecker implements Runnable {

    private Game game;
    private int interval;
    private int currentNextPeerId;
    private RemoteGame currentNextPeer;

    public AliveChecker(Game game, int checkInterval) {
        this.game = game;
        this.currentNextPeerId = (game.getPeerId() + 1) % game.getPeerCount();
        this.currentNextPeer = game.getPeer(this.currentNextPeerId);
        this.interval = checkInterval;
    }

    @Override
    public void run() {
        while(!game.isWon()) {
            if(currentNextPeer != null && currentNextPeer != game) {
                try {
                    currentNextPeer.isAlive();
                } catch (RemoteException e) {
                    game.onPeerCrash(currentNextPeerId);
                    currentNextPeerId = (currentNextPeerId + 1) % game.getPeerCount();
                    currentNextPeer = game.getPeer(currentNextPeerId);
                }
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {

            }
        }
    }
}
