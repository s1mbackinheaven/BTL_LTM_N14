package com.oop.game.server.enums;

public enum MessageType {

    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    LOGOUT_REQUEST,

    // Lobby
    PLAYER_LIST_REQUEST,
    PLAYER_LIST_RESPONSE,
    PLAYER_STATUS_UPDATE,

    // Game invitation
    INVITE_REQUEST,
    INVITE_RESPONSE,

    // Game play
    GAME_START,
    MOVE_REQUEST,
    MOVE_RESULT,
    GAME_STATE_UPDATE,
    GAME_END,
    PLAYER_LEFT,

    // Power-ups
    POWERUP_USE,
    POWERUP_EFFECT,

    // Leaderboard
    LEADERBOARD_REQUEST,
    LEADERBOARD_RESPONSE,

    // Error
    ERROR
    
}
