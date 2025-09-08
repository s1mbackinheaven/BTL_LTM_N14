****# Game Ném Phi Tiêu - Server Backend Implementation

## Tổng quan dự án

Dự án game ném phi tiêu đa người chơi với kiến trúc client-server. Server đóng vai trò trung tâm quản lý toàn bộ logic game, dữ liệu người chơi, và điều phối các trận đấu giữa các client.

### Phân công vai trò:
- **A (Bách):** Core game logic, luật chơi, công thức tính toán
- **B (Dũng):** Server backend, network, database, protocol handling
- **C (Châu):** Client UI, network client-side
- **D (Anh):** Phụ trợ UI, leaderboard, polish

## Checklist công việc Server Backend (Vai trò B)

### ✅ Socket Server Implementation
- [x] Tạo `ServerMain.java` - điểm khởi động server
- [x] Tạo `GameServer.java` - quản lý ServerSocket
- [x] Tạo `ClientHandler.java` - xử lý từng client (thread)
- [x] Cấu hình port từ `Config.java`

### ❌ Multi-threading Client Handling  
- [x] ClientHandler implements Runnable
- [ ] Quản lý danh sách client online (ConcurrentHashMap)
- [ ] Xử lý client disconnect đột ngột
- [ ] Thread pool management (tùy chọn)

### ❌ Database Integration
- [ ] Tạo database schema (users, matches tables)
- [ ] Implement `Database.java` - connection management
- [ ] Implement `UserDAO.java` - user CRUD operations
- [ ] Implement `MatchDAO.java` - match history storage
- [ ] Thêm MySQL dependency vào pom.xml

### ❌ Network Protocol Implementation
- [ ] Xử lý LoginRequest/LoginResponse
- [ ] Xử lý PlayerListRequest/Response
- [ ] Xử lý InviteRequest/Response
- [ ] Xử lý MoveRequest/MoveResult
- [ ] Xử lý GameStateUpdate, GameStart, GameEnd
- [ ] Error handling cho tất cả message types

### ❌ Connection Management
- [ ] ClientManager - quản lý client online
- [ ] Game session management
- [ ] Player status tracking (FREE/BUSY/IN_GAME)
- [ ] Broadcast mechanism cho multiple clients
- [ ] Graceful shutdown và resource cleanup

## Hướng dẫn chi tiết từng bước

---

## 1. Chi tiết bước 1: Tạo socket server

### a. Cấu trúc thư mục đề xuất

```
server/
├── ServerMain.java        // Điểm khởi động server
├── GameServer.java        // Quản lý ServerSocket, nhận kết nối client
├── ClientHandler.java     // Xử lý từng client riêng biệt (thread)
├── core/                  // Logic game (GameEngine, GameSession...)
├── protocol/              // Định nghĩa message giao tiếp
└── db/                    // Kết nối và thao tác database
```

### b. Các class cần tạo và chức năng

#### 1. `ServerMain.java`
- **Tác dụng:** Điểm khởi động server, gọi hàm khởi tạo và chạy server.
- **Function chính:**
  - `main(String[] args)`: Tạo đối tượng `GameServer` và gọi `start()`.
- **Ví dụ:**
  ```java
  public class ServerMain {
      public static void main(String[] args) {
          GameServer server = new GameServer(12345); // PORT
          server.start();
      }
  }
  ```

#### 2. `GameServer.java`
- **Tác dụng:** Quản lý ServerSocket, lắng nghe kết nối mới, tạo thread cho mỗi client.
- **Function chính:**
  - `start()`: Khởi tạo `ServerSocket`, vòng lặp nhận client mới, tạo `ClientHandler` cho mỗi client.
  - `stop()`: Đóng server khi cần.
- **Ví dụ:**
  ```java
  public class GameServer {
      private int port;
      private ServerSocket serverSocket;
      public GameServer(int port) { this.port = port; }
      public void start() {
          try {
              serverSocket = new ServerSocket(port);
              while (true) {
                  Socket clientSocket = serverSocket.accept();
                  new Thread(new ClientHandler(clientSocket)).start();
              }
          } catch (IOException e) { e.printStackTrace(); }
      }
      public void stop() throws IOException { serverSocket.close(); }
  }
  ```

#### 3. `ClientHandler.java`
- **Tác dụng:** Xử lý giao tiếp với từng client (đọc/gửi dữ liệu, xử lý logic từng người chơi).
- **Function chính:**
  - `run()`: Đọc/gửi message với client, xử lý logic tương tác.
  - Có thể bổ sung các hàm như `handleLogin()`, `handleMove()`, ...
