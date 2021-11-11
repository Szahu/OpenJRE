package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;

import org.solar.engine.Utils;

public class Shader {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    public Shader() {
        programId = glCreateProgram();
        if (programId == 0) {
            System.out.println("System could not cratte the shader program");
        }
    }

    //Load and crate shaders from a file containing two shaders
    public void load(String bothShadersFileName) {
        String[] shadersContent = Utils.multipleShadersFromFile(bothShadersFileName);
        try {
            createVertexShader(shadersContent[0]);
            createFragmentShader(shadersContent[1]);
            link();
        } catch (Exception e) {
            System.out.print("Error while loading shaders from path: " + bothShadersFileName + " , " + e.toString());
        }

    }


    //Load and create shaders from two separate files 
    public void load(String vertexShaderName, String fragmentShaderName) {
        try {
            String shaderCode = Utils.singleShaderFromFile(vertexShaderName);
            createVertexShader(shaderCode);
        } catch (Exception e) {
            System.out.print("Error while loading shaders from path: " + vertexShaderName + " , " + e.toString());
        }

        try {
            String shaderCode = Utils.singleShaderFromFile(fragmentShaderName);
            createFragmentShader(shaderCode);
        } catch (Exception e) {
            System.out.print("Error while loading shaders from path: " + fragmentShaderName + " , " + e.toString());
        }

        try {
            link();
        } catch (Exception e) {
            System.out.println("Erro while linking " + vertexShaderName + " and " + fragmentShaderName + " , " + e.toString());
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}