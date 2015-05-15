package SpaceInvaders.Controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class BulletController extends AbstractControl {
    private int screenWidth, screenHeight;
    private final float speed = 1100f;
    public Vector3f direction;

    public BulletController(int screenWidth, int screenHeight, Vector3f direction) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.direction = direction;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.move(direction.mult(speed*tpf));
//        check boundaries
        Vector3f loc = spatial.getLocalTranslation();
        if (loc.x > screenWidth ||
                loc.y > screenHeight ||
                loc.x < 0 ||
                loc.y < 0) {
            spatial.removeFromParent();
        }
    }
}
