package org.solar.engine;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import static org.lwjgl.assimp.Assimp.*;
import org.solar.engine.renderer.FloatArray;
import org.solar.engine.renderer.RenderData;
import org.solar.engine.renderer.RenderableEntity;
import org.solar.engine.renderer.Texture;
import org.solar.engine.renderer.VertexArray;
import org.solar.engine.renderer.VertexData;
import org.solar.engine.renderer.Texture.TextureType;


public class ModelLoader {

   /*  //TODO add size normalization, add materials
    public static RenderableEntity loadModel(String path) throws Exception {
        AIScene aiScene = aiImportFile(path, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
        | aiProcess_FixInfacingNormals | aiProcess_PreTransformVertices  | aiProcess_CalcTangentSpace);
        if (aiScene == null) {
            throw new Exception("Error loading model");
        }

        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();
        RenderData[] meshes = new RenderData[numMeshes];
        for(int i =0;i < numMeshes;i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            VertexArray mesh = processMesh(aiMesh);

            Texture diffuseTex = new Texture(path.split("\\.")[0] + "_diffuse.png", TextureType.Diffuse);
            Texture normalTexture = new Texture(path.split("\\.")[0] + "_normal.png", TextureType.Normal);

            Material mat = new Material(diffuseTex, normalTexture);
            meshes[i] = new RenderData(mesh, mat);
        }
        
        return new RenderableEntity(meshes);
    } */

     //TODO add size normalization, add materials
     public static VertexData loadModel(String path) throws Exception {
        AIScene aiScene = aiImportFile(path, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
        | aiProcess_FixInfacingNormals | aiProcess_PreTransformVertices  | aiProcess_CalcTangentSpace);
        if (aiScene == null) {
            throw new Exception("Error loading model");
        }

        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();
        AIMesh aiMesh = AIMesh.create(aiMeshes.get(0));
        List<Integer> indices = new ArrayList<>();
        VertexData mesh = processMesh(aiMesh);
        processIndices(aiMesh, indices);

        return mesh;
    }


    private static VertexData processMesh(AIMesh aiMesh) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> tangents = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
    
        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processTangents(aiMesh, tangents);
        processIndices(aiMesh, indices);
    
        return new VertexData(
            Utils.intListToArray(indices), 
            new FloatArray(3, Utils.floatListToArray(vertices)), 
            new FloatArray(2, Utils.floatListToArray(textures)), 
            new FloatArray(3, Utils.floatListToArray(normals)),
            new FloatArray(3, Utils.floatListToArray(tangents)));
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    private static void processTangents(AIMesh aiMesh, List<Float> tangents) {
        AIVector3D.Buffer aiTangents = aiMesh.mTangents();
        while (aiTangents != null && aiTangents.remaining() > 0) {
            AIVector3D aiTangent = aiTangents.get();
            tangents.add(aiTangent.x());
            tangents.add(aiTangent.y());
            tangents.add(aiTangent.z());
        }
    }

    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

}