- **Ví dụ:**
  ```java
  public class ClientHandler implements Runnable {
      private Socket socket;
      public ClientHandler(Socket socket) { this.socket = socket; }
      public void run() {
          try {
              ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
              ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
              // Vòng lặp nhận và xử lý message
              while (true) {
                  Object msg = in.readObject();
                  // Xử lý message
              }
          } catch (Exception e) { e.printStackTrace(); }
      }
  }
  ```

### c. Ý nghĩa từng thành phần
- **ServerMain:** Đơn giản hóa việc khởi động server, tách biệt với logic xử lý.
- **GameServer:** Chịu trách nhiệm quản lý socket, đảm bảo server luôn sẵn sàng nhận client mới.
- **ClientHandler:** Đảm bảo mỗi client được xử lý độc lập, không ảnh hưởng tới các client khác.

---

## 2. Multi-threading Client Handling

**Mục tiêu:** Mỗi client được xử lý ở một thread riêng biệt để server phục vụ đồng thời nhiều người chơi.

**Các bước:**
1. Tạo class `ClientHandler` implements `Runnable`:
   ```java
   public class ClientHandler implements Runnable {
       private Socket socket;
       public ClientHandler(Socket socket) { this.socket = socket; }
       public void run() {
           // Đọc/ghi dữ liệu với client
       }
   }
   ```
2. Khi có client mới:
   ```java
   Socket clientSocket = serverSocket.accept();
   new Thread(new ClientHandler(clientSocket)).start();
   ```

**Ý nghĩa:** Đảm bảo server không bị block khi nhiều client cùng kết nối/chơi.

---

## 3. Database Integration

**Mục tiêu:** Lưu thông tin user, elo, lịch sử trận đấu vào database MySQL.

### a. Tạo database schema
```sql
-- File: /sql/create_database.sql
CREATE DATABASE dart_game;
USE dart_game;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    elo INT DEFAULT 1000,
    total_wins INT DEFAULT 0,
    total_losses INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE matches (
    id INT PRIMARY KEY AUTO_INCREMENT,
    player1_username VARCHAR(50),
    player2_username VARCHAR(50),
    winner_username VARCHAR(50),
    player1_score INT,
    player2_score INT,
    elo_change INT,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player1_username) REFERENCES users(username),
    FOREIGN KEY (player2_username) REFERENCES users(username)
);
```

### b. Thêm dependency vào pom.xml
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### c. Implement Database classes
```java
// Database.java
public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/dart_game";
    private static final String USER = "root";
    private static final String PASS = "password";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

// UserDAO.java
public class UserDAO {
    public boolean authenticateUser(String username, String password) {
        // Kiểm tra username/password trong database
    }
    
    public PlayerInfo getUserInfo(String username) {
        // Lấy thông tin user (elo, wins, losses)
    }
    
    public void updateElo(String username, int newElo) {
        // Cập nhật elo sau trận đấu
    }
}
```

**Ý nghĩa:** Lưu trữ bền vững thông tin người chơi, kết quả trận đấu, hỗ trợ leaderboard.

---

## 4. Network Protocol Implementation

**Mục tiêu:** Xử lý các message protocol đã được định nghĩa trong `/protocol` package.

### a. Cấu trúc message handling trong ClientHandler
```java
private void handleMessage(Object obj, ObjectOutputStream output) throws IOException {
    if (obj instanceof LoginRequest) {
        handleLogin((LoginRequest) obj, output);
    } else if (obj instanceof PlayerListRequest) {
        handlePlayerListRequest(output);
    } else if (obj instanceof InviteRequest) {
        handleInviteRequest((InviteRequest) obj, output);
    } else if (obj instanceof MoveRequest) {
        handleMoveRequest((MoveRequest) obj, output);
    }
    // ... các message type khác
}
```

### b. Implement từng handler method
```java
private void handleLogin(LoginRequest req, ObjectOutputStream output) throws IOException {
    // 1. Kiểm tra username/password qua UserDAO
    UserDAO userDAO = new UserDAO();
    boolean isValid = userDAO.authenticateUser(req.getUsername(), req.getPassword());
    
    if (isValid) {
        // 2. Lưu username, thêm vào danh sách online
        this.username = req.getUsername();
        ClientManager.addClient(username, this);
        
        // 3. Lấy thông tin player và gửi response
        PlayerInfo playerInfo = userDAO.getUserInfo(username);
        output.writeObject(new LoginResponse("server", true, null, playerInfo));
    } else {
        output.writeObject(new LoginResponse("server", false, "Invalid credentials", null));
    }
}

private void handleMoveRequest(MoveRequest req, ObjectOutputStream output) throws IOException {
    // 1. Lấy GameSession hiện tại
    GameSession session = ClientManager.getGameSession(username);
    
    // 2. Gọi core logic (phần A) để xử lý move
    MoveResult result = session.processMove(req.getPlayerX(), req.getPlayerY(), req.getUsedPowerUp());
    
    // 3. Gửi kết quả về client và update game state
    output.writeObject(result);
    broadcastGameState(session);
}
```

