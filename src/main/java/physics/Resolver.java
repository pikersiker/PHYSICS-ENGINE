package physics;

import object.Entity;
import object.RigidBody;
import org.joml.Vector2f;

public class Resolver {
    public static void resolve(Entity a, Entity b, Collision.SATResult sat) {
        if (!sat.collision) return;

        RigidBody A = a.rigidBody;
        RigidBody B = b.rigidBody;

        if (A.isStatic() && B.isStatic()) return;

        Vector2f rv = new Vector2f(B.getVelocity()).sub(A.getVelocity());

        float velAlongNormal = rv.dot(sat.axis);

        if (velAlongNormal > 0) return;

        float e = Math.min(A.getRestitution(), B.getRestitution());

        float invMassA = A.isStatic() ? 0f : 1f / A.getMass();
        float invMassB = B.isStatic() ? 0f : 1f / B.getMass();
        float j = -(1 + e) * velAlongNormal;
        float invMassSum = invMassA + invMassB;

        if (invMassSum == 0) return;

        j /= invMassSum;

        Vector2f impulse = new Vector2f(sat.axis).mul(j);
        if (!A.isStatic()) A.setVelocity(new Vector2f(A.getVelocity()).sub(new Vector2f(impulse).mul(invMassA)));
        if (!B.isStatic()) B.setVelocity(new Vector2f(B.getVelocity()).add(new Vector2f(impulse).mul(invMassB)));

        Vector2f tangent = new Vector2f(rv).sub(new Vector2f(sat.axis).mul(rv.dot(sat.axis)));
        if (tangent.lengthSquared() != 0) tangent.normalize();

        float jt = -rv.dot(tangent) / invMassSum;
        float mu = (float) Math.sqrt(A.getFriction() * B.getFriction());

        Vector2f frictionImpulse;
        if (Math.abs(jt) < j * mu) {
            frictionImpulse = new Vector2f(tangent).mul(jt);
        } else {
            frictionImpulse = new Vector2f(tangent).mul(-j * mu);
        }

        if (!A.isStatic()) A.setVelocity(new Vector2f(A.getVelocity()).sub(new Vector2f(frictionImpulse).mul(invMassA)));
        if (!B.isStatic()) B.setVelocity(new Vector2f(B.getVelocity()).add(new Vector2f(frictionImpulse).mul(invMassB)));

        final float percent = 0.6f;
        final float slop = 0.01f;
        float correctionMag = Math.max(sat.overlap - slop, 0.0f) / invMassSum;
        Vector2f correction = new Vector2f(sat.axis).mul(correctionMag * percent);

        if (!A.isStatic()) A.setPosition(new Vector2f(A.getPosition()).sub(new Vector2f(correction).mul(invMassA)));
        if (!B.isStatic()) B.setPosition(new Vector2f(B.getPosition()).add(new Vector2f(correction).mul(invMassB)));
    }
}