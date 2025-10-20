package server.models;

public class Player {

    private int user_id;

    private String name; // tên game
    private int elo; // điểm elo
    private int totalWin; // tổng số trận thắng
    private int totalLoss; // tổng số trận thua

    public Player() {
    }

    public Player(String name, int elo, int totalWin, int totalLoss) {
        this.name = name;
        this.elo = elo;
        this.totalLoss = totalLoss;
        this.totalWin = totalWin;
    }

    // Getters
    public int getId() {
        return user_id;
    }

    public String getName() {
        return this.name;
    }

    public int getElo() {
        return elo;
    }

    public int getTotalWin() {
        return totalWin;
    }

    public int getTotalLoss() {
        return totalLoss;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setId(int user_id) {
        this.user_id = user_id;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void setTotalWin(int totalWin) {
        this.totalWin = totalWin;
    }

    public void setTotalLoss(int totalLoss) {
        this.totalLoss = totalLoss;
    }

}
