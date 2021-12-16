#vertexShader

#version 330

layout (location = 0) in vec3 inVertexPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec3 offset;

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_worldMatrix;

out vec3 Normal;
out vec3 FragmentPosition;

void main() {
    FragmentPosition = vec3(vec4(inVertexPosition, 1.0));
    Normal = inNormal;
    gl_Position =  u_projectionMatrix * u_viewMatrix * u_worldMatrix * vec4(inVertexPosition + offset, 1.0);
}

#fragmentShader

#version 330

in vec3 Normal;
in vec3 FragmentPosition; 

out vec4 fragColor;

uniform vec3 u_lightDirection;

void main()
{   
    vec3 objectColor = vec3(250.0/255.0, 218.0/255.0, 94.0/255.0);

    //ambient light
    vec3 lightColor = vec3(1.0);
    float ambientStrength = 0.05;
    vec3 ambient = ambientStrength * lightColor;

    //diffuse light
    vec3 norm = normalize(Normal);
    //vec3 lightDir = normalize(u_lightPosition - FragmentPosition);  
    float diff = max(dot(norm, u_lightDirection), 0.0);
    float diffStrength = 0.5;
    vec3 diffuse = diffStrength * diff * lightColor;


    vec3 result = (ambient + diffuse) * objectColor;

    fragColor = vec4(result, 1.0);
}

