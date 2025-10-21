package server.enums;

/**
 * Enum định nghĩa các trạng thái của lời mời thách đấu
 */
public enum InviteStatus {
    // Validation errors
    INVALID_INPUT,          // Thiếu thông tin
    SENDER_NOT_FOUND,       // Người gửi không online
    TARGET_NOT_FOUND,       // Người nhận không online
    SENDER_BUSY,            // Người gửi đang bận
    TARGET_BUSY,            // Người nhận đang bận
    SELF_INVITE,            // Tự mời chính mình
    DUPLICATE_INVITE,       // Đã gửi lời mời trước đó
    
    // Success states
    SENT,                   // Gửi thành công
    ACCEPTED,               // Chấp nhận
    REJECTED,               // Từ chối
    CANCELLED,              // Hủy bỏ
    EXPIRED,                // Hết hạn (timeout)
    
    // Error
    SEND_FAILED             // Lỗi khi gửi
}

