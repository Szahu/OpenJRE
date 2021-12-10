package org.solar.appllication.terrain;

import java.util.List;

import org.joml.Vector3f;

public class Triangle {
    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3) {
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
    }
    public Vector3f[] points = new Vector3f[]{new Vector3f(), new Vector3f(),new Vector3f()};
    public void copyToRawList(List<Float> list) {
        list.add(points[0].x);
        list.add(points[0].y);
        list.add(points[0].z);
        list.add(points[1].x);
        list.add(points[1].y);
        list.add(points[1].z);
        list.add(points[2].x);
        list.add(points[2].y);
        list.add(points[2].z);
    }
}
