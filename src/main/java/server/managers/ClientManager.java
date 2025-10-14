package server.managers;

import com.oop.game.JAR.dto.PlayerInfoDTO;

import server.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientManager {

    private final List<Player> listUserOnline;

    private static ClientManager instance;

    public static synchronized ClientManager getInstance() {

        if (instance == null)
            instance = new ClientManager();

        return instance;
    }

    public ClientManager() {
        listUserOnline = new CopyOnWriteArrayList<>();
    }

    public List<PlayerInfoDTO> getListUserOnline() {
        List<PlayerInfoDTO> pList = new ArrayList<>();

        for (Player p : listUserOnline)
            pList.add(new PlayerInfoDTO(p.getUsername(), p.getElo(), p.getTotalWins(), p.getTotalLosses(), p.isBusy()));

        return pList;
    }

    public void addUserOnline(Player player) {
        if (player != null) {
            if (!listUserOnline.contains(player))
                listUserOnline.add(player);
        }
    }

    public void userOffLine(Player player) {
        listUserOnline.remove(player);
    }

    /**
     * Lấy số lượng user online
     */
    public int getUserCount() {
        return listUserOnline.size();
    }

    public Player getUserByName(String un) {
        for (Player i : listUserOnline) {
            if (un.equals(i.getUsername()))
                return i;
        }

        return null;
    }

    /**
     * kiểm tra player này đã onl hay chưa
     */
    public boolean isOnline(Player player) {
        if (listUserOnline.contains(player))
            return true;
        return false;
    }

    /**
     * In thông tin debug
     */
    public void printStatus() {
        System.out.println("=== ClientManager Status ===");
        System.out.println("Online users: " + getUserCount());
        System.out.println("Users: " + listUserOnline);
        System.out.println("============================");
    }
}
