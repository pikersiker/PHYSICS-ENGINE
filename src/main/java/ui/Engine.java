package ui;

import java.util.ArrayList;
import java.util.List;

import forces.ForceRegistry;
import forces.Gravity;
import object.Entity;
import org.joml.Vector2f;
import physics.Collision;
import physics.Resolver;

public class Engine {
    private final List<Entity> entities = new ArrayList<>();
    private final ForceRegistry forceRegistry = new ForceRegistry();
    private final Gravity gravity = new Gravity(new Vector2f(0, 500f));
    private static final float timeStep = 1.0f / 60.0f;

    public Engine() {
        entities.add(new Entity(new Vector2f(400, 550), 0f, new Vector2f(600, 25), true));
        entities.add(new Entity(new Vector2f(200, 350), 3.14f / 12f , new Vector2f(200, 25), true));
        entities.add(new Entity(new Vector2f(600, 350), 3.14f - (3.14f / 12f) , new Vector2f(200, 25), true));
    }

    public void addEntity(Entity e) {
        entities.add(e);
        if (!e.rigidBody.isStatic()) {
            forceRegistry.add(e.rigidBody, gravity);
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void clearDynamicEntities() {
        entities.removeIf(e -> !e.rigidBody.isStatic());
    }

    public void update() {
        final int SUBSTEPS = 8;
        final float subDelta = timeStep / SUBSTEPS;

        for (int step = 0; step < SUBSTEPS; step++) {
            forceRegistry.updateForce(subDelta);

            for (Entity e : entities) {
                if (!e.rigidBody.isStatic()) {
                    e.rigidBody.integrate(subDelta);
                    e.updateOBB();
                }
            }

            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {
                    Entity a = entities.get(i), b = entities.get(j);
                    Collision.SATResult result = Collision.test(a, b);
                    if (result.collision) Resolver.resolve(a, b, result);
                }
            }
        }
    }
}