package physics;

import object.Entity;
import org.joml.Vector2f;

public class Collision {

    private static float[] project(Vector2f[] vertices, Vector2f axis) {
        float min = vertices[0].dot(axis), max = min;
        for (int i = 1; i < vertices.length; i++) {
            float p = vertices[i].dot(axis);
            min = Math.min(min, p);
            max = Math.max(max, p);
        }
        return new float[] {min, max};
    }

    private static boolean overlaps(float minA, float maxA, float minB, float maxB) {
        return maxA >= minB && maxB >= minA;
    }

    private static float getOverlap(float minA, float maxA, float minB, float maxB) {
        return Math.min(maxA, maxB) - Math.max(minA, minB);
    }

    private static Vector2f[] getNormals(Vector2f[] vertices) {
        Vector2f[] normals = new Vector2f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            Vector2f edge = new Vector2f(vertices[(i + 1) % vertices.length]).sub(vertices[i]);
            //noinspection SuspiciousNameCombination
            normals[i] = new Vector2f(edge.y, -edge.x).normalize();
        }
        return normals;
    }

    public static class SATResult {
        public final boolean collision;
        public final Vector2f axis;
        public final float overlap;

        public SATResult(boolean collision, Vector2f axis, float overlap) {
            this.collision = collision;
            this.axis = axis;
            this.overlap = overlap;
        }
    }

    public static SATResult test(Entity a, Entity b) {
        Vector2f[] verticesA = a.obb.getVertices();
        Vector2f[] verticesB = b.obb.getVertices();

        Vector2f[] axes = new Vector2f[verticesA.length + verticesB.length];
        System.arraycopy(getNormals(verticesA), 0, axes, 0, verticesA.length);
        System.arraycopy(getNormals(verticesB), 0, axes, verticesA.length, verticesB.length);

        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = null;

        for (Vector2f axis : axes) {
            float[] projA = project(verticesA, axis);
            float[] projB = project(verticesB, axis);

            if (!overlaps(projA[0], projA[1], projB[0], projB[1])) {
                return new SATResult(false, null, 0);
            }

            float overlap = getOverlap(projA[0], projA[1], projB[0], projB[1]);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallestAxis = new Vector2f(axis);
            }
        }

        Vector2f direction = new Vector2f(b.rigidBody.getPosition()).sub(a.rigidBody.getPosition());
        if (direction.dot(smallestAxis) < 0) {
            smallestAxis.negate();
        }

        return new SATResult(true, smallestAxis.normalize(), minOverlap);
    }
}