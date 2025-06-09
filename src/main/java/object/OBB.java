package object;
import org.joml.Vector2f;

public class OBB {
    private final Vector2f halfSize;
    private Vector2f[] vertices = new Vector2f[4];

    public OBB(Vector2f halfSize) {
        this.halfSize = new Vector2f(halfSize);
        vertices = new Vector2f[4];
        for (int i = 0; i < 4; i++) {
            vertices[i] = new Vector2f();
        }
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public Vector2f[] getVertices() {
        return vertices;
    }

    public void update(Vector2f center, float rads) {
        float cos = (float) Math.cos(rads);
        float sin = (float) Math.sin(rads);

        Vector2f[] localVertices = {
                new Vector2f(-halfSize.x, -halfSize.y),
                new Vector2f(halfSize.x, -halfSize.y),
                new Vector2f(halfSize.x, halfSize.y),
                new Vector2f(-halfSize.x, halfSize.y),
        };

        for (int i = 0; i < 4; i++) {
            float x = localVertices[i].x * cos - localVertices[i].y * sin;
            float y = localVertices[i].x * sin + localVertices[i].y * cos;
            vertices[i].set(center.x + x, center.y + y);
        }
    }
}