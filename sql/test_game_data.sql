-- ========================================
-- TEST DATA CHO GAME ĐẤU PHI TIÊU
-- Script tạo dữ liệu test với database schema hiện tại
-- ========================================

USE dungcony;

-- ========================================
-- 1. TẠO TEST USERS
-- ========================================

-- Xóa dữ liệu cũ (nếu có)
DELETE FROM matches WHERE id > 0;
DELETE FROM users WHERE username LIKE 'test%';

-- Tạo test users
INSERT INTO users (username, password, elo, total_wins, total_losses) VALUES
('testplayer1', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 1200, 15, 5),
('testplayer2', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 1100, 12, 8),
('testplayer3', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 1300, 20, 3),
('testplayer4', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 950, 5, 15),
('testplayer5', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 1050, 8, 12);

-- ========================================
-- 2. TẠO TEST MATCHES
-- ========================================

-- Lấy user IDs
SET @user1 = (SELECT id FROM users WHERE username = 'testplayer1');
SET @user2 = (SELECT id FROM users WHERE username = 'testplayer2');
SET @user3 = (SELECT id FROM users WHERE username = 'testplayer3');
SET @user4 = (SELECT id FROM users WHERE username = 'testplayer4');
SET @user5 = (SELECT id FROM users WHERE username = 'testplayer5');

-- Tạo các trận đấu đã hoàn thành
INSERT INTO matches (player1_id, player2_id, winner_id, player1_score, player2_score, elo_change, played_at) VALUES
-- Trận 1: testplayer1 thắng testplayer2 (16-12)
(@user1, @user2, @user1, 16, 12, 101, DATE_SUB(NOW(), INTERVAL 2 HOUR)),

-- Trận 2: testplayer3 thắng testplayer4 (16-8) 
(@user3, @user4, @user3, 16, 8, 101, DATE_SUB(NOW(), INTERVAL 1 HOUR 30 MINUTE)),

-- Trận 3: testplayer2 thắng testplayer5 (16-14)
(@user2, @user5, @user2, 16, 14, 101, DATE_SUB(NOW(), INTERVAL 1 HOUR)),

-- Trận 4: testplayer1 vs testplayer3 - player1 rời giữa chừng
(@user1, @user3, @user3, 10, 16, 51, DATE_SUB(NOW(), INTERVAL 45 MINUTE)),

-- Trận 5: testplayer4 thắng testplayer5 (16-13)
(@user4, @user5, @user4, 16, 13, 101, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

-- Trận 6: Trận đang chơi (chưa kết thúc)
(@user1, @user5, NULL, 8, 6, 0, NULL);

-- ========================================
-- 3. VERIFICATION QUERIES
-- ========================================

SELECT '=== TEST USERS CREATED ===' as info;
SELECT 
    id,
    username,
    elo,
    total_wins,
    total_losses,
    ROUND(total_wins * 100.0 / NULLIF(total_wins + total_losses, 0), 2) as win_rate
FROM users 
WHERE username LIKE 'test%'
ORDER BY elo DESC;

SELECT '=== TEST MATCHES CREATED ===' as info;
SELECT 
    m.id,
    u1.username as player1,
    u2.username as player2,
    COALESCE(uw.username, 'ONGOING') as winner,
    m.player1_score,
    m.player2_score,
    m.elo_change,
    COALESCE(m.played_at, 'NULL') as played_at
FROM matches m
JOIN users u1 ON m.player1_id = u1.id
JOIN users u2 ON m.player2_id = u2.id
LEFT JOIN users uw ON m.winner_id = uw.id
WHERE u1.username LIKE 'test%' OR u2.username LIKE 'test%'
ORDER BY COALESCE(m.played_at, NOW()) DESC;

-- ========================================
-- 4. SAMPLE QUERIES FOR TESTING
-- ========================================

SELECT '=== LEADERBOARD ===' as info;
SELECT 
    username,
    elo,
    total_wins,
    total_losses,
    ROUND(total_wins * 100.0 / NULLIF(total_wins + total_losses, 0), 2) as win_rate
FROM users 
WHERE username LIKE 'test%'
ORDER BY elo DESC, total_wins DESC;

SELECT '=== MATCH HISTORY FOR testplayer1 ===' as info;
SELECT 
    m.id,
    CASE 
        WHEN m.player1_id = @user1 THEN u2.username 
        ELSE u1.username 
    END as opponent,
    CASE 
        WHEN m.winner_id = @user1 THEN 'WIN'
        WHEN m.winner_id IS NULL THEN 'ONGOING'
        ELSE 'LOSS'
    END as result,
    CASE 
        WHEN m.player1_id = @user1 THEN m.player1_score 
        ELSE m.player2_score 
    END as my_score,
    CASE 
        WHEN m.player1_id = @user1 THEN m.player2_score 
        ELSE m.player1_score 
    END as opponent_score,
    m.elo_change,
    m.played_at
FROM matches m
JOIN users u1 ON m.player1_id = u1.id
JOIN users u2 ON m.player2_id = u2.id
WHERE m.player1_id = @user1 OR m.player2_id = @user1
ORDER BY COALESCE(m.played_at, NOW()) DESC;

SELECT '=== RECENT MATCHES ===' as info;
SELECT 
    m.id,
    u1.username as player1,
    u2.username as player2,
    COALESCE(uw.username, 'ONGOING') as winner,
    CONCAT(m.player1_score, '-', m.player2_score) as score,
    m.elo_change,
    m.played_at
FROM matches m
JOIN users u1 ON m.player1_id = u1.id
JOIN users u2 ON m.player2_id = u2.id
LEFT JOIN users uw ON m.winner_id = uw.id
WHERE u1.username LIKE 'test%' OR u2.username LIKE 'test%'
ORDER BY COALESCE(m.played_at, NOW()) DESC
LIMIT 5;

SELECT '=== GAME STATISTICS ===' as info;
SELECT 
    COUNT(*) as total_matches,
    COUNT(CASE WHEN winner_id IS NOT NULL THEN 1 END) as completed_matches,
    COUNT(CASE WHEN winner_id IS NULL THEN 1 END) as ongoing_matches,
    ROUND(AVG(player1_score + player2_score), 2) as avg_total_score,
    MAX(elo_change) as max_elo_change,
    COUNT(CASE WHEN elo_change = 51 THEN 1 END) as abandoned_matches
FROM matches m
JOIN users u1 ON m.player1_id = u1.id
JOIN users u2 ON m.player2_id = u2.id
WHERE u1.username LIKE 'test%' OR u2.username LIKE 'test%';

-- ========================================
-- 5. CLEANUP SCRIPT (Uncomment to clean up)
-- ========================================

/*
-- Uncomment these lines to clean up test data:

DELETE FROM matches 
WHERE player1_id IN (SELECT id FROM users WHERE username LIKE 'test%')
   OR player2_id IN (SELECT id FROM users WHERE username LIKE 'test%');

DELETE FROM users WHERE username LIKE 'test%';

SELECT 'Test data cleaned up!' as result;
*/

SELECT '✅ TEST DATA CREATED SUCCESSFULLY!' as result;
SELECT 'Bạn có thể test game logic với data này' as note;
