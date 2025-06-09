package physics;

import object.Entity;
import org.joml.Vector2f;

public class Collision {

    private static float[] project(Vector2f[] verts, Vector2f axis) {
        float min = verts[0].dot(axis), max = min;
        for (int i = 1; i < verts.length; i++) {
            float p = verts[i].dot(axis);
            if (p < min) min = p;
            if (p > max) max = p;
        }
        return new float[]{min, max};
    }

    private static boolean overlaps(float minA, float maxA, float minB, float maxB) {
        return maxA >= minB && maxB >= minA;
    }

    private static float getOverlap(float minA, float maxA, float minB, float maxB) {
        return Math.min(maxA, maxB) - Math.max(minA, minB);
    }

    private static Vector2f[] getNormals(Vector2f[] verts) {
        Vector2f[] normals = new Vector2f[verts.length];
        for (int i = 0; i < verts.length; i++) {
            Vector2f edge = new Vector2f(verts[(i + 1) % verts.length]).sub(verts[i]);
            normals[i] = new Vector2f(edge.y, -edge.x).normalize();
        }
        return normals;
    }

    // Result for collision test
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

    // SAT collision test between two Entities
    public static SATResult test(Entity a, Entity b) {
        Vector2f[] vA = a.obb.getVertices();
        Vector2f[] vB = b.obb.getVertices();

        Vector2f[] axes = new Vector2f[vA.length + vB.length];
        System.arraycopy(getNormals(vA), 0, axes, 0, vA.length);
        System.arraycopy(getNormals(vB), 0, axes, vA.length, vB.length);

        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = null;

        for (Vector2f axis : axes) {
            float[] pA = project(vA, axis);
            float[] pB = project(vB, axis);

            if (!overlaps(pA[0], pA[1], pB[0], pB[1])) {
                return new SATResult(false, null, 0);
            }

            float o = getOverlap(pA[0], pA[1], pB[0], pB[1]);
            if (o < minOverlap) {
                minOverlap = o;
                smallestAxis = new Vector2f(axis);
            }
        }

        Vector2f dir = new Vector2f(b.rigidBody.getPosition()).sub(a.rigidBody.getPosition());
        if (dir.dot(smallestAxis) < 0) smallestAxis.negate();

        return new SATResult(true, smallestAxis.normalize(), minOverlap);
    }
}