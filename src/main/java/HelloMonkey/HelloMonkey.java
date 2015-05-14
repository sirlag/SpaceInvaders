package HelloMonkey;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class HelloMonkey extends SimpleApplication{

    public static void main(String[] args){
        HelloMonkey app = new HelloMonkey();
        app.start();
    }

    @Override
    public void simpleInitApp(){
        //blue box
        Box box1 = new Box(1, 1, 1);
        Geometry blue = new Geometry("Box", box1);
        blue.setLocalTranslation(new Vector3f(1, -1, 1));
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);

        //red box
        Box box2 = new Box(1,1,1);
        Geometry red = new Geometry("Box", box2);
        red.setLocalTranslation(new Vector3f(1,3,1));
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);

        //green box
        Box box3 = new Box(1,2,1);
        Geometry green = new Geometry("Box", box3);
        green.setLocalTranslation(new Vector3f(3,1,-1));
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.Green);
        green.setMaterial(mat3);


        //Pivot node
        Node pivot = new Node("Pivot");
        rootNode.attachChild(pivot);

        //Add the boxes
        pivot.attachChild(blue);
        pivot.attachChild(red);
        pivot.attachChild(green);

        //roate this stuff
        pivot.rotate(.4f,.4f,0f);
    }

}
