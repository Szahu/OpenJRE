#vertexShader

#version 330

layout (location =0) in vec3 position;
layout (location =1) in vec3 inColour;

uniform mat4 u_projectionMatrix;
uniform mat4 u_worldMatrix;

out vec3 exColour;

void main()
{
    gl_Position =  u_projectionMatrix * u_worldMatrix * vec4(position, 1.0);
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
