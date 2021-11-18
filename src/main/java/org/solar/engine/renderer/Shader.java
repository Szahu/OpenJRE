package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.solar.engine.Utils;

public class Shader {

    private final int m_programId; //openGL id of the shader program
    private int vertexShaderId; //openGl id of the vertex shader program
    private int fragmentShaderId; //openGl id of the fragment shader program
    private final Map<String, Integer> m_uniforms; //map of uniform names and locations
    private FloatBuffer m_floatBuffer16; //FloatBuffer for loading matrices

    public static final String uniformProjectionMatrixToken = "u_projectionMatrix"; 
    public static final String uniformViewMatrixToken = "u_viewMatrix"; 
    public static final String uniformTransformMatrixToken = "u_worldMatrix"; 

    public Shader() {

        m_uniforms = new HashMap<>();

        m_programId = glCreateProgram();
        
        if (m_programId == 0) {
            Utils.LOG_ERROR("System could not create the shader program");
        }
    }

    /**
     * Creates a shader program from a file which stores two shader, each one starting with a proper token (#vertexShader, #fragmentShader).
     * @param bothShadersFileName Name of the file (including suffix) in which both of our shaders are stored
     */
    public Shader(String bothShadersFileName) {
        m_uniforms = new HashMap<>();

        m_programId = glCreateProgram();
        
        if (m_programId == 0) {
            Utils.LOG_ERROR("System could not create the shader program");
        }

        load(bothShadersFileName);
    }

    /**
     * Creates a shader program from two files containing vertex and fragment shaders.
     * @param vertexShaderName Name of the file (including suffix) containing the vertex shader (no token).
     * @param fragmentShaderName Name of the file (including suffix) containing the fragment shader (no token).
     */
    public Shader(String vertexShaderName, String fragmentShaderName) {
        m_uniforms = new HashMap<>();

        m_programId = glCreateProgram();
        
        if (m_programId == 0) {
            Utils.LOG_ERROR("System could not create the shader program");
        }

        load(vertexShaderName, fragmentShaderName);
    }

    /**
     * Sets a value of a uniform in a shader.
     * @param uniformName Name of the uniform to be set.
     * @param value Data to be sent to the gpu.
     */
    public void setUniform(String uniformName, Matrix4f value) {
        
        // Dump the matrix into a float buffer
        if(m_uniforms.containsKey(uniformName)) {
            if (m_floatBuffer16==null)
                m_floatBuffer16 = BufferUtils.createFloatBuffer(16);
            m_floatBuffer16.clear();
            value.get(m_floatBuffer16);
            glUniformMatrix4fv(m_uniforms.get(uniformName), false, m_floatBuffer16);
            m_floatBuffer16.flip();
        } else {
            Utils.LOG_ERROR("Trying to set value of the uniform that does not exist: " + uniformName);
        }

    }

     /**
     * Sets a value of a uniform in a shader.
     * @param uniformName Name of the uniform to be set.
     * @param value Data to be sent to the gpu.
     */
    public void setUniform(String uniformName, int value) {
        if(m_uniforms.containsKey(uniformName)) {
            glUniform1i(m_uniforms.get(uniformName), value);
        } else {
            Utils.LOG_ERROR("Trying to set value of the uniform that does not exist: " + uniformName);
        }
    }

    private void generateUniforms(String shaderCode) throws Exception {
        
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

    /**
     * Loads a shader program from a single file. Vertex shader must beging with #vertexShader 
     * and fragment shader needs to begin with #fragmentShader
     * @param bothShadersFileName Name of the file containing shader code. 
     */
    private void load(String bothShadersFileName) {
        
        try {
            String[] shadersContent = multipleShadersFromFile(bothShadersFileName);
            createVertexShader(shadersContent[0]);
            createFragmentShader(shadersContent[1]);
            link();
            generateUniforms(shadersContent[0]);
            generateUniforms(shadersContent[1]);
        } catch (Exception e) {
            Utils.LOG_ERROR("Error while loading shaders from path: " + bothShadersFileName + " , " + e.toString());
        }
    }
    //Load and create shaders from two separate files
    public void load(String vertexShaderName, String fragmentShaderName) {

        try {
            String shaderCode = Utils.getFileAsString(vertexShaderName);
            createVertexShader(shaderCode);
            generateUniforms(shaderCode);
        } catch (Exception e) {

            Utils.LOG_ERROR("Error while loading shaders from path: " + vertexShaderName + " , " + e.toString());
        }

        try {

            String shaderCode = Utils.getFileAsString(fragmentShaderName);
            createFragmentShader(shaderCode);
        } catch (Exception e) {
            Utils.LOG_ERROR("Error while loading shaders from path: " + fragmentShaderName + " , " + e.toString());
        }

        try {
            link();
        } catch (Exception e) {
            Utils.LOG_ERROR("Error while linking " + vertexShaderName + " and " + fragmentShaderName + " , " + e.toString());
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
            Utils.LOG_WARNING("Warning validating Shader code: " + glGetProgramInfoLog(m_programId, 1024));
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
    
    public final static String SHADERS_FOLDER_PATH = "src/main/resources/shaders/";
    public final static int VERTEX_SHADER_IDX = 0;
    public final static int FRAGMENT_SHADER_IDX = 1;
    private final static String VERTEX_SHADER_TOKEN = "#vertexShader";
    private final static String FRAGMENT_SHADER_TOKEN = "#fragmentShader";

    //This function takes a text file and splits it into two after each token
    private static String[] multipleShadersFromFile(String shaderName) throws IOException{
        String vertexShaderContent = "";
        String fragmentShaderContent = "";
        String path = SHADERS_FOLDER_PATH + shaderName;
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);

        boolean foundVertexShader = false;
        boolean foundFragmentShader = false;
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            //Checking if the line is our token
            if (line.contains(VERTEX_SHADER_TOKEN)) {
                foundVertexShader = true;
                foundFragmentShader = false;
                continue;
            }
            //Checking if the line is our token
            else if (line.contains(FRAGMENT_SHADER_TOKEN)) {
                foundVertexShader = false;
                foundFragmentShader = true;
                continue;
            }
            if (foundVertexShader) {vertexShaderContent += (lines.get(i) + "\n") ;}
            else if (foundFragmentShader) {fragmentShaderContent += (lines.get(i) + "\n");}
        }

        String[] result = new String[2];
        result[VERTEX_SHADER_IDX] = vertexShaderContent;
        result[FRAGMENT_SHADER_IDX] = fragmentShaderContent;
        return result;
    }
}