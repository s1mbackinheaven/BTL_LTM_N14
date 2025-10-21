package server.DAO;

import server.entities.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDAO extends DAO {

    private static final Logger logger = Logger.getLogger(PlayerDAO.class.getName());

    public PlayerDAO(Connection con) {
        super(con);
    }

    // Tạo player mới
    public boolean createPlayer(Player p) {
        String sql = "INSERT INTO tbl_players (user_id, name, elo, total_win, total_loss) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = this.con.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getElo());
            ps.setInt(4, p.getTotalWin());
            ps.setInt(5, p.getTotalLoss());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating player", e);
            return false;
        }
    }

    // Lấy thông tin player theo user_id
    public Player getPlayer(int user_id) {
        String sql = "SELECT * FROM tbl_players WHERE user_id = ?";

        try (PreparedStatement ps = this.con.prepareStatement(sql)) {
            ps.setInt(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Player(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting player with id=" + user_id, e);
        }
        return null;
    }

    // Lấy danh sách ELO (tăng hoặc giảm dần)
    public ArrayList<Integer> bangDiem(boolean desc) {
        String sql = desc
                ? "SELECT elo FROM tbl_players ORDER BY elo DESC"
                : "SELECT elo FROM tbl_players ORDER BY elo ASC";

        ArrayList<Integer> result = new ArrayList<>();

        try (PreparedStatement ps = this.con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getInt("elo"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting elo list", e);
        }

        return result;
    }

    // Lấy danh sách toàn bộ player
    public ArrayList<Player> getListPlayer() {
        String sql = "SELECT * FROM tbl_players";
        ArrayList<Player> res = new ArrayList<>();

        try (PreparedStatement ps = this.con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                res.add(new Player(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting player list", e);
        }

        return res;
    }

    // Cập nhật thông tin player
    public boolean updatePlayer(Player p) {
        // ❌ lỗi ở dòng này trong code của bạn:
        // "update from tbl_players set ..."  → phải bỏ "from"
        String sql = "UPDATE tbl_players SET name = ?, elo = ?, total_win = ?, total_loss = ? WHERE user_id = ?";

        try (PreparedStatement ps = this.con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getElo());
            ps.setInt(3, p.getTotalWin());
            ps.setInt(4, p.getTotalLoss());
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating player with id=" + p.getId(), e);
        }

        return false;
    }

    //xoá
    public boolean deletePlayer(int userId) {
        String sql = "DELETE FROM tbl_players WHERE user_id = ?";
        try (PreparedStatement ps = this.con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting player with id=" + userId, e);
            return false;
        }
    }
}
