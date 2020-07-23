package mensch.game;

import mensch.main.MenschMain;
import mensch.ui.GameView;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Game extends UnicastRemoteObject implements RemoteGame {

    private int peerId;
    private RemoteGame[] games;
    private boolean[] aliveGames;

    private Board board;
    private Figure[] figures;

    private int currentTurn = 0;
    private int rolledTurnNumber = 0;
    private boolean isBadRoll = false;
    private int houseRollCounter = 0;

    private Random random = new Random();

    private boolean waitForMove = false;
    private boolean waitForRoll = false;
    private boolean waitForTurn = false;

    private boolean isGameWon = false;

    private GameView view;

    public Game() throws RemoteException {
        super();
        Registry registry = LocateRegistry.createRegistry(35295);
        try {
            registry.bind("game", this);
        } catch (AlreadyBoundException e) {
            registry.rebind("game", this);
        }
    }

    // Needs to be called a few seconds after constructor,
    // to make sure every client created and bound their game
    public void initialize(String[] ipList) {
        games = new RemoteGame[ipList.length];
        aliveGames = new boolean[ipList.length];
        System.out.println("Num of IPs: " + ipList.length);
        for(int peerIndex = 0; peerIndex < ipList.length; peerIndex++) {
            System.out.println("IP: \"" + ipList[peerIndex] + "\"");
            if(ipList[peerIndex].equals("self")) {
                games[peerIndex] = this;
                peerId = peerIndex;
            } else {
                games[peerIndex] = getRemote(ipList[peerIndex]);
            }
            aliveGames[peerIndex] = true;
        }

        this.board = new Board();
        this.figures = new Figure[ipList.length * 4];
        for(int i = 0; i < this.figures.length; i++) {
            this.figures[i] = new Figure(i / 4, i % 4);
        }

        System.out.println("Game initialized!");
    }

    public boolean isWon() {
        return isGameWon;
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public Board getBoard() {
        return board;
    }

    public int getRolledTurnNumber() {
        return rolledTurnNumber;
    }

    public boolean hasRolled() {
        return rolledTurnNumber > 0;
    }

    public boolean hasTurn() {
        return waitTimer == -1 && !isGameWon && this.peerId == currentTurn && !waitForTurn && !waitForRoll && !waitForMove;
    }

    public boolean canFigureMove(Figure f, int steps) {
        if(f.isInStart() && steps != 6) {
            return false;
        }
        boolean anyInStart = false;
        Figure figureOnEntry = null;
        for(int i = 0; i < 4; i++) {
            Figure g = getFigure(this.currentTurn, i);
            if(g.isInStart()) {
                anyInStart = true;
            }
            if(g.getPosition() == g.getTeam() * 10) {
                figureOnEntry = g;
            }
        }
        // When any in start and 6: force moving out of start
        if(anyInStart && steps == 6 && figureOnEntry == null && !f.isInStart()) {
            return false;
        }
        // When any in start and figure on entry: force moving figure on entry
        if(anyInStart && figureOnEntry != null && figureOnEntry != f && canFigureMove(figureOnEntry, steps)) {
            return false;
        }
        int newCell = getMovedFigurePosition(f, steps);
        if(newCell > -1) {
            Figure on = getFigureOn(newCell);
            return on == null || on.getTeam() != f.getTeam();
        }
        return false;
    }

    public int getHouseRollCounter() {
        return houseRollCounter;
    }

    public int getMovedFigurePosition(Figure f, int steps) {
        if(f.isInStart()) {
            return steps == 6 ? f.getTeam() * 10 : -1;
        }
        return board.getCellIdAhead(f.getPosition(), f.getTeam(), steps);
    }

    public Figure getFigureOn(int cellId) {
        for(int i = 0; i < figures.length; i++) {
            if(figures[i].getPosition() == cellId) {
                return figures[i];
            }
        }
        return null;
    }

    private int getNextPeerId() {
        int next = (peerId + 1) % games.length;
        boolean hasFoundNext = false;
        do {
            if(!aliveGames[next]) {
                next = (next + 1) % games.length;
                continue;
            }
            try {
                games[next].isAlive();
                hasFoundNext = true;
            } catch(RemoteException e) {
                onPeerCrash(next);
                next = (next + 1) % games.length;
            }
        } while(!hasFoundNext);
        return next;
    }

    public int getTeamCount() {
        return games.length;
    }

    public Figure getFigure(int team, int id) {
        int index = team * 4 + id;
        if(index < 0 || index >= figures.length) {
            throw new IllegalArgumentException("Could not get Figure with team " + team + " and id " + id + ". Out of bounds.");
        }
        return figures[index];
    }

    public Figure[] getFigures() {
        return figures;
    }

    public int waitTimer = -1;
    public int giveTurnPeerId = -1;
    public void waitAndGiveTurn(int waitTime, int peerId) {
        this.waitTimer = waitTime;
        this.giveTurnPeerId = peerId;
    }

    public void giveTurn(int peerId) {
        if(waitForTurn) {
            waitForTurn = false;
            return;
        }
        isBadRoll = false;
        this.rolledTurnNumber = 0;
        this.currentTurn = peerId;
        if(peerId == this.peerId) {
            waitForTurn = true;
        }

        // Propagate
        int next = (this.peerId + 1) % games.length;
        boolean hasFoundNext = false;
        do {
            if(!aliveGames[next]) {
                next = (next + 1) % games.length;
                continue;
            }
            try {
                games[next].giveTurn(peerId);
                hasFoundNext = true;
            } catch(RemoteException e) {
                onPeerCrash(next);
                next = (next + 1) % games.length;
            }
        } while(!hasFoundNext);
    }

    public int getCurrentTurn() {
        return this.currentTurn;
    }

    public boolean isBadRoll() {
        return isBadRoll;
    }

    public void roll() {
        if(!hasTurn()) {
            throw new IllegalStateException("Can not roll when it's not this player's turn.");
        }
        int number = random.nextInt(6) + 1;
        waitForRoll = true;

        // Send to next peer
        rollPeer(number);
    }

    public void roll(int number) {
        this.rolledTurnNumber = number;
        boolean allInStart = true;
        boolean allInStartExceptHouse = true;
        boolean canAnyMove = false;
        boolean allAtEndOfHouse = true;
        for(int i = 0; i < 4; i++) {
            Figure f = getFigure(this.currentTurn, i);
            if(canFigureMove(f, number)) {
                canAnyMove = true;
            }
            if(!f.isInStart()) {
                allInStart = false;
                if(f.isInHouse()) {
                    boolean isAtEnd = true;
                    for(int p = f.getPosition() + 1; p <= 43 + (f.getTeam() * 4) && isAtEnd; p++) {
                        boolean hasFigureOnP = false;
                        for(int j = 0; j < 4 && !hasFigureOnP; j++) {
                            if(getFigure(this.currentTurn, j).getPosition() == p) {
                                hasFigureOnP = true;
                            }
                        }
                        if(!hasFigureOnP) {
                            isAtEnd = false;
                        }
                    }
                    if(!isAtEnd) {
                        allAtEndOfHouse = false;
                    }
                } else {
                    allInStartExceptHouse = false;
                }
            }
        }
        if(!canAnyMove) {
            isBadRoll = true;
            if(this.currentTurn == this.peerId) {
                if((allInStart || (allAtEndOfHouse && allInStartExceptHouse)) && houseRollCounter < 2) {
                    houseRollCounter++;
                    waitAndGiveTurn(1500, peerId);
                } else {
                    houseRollCounter = 0;
                    waitAndGiveTurn(1500, getNextPeerId());
                }
            }
        } else {
            houseRollCounter = 0;
        }
        if(waitForRoll) {
            waitForRoll = false;
            return;
        }

        // Propagate
        rollPeer(number);
    }

    private void rollPeer(int number) {
        int next = (peerId + 1) % games.length;
        boolean hasFoundNext = false;
        do {
            if(!aliveGames[next]) {
                next = (next + 1) % games.length;
                continue;
            }
            try {
                games[next].roll(number);
                hasFoundNext = true;
            } catch(RemoteException e) {
                onPeerCrash(next);
                next = (next + 1) % games.length;
            }
        } while(!hasFoundNext);
    }

    public void moveFigureForAll(int figure, int cell) {
        if(!hasTurn()) {
            throw new IllegalArgumentException("Can not call moveFigure when it's not this player's turn!");
        }
        waitForMove = true;

        // Send to next peer
        moveFigurePeer(figure, cell);
    }

    public void moveFigure(int figure, int cell) {
        Figure fig = getFigureOn(cell);
        if(fig != null) {
            fig.resetPosition();
        }
        figures[figure].setPosition(cell);

        int win = getWinningPlayer();
        if(win > -1) {
            endGame(win);
        }

        if(waitForMove) {
            waitForMove = false;
            if(this.getRolledTurnNumber() == 6) {
                giveTurn(peerId);
            } else {
                giveTurn(getNextPeerId());
            }
            return;
        }

        // Propagate
        moveFigurePeer(figure, cell);
    }

    public int getWinningPlayer() {
        for(int i = 0; i < games.length; i++) {
            boolean won = true;
            for(int j = 0; j < 4 && won; j++) {
                if(!getFigure(i, j).isInHouse()) {
                    won = false;
                }
            }
            if(won) {
                return i;
            }
        }
        return -1;
    }

    private void moveFigurePeer(int figure, int cell) {
        int next = (peerId + 1) % games.length;
        boolean hasFoundNext = false;
        do {
            if(!aliveGames[next]) {
                next = (next + 1) % games.length;
                continue;
            }
            try {
                games[next].moveFigure(figure, cell);
                hasFoundNext = true;
            } catch(RemoteException e) {
                onPeerCrash(next);
                next = (next + 1) % games.length;
            }
        } while(!hasFoundNext);
    }

    public synchronized void onPeerCrash(int peerId) {
        aliveGames[peerId] = false;
        boolean lastPlayerStanding = true;
        for(int i = 0; i < aliveGames.length && lastPlayerStanding; i++) {
            if(aliveGames[i] != (i == this.peerId)) {
                lastPlayerStanding = false;
            }
        }
        if(lastPlayerStanding) {
            endGame(this.peerId);
        } else {
            view.showMessages("Player " + MenschMain.PLAYER_COLORS[peerId] + " has disconnected.");
            if(peerId == currentTurn) {
                giveTurn(getNextPeerId());
            }
        }
    }

    private void endGame(int winningPeer) {
        if(winningPeer == this.peerId) {
            view.showMessages("Congratulations!\nYou won!", "Congratulations!\nYou won!", "Congratulations!\nYou won!", "Congratulations!\nYou won!", "Congratulations!\nYou won!", "Why are you still here?", "Why are you still here?", "Why are you still here?", "Why are you still here?", "Why are you still here?");
        } else {
            view.showMessages("Player " + MenschMain.PLAYER_COLORS[winningPeer] + " won the game.");
        }
        isGameWon = true;
    }

    public boolean isAlive() {
        return true;
    }

    public int getPeerId() {
        return peerId;
    }

    public RemoteGame getPeer(int peerId) {
        if(peerId < 0 || peerId >= games.length) {
            throw new IllegalArgumentException("Peer ID " + peerId + " was out of bounds for Game.getPeer");
        }
        return games[peerId];
    }

    public int getPeerCount() {
        return games.length;
    }

    public static RemoteGame getRemote(String ip) {
        try {
            return (RemoteGame)Naming.lookup("rmi://" + ip + ":" + 35295 + "/game");
        } catch (Exception e) {
            System.err.println("Exception trying to get remote game: " + e.getMessage());
            return null;
        }
    }
}
