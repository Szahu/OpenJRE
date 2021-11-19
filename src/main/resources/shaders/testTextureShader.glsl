#vertexShader

#version 330

layout (location =0) in vec3 position;
layout (location =1) in vec2 inTexCoords;
layout (location =2) in vec3 normal;

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_worldMatrix;

out vec2 psTexCoords;

void main()
{
    gl_Position =  u_projectionMatrix * u_viewMatrix * u_worldMatrix * vec4(position, 1.0);
    psTexCoords = inTexCoords;
}

#fragmentShader

#version 330

in  vec2 psTexCoords;
out vec4 fragColor;

uniform sampler2D u_texture_sampler;

void main()
{
    fragColor = texture(u_texture_sampler, psTexCoords);
}
