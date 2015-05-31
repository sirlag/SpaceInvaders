import SpaceInvaders.Database.H2Manager;
import SpaceInvaders.Util.Score;

import java.util.Optional;

public class TestDatabase {

    @org.junit.Test
    public void TestCreate(){

        //Tests creation of a Score, and the insert of it.
        Score insert = new Score(100000, "HGF");
        H2Manager.INSTANCE.addScore(insert);

        //see above, in one state.
        assert(H2Manager.INSTANCE.addScore(new Score(10, "JNT")));

        //gets highest score of the two, make sure it was actually created, and then prints it.
        Optional<Score> score = H2Manager.INSTANCE.getHighScore();
        assert(score != null);
        System.out.println(score);

        //Assuming the other tests go well, drops the table in preparation for production
        H2Manager.INSTANCE.reset();
    }
}
