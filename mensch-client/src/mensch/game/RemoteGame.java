package mensch.game;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGame extends Remote {

    boolean isAlive() throws RemoteException;
    void moveFigure(int figureId, int toCellId) throws RemoteException;
    void roll(int number) throws RemoteException;
    void giveTurn(int peerId) throws RemoteException;
}