### c. Error handling cho message processing
```java
try {
    Object obj = input.readObject();
    handleMessage(obj, output);
} catch (ClassNotFoundException e) {
    output.writeObject(new ErrorMessage("server", "Unknown message type"));
} catch (Exception e) {
    output.writeObject(new ErrorMessage("server", "Server error: " + e.getMessage()));
}
```

**Ý nghĩa:** Đảm bảo client và server giao tiếp đúng protocol, xử lý lỗi gracefully.

---

## 5. Connection Management

**Mục tiêu:** Quản lý danh sách client online, game sessions, trạng thái player, broadcast mechanism.

### a. Tạo ClientManager
```java
public class ClientManager {
    private static Map<String, ClientHandler> onlineClients = new ConcurrentHashMap<>();
    private static Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
    
    // Client management
    public static void addClient(String username, ClientHandler handler) {
        onlineClients.put(username, handler);
        broadcastPlayerListUpdate();
    }
    
    public static void removeClient(String username) {
        onlineClients.remove(username);
        broadcastPlayerListUpdate();
    }
    
    public static List<PlayerInfo> getOnlinePlayersList() {
        // Trả về danh sách player online với status
    }
    
    // Game session management  
    public static void createGameSession(String player1, String player2) {
        String sessionId = player1 + "_vs_" + player2;
        GameSession session = new GameSession(player1, player2);
        activeSessions.put(sessionId, session);
    }
    
    public static GameSession getGameSession(String username) {
        // Tìm session mà player đang tham gia
    }
    
    // Broadcast methods
    public static void broadcastPlayerListUpdate() {
        PlayerListResponse response = new PlayerListResponse(getOnlinePlayersList());
        for (ClientHandler handler : onlineClients.values()) {
            handler.sendMessage(response);
        }
    }
}
```

### b. Player status tracking
```java
public enum PlayerStatus {
    FREE,       // Rảnh rỗi, có thể nhận lời mời
    BUSY,       // Đang trong menu, không nhận lời mời
    IN_GAME,    // Đang chơi game
    OFFLINE     // Đã disconnect
}

// Trong ClientHandler
private PlayerStatus status = PlayerStatus.FREE;
```

### c. Graceful cleanup
```java
// Trong ClientHandler.run()
finally {
    cleanup();
}

private void cleanup() {
    try {
        if (username != null) {
            // Remove từ online list
            ClientManager.removeClient(username);
            
            // Nếu đang trong game, thông báo opponent
            GameSession session = ClientManager.getGameSession(username);
            if (session != null) {
                session.playerLeft(username);
            }
        }
        
        // Đóng resources
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
    } catch (IOException ignored) {}
}
```

**Ý nghĩa:** Đảm bảo server luôn biết trạng thái chính xác của tất cả client, quản lý game sessions hiệu quả.

---

## Thứ tự triển khai đề xuất

### Phase 1: Foundation (Tuần 1)
1. **Database Integration** - Tạo schema, DAO classes
2. **Protocol Implementation** - Xử lý message cơ bản (login, player list)
3. **Connection Management** - ClientManager, basic online list

### Phase 2: Game Features (Tuần 2)  
1. **Invite System** - Xử lý invite/accept/reject
2. **Game Session Management** - Tạo/quản lý trận đấu
3. **Core Logic Integration** - Kết nối với phần A (GameEngine, GameSession)

### Phase 3: Advanced Features (Tuần 3)
1. **Realtime Game State** - Đồng bộ game state giữa 2 client
2. **Match History** - Lưu kết quả, cập nhật ELO
3. **Error Handling** - Xử lý edge cases, disconnect handling

### Phase 4: Testing & Polish (Tuần 4)
1. **Integration Testing** - Test với multiple clients
2. **Performance Optimization** - Thread pool, connection pooling
3. **Documentation** - Code comments, API docs

---

## Tài liệu tham khảo
- [Java Socket Programming](https://docs.oracle.com/javase/tutorial/networking/sockets/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [Java Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

## Lưu ý quan trọng
- **Thread Safety:** Sử dụng ConcurrentHashMap cho shared data structures
- **Resource Management:** Luôn đóng socket/stream trong finally block
- **Error Handling:** Catch và log tất cả exceptions, gửi error message về client
- **Testing:** Test từng feature riêng biệt trước khi integrate
- **Communication:** Sync thường xuyên với phần A về core logic interface

---

## Cách chạy và test
```bash
# Compile và chạy server
mvn compile
mvn exec:java -Dexec.mainClass="com.oop.game.server.ServerMain"

# Hoặc dùng script
chmod +x run.sh
./run.sh
```
