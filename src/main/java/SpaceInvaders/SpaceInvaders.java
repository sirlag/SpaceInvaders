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
import com.jme3.ui.Picture;

import java.util.Optional;
import java.util.Random;

public class SpaceInvaders extends SimpleApplication {

    private Node enemyNode, border, cannonNode, lives, ufoNode, gameNode, bulletNode, leaderNode, menuNode;
    private int direction, iter, dir, ufoD, highScore, enemyShots, gameRound, flickerNum;
    private AudioNode bounce_sound, shoot_sound, ufo_sound, music_sound;
    private Float enemySpeed;
    private BitmapText scoreText, highscoreText, livesText, roundText;
    private BitmapFont myFont;
    private Score gameScore;
    private Boolean ufoExists, game, shot, enemyShot, flicker, muted;
    private Picture muteImage;

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
        setupMuteImage();
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
        enemySpeed = .04f;
        enemyShots = 0;
        gameRound = 1;
        flickerNum = 0;
        game = false;

        resetBools();

        AttachInputs();
        AttachSounds();

        menu();
        createLeaderBoard();

        roundText = new BitmapText(myFont, false);
    }

    private void menu() {
        menuNode = new Node("Menu");
        Box TitleBox = new Box(11.5f, 1.15f, 0.1f);
        Material mat = makeColoredMaterial(ColorRGBA.White);
        Geometry titleBox = new Geometry("back", TitleBox);
        titleBox.setMaterial(mat);
        //titleBox.setLocalScale(.9f);
        titleBox.setLocalTranslation(0, 3.3f, -4);

        Geometry tB = new Geometry("tb", TitleBox);
        tB.setMaterial(makeColoredMaterial(ColorRGBA.Black));
        tB.setLocalScale(.8f);
        tB.setLocalTranslation(0, 3.05f, -3);
        menuNode.attachChild(titleBox);
        menuNode.attachChild(tB);

        //rootNode.attachChild(titleBox);
        //rootNode.attachChild(tB);

        myFont = assetManager.loadFont("assets/Fonts/Arcadepix.fnt");

        Node menuTextNode = new Node("text");

        makeText(myFont, "Space Invaders", .04f,-6.35f, 3, 0, menuTextNode);
        makeText(myFont,"Start - Press Enter", .012f,-2.0f, 1.2f, 0,menuTextNode);
        makeText(myFont,"Leader Boards - Press Shift",.012f,-4.3f, .51f, 0,menuTextNode);
        makeText(myFont, "Movement <- ->       space  shoot",.013f,-5.465f, -2.51f, 0,menuTextNode);
        makeText(myFont, "         A  S",.013f,-5.465f, -3.15f, 0,menuTextNode);

        menuNode.attachChild(menuTextNode);
        //rootNode.attachChild(menuTextNode);

        Node picNode = new Node("menu pics");
        picNode.attachChild(makeInvader(ColorRGBA.White, new Vector3f(-3, -1.5f, 0)).rotate(0, .1f, 0));
        Node cN = new Node("CannonNode");
        cN.attachChild(makeCannon());
        cN.setLocalTranslation(0, -1.5f, 0);
        picNode.attachChild(cN);
        Spatial ufo = assetManager.loadModel("assets/Models/UFO/UFO.j3o");
        ufo.setLocalScale(.1f);
        ufo.setMaterial(makeColoredMaterial(ColorRGBA.White));
        ufo.setLocalTranslation(3, -1.5f, 0);
        ufo.rotate(0, -.1f, 0);
        picNode.attachChild(ufo);
        menuNode.attachChild(picNode);
        //rootNode.attachChild(picNode);

        Box key = new Box(.3f, .3f, .1f);
        Box bar = new Box(1f, .3f, .1f);
        mat = makeColoredMaterial(ColorRGBA.DarkGray);
        //uses helper method
        makeKey(key, mat, -2.8f, -3, -1, menuNode);
        makeKey(key, mat, -1.9f, -3, -1, menuNode);
        makeKey(bar, mat, 1.5f, -3, -1, menuNode);
        makeKey(key, mat, -2.8f, -3.75f, -1, menuNode);
        makeKey(key, mat, -1.9f, -3.75f, -1, menuNode);
        rootNode.attachChild(menuNode);
    }

    public void setupMuteImage() {
        muteImage = new Picture("Mute Picture");
        muteImage.setImage(assetManager, "assets/Interface/icons/mute.png", true);
        muteImage.setLocalTranslation(0, 0, 0);
        muteImage.setLocalScale(.1f);
    }

    //helper method to make keys in menu
    private void makeKey(Box b, Material mat, float x, float y, float z, Node node) {
        Geometry key = new Geometry("key", b);
        key.setMaterial(mat);
        key.setLocalTranslation(x, y, z);
        node.attachChild(key);
    }

    /*helper method to move between menu guide*/
    private void goTo(Node node) {
        for (Spatial s : rootNode.getChildren()) {
            if (!s.equals(node))
                s.setCullHint(Spatial.CullHint.Always);
            else
                s.setCullHint(Spatial.CullHint.Inherit);
        }
        game = node.getName().equals("game nodes");
        if (game)
            reset();
    }

    private void makeText(BitmapFont f, String text, float s,float x, float y, float z, Node node)
    {
        BitmapText b = new BitmapText(f,false);
        b.setText(text);
        b.setLocalScale(s);
        b.setLocalTranslation(x, y, z);
        guiNode.attachChild(b);
        node.attachChild(b);
    }

    public void createLeaderBoard() {
        leaderNode = new Node("LeaderBoardNodes");
        makeText(myFont, "Leader Board", .035f, -5.5f,2.8f, 0, leaderNode);
        makeText(myFont, "To menu   backspace", .0068f, 2.62f, 2.8f, 1, leaderNode);

        int i = 0;
        for(Score s : H2Manager.INSTANCE.getScores()) {
            if (i > 10)
                break;
            makeText(myFont, i+1 + " " + s.toString(), .015f, -5+(-.1f*i), .4f*2.8f*i -1, 0, leaderNode);
            i++;
        }

        Box space = new Box(.8f, .3f, .1f);
        makeKey(space, makeColoredMaterial(ColorRGBA.DarkGray), 5.5f, 3, 0, leaderNode);

        leaderNode.setCullHint(Spatial.CullHint.Always);
        rootNode.attachChild(leaderNode);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //makes the jerking moving motion
        if (game) {
            if (iter % 30 == 0)//(System.currentTimeMillis()-iter)%20000==0)
            {
                moveBullets();
                moveEnemyNode();
                scoreText.setText("Score: " + gameScore.getScore());
                super.simpleUpdate(tpf);
            }
            if ((iter + 1) % 6000 == 0 && !ufoExists) {
                makeUFO();
            }
            if ((iter + 1) % (450 - 2 * gameRound) == 0)
                enemyShoot();
            iter++;
            if (ufoExists) {
                moveUFO();
            }
            if (gameScore.getScore() > highScore) {
                highScore = gameScore.getScore();
                highscoreText.setText(String.format("High Score : %d - YOU", gameScore.getScore()));
            }
            if (flicker && iter % 40 == 0 && flickerNum < 5) {
                cannonNode.setCullHint(Spatial.CullHint.Always);
            }
            if (flicker && (iter + 20) % 40 == 0) {
                cannonNode.setCullHint(Spatial.CullHint.Inherit);
                if (flickerNum == 4)
                    flicker = false;
                flickerNum++;
            }
            if (iter > 300 && roundText.getText().contains("R"))
                roundText.setText("");
            music_sound.play();
            roundEnded();
        }
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
        if (score != null && score.isPresent()) {
            highscoreText.setText(String.format("High Score : %d - %s", score.get().getScore(), score.get().getInitials()));
            highScore = score.get().getScore();
        } else {
            highscoreText.setText("High Score : 0");
            highScore = 0;
        }
        highscoreText.setLocalScale(.02f);
        highscoreText.setLocalTranslation(-5, 3.5f, 0);

        livesText = new BitmapText(guiFont, false);
        livesText.setSize(guiFont.getCharSet().getRenderedSize());
        livesText.setText("Lives ");
        livesText.setLocalScale(.01f);
        livesText.setLocalTranslation(2, -3.3f, 0);
    }

    public void makeLives() {
        lives = new Node("lives");
        for (int i = 0; i < 3; i++) {
            lives.attachChild(Life(i));
        }
    }


    private Spatial Life(int i) {
        Spatial life = assetManager.loadModel("assets/Models/Cannon/Cannon.j3o");
        life.setLocalScale(.04f);
        life.setMaterial(makeColoredMaterial(ColorRGBA.Red));
        life.setLocalTranslation(.7f * i + 3, -3.4f, 0);
        lives.attachChild(life);
        return life;
    }

    public void removeLife() {
        if (lives.getChildren().size() > 0) {
            lives.getChildren().remove(lives.getChildren().size() - 1);
            flicker = true;
            flickerNum = 0;
        } else
            gameOver();
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
        switch (random.nextInt(2)) {
            case 0:
                ufoD = -1;
                break;
            case 1:
                ufoD = 1;
                break;
        }
        ufoNode.setLocalTranslation(0, 5, -9.5f);
        ufo.setLocalTranslation(9 * (-1 * ufoD), 0, 0);
        ufo_sound.play();
    }

    private Node border() {
        float h = 7, w = 11;
        Material material = makeColoredMaterial(ColorRGBA.DarkGray);
        Box width = new Box(w, 1, 3);
        Box height = new Box(1, h, 3);

        Node borderNode = new Node("Border");

        makeKey(width, material, 0, h, 0, borderNode);
        makeKey(width, material, 0, -h, 0, borderNode);
        makeKey(height, material, -w, 0, 0, borderNode);
        makeKey(height,material,w,0,0,borderNode);
        makeKey(width, makeColoredMaterial(ColorRGBA.Black), 0, h + 1, -6, borderNode);
        makeKey(width, makeColoredMaterial(ColorRGBA.Black), 0, -h, -6, borderNode);

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
        inputManager.addMapping("Menu", new KeyTrigger((KeyInput.KEY_BACK)));
        inputManager.addMapping("Mute", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("Pause", new KeyTrigger((KeyInput.KEY_P)));

        inputManager.addListener((AnalogListener) (name, keyPressed, tpf) -> {
            if (name.equals("Move Left") && dir != -1 && game)
                if (movePlayer(-1)) {
                    dir = -1;
                    movePlayer(1);
                } else
                    dir = 0;
            else if (name.equals("Move Right") && dir != 1)
                if (movePlayer(1)) {
                    dir = 1;
                    movePlayer(-1);
                } else
                    dir = 0;
        }, "Move Left", "Move Right");

        inputManager.addListener((ActionListener) (name, keyPressed, tpf) -> {
            if (name.equals("Shoot") && keyPressed && game) {
                playerShoot();
            } else if (name.equals("Start") && keyPressed && !roundText.getText().contains("G")) {
                goTo(gameNode);
                gameRound = 1;
                roundText.setLocalScale(.04f);
                roundText.setLocalTranslation(-2.7f, 2, 0);
                rootNode.attachChild(roundText);
            } else if (name.equals("Score Board") && !game) {
                goTo(leaderNode);
            } else if (name.equals("Menu")) {
                //if (roundText.getText().contains("G")||roundText.getText().contains("R")))
                endGame();
                goTo(menuNode);
            } else if (name.equals("Mute") && keyPressed)
                muteSound();
            else if ((name.equals("Pause"))&&keyPressed)
                pause();

        }, "Shoot", "Start", "Score Board", "Menu", "Mute","Pause");

    }

    private boolean pause()
    {
        game = !game;
        muteSound();
        return  !game;
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
                    enemyNode.move(.01f*direction, -.3f, 0);
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
        cannonNode.move(.02f * direction, 0, 0);
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

    private void gameOver()
    {
        roundText.setText("Game Over");
        pause();
    }

    private void endGame() {
        roundText.setText("");
        for(Spatial s: rootNode.getChildren()) {
            if(s.equals(gameNode)||s.equals(leaderNode))
                s.setCullHint(Spatial.CullHint.Always);
            else
                s.setCullHint(Spatial.CullHint.Inherit);
        }
        if (gameScore.getScore() >= highScore) {
            H2Manager.INSTANCE.addScore(gameScore);
        }
        gameScore.addScore(-gameScore.getScore());
        gameNode.detachChild(lives);
        makeLives();
        gameNode.attachChild(lives);
        reset();
        music_sound.stop();
        ufo_sound.stop();

    }

    private void roundEnded()
    {
        if(enemyNode.getChildren().size()==0)
        {
            lives.attachChild(Life(lives.getChildren().size()));
            gameRound++;
            reset();
        }
    }

    private void reset() {
        if (pause())
            pause();
        rootNode.detachChild(roundText);
        if(game)
            roundText.setText("Round " + gameRound);
        rootNode.attachChild(roundText);
        gameNode.detachChild(enemyNode);
        enemyNode = invaderNode();
        enemyNode.setLocalTranslation(-6, -1, -9);
        gameNode.attachChild(enemyNode);
        bulletNode.detachAllChildren();
        gameNode.detachChild(cannonNode);
        cannonNode = makeCannon();
        cannonNode.setLocalTranslation(0, -5.9f, -9);
        gameNode.attachChild(cannonNode);
        music_sound.stop();
        gameNode.detachChild(lives);
        gameNode.attachChild(lives);
        gameNode.detachChild(ufoNode);
        if(ufoExists) {
            ufoNode.detachChildAt(1);
            ufo_sound.stop();
        }
        gameNode.attachChild(ufoNode);
        resetBools();
        resetValues();
    }

    private void resetValues() {
        iter = 0;
        enemySpeed = .05f + .025f*gameRound;
    }

    private void resetBools() {
        ufoExists = false;
        shot = false;
        enemyShot = false;
        flicker = false;
        muted = false;
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
                        //gameOver();
                        //endGame();
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
            Spatial bullet = Bullet(2, "Invader");
            bullet.setLocalTranslation(cannonNode.getWorldTranslation().add((float)1.350 + cannonNode.getWorldTranslation().getX() * .03f, 1, -1.5f));
            bulletNode.attachChild(bullet);
            shoot_sound.play();
            shot = true;
        }
    }

    private void enemyShoot() {
        int nodes = enemyNode.getChildren().size();
        int en1 = (int)(Math.random()*nodes-1);
        Spatial enemy1 = enemyNode.getChildren().get(en1);
        if(!enemyShot) {
            Spatial bullet = Bullet(-1, "Player");
            bullet.setLocalTranslation(enemy1.getWorldTranslation().add((float) 1.35 + enemy1.getWorldTranslation().getX() * .03f,  -1f, -.5f));
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

    public void muteSound(){
        if(!muted) {
            bounce_sound.setVolume(0);
            shoot_sound.setVolume(0);
            ufo_sound.setVolume(0);
            music_sound.setVolume(0);
            muted = true;
            guiNode.attachChild(muteImage);
            rootNode.attachChild(muteImage);
        }
        else{
            music_sound.setVolume(3);
            bounce_sound.setVolume(5);
            shoot_sound.setVolume(2);
            ufo_sound.setVolume(.5f);
            muted = false;
            guiNode.detachChild(muteImage);
            rootNode.detachChild(muteImage);
        }
    }
}
