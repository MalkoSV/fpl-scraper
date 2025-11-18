package fpl.mals;

public class Player {
    private final String name;
    private final int count;
    private int start;
    private int captain;
    private int tripleCaptain;
    private int vice;
    private final int score;

    public Player(String name, int count, int start, int captain, int tripleCaptain, int vice, int score) {
        this.name = name;
        this.count = count;
        this.start = start;
        this.captain = captain;
        this.tripleCaptain = tripleCaptain;
        this.vice = vice;
        this.score = score;
    }

    public Player(String name, int count, int score) {
        this.name = name;
        this.count = count;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getStart() {
        return start;
    }

    public int getCaptain() {
        return captain;
    }

    public int getTripleCaptain() {
        return tripleCaptain;
    }

    public int getVice() {
        return vice;
    }

    public int getScore() {
        return score;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setCaptain(int captain) {
        this.captain = captain;
    }

    public void setTripleCaptain(int tripleCaptain) {
        this.tripleCaptain = tripleCaptain;
    }

    public void setVice(int vice) {
        this.vice = vice;
    }
}
