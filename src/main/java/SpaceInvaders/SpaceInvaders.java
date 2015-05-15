package SpaceInvaders;


import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

public class SpaceInvaders extends SimpleApplication{

    public static void main(String[] args){
        SpaceInvaders game = new SpaceInvaders();
        game.start();
    }


    @Override
    public void simpleInitApp(){
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0f,0f,.5f));

    }

}
