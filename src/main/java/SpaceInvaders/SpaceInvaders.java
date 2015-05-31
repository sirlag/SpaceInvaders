package SpaceInvaders;

import SpaceInvaders.Util.Score;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class SpaceInvaders extends SimpleApplication{

    private Node enemyNode, border ,cannonNode, lives;
    private int direction, iter, dir, numLIves;
    private AudioNode bounce_sound;
    private AudioNode shoot_sound;
    private Float enemySpeed;
    private BitmapText scoreText, highscoreText, livesText;
    private Score gameScore;

    public static void main(String[] args){
        SpaceInvaders game = new SpaceInvaders();
        game.start();
    }


    @Override
    public void simpleInitApp(){
        cam.setLocation(new Vector3f(0f,0f,9f));
        flyCam.setEnabled(false);

        direction = 1;

        createText();
        makeLives();
        gameScore = new Score(0, "AAA");

        border = border();
        border.setLocalTranslation(0,0,-6);
        enemyNode = invaderNode();
        enemyNode.setLocalTranslation(-6,-1,-9);
        cannonNode = makeCannon();
        cannonNode.setLocalTranslation(new Vector3f(0,-5.9f,-9));


        rootNode.attachChild(border);
        rootNode.attachChild(enemyNode);
        rootNode.attachChild(cannonNode);
        rootNode.attachChild(scoreText);
        rootNode.attachChild(highscoreText);
        rootNode.attachChild(livesText);
        rootNode.attachChild(lives);

        iter = 0;//System.currentTimeMillis();
        dir = 0;
        enemySpeed = .05f;

        AttachInputs();
        AttachSounds();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //makes the jerking moving motion
        if(iter%30==0)//(System.currentTimeMillis()-iter)%20000==0)
        {
            moveEnemyNode();
            scoreText.setText("Score: " + gameScore.getScore());
            super.simpleUpdate(tpf);
        }
        iter++;
    }

    private void createText(){
        scoreText = new BitmapText(guiFont,false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setText("Score : 0");
        scoreText.setLocalTranslation(70, 97,-280);

        highscoreText = new BitmapText(guiFont,false);
        highscoreText.setSize(guiFont.getCharSet().getRenderedSize());
        highscoreText.setText("High Score : 0");
        highscoreText.setLocalTranslation(-150,97,-280);

        livesText = new BitmapText(guiFont,false);
        livesText.setSize(guiFont.getCharSet().getRenderedSize());
        livesText.setText("Lives ");
        livesText.setLocalScale(.01f);
        livesText.setLocalTranslation(2,-3.3f,0);
    }

    public void makeLives()
    {
        lives = new Node("lives");
        for(int i = 0;i<3;i++) {
            Spatial life = assetManager.loadModel("assets/Models/Cannon/Cannon.j3o");
            life.setLocalScale(.04f);
            life.setMaterial(makeColoredMaterial(ColorRGBA.Red));
            life.setLocalTranslation(.7f*i+3,-3.4f,0);
            lives.attachChild(life);
        }
    }

    public void removeLife()
    {
        lives.getChildren().remove(lives.getChildren().size()-1);
    }


    private Spatial makeInvader(ColorRGBA color, Vector3f offsetVector){
        Spatial invader = assetManager.loadModel("assets/Models/Invader/Invader.j3o");
        invader.setLocalScale(.1f);
        invader.setLocalTranslation(offsetVector);
        invader.setMaterial(makeColoredMaterial(color));
        //invader.setModelBound(new BoundingBox());
        //invader.updateModelBound();
        invader.setUserData("Score",50);
        return invader;
    }

    private Node makeCannon() {
        Node node = new Node("cannon");
        Spatial cannon = assetManager.loadModel("assets/Models/Cannon/Cannon.j3o");
        cannon.setLocalScale(.1f);
        cannon.setMaterial(makeColoredMaterial(ColorRGBA.Red));
        node.attachChild(cannon);
        return node;
    }

    private Node border(){
        float h=7,w=11;
        Material material = makeColoredMaterial(ColorRGBA.randomColor());
        Box width = new Box(w, 1, 3);
        Box height = new Box(1, h, 3);

        Geometry top = new Geometry("Top", width);
        top.setMaterial(material);
        top.setLocalTranslation(0,h,0);

        Geometry bottom = new Geometry("Bottom", width);
        bottom.setMaterial(material);
        bottom.setLocalTranslation(0, -h, 0);

        Geometry leftSide = new Geometry("Left Side", height);
        leftSide.setMaterial(material);
        leftSide.setLocalTranslation(-w, 0, 0);

        Geometry rightSide = new Geometry("Right Side", height);
        rightSide.setMaterial(material);
        rightSide.setLocalTranslation(w,0 , 0);

        Node borderNode = new Node("Border");
        borderNode.attachChild(top);
        borderNode.attachChild(bottom);
        borderNode.attachChild(leftSide);
        borderNode.attachChild(rightSide);

        borderNode.getChildren().forEach(com.jme3.scene.Spatial::updateModelBound);

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
                //rotates invaders to center only at beginning
                case 0  :   node.attachChild(makeInvader(ColorRGBA.Pink, offset));//.rotate(FastMath.PI / 8, (FastMath.PI / 100) * (5 - i % 11), 0));
                            break;
                case 1  :
                case 2  :   node.attachChild(makeInvader(ColorRGBA.Blue, offset));//.rotate(FastMath.PI / (i / 11 * 16), (FastMath.PI / 100) * (5 - i % 11), 0));
                            break;
                case 3  :
                case 4  :   node.attachChild(makeInvader(ColorRGBA.Green, offset));//.rotate(0, (FastMath.PI / 100) * (5 - i % 11), 0));
                            break;
            }
        }
        return node;
    }

    public void AttachInputs(){
        inputManager.addMapping("Move Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Move Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener((AnalogListener) (name, keyPressed, tpf) -> {
            if (name.equals("Move Left")&&dir!=-1)
                if(movePlayer(-1))
                    dir = -1;
                else
                    dir = 0;
            else if (name.equals("Move Right")&&dir!=1)
                if(movePlayer(1))
                    dir = 1;
                else
                    dir = 0;
        }, "Move Left", "Move Right");

        inputManager.addListener((ActionListener) (name, keyPressed, tpf) -> {
            if(name.equals("Shoot") && keyPressed) {
                shoot_sound.play();
                gameScore.addScore(50);
                removeLife();
            }
        }, "Shoot");
    }

    public void moveEnemyNode(){
        for (Spatial spatial : enemyNode.getChildren()){
            for (Spatial geo : border.getChildren()){
                CollisionResults results = new CollisionResults();
                spatial.collideWith(geo.getWorldBound(), results);
                if (results.size() > 0) {
                    if(geo.getName().equals("Bottom"))
                        endGame();
                    bounce_sound.play();
                    direction *= -1;
                    enemyNode.move(0, -.025f, 0);
                    enemySpeed += .001f;
                }
            }
            CollisionResults cr = new CollisionResults();
            spatial.collideWith(cannonNode.getChild(0).getWorldBound(), cr);
        }
        //rotateInvaders();
        enemyNode.move(enemySpeed * direction, 0, 0);
    }

    /*public void rotateInvaders()
    {
        for(int i = 0; i <enemyNode.getChildren().size();i++)
        {
            Spatial s = enemyNode.getChildren().get(i);
            Vector3f v = s.getLocalTranslation();
            float x = v.getX();
            float y = v.getY();
            float z = v.getZ();
        }
    }*/

    private Material makeColoredMaterial(ColorRGBA color){
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        newMaterial.setColor("Color", color);
        return newMaterial;
    }

    private boolean movePlayer(float direction){
        boolean col = false;
        for(Spatial geo : border.getChildren()){
            CollisionResults cr = new CollisionResults();
            cannonNode.getChild(0).collideWith(geo.getWorldBound(), cr);
            if(cr.size() >= 1){
                direction *= -1;
                col = true;
            }
        }
        cannonNode.move(.05f * direction, 0, 0);
        return col;
    }

    private void AttachSounds(){
        AudioNode music_sound = new AudioNode(assetManager, "assets/Sounds/Music/InvadersStage.ogg", false);
        music_sound.setPositional(false);
        music_sound.setLooping(true);
        music_sound.setVolume(3);
        rootNode.attachChild(music_sound);
        music_sound.play();

        bounce_sound = new AudioNode(assetManager, "assets/Sounds/Effects/Bounce.wav", false);
        bounce_sound.setPositional(false);
        bounce_sound.setLooping(false);
        bounce_sound.setVolume(5);

        shoot_sound = new AudioNode(assetManager, "assets/Sounds/Effects/Shoot.wav", false);
        shoot_sound.setPositional(false);
        shoot_sound.setLooping(false);
        shoot_sound.setVolume(2);



        rootNode.attachChild(bounce_sound);
        rootNode.attachChild(shoot_sound);


    }

    private void endGame(){
        System.out.println("Game Over!");
    }

}
