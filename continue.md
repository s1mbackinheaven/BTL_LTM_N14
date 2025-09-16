## Kế hoạch tiếp theo (Next steps)

Mục tiêu: Hoàn thiện các thiếu sót chức năng, đảm bảo an toàn, tính ổn định, và trải nghiệm người chơi theo đặc tả trong `README.md` và `game.md`.

### 1) Ưu tiên cao (Critical)
- **Đăng ký tài khoản + bảo mật mật khẩu**
  - Thêm `RegisterRequest/Response`, handler trong `ClientHandler`.
  - Hash mật khẩu (BCrypt/Argon2) khi register và verify khi login (`UserDAO`).
  - Chấp nhận: có thể tạo user mới, login bằng mật khẩu đã hash, DB không chứa plaintext.

- **Kết thúc trận khi 1 bên rời**
  - Hoàn thiện `GameSession.playerLeft(...)` để gọi `endGame(..., OPPONENT_LEFT)` và trigger `GameSessionManager.endGameSession(...)`.
  - Chấp nhận: rời trận => đối thủ thắng, ELO +51/-36, DB cập nhật match và user stats.

- **Đồng bộ/broadcast sự kiện game**
  - Thiết kế và gửi `GameUpdate` (điểm, lượt, kết quả ném), `TurnChange`, `BoardUpdate`, `GameEnd` đến cả 2 client qua `ClientConnectionManager`.
  - Chấp nhận: sau mỗi lượt ném, cả 2 client nhận được điểm, lượt, và trạng thái bảng thống nhất; khi kết thúc trận, cả 2 nhận `GameEnd`.

- **Timer 15s mỗi lượt + auto-random**
  - Thêm cơ chế đếm thời gian lượt (scheduler/timer per-session). Nếu quá 15s, thực hiện lượt auto-random như spec.
  - Chấp nhận: nếu không gửi `MoveRequest` trong 15s, server tự xử lý một lượt cho người chơi, sau đó chuyển lượt.

### 2) Tính năng cốt lõi còn thiếu
- **Leaderboard & Match history API**
  - Thêm `LeaderboardRequest/Response`, trả top N từ `UserDAO.getTopUsers(...)`.
  - Thêm `RecentMatchesRequest/Response` dùng `MatchDAO.getRecentMatches(...)` hoặc theo user.
  - Chấp nhận: client yêu cầu được danh sách top ELO và lịch sử trận gần đây.

- **Logout chủ động**
  - Thêm `LogoutRequest` để client thoát sạch: unregister kết nối, gỡ khỏi online, kết thúc trận nếu đang chơi.
  - Chấp nhận: client logout không cần đóng socket cưỡng bức; server dọn dẹp như khi disconnect.

- **Invite lifecycle**
  - Thêm timeout/hủy lời mời (ví dụ 30s). Trạng thái lời mời pending, hủy khi 2 bên bận/timeout.
  - Chấp nhận: lời mời hết hạn hoặc bị hủy sẽ thông báo rõ ràng cho cả 2 bên.

- **Power-up: lựa chọn đổi màu có chủ đích**
  - Thay cơ chế random trong `handleBoardEffects` bằng quy trình: server yêu cầu client chọn, nhận `ColorSwapChoice` rồi áp dụng và broadcast `BoardUpdate`.
  - Chấp nhận: người chơi được chọn 2 vùng để swap theo luật, đối thủ nhìn thấy thay đổi đồng bộ.

### 3) Ổn định và bảo vệ
- **Validation & rate limiting**
  - Ràng buộc tọa độ đầu vào, từ chối spam `MoveRequest`, đảm bảo lượt hợp lệ (đúng người, đúng thời gian).
  - Chấp nhận: không thể thực hiện nhiều lượt liên tiếp, không thể gửi dữ liệu ngoài miền hợp lệ.

- **Reconnect/Resume (nếu kịp)**
  - Cho phép client khôi phục vào trận đang diễn ra trong cửa sổ ngắn (ví dụ 30–60s) dựa trên `sessionId` + `username`.
  - Chấp nhận: nếu reconnect trong thời gian cho phép, tiếp tục trận; quá hạn coi như rời trận.

### 4) Dọn dẹp & nhất quán
- **Nhất quán cấu hình DB**
  - `sql/init.sql` dùng `USE dungcony;` trong khi README hướng dẫn `game_db`. Quyết định 1 tên duy nhất, cập nhật `README`, `.env`, docker-compose cho thống nhất.

- **ELO mặc định**
  - `Player(String username)` đang set `elo = 36`; sửa về 1000 hoặc loại bỏ constructor này nơi không cần.

- **Loại bỏ trùng lặp handler**
  - `ClientHandler` có 2 nhánh `InviteResponse`; giữ một nhánh duy nhất.

### 5) Mở rộng giao thức (Protocol)
- Message mới đề xuất:
  - `RegisterRequest/Response`
  - `LogoutRequest`
  - `GameUpdate` (điểm lượt, tổng điểm, hit color, power-up đã dùng)
  - `TurnChange`
  - `BoardUpdate`
  - `GameEnd`
  - `LeaderboardRequest/Response`, `RecentMatchesRequest/Response`

Thêm enum vào `MessageType`, tạo lớp message tương ứng trong `protocol/request|response`, và nhánh xử lý trong `ClientHandler`.

### 6) Kiểm thử
- **Unit cho core game**: `GameEngine` (random force trong miền, tính điểm, power-up), `ColorBoard`.
- **Integration**: tạo session → ném lượt → kết thúc trận → kiểm tra DB (`matches`, `users`).
- **Concurrency**: 2 client gửi đồng thời; đảm bảo luân phiên lượt và khóa dữ liệu hợp lý.

### 7) Milestones gợi ý
- M1: Register + password hashing + logout + fix playerLeft → endGame.
- M2: Game events broadcast + timer 15s + validation.
- M3: Leaderboard & recent matches API + invite timeout.
- M4: Power-up lựa chọn đổi màu có chủ đích + UI sync.
- M5: Reconnect/resume (tùy thời gian) + dọn dẹp cấu hình.

### 8) Điểm chạm mã nguồn chính (để bắt đầu)
- `src/main/java/com/oop/game/server/ClientHandler.java`
- `src/main/java/com/oop/game/server/core/GameSession.java`
- `src/main/java/com/oop/game/server/managers/GameSessionManager.java`
- `src/main/java/com/oop/game/server/DAO/UserDAO.java`
- `src/main/java/com/oop/game/server/DAO/MatchDAO.java`
- `src/main/java/com/oop/game/server/enums/MessageType.java`
- `src/main/java/com/oop/game/server/protocol/*`

---
Gợi ý thực thi nhanh: bắt đầu với đăng ký + hashing, sửa `playerLeft`, bổ sung `GameEnd` broadcast, rồi thêm timer 15s. Đây là các thay đổi tác động lớn nhất đến trải nghiệm và tính đúng đắn.


