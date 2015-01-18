package ru.mars.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import ru.mars.seawar.server.game.GameWorker;
import ru.mars.seawar.server.game.Player;
import ru.mars.seawar.server.network.message.MessageFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Date: 04.11.14
 * Time: 18:34
 */
public abstract class AbstractGameLogic {
    protected Channel channel1, channel2;
    protected Player player1, player2;
    protected int[][] player1field1 = new int[10][10];
    protected int[][] player2field1 = new int[10][10];
    protected int[][] player1field2 = new int[10][10];
    protected int[][] player2field2 = new int[10][10];
    protected Map<Channel, Boolean> playerReady = new HashMap<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    protected boolean firstPlayerMove = false;
    protected volatile Boolean game = true;

    public synchronized void setPlayerReady(Channel playerChannel) {
        playerReady.put(playerChannel, true);
        onPlayerReady(playerChannel);
    }

    public synchronized boolean isAllReady() {
        return playerReady.get(channel1) && playerReady.get(channel2);
    }

    protected abstract void onPlayerReady(Channel channel);

    protected void sendGameOverMessage(Integer deadPlayer) {
        channel1.write(MessageFactory.createGameOverMessage(deadPlayer));
        channel2.write(MessageFactory.createGameOverMessage(deadPlayer));
    }

    public synchronized void playerDisconnect(Channel channel) {
        if (channel.equals(channel1)) {
            try {
                channel2.write(MessageFactory.createGameOverMessage(2));
                GameWorker.getInstance().setPlayerState(channel2, GameState.LOGIN);
            } catch (Exception e) {
                logger.error("Unable to send player disconnection message to channel2", e);
            }

        } else {
            try {
                channel1.write(MessageFactory.createGameOverMessage(1));
                GameWorker.getInstance().setPlayerState(channel1, GameState.LOGIN);
            } catch (Exception e) {
                logger.error("Unable to send player disconnection message to channel1", e);
            }
        }
        game = false;
    }


    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public Channel getChannel1() {
        return channel1;
    }

    public void setChannel1(Channel channel1) {
        this.channel1 = channel1;
    }

    public Channel getChannel2() {
        return channel2;
    }

    public void setChannel2(Channel channel2) {
        this.channel2 = channel2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public int[][] getPlayer1field1() {
        return player1field1;
    }

    public void setPlayer1field1(int[][] player1field1) {
        this.player1field1 = player1field1;
    }

    public int[][] getPlayer2field1() {
        return player2field1;
    }

    public void setPlayer2field1(int[][] player2field1) {
        this.player2field1 = player2field1;
    }

    public int[][] getPlayer1field2() {
        return player1field2;
    }

    public void setPlayer1field2(int[][] player1field2) {
        this.player1field2 = player1field2;
    }

    public int[][] getPlayer2field2() {
        return player2field2;
    }

    public void setPlayer2field2(int[][] player2field2) {
        this.player2field2 = player2field2;
    }
}
