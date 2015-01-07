package ru.mars.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import ru.mars.seawar.server.game.GameThread;
import ru.mars.seawar.server.game.Player;
import ru.mars.seawar.server.game.Statistic;
import ru.mars.seawar.server.network.message.MessageFactory;
import ru.mars.seawar.server.network.message.MessageType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Date: 07.01.15
 * Time: 15:10
 */
public abstract class AbstractGameWorker {
    protected Logger logger = Logger.getLogger(this.getClass());
    protected Map<Channel, GameState> gameStateMap = new HashMap<>();
    protected Map<Channel, Player> playerMap = new HashMap<>();
    protected Map<Future, Channel> finders = new WeakHashMap<>();
    protected ExecutorService service = Executors.newFixedThreadPool(10);
    protected Map<Channel, GameThread> gameThreadMap = new HashMap<>();

    public synchronized void addPlayer(Channel channel) {
        gameStateMap.put(channel, GameState.LOGIN);
        playerMap.put(channel, new Player());
    }

    public synchronized void removePlayer(Channel channel) {
        if (playerMap.containsKey(channel)) {
            gameThreadMap.get(channel).playerDisconnect(channel);
            gameStateMap.remove(channel);
            playerMap.remove(channel);
        }
    }

    public synchronized void handlePlayerCommand(Channel channel, String xml) {
        logger.info("Received xml = " + xml + " from channel " + channel.toString());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            logger.error("Unable to parse xml", e);
        }

        if (doc != null) {
            Element root = doc.getDocumentElement();
            Integer command = Integer.parseInt(root.getElementsByTagName("id").item(0).getTextContent());

            if (command == MessageType.C_PING) {
                logger.info("PING");
                channel.write(MessageFactory.createPingMessage(getStatistic()));
            } else if (command == MessageType.C_PLAYER_CANCEL_WAIT) {
                logger.info("Player " + channel + " cancelled waiting");
                for (Future pairFinder : finders.keySet())
                    if (finders.get(pairFinder).equals(channel))
                        pairFinder.cancel(true);//стопаем поиск пары если игрок отказался
            } else
                processCommand(command, root, channel);

        }
    }

    protected abstract void processCommand(Integer command, Element root, Channel channel);

    public Map<Channel, Player> getPlayerMap() {
        synchronized (playerMap) {
            return playerMap;
        }
    }

    public void addGameThreadForChannel(Channel channel, GameThread gameThread) {
        synchronized (gameThreadMap) {
            gameThreadMap.put(channel, gameThread);
        }
    }

    public void setPlayerState(Channel channel, GameState gameState) {
        synchronized (gameStateMap) {
            gameStateMap.put(channel, gameState);
        }
    }

    public GameState getPlayerState(Channel channel) {
        synchronized (gameStateMap) {
            return gameStateMap.get(channel);
        }
    }

    public synchronized Statistic getStatistic() {
        return new Statistic(getPlayerMap().size(), gameThreadMap.size() > 0 ? gameThreadMap.size() / 2 : 0);
    }
}
