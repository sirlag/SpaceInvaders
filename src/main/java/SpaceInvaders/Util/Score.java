package SpaceInvaders.Util;

public class Score {
    int score;
    String initials;

    public Score(int score, String initials) {
        this.score = score;
        this.initials = initials;
    }

    public int getScore() {
        return score;
    }

    public String getInitials() {
        return initials;
    }
    
    public void setInitials(String i)
    {
        initials = i;
    }

    public String toString() {
        return String.format("%d - %s", score, initials);
    }

    public void addScore(int i){
        score += i;
    }
}
