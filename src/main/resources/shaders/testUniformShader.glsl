#vertexShader

#version 330

layout (location =0) in vec3 position;
layout (location =1) in vec3 inColour;

uniform mat4 projectionMatrix;
uniform mat4 transformMatrix;

out vec3 exColour;

void main()
{
    gl_Position = vec4(position, 1.0) * projectionMatrix * transformMatrix;
    exColour = inColour;
}

#fragmentShader

#version 330

in  vec3 exColour;
out vec4 fragColor;

void main()
{
    fragColor = vec4(exColour, 1.0);
}
