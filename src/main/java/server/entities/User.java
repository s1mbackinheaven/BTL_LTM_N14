package server.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class User {
    private int id; // id
    private String username; // tên tài khoản
    private String password; // mật khẩu
    private String email;
    private Timestamp createdAt; // thời gian tạo acc
    private Timestamp last_login; // ghi lại log đăng nhập

    public User(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.username = rs.getString("username");
        this.password = rs.getString("password");
        this.createdAt = rs.getTimestamp("create_at");
    }

    public User(int id, String username, String password, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
