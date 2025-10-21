package server.DAO;

import server.enums.regisStatus;
import server.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {

    public UserDAO(Connection con) {
        super(con);
    }

    //khởi tạo
    public boolean createUser(String username, String hashedPassword) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }
    
    //trả về user dựa vào username
    public User getUserByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return null;
    }

    // trả về dựa trên id
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return null;
    }

    // trả về toàn bộ user
    public List<User> getAllUserByOrder(String order) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY " + order + " DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(rs));
            }

        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    //kiểm tra sự tồn tại của username
    public boolean userExists(String username) {
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

    //xoá user
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

//
//    public boolean updateUserStats(int userId, int newElo, boolean isWin) {
//        String sql = "UPDATE users SET elo = ?, " +
//                (isWin ? "total_wins = total_wins + 1" : "total_losses = total_losses + 1") +
//                " WHERE id = ?";
//
//        try (PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, newElo);
//            ps.setInt(2, userId);
//
//            int rowsAffected = ps.executeUpdate();
//            return rowsAffected > 0;
//
//        } catch (Exception e) {
//            System.err.println("Error updating user stats: " + e.getMessage());
//            return false;
//        }
//    }
}
