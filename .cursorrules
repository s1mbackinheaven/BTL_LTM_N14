Sau đây là tất cả những gì dự kiến của project
Logic, luật chơi và nghiệp vụ game ném phi tiêu:
Hệ thống có một server và nhiều client. Server lưu toàn bộ thông tin và dữ liệu.
Để chơi, người chơi phải login vào tài khoản của mình từ một máy client. Sau khi login thành công, giao diện hiện lên một danh sách các người chơi đang online, mỗi người chơi có các thông tin: tên, tổng số điểm hiện có của người chơi (elo), trạng thái (hoặc đang bận nếu đang chơi với người khác, hoặc đang rỗi nếu không chơi với ai)
Muốn mời (thách đấu) ai thì người chơi click vào tên của đối thủ đó trong danh sách online, khi đó sẽ hiện lên option thách đầu.
Khi bị người chơi khác thách đấu, người chơi được thách đấu sẽ hiện lên 1 màn hình pop-up thông báo có người thách đấu, người đó có thể chấp nhận (OK), hoặc từ chối (Reject).
Khi chấp nhận, 2 người chơi sẽ vào chơi với nhau, và server sẽ làm trọng tài. Giao diện chơi sẽ hiện ra với vị trí phía dưới (giữa) màn hình là nhân vật người chơi, vị trí phía trên (giữa) màn hình là đối thủ, vị trí giữa màn hình ngăn cách 2 người chơi sẽ là bảng ném phi tiêu.
Mỗi người chơi sẽ được cho ngẫu nhiên 3 phụ trợ trong 6 phụ trợ của game gồm:
Nhân đôi điểm ném được
Trừ nửa điểm tiếp theo có được của đối thủ
Hiện ra 3/5 vùng màu có trên bảng ném
Đổi 2 vùng màu ngẫu nhiên ở phía đối thủ
Ném thêm 1 lần nữa
Lực đẩy = 0
Bảng ném sẽ có 5 vùng màu trắng, xanh, vàng, tím, đỏ tương ứng 1, 2, 3, 4, 5 điểm, nếu ném ra ngoài sẽ là 0 điểm. Với 5 khoảng màu tạo thành hình chữ thập.
Đầu màn chơi, người thách đấu sẽ được ném trước, với các ván sau đó sẽ là luân phiên.
Ở mối lượt chơi hệ thống (sever) sẽ random lực đẩy từ -3 đến 3 (số nguyên), với 1 công thức nhất định mà người chơi cần dựa theo kinh nghiệm có thể có theo mỗi lượt hoặc theo những trận đấu trước đó để lựa chọn cách ném sao cho đạt được mong muốn. Khi chơi người chơi sẽ được hiện lên bảng điền x, y lần lượt là tọa độ trên dưới tương ứng, trong 15s người chơi cần nhanh chóng điền nếu không sẽ random x, y.
Khi send vị trí x, y thì hệ thống (Sever) tiếp nhận và ném, khi ném vào màu nào, thì người chơi được quyền đổi màu đó với 1 màu bất kì. Đối thủ cần dự đoán vị trí màu dựa trên điểm người chơi được cộng rồi dự đoán xem màu nào đã hoán đổi vị trí.
Vị trí: 
Sẽ dựa theo x, y của người chơi điền cũng với lực đẩy sẽ hiện ra trên bảng điền x, y của người chơi.
Thời gian ban đầu người chơi sẽ không biết công thức mà cần tự dò công thức hoặc chơi theo kinh nghiệm, khi đạt 1036 elo người chơi sẽ được hệ thống thông báo công thức.
Ở bảng điểm, mặc định với cặp (x, y) lần lượt là màu trắng, xanh, đỏ, vàng, tím: (0, 1), (0, -1), (0, 0), (1, 0), (-1, 0).
Công thức: 
Xfinal = X + f
Yfinal = Y + sign(X) * floor(f/2) 
Với sign(X) = -1 nếu X < 0, sign(X) = 1 nếu X >= 0
Trò chơi sẽ kết thúc khi chạm 16 điểm. Ai chạm trước người đó chiến thắng và kết thúc game đấu. Ai thắng game đấu sẽ được 101 elo, nếu thua sẽ bị trừ 36 elo.
Nếu 1 người chơi tự ý kết thúc, rời cuộc chơi khi game đấu chưa kết thúc thì sẽ mặc định thua. Hệ thống sẽ thông báo cho người chơi còn lại và sẽ được cộng 51 điểm.
Kết quá tất cả trận đấu sẽ lưu vào sever. Mỗi người chơi đều có thể vào xem bảng xếp hạng các người chơi trong toàn bộ hệ thống, theo lần lượt các tiêu chí: tổng số điểm (giảm dần), tổng số trận thắng (giảm dần)
Phân công chi tiết:
Trịnh Lê Xuân Bách (Lead)
Thiết kế và quản lý luật game & nghiệp vụ tổng thể (đảm bảo đúng như mô tả trên).
Viết module xử lý logic game trên server:
Quản lý elo, thắng/thua, cộng/trừ điểm.
Áp dụng công thức tính tọa độ (Xfinal, Yfinal).
Random phụ trợ, random lực đẩy f.
Xử lý kết thúc game, rời game, lưu kết quả.
Quản lý game session trên server (ai đang chơi với ai, ai đang online, trạng thái busy/free).
Điều phối và review code Git, merge nhánh.
Lường Tiến Dũng
Xây dựng server backend (network + thread):
Tạo server socket, quản lý nhiều client (multi-thread).
Đảm bảo đồng bộ dữ liệu game giữa 2 người chơi.
Triển khai chức năng: login, logout, hiển thị danh sách người chơi online, gửi/nhận lời mời thách đấu.
Xử lý luồng dữ liệu realtime giữa client–server khi đang chơi: gửi tọa độ (x, y), nhận kết quả, gửi trạng thái phụ trợ, update điểm.
Kết nối server ↔ DB (cơ bản: lưu user, elo, kết quả trận).
Lê Minh Châu
Thiết kế client (UI + network):
Màn hình login, danh sách người chơi online, trạng thái busy/free.
Pop-up khi có lời mời thách đấu (OK/Reject).
Màn hình trận đấu: hiển thị nhân vật mình (dưới), đối thủ (trên), bảng phi tiêu ở giữa.
Form nhập tọa độ (x, y) trong 15s, hoặc auto-random nếu hết giờ.
Hiển thị 3 phụ trợ của người chơi.
Hiển thị điểm số realtime khi chơi.
Kết nối socket đến server, gửi/nhận data (login, mời, thao tác chơi).
Nguyễn Tuấn Anh
Thiết kế cơ chế và UI phụ trợ (UI + network):
Giao diện hiển thị 6 loại phụ trợ (chọn ngẫu nhiên 3 cho mỗi người).
Thực hiện các hiệu ứng phụ trợ (x2 điểm, trừ điểm đối thủ, đổi màu, hiện vùng, thêm lượt, lực đẩy = 0).
Xử lý đổi màu bảng phi tiêu sau khi người chơi ném trúng (update UI + đồng bộ server).
Logic đoán màu bị đổi cho đối thủ (dựa vào điểm + quan sát).
Xây dựng bảng xếp hạng (leaderboard):
Lấy dữ liệu elo từ server.
Hiển thị danh sách theo thứ tự: điểm giảm dần → số trận thắng giảm dần.
Kiểm thử giao diện + fix bug client.
Flow code (dự kiến)
A
Viết spec luật game + công thức tính rõ ràng (đóng băng nghiệp vụ).
Xây dựng module core game logic trên server (có thể chạy offline, chưa cần socket):
Input: (x, y), f → Output: điểm số + màu trúng.
Xử lý phụ trợ (random, x2, trừ điểm, đổi màu...).
Quản lý Elo, thắng/thua, kết thúc game.
Tạo repo Git, setup cấu trúc thư mục (server/, client/, doc/).
Viết test nhỏ chứng minh module chạy đúng.
B
Xây socket server + quản lý đa client (thread hoặc async).
Code login/logout, quản lý danh sách online, trạng thái busy/free.
Thêm game session manager: kết nối 2 client khi thách đấu.
Kết nối core logic game của A vào server → xử lý ném thật.
Tích hợp DB đơn giản: user, mật khẩu, elo, kết quả trận.
C
Xây UI login + connect socket đến server.
Làm danh sách người chơi online + popup khi có invite.
Giao diện trận đấu:
Nhân vật mình (dưới), đối thủ (trên), bảng phi tiêu ở giữa.
Form nhập tọa độ (x, y) trong 15s.
Hiển thị điểm realtime (server gửi về).
Gửi/nhận data qua socket (đúng format B định nghĩa).
D
Làm UI 6 phụ trợ + hiệu ứng.
Tích hợp vào trận đấu: khi server random phụ trợ → client nhận → hiển thị.
Làm cơ chế đổi màu bảng phi tiêu + update UI.
Làm leaderboard: lấy elo từ server, sắp xếp và hiển thị.
Kiểm thử UI client, phối hợp fix bug với C.
A
Review code toàn bộ repo, merge branch Git.
Viết README + hướng dẫn chạy server/client.
Demo thử nghiệm nhóm.
Tóm tắt
A: viết luật + core logic game (offline).
B: dựng server socket + tích hợp core logic.
C: dựng client UI chính + network.
D: thêm phụ trợ + leaderboard + polish UI.
A: merge, review, finalize.
Cấu trúc dự án (dự kiến)
dart-game-project/
│── README.md                 # Tài liệu hướng dẫn
│── pom.xml / build.gradle    # File build Maven/Gradle (nếu dùng)
│── /docs                     # Tài liệu thiết kế, ERD, use case, v.v.
│── /sql                      # Script tạo DB (user, elo, match history)
│
├── /server                   # Code phía server
│   ├── ServerMain.java       # Điểm khởi chạy server
│   ├── GameServer.java       # Quản lý socket, thread, client handler
│   ├── ClientHandler.java    # Luồng riêng cho từng client
│   │
│   ├── /core                 # Core game logic (phần A phụ trách chính)
│   │   ├── GameEngine.java   # Xử lý lượt ném, random f, áp dụng công thức
│   │   ├── GameSession.java  # Quản lý trận đấu giữa 2 người chơi
│   │   ├── Player.java       # Thông tin người chơi trong trận
│   │   ├── PowerUp.java      # Enum các loại phụ trợ
│   │   └── ColorBoard.java   # Xử lý bảng màu, hoán đổi, điểm số
│   │
│   ├── /protocol             # Định nghĩa message giữa client ↔ server
│   │   ├── Message.java      # Lớp cha message
│   │   ├── LoginRequest.java
│   │   ├── InviteRequest.java
│   │   ├── MoveRequest.java
│   │   ├── MoveResult.java
│   │   └── LeaderboardData.java
│   │
│   ├── /db                   # Tầng dữ liệu (phần B code chính)
│   │   ├── Database.java     # Kết nối DB
│   │   ├── UserDAO.java      # CRUD user
│   │   └── MatchDAO.java     # Lưu kết quả trận đấu
│   │
│   └── /util                 # Tiện ích chung (JSON parser, logger, config)
│       └── JsonUtils.java
│
├── /client                   # Code phía client
│   ├── ClientMain.java       # Điểm khởi chạy client
│   ├── GameClient.java       # Quản lý socket từ client tới server
│   │
│   ├── /ui                   # UI hiển thị (Swing/JavaFX)
│   │   ├── LoginScreen.java
│   │   ├── LobbyScreen.java  # Danh sách người chơi online
│   │   ├── GameScreen.java   # Màn hình trận đấu
│   │   ├── PowerUpUI.java    # Hiển thị phụ trợ
│   │   └── LeaderboardUI.java
│   │
│   └── /model                # Model dùng trên client (mirror với server)
│       ├── PlayerInfo.java
│       ├── GameState.java
│       └── ScoreBoard.java
│
└── /test                     # Unit test
    ├── GameEngineTest.java
    ├── ProtocolTest.java
    └── DatabaseTest.java
Cấu trúc chỉ là để tham khảo, dụa theo repo sẵn có + yêu cầu của cá nhân mà thêm file, package theo tên để phù hợp, tất nhiên form chung sẽ như trên, hầu như chỉ khác các file code, còn package thì sẽ giống.
Vai trò:
/server/game → phần A:
GameEngine: công thức ném, random lực đẩy, xử lý phụ trợ.
GameSession: quản lý ván chơi, tính thắng/thua, elo.
/server/protocol → phần A + B:
A định nghĩa message.
B dùng trong socket handler.
/server/db → phần B (socket server + DB).	
/client/ui → phần C + D (UI + tương tác mạng).
/client/model → đồng bộ với server (POJO).	
/test → mọi người cùng viết, nhưng chủ yếu A test logic game, B test network, C+D test UI.


Công việc của tôi là làm phần việc của A.
Bạn sẽ giao tiếp với tôi bằng tiếng Việt.