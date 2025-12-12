package fpl.domain.model;

public class Player implements HasPosition {
    private final String name;
    private final int count;
    private int start;
    private int captain;
    private int tripleCaptain;
    private int vice;
    private int points;
    private int availability;
    private int position;

    public Player(String name, int count, int start, int captain, int tripleCaptain,
                  int vice, int points, int availability, int position) {
        this.name = name;
        this.count = count;
        this.start = start;
        this.captain = captain;
        this.tripleCaptain = tripleCaptain;
        this.vice = vice;
        this.points = points;
        this.availability = availability;
        this.position = position;
    }

    public Player(String name, int count, int points) {
        this.name = name;
        this.count = count;
        this.points = points;
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

    public int getPoints() {
        return points;
    }

    public int getAvailability() {
        return availability;
    }

    public int getPosition() {
        return position;
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

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
