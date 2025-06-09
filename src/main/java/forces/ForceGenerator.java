package forces;

import object.RigidBody;

public interface ForceGenerator {
    void updateForce(RigidBody body, float dt);
}
