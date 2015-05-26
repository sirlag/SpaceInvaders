/*
    Hello Monkey was the tutorial class created for the JMonkeyEngine, however, I am currently using it to prototype
    features, and functions, that are to be used in the real, SpaceInvaders, class.
 */


package HelloMonkey;

import SpaceInvaders.Database.H2Manager;
import SpaceInvaders.Util.Score;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;

import java.util.Optional;

public class HelloMonkey extends SimpleApplication{

    public static void main(String[] args){
        HelloMonkey app = new HelloMonkey();
        app.start();
    }

    @Override
    public void simpleInitApp(){

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);

        H2Manager.INSTANCE.addScore(new Score(400_000_000, "NED"));

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        nifty.addScreen("Hello_World", new ScreenBuilder("Hello Nifty Screen"){{
            controller(new DefaultScreenController());

            layer(new LayerBuilder("Layer_ID") {{
                childLayoutVertical();

                panel(new PanelBuilder("Panel_ID") {{
                    childLayoutCenter();
                    control(new ButtonBuilder("Button_ID", "Start"){{
                    alignCenter();
                    valignCenter();
                    height("5%");
                    width("15%");
                    }});
                }});
                panel(new PanelBuilder("Panel_ID_2"){{
                    childLayoutAbsolute();
                    text(new TextBuilder(){{
                        font("Interface/Fonts/Default.fnt");
                        text("High Score!");
                        height("80%");
                        width("100%");

                    }});
                    text(new TextBuilder() {{
                        Optional<Score> score = H2Manager.INSTANCE.getHighScore();
                        assert score != null;
                        if (score.isPresent())
                            text(score.get().toString());
                        else
                            text("There are no High Scores! Play to get one!");
                        font("Interface/Fonts/Default.fnt");
                        height("100%");
                        width("100%");
                    }});
                }});
            }});
        }}.build(nifty));



        nifty.gotoScreen("Hello_World");



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

        Spatial invader = assetManager.loadModel("assets/Models/Invader/Invader.j3o");
        invader.setLocalScale(.1f);
        invader.setMaterial(mat3);

        Spatial invaderBlue = assetManager.loadModel("assets/Models/Invader/Invader.j3o");
        invaderBlue.setLocalScale(.1f);
        invaderBlue.setLocalTranslation(-1, 0 , 0);
        invaderBlue.setMaterial(mat1);


        //Pivot node
        Node pivot = new Node("Pivot");
        Node node = new Node("Enemy");

        rootNode.attachChild(pivot);
        rootNode.attachChild(node);


        //Add the boxes
        pivot.attachChild(blue);
        pivot.attachChild(red);
        pivot.attachChild(green);
        node.attachChild(invader);
        node.attachChild(invaderBlue);

        //rotate this stuff
        pivot.rotate(.4f,.4f,0f);

        node.setLocalTranslation(12, 0 ,0);
    }

}
