package SpaceInvaders;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class SpaceInvaders extends SimpleApplication{

    private Node enemyNode, border;
    private int direction;

    public static void main(String[] args){
        SpaceInvaders game = new SpaceInvaders();
        game.start();
    }


    @Override
    public void simpleInitApp(){
        //cam.setParallelProjection(true);
        //cam.setLocation(new Vector3f(0f,0f,3f));

        direction = 1;

        border = border();
        enemyNode = invaderNode();

        rootNode.attachChild(border);
        rootNode.attachChild(enemyNode);

        AttachInputs();
    }

    @Override
    public void simpleUpdate(float tpf) {
        moveEnemyNode();
        super.simpleUpdate(tpf);
    }

    private Spatial makeInvader(ColorRGBA color, Vector3f offsetVector){
        Spatial invader = assetManager.loadModel("assets/Models/Invader/Invader.j3o");
        invader.setLocalScale(.1f);
        invader.setLocalTranslation(offsetVector);
        invader.setMaterial(makeColoredMaterial(color));
        return invader;
    }

    private Node border(){
        Material material = makeColoredMaterial(ColorRGBA.randomColor());
        Box width = new Box(12, .5f, 3);
        Box height = new Box(1, .5f, 3);

        Geometry top = new Geometry("Top", width);
        top.setMaterial(material);
        top.setLocalTranslation(0,6,0);

        Geometry bottom = new Geometry("Bottom", width);
        bottom.setMaterial(material);

        Geometry leftSide = new Geometry("Left Side", height);
        leftSide.setMaterial(material);

        Geometry rightSide = new Geometry("Right Side", height);
        rightSide.setMaterial(material);
        rightSide.setLocalTranslation(12, 0, 0);

        Node borderNode = new Node("Border");
        borderNode.attachChild(top);
        borderNode.attachChild(bottom);
        borderNode.attachChild(leftSide);
        borderNode.attachChild(rightSide);

        return borderNode;
    }

    private Node invaderNode(){
        Node node = new Node("Enemy");
        Vector3f offset;
        for (int i = 0; i < 55; i++){
            offset = new Vector3f(i%11, 5-(i/11), 0);
            if (i%11>0)
                offset = offset.add(.3f*(i%11), 0, 0);

            /*
                If Java Ever gets Pattern Matching, this would be

                node.attachChild(makeInvader("Color", match (i/11){
                       case 0 : ColorRGBA.Pink,
                       case 1 ..............
                       }, offset));

               Would Have been so much cleaner.
             */
            switch (i/11){
                case 0  :   node.attachChild(makeInvader(ColorRGBA.Pink, offset));
                            break;
                case 1  :
                case 2  :   node.attachChild(makeInvader(ColorRGBA.Blue, offset));
                            break;
                case 3  :
                case 4  :   node.attachChild(makeInvader(ColorRGBA.Green, offset));
                            break;
            }
        }
        return node;
    }

    public void AttachInputs(){
        inputManager.addMapping("Move Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Move Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Change Direction", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener((ActionListener) (name, keyPressed, tpf) -> {
            if (name.equals("Move Left") && !keyPressed)
                enemyNode.move(-.5f, 0, 0);
            else if (name.equals("Move Right") && !keyPressed)
                enemyNode.move(.5f*direction, 0 ,0);
        }, "Move Left", "Move Right");
    }

    public void moveEnemyNode(){
        for (Spatial spatial : enemyNode.getChildren()){
            for (Spatial geo : border.getChildren()){
                CollisionResults results = new CollisionResults();
                spatial.collideWith(geo, results);
                if (results.size() > 0)
                    direction *= -1;
            }
        }
        enemyNode.move(.5f*direction, 0 , 0);
    }

    private Material makeColoredMaterial(ColorRGBA color){
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        newMaterial.setColor("Color", color);
        return newMaterial;
    }

}
