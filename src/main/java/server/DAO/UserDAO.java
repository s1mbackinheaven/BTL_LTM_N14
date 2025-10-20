package server.DAO;

import server.enums.UserStatus;
import server.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAO extends DAO {

    public UserDAO(Connection con) {
        super(con);
    }

    // Xác thực người dùng
    public UserStatus AuthenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (PreparedStatement ps = con.prepareStatement(sql)) {

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
        try (PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    private boolean userExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (PreparedStatement ps = con.prepareStatement(sql)) {

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
                rs.getTimestamp("create_at")
        );
    }
}
