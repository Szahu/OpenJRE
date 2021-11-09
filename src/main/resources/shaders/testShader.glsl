#vertexShader 

#version 330 

layout (location=0) in vec3 position;

void main()
{
    gl_Position = vec4(position, 1.0);
}

#fragmentShader

#version 330

out vec4 fragColor;

void main()
{
    fragColor = vec4(0.0, 0.5, 0.5, 1.0);
}