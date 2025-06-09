package object;

import org.joml.Vector2f;

public class Entity {
    public RigidBody rigidBody;
    public OBB obb;

    public Entity(Vector2f position, float rotation, Vector2f size, boolean isStatic) {
        rigidBody = new RigidBody(position, isStatic);
        rigidBody.setRotation(rotation);
        if (!isStatic) {
            rigidBody.setMass(1f);
            float w = size.x;
            float h = size.y;
            rigidBody.setMoi((1.0f / 12.0f) * rigidBody.getMass() * ((w * w) + (h * h)));
        }
        obb = new OBB(new Vector2f(size).mul(0.5f));
        updateOBB();
    }

    public void updateOBB() {
        obb.update(rigidBody.getPosition(), rigidBody.getRotation());
    }
}