package com.oop.game.server.DAO;

import com.oop.game.server.enums.AuthStatus;
import com.oop.game.server.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAO extends DAO {

    // Xác thực người dùng với mật khẩu đã được mã hóa bằng BCrypt
    public AuthStatus authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return AuthStatus.SUCCESS;
                    } else {
                        return AuthStatus.INVALID_CREDENTIALS;
                    }
                } else {
                    return AuthStatus.INVALID_CREDENTIALS;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error authenticating user: " + e.getMessage());
            return AuthStatus.DB_ERROR;
        }
    }


    public boolean registerUser(String username, String password) {
        // Check if username already exists
        if (userExists(username))
            return false;

        // Hash password với BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public User getUserByUsername(String username) {
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

    public User getUserById(int id) {
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

    public List<User> getAllUserByOrder(String order) {
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

    public List<User> getTopUsers(int limit) {
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

    public boolean updateElo(String username, int newElo) {
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

    public boolean recordWin(String username, int eloGained) {
        String sql = "UPDATE users SET total_wins = total_wins + 1, elo = elo + ? WHERE username = ?";
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

    public boolean recordLoss(String username, int eloLost) {
        String sql = "UPDATE users SET total_losses = total_losses + 1, elo = GREATEST(0, elo - ?) WHERE username = ?";
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

    public boolean userExists(String username) {
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

    /**
     * ✅ THÊM: Cập nhật thống kê user sau trận đấu
     *
     * @param userId ID của user
     * @param newElo ELO mới sau trận
     * @param isWin  true nếu thắng, false nếu thua
     */
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
