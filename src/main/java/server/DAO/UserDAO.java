package server.DAO;

import server.enums.UserStatus;
import server.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAO extends DAO {

    // Xác thực người dùng
    public UserStatus AuthenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword))
                        return UserStatus.LOGIN_SUCCESS;
                    else {
                        return UserStatus.LOGIN_INCORRECT;
                    }
                } else {
                    return UserStatus.LOGIN_USERNAME_NOT_EXITS;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error authenticating user: " + e.getMessage());
            return UserStatus.ERROR;
        }
    }

    //đăng ký
    public UserStatus RegisterUser(String username, String password) {
        // Check if username already exists
        if (userExists(username))
            return UserStatus.REGIS_USERNAME_EXITS;

        // Hash password với BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0)
                return UserStatus.REGIS_SUCCESS;

            return UserStatus.ERROR;

        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return UserStatus.ERROR;
        }
    }

    //trả về user dựa vào username
    public User GetUserByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return null;
    }

    public User GetUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return null;
    }

    // trả về toàn bộ user
    public List<User> GetAllUserByOrder(String order) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY " + order + " DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    public List<User> GetTopUsers(int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY elo DESC, total_wins DESC LIMIT ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting top users: " + e.getMessage());
        }
        return users;
    }

    public boolean UpdateElo(String username, int newElo) {
        String sql = "UPDATE users SET elo = ? WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Math.max(0, newElo)); // Ensure ELO doesn't go below 0
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error updating ELO: " + e.getMessage());
            return false;
        }
    }

    public boolean UpdateResultGame(String unWin, String unLoss, int eloGrant) {
        String win = "UPDATE users SET total_wins = total_wins + 1, elo = elo + ? WHERE username = ?";
        String loss = "UPDATE users SET total_losses = total_losses + 1, elo = GREATEST(0, elo - ?) WHERE username = ?";

        Connection con = null;

        try {
            con = getConnection();

            if(con == null)
                return

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public boolean RecordWin(String username, int eloGained) {

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eloGained);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error recording win: " + e.getMessage());
            return false;
        }
    }

    public boolean RecordLoss(String username, int eloLost) {

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eloLost);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error recording loss: " + e.getMessage());
            return false;
        }
    }

    private boolean userExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("Error getting user count: " + e.getMessage());
        }
        return 0;
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserStats(int userId, int newElo, boolean isWin) {
        String sql = "UPDATE users SET elo = ?, " +
                (isWin ? "total_wins = total_wins + 1" : "total_losses = total_losses + 1") +
                " WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newElo);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error updating user stats: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("elo"),
                rs.getInt("total_wins"),
                rs.getInt("total_losses"),
                rs.getTimestamp("created_at"));
    }
}
