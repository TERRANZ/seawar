package ru.mars.seawar.server.network.message;

import ru.mars.seawar.server.game.Statistic;

/**
 * Date: 01.11.14
 * Time: 22:41
 */
public class MessageFactory {

    public static String header(int msgId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='utf-8'?>");
        sb.append("<msg>");
        sb.append("<id>");
        sb.append(msgId);
        sb.append("</id>");
        return sb.toString();
    }

    public static String footer(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        sb.append("</msg>");
        return sb.toString();
    }

    public static String wrap(int msgId, String msg) {
        return footer(header(msgId) + msg);
    }

    public static String createPingMessage(Statistic statistic) {
        return footer(header(MessageType.S_PING) + "<text> hello </text> <online>" + statistic.getOnline() + "</online><games>" + statistic.getGames() + "</games>");
    }

    public static String createWaitMessage() {
        return footer(header(MessageType.S_WAIT));
    }

    public static String createGameOverMessage(Integer deadPlayer) {
        return footer(header(MessageType.S_GAME_OVER) + "<deadplayer>" + deadPlayer + "</deadplayer>");
    }

    public static String createPairFoundMessage(int playerNum) {
        return footer(header(MessageType.S_PAIR_FOUND) + "<playerid>" + playerNum + "</playerid>");
    }
}
