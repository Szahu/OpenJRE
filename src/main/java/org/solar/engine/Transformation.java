package org.solar.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {
    private Matrix4f transformMatrix;
    private Matrix4f projectionMatrix;

    public Transformation() {
        transformMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float z_near, float z_far) {
        float aspectRatio = width / height;
        projectionMatrix.identity().perspective(fov, aspectRatio, z_near, z_far);
        return projectionMatrix;
    }

    public final Matrix4f getProjectionMatrix(float fov, float aspectRatio, float z_near, float z_far) {
        projectionMatrix.identity().perspective(fov, aspectRatio, z_near, z_far);
        return projectionMatrix;
    }

    public Matrix4f getTransformMatrix(Vector3f offset, Vector3f rotation, float scale) {
        transformMatrix.identity().translate(offset).
            rotateX((float)Math.toRadians(rotation.x)).
            rotateY((float)Math.toRadians(rotation.y)).
            rotateZ((float)Math.toRadians(rotation.z)).
            scale(scale);
        return transformMatrix;
    }

}
