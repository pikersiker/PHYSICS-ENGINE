package forces;

import org.joml.Vector2f;
import object.RigidBody;

public class Gravity implements ForceGenerator {
    private final Vector2f gravity;

    public Gravity(Vector2f gravity) {
        this.gravity = new Vector2f(gravity);
    }

    @Override
    public void updateForce(RigidBody body, float dt) {
        if (!body.isStatic()) {
            Vector2f force = new Vector2f(gravity).mul(body.getMass());
            body.addForce(force);
        }
    }
}