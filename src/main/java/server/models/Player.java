package server.models;

public class Player {

    private int user_id;

    private String name; // tên game
    private int elo; // điểm elo
    private int totalWins; // tổng số trận thắng
    private int totalLosses; // tổng số trận thua

    public Player(String name, int elo, int totalWins, int totalLosses) {
        this.name = name;
        this.elo = elo;
        this.totalLosses = totalLosses;
        this.totalWins = totalWins;
    }

    // Getters
    public int getId() {
        return user_id;
    }

    public int getElo() {
        return elo;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }


    public void setElo(int elo) {
        this.elo = elo;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }

}
