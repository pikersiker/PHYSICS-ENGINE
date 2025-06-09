package physics;

import object.Entity;
import org.joml.Vector2f;

public class Collision {

    private static float[] project(Vector2f[] verts, Vector2f axis) {
        float min = verts[0].dot(axis), max = min;
        for (int i = 1; i < verts.length; i++) {
            float p = verts[i].dot(axis);
            min = Math.min(min, p);
            max = Math.max(max, p);
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
            //noinspection SuspiciousNameCombination
            normals[i] = new Vector2f(edge.y, -edge.x).normalize();  // perpendicular vector
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
        Vector2f[] vertsA = a.obb.getVertices();
        Vector2f[] vertsB = b.obb.getVertices();

        Vector2f[] axes = new Vector2f[vertsA.length + vertsB.length];
        System.arraycopy(getNormals(vertsA), 0, axes, 0, vertsA.length);
        System.arraycopy(getNormals(vertsB), 0, axes, vertsA.length, vertsB.length);

        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = null;

        for (Vector2f axis : axes) {
            float[] projA = project(vertsA, axis);
            float[] projB = project(vertsB, axis);

            if (!overlaps(projA[0], projA[1], projB[0], projB[1])) {
                return new SATResult(false, null, 0);
            }

            float overlap = getOverlap(projA[0], projA[1], projB[0], projB[1]);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallestAxis = new Vector2f(axis);
            }
        }

        if (smallestAxis == null) {
            return new SATResult(false, null, 0);
        }

        Vector2f direction = new Vector2f(b.rigidBody.getPosition()).sub(a.rigidBody.getPosition());
        if (direction.dot(smallestAxis) < 0) {
            smallestAxis.negate();
        }

        return new SATResult(true, smallestAxis.normalize(), minOverlap);
    }
}