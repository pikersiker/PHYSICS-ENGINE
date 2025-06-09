package forces;

import object.RigidBody;
import java.util.List;
import java.util.ArrayList;

public class ForceRegistry {

    private static class ForceRegistration {
        RigidBody body;
        ForceGenerator fg;

        ForceRegistration(RigidBody body, ForceGenerator fg) {
            this.body = body;
            this.fg = fg;
        }
    }

    private final List<ForceRegistration> registrations = new ArrayList<>();

    public void add(RigidBody body, ForceGenerator fg) {
        registrations.add(new ForceRegistration(body, fg));
    }

    public void updateForce(float dt) {
        for (ForceRegistration reg : registrations) {
            reg.fg.updateForce(reg.body, dt);
        }
    }
}