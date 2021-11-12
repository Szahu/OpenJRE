package org.solar.engine.renderer;
import static org.lwjgl.opengl.GL20.*;
import static org.solar.engine.Utils.*;
import static java.lang.System.*;
import org.solar.engine.Utils;

import java.io.IOException;

public class Shader {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    public Shader() {
        programId = glCreateProgram();
        if (programId == 0) {
            out.println("System could not create the shader program");
        }
    }
    //Load and crate shaders from a file containing two shaders
    @SuppressWarnings("unused")
    public void load(String bothShadersFileName) throws IOException {
            String[] shadersContent = Utils.getTwoShaderStringsFromFile( ABS_PROJECT_PATH + bothShadersFileName);
            createVertexShader(shadersContent[VERTEX_SHADER_IDX]);
            createFragmentShader(shadersContent[FRAGMENT_SHADER_IDX]);
            link();
    }
    //Load and create shaders from two separate files
    public void load(String vertexShaderName, String fragmentShaderName) throws RuntimeException, IOException{
            String shaderCode = Utils.getShaderStringFromFile( ABS_PROJECT_PATH + vertexShaderName );
            out.println("vertex shader:\n" + shaderCode);
            createVertexShader(shaderCode);

            shaderCode = Utils.getShaderStringFromFile( ABS_PROJECT_PATH + fragmentShaderName);
            out.println("fragment shader:\n" + shaderCode);
            createFragmentShader(shaderCode);

            link();
    }
    public void createVertexShader(String shaderCode) throws RuntimeException{
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }
    public void createFragmentShader(String shaderCode) throws RuntimeException {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }
    protected int createShader(String shaderCode, int shaderType){
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        glAttachShader(programId, shaderId);
        return shaderId;
    }
    public void link() throws RuntimeException {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
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