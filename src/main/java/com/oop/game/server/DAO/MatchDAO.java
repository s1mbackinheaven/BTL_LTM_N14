package com.oop.game.server.DAO;

import com.oop.game.server.models.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO extends DAO {

    public int createMatch(int player1Id, int player2Id) {
        String sql = "INSERT INTO matches (player1_id, player2_id, player1_score, player2_score, elo_change) VALUES (?, ?, 0, 0, 0)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, player1Id);
            ps.setInt(2, player2Id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error creating match: " + e.getMessage());
        }
        return -1;
    }

    public boolean finishMatch(int matchId, int winnerId, int winnerScore, int loserScore, int eloChange) {

        String sql = "UPDATE matches SET winner_id = ?, player1_score = ?, player2_score = ?, elo_change = ?, played_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            Match match = getMatchById(matchId);
            if (match == null) {
                return false;
            }

            ps.setInt(1, winnerId);

            if (winnerId == match.getPlayer1Id()) {
                ps.setInt(2, winnerScore); // player1_score
                ps.setInt(3, loserScore); // player2_score
            } else {
                ps.setInt(2, loserScore); // player1_score
                ps.setInt(3, winnerScore); // player2_score
            }

            ps.setInt(4, eloChange);
            ps.setInt(5, matchId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error finishing match: " + e.getMessage());
            return false;
        }
    }

    public boolean finishMatchByLeaver(int matchId, int leaverId) {
        String sql = "UPDATE matches SET winner_id = ?, player1_score = ?, player2_score = ?, elo_change = 51, played_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Get match to determine players
            Match match = getMatchById(matchId);
            if (match == null) {
                return false;
            }

            // Winner is the player who didn't leave
            int winnerId = leaverId == match.getPlayer1Id() ? match.getPlayer2Id() : match.getPlayer1Id();

            ps.setInt(1, winnerId);

            // Set scores: leaver gets 0, winner gets at least 16
            if (leaverId == match.getPlayer1Id()) {
                ps.setInt(2, 0); // player1_score (leaver)
                ps.setInt(3, 16); // player2_score (winner)
            } else {
                ps.setInt(2, 16); // player1_score (winner)
                ps.setInt(3, 0); // player2_score (leaver)
            }

            ps.setInt(4, matchId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error finishing match by leaver: " + e.getMessage());
            return false;
        }
    }

    public Match getMatchById(int matchId) {
        String sql = "SELECT * FROM matches WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matchId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMatch(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting match by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Match> getMatchesByPlayer(int playerId) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE player1_id = ? OR player2_id = ? ORDER BY played_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            ps.setInt(2, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    matches.add(mapResultSetToMatch(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting matches for player: " + e.getMessage());
        }
        return matches;
    }

    public List<Match> getRecentMatches(int limit) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE winner_id IS NOT NULL ORDER BY played_at DESC LIMIT ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    matches.add(mapResultSetToMatch(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting recent matches: " + e.getMessage());
        }
        return matches;
    }

    public int getTotalMatchCount() {
        String sql = "SELECT COUNT(*) FROM matches WHERE winner_id IS NOT NULL";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("Error getting match count: " + e.getMessage());
        }
        return 0;
    }

    private Match mapResultSetToMatch(ResultSet rs) throws SQLException {
        return new Match(
                rs.getInt("id"),
                rs.getInt("player1_id"),
                rs.getInt("player2_id"),
                rs.getInt("winner_id"),
                rs.getInt("player1_score"),
                rs.getInt("player2_score"),
                rs.getInt("elo_change"),
                rs.getTimestamp("played_at"));
    }
}