package ru.mars.seawar.server.game;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import ru.mars.gameserver.GameState;
import ru.mars.seawar.server.network.message.MessageFactory;

/**
 * Date: 03.11.14
 * Time: 14:15
 */
public class PairFinder implements Runnable {

    private Channel player1Channel;
    private Player player1;
    protected Logger logger = Logger.getLogger(this.getClass());

    public PairFinder(Channel player1Channel, Player player1) {
        this.player1Channel = player1Channel;
        this.player1 = player1;
    }

    @Override
    public void run() {
        while (true) {
            for (Channel player2channel : GameWorker.getInstance().getPlayerMap().keySet()) {
                if (!player2channel.equals(player1Channel) && GameWorker.getInstance().getPlayerState(player2channel).equals(GameState.LOGGED_IN)) {
                    Player p = GameWorker.getInstance().getPlayerMap().get(player2channel);
                    if (!p.isInGame()) {
                        try {
                            player1Channel.write(MessageFactory.createPairFoundMessage(1));
                            player2channel.write(MessageFactory.createPairFoundMessage(2));
                            GameThread gameThread = new GameThread(player1Channel, player2channel, player1, p);
                            //добавляем для каналов игру
                            GameWorker.getInstance().addGameThreadForChannel(player1Channel, gameThread);
                            GameWorker.getInstance().addGameThreadForChannel(player2channel, gameThread);
                            GameWorker.getInstance().setPlayerState(player1Channel, GameState.GAME_LOADING);
                            GameWorker.getInstance().setPlayerState(player2channel, GameState.GAME_LOADING);
                        } catch (Exception e) {
                            logger.error("Unable to send pair message", e);
                        }
                        return;
                    }
                }
            }
            try {
                Thread.sleep(500);//засыпаем на полминуты, если не найдена пара и увеличиваем разброс
            } catch (InterruptedException e) {
                logger.error("Interrupted while sleeping", e);
            }
        }
    }
}
