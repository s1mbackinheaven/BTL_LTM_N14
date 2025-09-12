****# 🎯 Hướng dẫn sử dụng chức năng Thách đấu

## 📋 Tổng quan

Chức năng thách đấu cho phép người chơi mời người chơi khác tham gia trận đấu 1v1 trong game Ném Phi Tiêu.

## 🏗️ Kiến trúc hệ thống

### Các thành phần chính:

1. **InviteRequest** - Message gửi lời mời thách đấu
2. **InviteResponse** - Message phản hồi chấp nhận/từ chối
3. **GameStartMessage** - Thông báo bắt đầu trận đấu
4. **ClientHandler** - Xử lý các message invitation
5. **GameSessionManager** - Quản lý các trận đấu
6. **ClientConnectionManager** - Quản lý kết nối client

## 🔄 Flow hoạt động

### 1. Gửi lời mời thách đấu

```java
// Client A gửi lời mời đến Client B
InviteRequest inviteRequest = new InviteRequest("Alice", "Bob");
// Gửi qua socket đến server
```

**Server xử lý:**

- Kiểm tra cả 2 người chơi có online không
- Kiểm tra cả 2 người chơi có đang bận không
- Gửi InviteRequest đến Client B
- Gửi response xác nhận về Client A

### 2. Phản hồi lời mời

```java
// Client B phản hồi
InviteResponse response = new InviteResponse("Bob", "Alice", true); // true = chấp nhận
```

**Server xử lý:**

- Nếu chấp nhận: Tạo GameSession mới
- Gửi GameStartMessage đến cả 2 client
- Cập nhật trạng thái busy cho cả 2 người chơi
- Nếu từ chối: Thông báo cho người mời

### 3. Bắt đầu trận đấu

```java
// Server gửi đến cả 2 client
GameStartMessage gameStart = new GameStartMessage(
                "Alice", "Bob", "sessionId", true  // Alice ném trước
        );
```

## 📝 Các message types

### InviteRequest

```java
public class InviteRequest extends Message {
    private String targetUsername; // Người được mời

    public InviteRequest(String senderUsername, String targetUsername) {
        super(MessageType.INVITE_REQUEST, senderUsername);
        this.targetUsername = targetUsername;
    }
}
```

### InviteResponse

```java
public class InviteResponse extends Message {
    private boolean accepted;        // Chấp nhận hay từ chối
    private String inviterUsername;  // Người đã mời

    public InviteResponse(String responderUsername, String inviterUsername, boolean accepted) {
        super(MessageType.INVITE_RESPONSE, responderUsername);
        this.inviterUsername = inviterUsername;
        this.accepted = accepted;
    }
}
```

### GameStartMessage

```java
public class GameStartMessage extends Message {
    private String opponentUsername; // Tên đối thủ
    private String sessionId;        // ID trận đấu
    private boolean isYourTurn;      // Có phải lượt của mình không

    public GameStartMessage(String playerUsername, String opponentUsername,
                            String sessionId, boolean isYourTurn) {
        super(MessageType.GAME_START, playerUsername);
        this.opponentUsername = opponentUsername;
        this.sessionId = sessionId;
        this.isYourTurn = isYourTurn;
    }
}
```

## 🛠️ Cách sử dụng trong Client

### 1. Gửi lời mời

```java
// Trong client code
public void sendInvite(String targetUsername) {
    InviteRequest invite = new InviteRequest(currentUsername, targetUsername);
    sendToServer(invite);
}
```

### 2. Xử lý nhận lời mời

```java
// Trong client message handler
if(message instanceof InviteRequest){
InviteRequest invite = (InviteRequest) message;

// Hiển thị popup xác nhận
showInviteDialog(invite.getSenderUN());
        }
```

### 3. Phản hồi lời mời

```java
public void respondToInvite(String inviterUsername, boolean accepted) {
    InviteResponse response = new InviteResponse(
            currentUsername, inviterUsername, accepted
    );
    sendToServer(response);
}
```

### 4. Xử lý bắt đầu trận đấu

```java
if(message instanceof GameStartMessage){
GameStartMessage gameStart = (GameStartMessage) message;

// Chuyển đến màn hình game
startGame(gameStart.getOpponentUsername(), 
              gameStart.

getSessionId(), 
              gameStart.

isYourTurn());
        }
```

## 🔧 Cấu hình và Testing

### Chạy demo

```bash
# Compile project
mvn clean compile

# Chạy demo invitation system
java -cp target/classes com.oop.game.server.demo.InvitationDemo
```

### Test với server thật

```bash
# Chạy server
java -cp target/classes com.oop.game.server.ServerMain

# Chạy 2 client để test
# Client 1: Login với username "Alice"
# Client 2: Login với username "Bob"
# Client 1: Gửi lời mời đến "Bob"
# Client 2: Chấp nhận lời mời
```

## ⚠️ Lưu ý quan trọng

1. **Trạng thái busy**: Người chơi đang trong trận đấu không thể nhận lời mời mới
2. **Connection management**: Client disconnect sẽ tự động cleanup
3. **Session management**: Mỗi trận đấu có sessionId duy nhất
4. **Turn order**: Người mời luôn ném trước
5. **Error handling**: Tất cả lỗi đều được xử lý và thông báo rõ ràng

## 🐛 Troubleshooting

### Lỗi thường gặp:

1. **"Người chơi không online"**
    - Kiểm tra target player có đăng nhập không
    - Kiểm tra connection

2. **"Người chơi đang bận"**
    - Target player đang trong trận đấu khác
    - Chờ trận đấu kết thúc

3. **"Không thể gửi lời mời"**
    - Kiểm tra network connection
    - Kiểm tra server status

### Debug tips:

```java
// Bật debug logging
System.setProperty("java.util.logging.config.file","logging.properties");

// Kiểm tra trạng thái server
ClientManager.

getInstance().

printStatus();
GameSessionManager.

getInstance().

printStatus();
ClientConnectionManager.

getInstance().

printStatus();
```

## 📚 Tài liệu tham khảo

- [README.md](README.md) - Tổng quan dự án
- [server.md](server.md) - Hướng dẫn server
- [game.md](game.md) - Luật chơi chi tiết
- [InvitationDemo.java](src/main/java/com/oop/game/server/demo/InvitationDemo.java) - Demo code
