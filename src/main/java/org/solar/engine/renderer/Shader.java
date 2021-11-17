package org.solar.engine.renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.solar.engine.Utils.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.solar.engine.Utils;
import java.io.IOException;

public class Shader {

    private final int m_programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> m_uniforms;
    private FloatBuffer m_floatBuffer16;

    public Shader() {
        m_uniforms = new HashMap<>();
        m_programId = glCreateProgram();
        if (m_programId == 0) Utils.LOG_ERROR("System could not create the shader program");
    }

    /**
     * Creates a shader program from a file which stores two shader, each one starting with a proper token (#vertexShader, #fragmentShader).
     * @param bothShadersFileName Name of the file (including suffix) in which both of our shaders are stored
     */
    public Shader(String bothShadersFileName) throws IOException {

        m_uniforms = new HashMap<>();
        m_programId = glCreateProgram();
        if (m_programId == 0) Utils.LOG_ERROR("System could not create the shader program");
        load(bothShadersFileName);
    }

    /**
     * Creates a shader program from two files containing vertex and fragment shaders.
     * @param vertexShaderName Name of the file (including suffix) containing the vertex shader (no token).
     * @param fragmentShaderName Name of the file (including suffix) containing the fragment shader (no token).
     */
    public Shader(String vertexShaderName, String fragmentShaderName) throws IOException {

        m_uniforms = new HashMap<>();
        m_programId = glCreateProgram();
        if (m_programId == 0) Utils.LOG_ERROR("System could not create the shader program");
        load(vertexShaderName, fragmentShaderName);
    }

    public void setUniform(String uniformName, Matrix4f value)  throws RuntimeException{
        
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

    public void setUniform(String uniformName, int value) {
        if(m_uniforms.containsKey(uniformName)) {
            glUniform1i(m_uniforms.get(uniformName), value);
        } else {
            Utils.LOG_ERROR("Trying to set value of the uniform that does not exist: " + uniformName);
        }
    }

    public void generateUniforms(String shaderCode) throws RuntimeException {
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
                throw new RuntimeException("Could not find uniform (or it is not used): " + uniformName);
            }

            m_uniforms.put(uniformName, uniformLocation);
        }
    }

    @SuppressWarnings("unused")
    public void load(String bothShadersFileName) throws IOException {
        String[] shadersContent = Utils.getTwoShaderStringsFromFile( ABS_PROJECT_PATH + bothShadersFileName);
        createVertexShader(shadersContent[VERTEX_SHADER_IDX]);
        createFragmentShader(shadersContent[FRAGMENT_SHADER_IDX]);
        link();
        generateUniforms(shadersContent[VERTEX_SHADER_IDX]);
        generateUniforms(shadersContent[FRAGMENT_SHADER_IDX]);
    }

    //Load and create shaders from two separate files
    public void load(String vertexShaderName, String fragmentShaderName) throws RuntimeException, IOException{
        String shaderCode = Utils.getWholeFileAsString( vertexShaderName );
        Utils.LOG_INFO("vertex shader:\n" + shaderCode);
        createVertexShader(shaderCode);
        generateUniforms(shaderCode);

        shaderCode = Utils.getWholeFileAsString( fragmentShaderName);
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
        glAttachShader(m_programId, shaderId);
        return shaderId;
    }

    public void link() throws RuntimeException {
        glLinkProgram(m_programId);
        if (glGetProgrami(m_programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(m_programId, 1024));
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

    public void bind() { glUseProgram(m_programId); }
    public void unbind() { glUseProgram(0); }

    public void cleanup() {
        unbind();
        if (m_programId != 0) {
            glDeleteProgram(m_programId);
        }
    }
}