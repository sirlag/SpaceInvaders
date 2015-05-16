package SpaceInvaders;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.SystemListener;

public class SpaceInvaders extends SimpleApplication{

    public static void main(String[] args){
        SpaceInvaders game = new SpaceInvaders();
        game.start();
    }


    @Override
    public void simpleInitApp(){
        //cam.setParallelProjection(true);
        //cam.setLocation(new Vector3f(0f,0f,3f));

        Node enemyNode = invaderNode();

        rootNode.attachChild(enemyNode);


    }

    private Spatial makeInvader(String colorName, ColorRGBA color, Vector3f offsetVector){
        Spatial invader = assetManager.loadModel("assets/Models/Invader/Invader.j3o");
        invader.setLocalScale(.1f);
        invader.setLocalTranslation(offsetVector);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor(colorName, color);
        invader.setMaterial(mat);
        return invader;
    }

    private Node invaderNode(){
        Node node = new Node("Enemy");
        Vector3f offset;
        for (int i = 0; i < 55; i++){
            offset = new Vector3f(i%11, 5-(i/11), 0);
            if (i%11>0)
                offset = offset.add(.3f*(i%11), 0, 0);
            switch (i/11){
                case 0  :   node.attachChild(makeInvader("Color", ColorRGBA.Pink, offset));
                            break;
                case 1  :
                case 2  :   node.attachChild(makeInvader("Color", ColorRGBA.Blue, offset));
                            break;
                case 3  :
                case 4  :   node.attachChild(makeInvader("Color", ColorRGBA.Green, offset));
                            break;
            }
        }
        return node;
    }


}
