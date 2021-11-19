package org.solar.engine;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.VertexData;

/**
 * To load models from .obj files.
 * Important! Tick Triangulate Faces when exporting
 */
public class ModelLoader {

    public static VertexArray loadModel(String objFileName) {;
        String objFile = objFileName;
		String inputObjContent = Utils.getFileAsString(objFileName);
		float[] vertices = getVertices( inputObjContent ); // non-indexed!
		int[] verticesIndices = getIndices ( inputObjContent, VERTICES_IDX);
		float[] texels = getTexels( inputObjContent ); // non-indexed!
		int[] texelsIndices	= getIndices( inputObjContent, TEXELS_IDX );
        float[] normals = getNormals( inputObjContent );
        int[] normalsIndices = getIndices( inputObjContent, NORMALS_IDX);

		int[] plainIndices = new int[verticesIndices.length];
		for(int i = 0; i < verticesIndices.length; i++) plainIndices[i] = i;
		VertexArray result = new VertexArray(plainIndices, new VertexData(new FloatArray(3, vertices, verticesIndices),  new FloatArray(2, texels, texelsIndices), new FloatArray(3, normals, normalsIndices)));
        return result;
    }

    private final static String VERTICES_PATTERN = "(v (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)\n)+";
    private final static String INDICES_PATTERN = "(f [0-9]+/([0-9]+)/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+ ([0-9]+)/[0-9]+/[0-9]+\n)+";
    private final static String TEXELS_PATTERN = "(vt (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)\n)+";
    private final static String NORMALS_PATTERN = "(vn (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)\n)+";
    public final static char VERTICES_IDX = 0;
    public final static char TEXELS_IDX = 1;
    public final static char NORMALS_IDX = 2;

    private static float[] getVertices(String fileContent){
        Vector<Float> vertices = new Vector<>();
        Pattern pattern = Pattern.compile(VERTICES_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( verticeAsString ->
                Arrays.stream(verticeAsString.replaceAll("^v ", "").split(" ")).forEach(verticePart ->
                    vertices.add(Float.parseFloat(verticePart))));
            int i = 0;
            float[] verticesArray = new float[vertices.size()];
            for(Float verticePart: vertices) verticesArray[i++] = ( verticePart != null? verticePart: Float.NaN);
            return verticesArray;
        }
        throw new RuntimeException("No vertices found at input file!");
    }

    private static float[] getNormals(String fileContent){
        Vector<Float> normals = new Vector<>();
        Pattern pattern = Pattern.compile(NORMALS_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( verticeAsString ->
                Arrays.stream(verticeAsString.replaceAll("^vn ", "").split(" ")).forEach(verticePart ->
                    normals.add(Float.parseFloat(verticePart))));
            int i = 0;
            float[] normalsArray = new float[normals.size()];
            for(Float normal: normals) normalsArray[i++] = ( normal != null? normal: Float.NaN);
            return normalsArray;
        }
        throw new RuntimeException("No normals found at input file!");
    }

    private static float[] getTexels(String fileContent ){
        Vector<Float> texels = new Vector<>();
        Pattern pattern = Pattern.compile(TEXELS_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( texelsAsString ->
                    Arrays.stream( texelsAsString.replaceAll("^vt ", "").split(" ")).forEach(texelPart ->
                            texels.add(Float.parseFloat( texelPart ))
                    ));
            int i = 0;
            float[] texelArray = new float[texels.size()];
            for(Float texel: texels) texelArray[i++] = ( texel != null? texel: Float.NaN);
            return texelArray;
        }
        throw new RuntimeException("No texels found at input file!");
    }

    private static int[] getIndices(String fileContent, int selectorIdx ){
        Vector<Integer> indices = new Vector<>();
        Pattern pattern = Pattern.compile(INDICES_PATTERN);
        Matcher matcher = pattern.matcher(fileContent);
        if(matcher.find()){
            Arrays.stream(matcher.group().split("\n")).forEach( indiceAsString ->
                Arrays.stream( indiceAsString.replaceAll("^f ", "").split(" ")).forEach(indicePart ->
                     indices.add(Integer.parseInt( indicePart.split("/")[selectorIdx] ))
            ));
            int i = 0;
            int[] indicesArray = new int[indices.size()];
            for(Integer index: indices) indicesArray[i++] = ( index != null? index - 1 : 0);
            return indicesArray;
        }
        throw new RuntimeException("No indices found at input file!");
    }


}
