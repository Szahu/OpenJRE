#vertexShader

#version 330

layout (location =0) in vec3 position;

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_worldMatrix;

out vec3 exColour;

void main()
{
    gl_Position =  u_projectionMatrix * u_viewMatrix * u_worldMatrix * vec4(position, 1.0);
}

#fragmentShader

#version 330

in  vec3 exColour;
out vec4 fragColor;

void main()
{
    fragColor = vec4(1.0, 1.0, 1.0, 0.7);
}
