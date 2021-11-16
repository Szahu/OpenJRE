#vertexShader

#version 330

layout (location =0) in vec3 position;
layout (location =1) in vec3 inColour;
layout (location =2) in vec2 inTexCoords;

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_worldMatrix;

out vec3 psColour;
out vec2 psTexCoords;

void main()
{
    gl_Position =  u_projectionMatrix * u_viewMatrix * u_worldMatrix * vec4(position, 1.0);
    psColour = inColour;
    psTexCoords = inTexCoords;
}

#fragmentShader

#version 330

in  vec3 psColour;
in  vec2 psTexCoords;
out vec4 fragColor;

void main()
{
    fragColor = vec4(psColour, 1.0);
}
