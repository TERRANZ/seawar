package ru.mars.seawar.server.game;

import org.jboss.netty.channel.Channel;
import ru.mars.gameserver.AbstractGameLogic;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread extends AbstractGameLogic implements Runnable {
    private Timer readyTimer;

    private class PlayerNotReadyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!isAllReady()) {
                sendGameOverMessage(-1);
                synchronized (game) {
                    game = false;
                }
            }
        }
    }


    public GameThread(Channel channel1, Channel channel2, Player player1, Player player2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.player1 = player1;
        this.player2 = player2;
        playerReady.put(channel1, false);
        playerReady.put(channel2, false);
    }

    @Override
    public void run() {
        //ждём в цикле действий
        while (game) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("Interrupted while sleep", e);
            }
        }
    }

    @Override
    protected void onPlayerReady(Channel channel) {
        if (readyTimer == null) {
            readyTimer = new Timer();
            readyTimer.schedule(new PlayerNotReadyTimerTask(), 1000 * 60 * 3);
        }
    }


}
