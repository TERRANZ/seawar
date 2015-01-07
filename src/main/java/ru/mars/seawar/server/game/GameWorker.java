package ru.mars.seawar.server.game;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import ru.mars.gameserver.AbstractGameWorker;
import ru.mars.seawar.server.network.message.MessageType;

/**
 * Date: 01.11.14
 * Time: 14:32
 */
public class GameWorker extends AbstractGameWorker {
    private static GameWorker instance = new GameWorker();

    private GameWorker() {
    }

    public static GameWorker getInstance() {
        return instance;
    }

    @Override
    protected void processCommand(Integer command, Element root, Channel channel) {
        switch (command) {
            case MessageType.C_READY_TO_PLAY: {
            }
            break;
            case MessageType.C_PLAYER_FIELD: {
            }
            break;
            case MessageType.C_TARGET: {
            }
            break;
            case MessageType.C_OK: {
            }
            break;
        }
    }

}
