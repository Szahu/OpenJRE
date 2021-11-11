package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.solar.engine.Utils;

public class Shader {

    private final int m_programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> m_uniforms;

    public Shader() {

        m_uniforms = new HashMap<>();

        m_programId = glCreateProgram();
        
        if (m_programId == 0) {
            System.out.println("System could not create the shader program");
        }
    }

    public Shader(String bothShadersFileName) {
        m_uniforms = new HashMap<>();

        m_programId = glCreateProgram();
        
        if (m_programId == 0) {
            System.out.println("System could not create the shader program");
        }

        load(bothShadersFileName);
    }

    public Shader(String vertexShaderName, String fragmentShaderName) {
        m_uniforms = new HashMap<>();

        m_programId = glCreateProgram();
        
        if (m_programId == 0) {
            System.out.println("System could not create the shader program");
        }

        load(vertexShaderName, fragmentShaderName);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        
        //TODO move it to the render function
        bind();
        
        // Dump the matrix into a float buffer
        if(m_uniforms.containsKey(uniformName)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(16);
                value.get(fb);
                glUniformMatrix4fv(m_uniforms.get(uniformName), false, fb);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else {
            System.out.println("Trying to set value of the uniform that does not exist");
        }
        
        
    }

    public void generateUniforms(String shaderCode) throws Exception {
        
        for(int index = shaderCode.indexOf("uniform");index >= 0; index = shaderCode.indexOf("uniform", index + 1)) {

            int nameBeginIndex = index;
            int numOfSpaces = 0;
            while (numOfSpaces != 2) {
                if(shaderCode.charAt(nameBeginIndex) == ' ') {
                    numOfSpaces++;
                }
                nameBeginIndex++;
            }
            int nameEndIndex = nameBeginIndex;
            while(shaderCode.charAt(nameEndIndex) != ';') {
                nameEndIndex++;
            }

            String uniformName = shaderCode.substring(nameBeginIndex, nameEndIndex);

            int uniformLocation = glGetUniformLocation(m_programId, uniformName);

            if (uniformLocation < 0) {
                throw new Exception("Could not find uniform (or it is not used): " + uniformName);
            }

            m_uniforms.put(uniformName, uniformLocation); 
        }

    }

    //Load and crate shaders from a file containing two shaders
    public void load(String bothShadersFileName) {
        
        try {

            String[] shadersContent = Utils.multipleShadersFromFile(bothShadersFileName);
            createVertexShader(shadersContent[0]);
            createFragmentShader(shadersContent[1]);
            link();
            generateUniforms(shadersContent[0]);
        } catch (Exception e) {
            System.out.println("Error while loading shaders from path: " + bothShadersFileName + " , " + e.toString());
        }
    }
    //Load and create shaders from two separate files
    public void load(String vertexShaderName, String fragmentShaderName) {

        try {
            String shaderCode = Utils.FileToString(vertexShaderName);
            System.out.println("vertex shader:\n" + shaderCode);
            createVertexShader(shaderCode);
            generateUniforms(shaderCode);
        } catch (Exception e) {

            System.out.println("Error while loading shaders from path: " + vertexShaderName + " , " + e.toString());
        }

        try {

            String shaderCode = Utils.FileToString(fragmentShaderName);
            System.out.println("fragment shader:\n" + shaderCode);
            createFragmentShader(shaderCode);
        } catch (Exception e) {
            System.out.println("Error while loading shaders from path: " + fragmentShaderName + " , " + e.toString());
        }

        try {
            link();
        } catch (Exception e) {
            System.out.println("Error while linking " + vertexShaderName + " and " + fragmentShaderName + " , " + e.toString());
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
        glAttachShader(m_programId, shaderId);
        return shaderId;
    }

    public void link() throws Exception {

        glLinkProgram(m_programId);
        if (glGetProgrami(m_programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(m_programId, 1024));
        }
        if (vertexShaderId != 0) {
            glDetachShader(m_programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(m_programId, fragmentShaderId);
        }
        glValidateProgram(m_programId);
        if (glGetProgrami(m_programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(m_programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(m_programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (m_programId != 0) {
            glDeleteProgram(m_programId);
        }
    }
}