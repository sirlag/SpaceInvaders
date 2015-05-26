package SpaceInvaders.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import SpaceInvaders.Util.Score;
import com.esotericsoftware.minlog.Log;

import org.h2.jdbc.JdbcSQLException;

public enum H2Manager {

    INSTANCE;

    private Statement stat;
    private Connection conn;

    H2Manager(){
        try{
            connectToScoreDB();
            createScoreTable();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void connectToScoreDB() throws ClassNotFoundException, SQLException{
        Class.forName("org.h2.Driver");

        try {
            conn = DriverManager.getConnection("jdbc:h2:./Scores");
        } catch (JdbcSQLException e){
            Log.error("Error opening database, possibly already open!", e);
        } catch (Exception x){
            Log.error("Error opening database", x);
            System.exit(0);
        }

        stat = conn.createStatement();
    }

    private void createScoreTable() throws SQLException{
        stat.execute("CREATE TABLE IF NOT EXISTS Scores (score INT, initials NVARCHAR)");
    }

    public boolean addScore(Score score){
        PreparedStatement ps;

        try {
            ps = conn.prepareStatement("INSERT INTO Scores VALUES (?, ?)");
            ps.setInt(1, score.getScore());
            ps.setString(2, score.getInitials());
            ps.execute();
        } catch (SQLException e) {
            System.out.println("Unable to add Score:");
            Log.error("Unable to add score: ", e);
            return false;
        }
        return true;
    }

    public ArrayList<Score> getScores(){
        ResultSet rs;
        ArrayList<Score> scores = new ArrayList<>();

        try {
            rs = stat.executeQuery("SELECT * FROM Scores");

            while (rs.next()) {
                Score temp = new Score(rs.getInt(0), rs.getString(1));
                scores.add(temp);
            }
        } catch (SQLException ex){
            Log.error("Failed to get scores", ex);
        }
        return scores;
    }

    public Optional<Score> getHighScore(){
        PreparedStatement ps;
        ResultSet rs;

        try{
            ps = conn.prepareStatement("SELECT * FROM Scores WHERE score = (SELECT MAX(SCORE) FROM SCORES)");
            rs = ps.executeQuery();
            rs.next();
            return Optional.ofNullable(new Score(rs.getInt(1), rs.getNString(2)));
        }catch (SQLException ex){
            Log.error("Failed to get High Score", ex);
        }
        return null;
    }

}
