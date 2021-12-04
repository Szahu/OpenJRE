#vertexShader

#version 330

layout (location = 0) in vec3 inVertexPosition;
layout (location = 1) in vec3 inNormal;

uniform mat4 u_projectionMatrix;
uniform mat4 u_worldMatrix;
uniform mat4 u_viewMatrix;

out vec3 Normal;
out vec3 FragmentPosition;

void main() {
    FragmentPosition = vec3(u_worldMatrix * vec4(inVertexPosition, 1.0));
    Normal = mat3(transpose(inverse(u_worldMatrix))) * inNormal;
    gl_Position =  u_projectionMatrix * u_viewMatrix * u_worldMatrix * vec4(inVertexPosition, 1.0);
}

#fragmentShader

#version 330

in vec3 Normal;
in vec3 FragmentPosition; 

out vec4 fragColor;

uniform vec3 u_lightPosition;
uniform vec3 u_cameraPosition;

void main()
{   
    vec3 objectColor = vec3(1.0, 0, 0);

    //ambient light
    vec3 lightColor = vec3(1.0);
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;

    //diffuse light
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(u_lightPosition - FragmentPosition);  
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    //specular light
    float specularStrength = 0.5;
    vec3 viewDir = normalize(u_cameraPosition - FragmentPosition);
    vec3 reflectDir = reflect(-lightDir, norm);  
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = specularStrength * spec * lightColor;  


    vec3 result = (ambient + diffuse + specular) * objectColor;

    fragColor = vec4(result, 1.0);
}

