package object;

import org.joml.Vector2f;

public class RigidBody {
    private Vector2f position;
    private float rotation;

    private Vector2f velocity = new Vector2f();
    private float angularVelocity = 0.0f;

    private float mass = 0.0f;
    private float inverseMass = 0.0f;
    private Vector2f force = new Vector2f();
    private float torque = 0.0f;

    private float moi;
    private float imoi;

    private float restitution = 0.4f;
    private float friction = 0.075f;

    private final boolean isStatic;

    public RigidBody(Vector2f position, boolean isStatic) {
        this.position = new Vector2f(position);
        this.rotation = 0;
        this.isStatic = isStatic;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public float getMass() {
        return mass;
    }

    public float getImoi() {
        return imoi;
    }

    public float getRestitution() {
        return restitution;
    }

    public float getFriction() {
        return friction;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != 0.0f) {
            this.inverseMass = 1.0f / this.mass;
        }
    }

    public void addForce(Vector2f force) {
        if (!isStatic) {
            this.force.add(force);
        }
    }

    public void addTorque(float torque) {
        if (!isStatic) {
            this.torque += torque;
        }
    }

    public void setMoi(float moi) {
        this.moi = moi;
        if (moi != 0.0f) {
            this.imoi = 1f / moi;
        }
        else {
            this.imoi = 0;
        }
    }

    public void integrate(float dt) {
        if (isStatic) return;

        Vector2f acceleration = new Vector2f(force.div(mass));
        velocity.add(new Vector2f(acceleration).mul(dt));
        position.add(new Vector2f(velocity).mul(dt));

        float angularAcceleration = torque * imoi;
        angularVelocity += angularAcceleration * dt;
        rotation += angularVelocity * dt;

        force.zero();
        torque = 0.0f;
    }
}