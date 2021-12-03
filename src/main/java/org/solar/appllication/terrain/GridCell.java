package org.solar.appllication.terrain;

import org.joml.Vector3f;
import org.solar.engine.Utils;

public class GridCell {
    public GridCell(double[] levels) {if(levels.length != 8) {Utils.LOG_ERROR("Wrong amount of levels");} this.levels = levels;}
    public Vector3f[] points;
    public double[] levels;
}
