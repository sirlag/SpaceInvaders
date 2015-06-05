package SpaceInvaders;

import SpaceInvaders.Database.H2Manager;
import SpaceInvaders.Util.Score;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.lwjgl.Sys;

import java.util.Optional;
import java.util.Random;

public class SpaceInvaders extends SimpleApplication {


    private Node enemyNode, border ,cannonNode, lives, ufoNode, gameNode, bulletNode, leaderNode;
    private int direction, iter, dir, ufoD, highScore, enemyShots;
    private AudioNode bounce_sound, shoot_sound, ufo_sound, music_sound;
    private Float enemySpeed;
    private BitmapText scoreText, highscoreText, livesText;
    private BitmapFont myFont;
    private Score gameScore;
    private Boolean ufoExists,game,shot, enemyShot;

    public static void main(String[] args) {
        SpaceInvaders game = new SpaceInvaders();
        game.start();
    }


    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0f, 0f, 9f));
        flyCam.setEnabled(false);

        direction = 1;

        createText();
        makeLives();
        gameScore = new Score(0, "AAA");

        border = border();
        border.setLocalTranslation(0, 0, -6);
        enemyNode = invaderNode();
        enemyNode.setLocalTranslation(-6, -1, -9);
        cannonNode = makeCannon();
        cannonNode.setLocalTranslation(new Vector3f(0, -5.9f, -9));

        ufoNode = new Node("UFO");
        ufoExists = false;

        bulletNode = new Node("Bullets");


        gameNode = new Node("game nodes");
        gameNode.attachChild(border);
        gameNode.attachChild(enemyNode);
        gameNode.attachChild(cannonNode);
        gameNode.attachChild(bulletNode);
        gameNode.attachChild(scoreText);
        gameNode.attachChild(ufoNode);
        gameNode.attachChild(highscoreText);
        gameNode.attachChild(livesText);
        gameNode.attachChild(lives);
        gameNode.setCullHint(Spatial.CullHint.Always);
        rootNode.attachChild(gameNode);

        iter = 0;//System.currentTimeMillis();
        dir = 0;
        enemySpeed = .05f;
        game = false;
        shot = false;
        enemyShot = false;
        enemyShots = 0;

        AttachInputs();
        AttachSounds();

        menu();
        createLeaderBoard();
    }

    private void menu() {
        Box TitleBox = new Box(11.5f,1.15f,0.1f);
        Material mat = makeColoredMaterial(ColorRGBA.White);
        Geometry titleBox = new Geometry("back",TitleBox);
        titleBox.setMaterial(mat);
        //titleBox.setLocalScale(.9f);
        titleBox.setLocalTranslation(0, 3.3f, -4);

        Geometry tB = new Geometry("tb", TitleBox);
        tB.setMaterial(makeColoredMaterial(ColorRGBA.Black));
        tB.setLocalScale(.8f);
        tB.setLocalTranslation(0,3.05f,-3);

        rootNode.attachChild(titleBox);
        rootNode.attachChild(tB);

        myFont = assetManager.loadFont("assets/Fonts/Arcadepix.fnt");

        BitmapText Title = new BitmapText(myFont, false);
        BitmapText start = new BitmapText(myFont, false);
        BitmapText leader = new BitmapText(myFont, false);
        BitmapText keyText = new BitmapText(myFont, false);
        BitmapText keyText2 = new BitmapText(myFont, false);

        start.setText("Start - Press Enter");
        Title.setText("Space Invaders");
        leader.setText("Leader Boards - Press Shift");
        keyText.setText("Movement <- ->       space  shoot");
        keyText2.setText("         A  S");

        Title.setLocalScale(.04f);
        start.setLocalScale(.012f);
        leader.setLocalScale(.012f);
        keyText.setLocalScale(.013f);
        keyText2.setLocalScale(.013f);

        Title.setLocalTranslation(-6.35f,3,0);
        start.setLocalTranslation(-2.0f,1.2f,0);
        leader.setLocalTranslation(-4.3f,.51f,0);
        keyText.setLocalTranslation(-5.465f,-2.51f,0);
        keyText2.setLocalTranslation(-5.465f,-3.15f, 0);

        guiNode.attachChild(Title);
        guiNode.attachChild(start);
        guiNode.attachChild(leader);
        guiNode.attachChild(keyText);
        guiNode.attachChild(keyText2);
        Node menuTextNode = new Node("text");
        menuTextNode.attachChild(start);
        menuTextNode.attachChild(Title);
        menuTextNode.attachChild(leader);
        menuTextNode.attachChild(keyText);
        menuTextNode.attachChild(keyText2);
        rootNode.attachChild(menuTextNode);

        Node picNode = new Node("menu pics");
        picNode.attachChild(makeInvader(ColorRGBA.White, new Vector3f(-3,-1.5f,0)).rotate(0,.1f,0));
        Node cN = new Node("CannonNode");
        cN.attachChild(makeCannon());
        cN.setLocalTranslation(0, -1.5f,0);
        picNode.attachChild(cN);
        Spatial ufo = assetManager.loadModel("assets/Models/UFO/UFO.j3o");
        ufo.setLocalScale(.1f);
        ufo.setMaterial(makeColoredMaterial(ColorRGBA.White));
        ufo.setLocalTranslation(3,-1.5f,0);
        ufo.rotate(0,-.1f,0);
        picNode.attachChild(ufo);
        rootNode.attachChild(picNode);

        Box key = new Box(.3f,.3f,.1f);
        Box bar = new Box(1f,.3f,.1f);
        mat = makeColoredMaterial(ColorRGBA.DarkGray);
        Geometry keys1 = new Geometry("key",key);
        Geometry keys2 = new Geometry("key",key);
        Geometry keys3 = new Geometry("Space",bar);
        Geometry keys4 = new Geometry("key", key);
        Geometry keys5 = new Geometry("key", key);
        keys1.setMaterial(mat);
        keys2.setMaterial(mat);
        keys3.setMaterial(mat);
        keys4.setMaterial(mat);
        keys5.setMaterial(mat);
        keys1.setLocalTranslation(-2.8f,-3,-1);
        keys2.setLocalTranslation(-1.9f,-3,-1);
        keys3.setLocalTranslation(1.5f,-3,-1);
        keys4.setLocalTranslation(-2.8f,-3.75f,-1);
        keys5.setLocalTranslation(-1.9f, -3.75f, -1);
        Node keysNode = new Node("keys");

        keysNode.attachChild(keys1);
        keysNode.attachChild(keys2);
        keysNode.attachChild(keys3);
        keysNode.attachChild(keys4);
        keysNode.attachChild(keys5);
        rootNode.attachChild(keysNode);
    }

    private void LeaderBoard()
    {
        for(Spatial s: rootNode.getChildren()) {
            if (!s.equals(leaderNode))
                s.setCullHint(Spatial.CullHint.Always);
            else
                s.setCullHint(Spatial.CullHint.Inherit);
        }
    }

    public void createLeaderBoard()
    {
        leaderNode = new Node("LeaderBoardNodes");
        BitmapText Title = new BitmapText(myFont,false);
        Title.setText("Leader Board");
        Title.setLocalScale(.035f);
        Title.setLocalTranslation(-5.5f,3,0);
        guiNode.attachChild(Title);
        leaderNode.attachChild(Title);

        leaderNode.setCullHint(Spatial.CullHint.Always);
        rootNode.attachChild(leaderNode);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //makes the jerking moving motion
        if(game) {
            if (iter % 30 == 0)//(System.currentTimeMillis()-iter)%20000==0)
            {
                moveBullets();
                moveEnemyNode();
                scoreText.setText("Score: " + gameScore.getScore());
                super.simpleUpdate(tpf);
            }
            if((iter+1)%2000 == 0&& !ufoExists) {
                makeUFO();
            }
            if((iter+1)%700==0)
                enemyShoot();
            iter++;
            if (ufoExists) {
                moveUFO();
            }
            if (gameScore.getScore() > highScore) {
                highScore = gameScore.getScore();
                highscoreText.setText(String.format("High Score : %d - YOU", gameScore.getScore()));
            }
            music_sound.play();
            roundEnded();
        }
    }

    private void startGame(){
        for(Spatial s: rootNode.getChildren()) {
            if(!s.equals(gameNode))
                s.setCullHint(Spatial.CullHint.Always);
            else
                s.setCullHint(Spatial.CullHint.Inherit);
        }
        game = true;
    }

    private void createText() {
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize());
        scoreText.setText("Score : 0");
        scoreText.setLocalScale(.02f);
        scoreText.setLocalTranslation(3, 3.5f, 0);

        highscoreText = new BitmapText(guiFont, false);
        highscoreText.setSize(guiFont.getCharSet().getRenderedSize());
        Optional<Score> score = H2Manager.INSTANCE.getHighScore();
        if(score != null && score.isPresent()) {
            highscoreText.setText(String.format("High Score : %d - %s", score.get().getScore(), score.get().getInitials()));
            highScore = score.get().getScore();
        }
        else {
            highscoreText.setText("High Score : 0");
            highScore = 0;
        }
        highscoreText.setLocalScale(.02f);
        highscoreText.setLocalTranslation(-5,3.5f,0);

        livesText = new BitmapText(guiFont, false);
        livesText.setSize(guiFont.getCharSet().getRenderedSize());
        livesText.setText("Lives ");
        livesText.setLocalScale(.01f);
        livesText.setLocalTranslation(2, -3.3f, 0);
    }

    public void makeLives() {
        lives = new Node("lives");
        for (int i = 0; i < 3; i++) {
            Spatial life = assetManager.loadModel("assets/Models/Cannon/Cannon.j3o");
            life.setLocalScale(.04f);
            life.setMaterial(makeColoredMaterial(ColorRGBA.Red));
            life.setLocalTranslation(.7f * i + 3, -3.4f, 0);
            lives.attachChild(life);
        }
    }

    public void removeLife()
    {
        if(lives.getChildren().size() > 0)
            lives.getChildren().remove(lives.getChildren().size()-1);
        else
            endGame();
    }


    private Spatial makeInvader(ColorRGBA color, Vector3f offsetVector) {
        Spatial invader = assetManager.loadModel("assets/Models/Invader/Invader.j3o");
        invader.setLocalScale(.1f);
        invader.setLocalTranslation(offsetVector);
        invader.setMaterial(makeColoredMaterial(color));
        //invader.setModelBound(new BoundingBox());
        //invader.updateModelBound();
        invader.setUserData("Score", 50);
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

    private void makeUFO() {
        ufoExists = true;
        Spatial ufo = assetManager.loadModel("assets/Models/UFO/UFO.j3o");
        ufo.setLocalScale(.1f);
        ufo.setMaterial(makeColoredMaterial(ColorRGBA.White));
        ufoNode.attachChild(ufo);
        Random random = new Random();
        switch (random.nextInt(2)){
            case 0 : ufoD = -1;
                     break;
            case 1 : ufoD = 1;
                     break;
        }
        ufoNode.setLocalTranslation(0, 5, -9.5f);
        ufo.setLocalTranslation(9*(-1*ufoD), 0, 0);
        ufo_sound.play();
    }

    private Node border() {
        float h = 7, w = 11;
        Material material = makeColoredMaterial(ColorRGBA.DarkGray);
        Box width = new Box(w, 1, 3);
        Box height = new Box(1, h, 3);

        Geometry top = new Geometry("Top", width);
        top.setMaterial(material);
        top.setLocalTranslation(0, h, 0);

        Geometry bottom = new Geometry("Bottom", width);
        bottom.setMaterial(material);
        bottom.setLocalTranslation(0, -h, 0);

        Geometry leftSide = new Geometry("Left Side", height);
        leftSide.setMaterial(material);
        leftSide.setLocalTranslation(-w, 0, 0);

        Geometry rightSide = new Geometry("Right Side", height);
        rightSide.setMaterial(material);
        rightSide.setLocalTranslation(w, 0, 0);

        Geometry pseudoTop = new Geometry("pseudo top", width);
        pseudoTop.setMaterial(makeColoredMaterial(ColorRGBA.Black));
        pseudoTop.setLocalTranslation(0,h,-6);

        Geometry pseudoBottom = new Geometry("pseudo Bottom", width);
        pseudoBottom.setMaterial(makeColoredMaterial(ColorRGBA.Black));
        pseudoBottom.setLocalTranslation(0,-h,-6);

        Node borderNode = new Node("Border");
        borderNode.attachChild(top);
        borderNode.attachChild(bottom);
        borderNode.attachChild(leftSide);
        borderNode.attachChild(rightSide);
        borderNode.attachChild(pseudoTop);
        borderNode.attachChild(pseudoBottom);

        borderNode.getChildren().forEach(com.jme3.scene.Spatial::updateModelBound);

        return borderNode;
    }

    private Node invaderNode() {
        Node node = new Node("Enemy");
        Vector3f offset;
        for (int i = 0; i < 55; i++) {
            offset = new Vector3f(i % 11, 5 - (i / 11), 0);
            if (i % 11 > 0)
                offset = offset.add(.3f * (i % 11), 0, 0);

            /*
                If Java Ever gets Pattern Matching, this would be

                node.attachChild(makeInvader("Color", match (i/11){
                       case 0 : ColorRGBA.Pink,
                       case 1 ..............
                       }, offset));

               Would Have been so much cleaner.
             */
            switch (i / 11) {
                //rotates invaders to center only at beginning
                case 0:
                    node.attachChild(makeInvader(ColorRGBA.Pink, offset));//.rotate(FastMath.PI / 8, (FastMath.PI / 100) * (5 - i % 11), 0));
                    break;
                case 1:
                case 2:
                    node.attachChild(makeInvader(ColorRGBA.Blue, offset));//.rotate(FastMath.PI / (i / 11 * 16), (FastMath.PI / 100) * (5 - i % 11), 0));
                    break;
                case 3:
                case 4:
                    node.attachChild(makeInvader(ColorRGBA.Green, offset));//.rotate(0, (FastMath.PI / 100) * (5 - i % 11), 0));
                    break;
            }
        }
        return node;
    }

    public void AttachInputs() {
        inputManager.addMapping("Move Left", new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Move Right", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Score Board", new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_RSHIFT));

        inputManager.addListener((AnalogListener) (name, keyPressed, tpf) -> {
            if (name.equals("Move Left") && dir != -1 && game)
                if (movePlayer(-1)) {
                    dir = -1;
                    movePlayer(1);
                }
                else
                    dir = 0;
            else if (name.equals("Move Right") && dir != 1)
                if (movePlayer(1)) {
                    dir = 1;
                    movePlayer(-1);
                }
                else
                    dir = 0;
        }, "Move Left", "Move Right");

        inputManager.addListener((ActionListener) (name, keyPressed, tpf) -> {
            if (name.equals("Shoot") && keyPressed && game) {
                playerShoot();
            }
        }, "Shoot");

        inputManager.addListener((ActionListener) (name, keyPressed, tpf) -> {
            if (name.equals("Start") && keyPressed) {
                startGame();
            }
            else if(name.equals("Score Board")&&!game)
            {
                LeaderBoard();
            }
        }, "Start","Score Board");
    }

    public void moveEnemyNode() {
        boolean col = false;
        for (Spatial spatial : enemyNode.getChildren()) {
            for (Spatial geo : border.getChildren()) {
                CollisionResults results = new CollisionResults();
                spatial.collideWith(geo.getWorldBound(), results);
                if (results.size() > 0) {
                    if (geo.getName().equals("Bottom"))
                        endGame();
                    bounce_sound.play();
                    direction *= -1;
                    enemyNode.move(.01f*direction, -.075f, 0);
                    enemySpeed += .001f;
                    col = true;
                    break;
                }
            }
            if(col)
                break;
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

    private Material makeColoredMaterial(ColorRGBA color) {
        Material newMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        newMaterial.setColor("Color", color);
        return newMaterial;
    }

    private boolean movePlayer(float direction) {
        boolean col = false;
        for (Spatial geo : border.getChildren()) {
            CollisionResults cr = new CollisionResults();
            cannonNode.getChild(0).collideWith(geo.getWorldBound(), cr);
            if (cr.size() >= 1) {
                direction *= -1;
                col = true;
            }
        }
        cannonNode.move(.05f * direction, 0, 0);
        return col;
    }


    private void moveUFO(){
        for (Spatial geo : border.getChildren()){
            CollisionResults results = new CollisionResults();
            ufoNode.getChild(1).collideWith(geo.getWorldBound(), results);
            if (results.size() > 0) {
                gameNode.detachChild(ufoNode);
                ufoNode.getChildren().remove(1);
                gameNode.attachChild(ufoNode);
                ufoExists = false;
                ufo_sound.stop();
                return;
            }
        }
        ufoNode.getChild(1).move(.003f * ufoD, 0, 0);
    }

    private void AttachSounds(){
        music_sound = new AudioNode(assetManager, "assets/Sounds/Music/InvadersStage.ogg", false);
        music_sound.setPositional(false);
        music_sound.setLooping(true);
        music_sound.setVolume(3);
        rootNode.attachChild(music_sound);

        bounce_sound = new AudioNode(assetManager, "assets/Sounds/Effects/Bounce.wav", false);
        bounce_sound.setPositional(false);
        bounce_sound.setLooping(false);
        bounce_sound.setVolume(5);

        shoot_sound = new AudioNode(assetManager, "assets/Sounds/Effects/Shoot.wav", false);
        shoot_sound.setPositional(false);
        shoot_sound.setLooping(false);
        shoot_sound.setVolume(2);

        ufo_sound = new AudioNode(assetManager, "assets/Sounds/Effects/UFO.wav", false);
        ufo_sound.setPositional(false);
        ufo_sound.setLooping(true);
        ufo_sound.setVolume(.5f);

        rootNode.attachChild(bounce_sound);
        rootNode.attachChild(shoot_sound);
        ufoNode.attachChild(ufo_sound);
    }

    private void endGame() {
        for(Spatial s: rootNode.getChildren()) {
            if(s.equals(gameNode)||s.equals(leaderNode))
                s.setCullHint(Spatial.CullHint.Always);
            else
                s.setCullHint(Spatial.CullHint.Inherit);
        }
        reset();
        game = false;
        music_sound.stop();
        ufo_sound.stop();
    }

    private void roundEnded()
    {
        if(enemyNode.getChildren().size()==0)
        {
            enemySpeed += .05f;
            reset();
        }
    }

    private void reset()
    {
        iter = 0;
        enemySpeed = .05f;
        gameNode.detachChild(enemyNode);
        enemyNode = invaderNode();
        enemyNode.setLocalTranslation(-6, -1, -9);
        gameNode.attachChild(enemyNode);
        bulletNode.detachAllChildren();
        gameNode.detachChild(cannonNode);
        cannonNode = makeCannon();
        cannonNode.setLocalTranslation(0, -5.9f, -9);
        gameNode.attachChild(cannonNode);
        //AttachInputs();
    }

    /**
     * Creates a new Bullet Spatial to be added to a node.
     * @param direction 1 for firing up (IE PLayer) -1 for firing down (IE Invaders)
     * @param target The name of the entity you are trying to attack.
     * @return A new Bullet Spatial
     */
    private Spatial Bullet(int direction, String target) {
        Spatial bullet = assetManager.loadModel("assets/Models/Bullet/Bullet.j3o");
        bullet.setLocalScale(.15f);
        bullet.setName("Bullet");
        bullet.setUserData("Target", target);
        bullet.setUserData("Direction", direction);
        bullet.setMaterial(makeColoredMaterial(ColorRGBA.White));
        return bullet;
    }

    private void moveBullets(){
        for(int i = 0; i < bulletNode.getChildren().size(); i++) {
            Spatial bullet = bulletNode.getChild(i);
            CollisionResults collisionResults = new CollisionResults();
            bullet.move(0, .27f*(int)bullet.getUserData("Direction"), 0);
            if(bullet.getUserData("Target").toString().equals("Invader")){
                for(int j = 0; j < enemyNode.getChildren().size(); j++){
                    bullet.collideWith(enemyNode.getChild(j).getWorldBound(), collisionResults);
                    if(collisionResults.size() > 0){
                        enemyNode.detachChildAt(j);
                        gameScore.addScore(50);
                        bulletNode.detachChildAt(i);
                        i--;
                        shot = false;
                        break;
                    }
                }
                collisionResults.clear();
                if(ufoExists) {
                    bullet.collideWith(ufoNode.getChild(1).getWorldBound(), collisionResults);
                    if (collisionResults.size() > 0) {
                        ufoNode.detachChildAt(1);
                        gameScore.addScore(150);
                        bulletNode.detachChildAt(i);
                        shot = false;
                        ufoExists = false;
                        ufo_sound.stop();
                    }
                }
            }
            else if (bullet.getUserData("Target").equals("Player")){
                collisionResults.clear();
                bullet.collideWith(cannonNode.getChild(0).getWorldBound(), collisionResults);
                if(collisionResults.size() > 0){
                    bulletNode.detachChild(bullet);
                    enemyShots--;
                    enemyShot = false;
                    removeLife();
                }
            }
            if (!(collisionResults.size() > 0)){
                for (Spatial b : border.getChildren()) {
                    bullet.collideWith(b.getWorldBound(), collisionResults);
                    if (collisionResults.size() > 0) {
                        if(bullet.getUserData("Target").equals("Invader"))
                            shot = false;
                        else if(bullet.getUserData("Target").equals("Player"))
                            enemyShot = false;
                        bulletNode.detachChild(bullet);
                        break;
                    }
                }
            }
        }
    }

    private void playerShoot() {
        if (!shot) {
            Spatial bullet = Bullet(1, "Invader");
            bullet.setLocalTranslation(cannonNode.getWorldTranslation().add((float)1.35 - cannonNode.getWorldTranslation().getX() * .01f, 1, -1.5f));
            bulletNode.attachChild(bullet);
            shoot_sound.play();
            shot = true;
        }
    }

    private void enemyShoot()
    {
        int nodes = enemyNode.getChildren().size();
        int en1 = 11;//(int)(Math.random()*nodes-1);
        Spatial enemy1 = enemyNode.getChildren().get(en1);
        if(!enemyShot) {
            Spatial bullet = Bullet(-1, "Player");
            bullet.setLocalTranslation(enemy1.getWorldTranslation().add((float) 1.35 - enemy1.getWorldTranslation().getX() * .01f, enemy1.getWorldTranslation().getY() - 1f, -.5f));
            bulletNode.attachChild(bullet);
            shoot_sound.play();
            enemyShots++;
            if(enemyShots==2)
            {
                enemyShots = 0;
                enemyShot = true;
            }

        }
    }
}
