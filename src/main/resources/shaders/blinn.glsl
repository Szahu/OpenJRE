#vertexShader

#version 330

layout (location = 0) in vec3 inVertexPosition;
layout (location = 1) in vec2 inTextureCoordinate;
layout (location = 2) in vec3 inNormal;
layout (location = 3) in vec3 inTangent;

struct PointLight {
    vec3 position;
    vec3 color;
    float ambientStrength;
    float specularStrength;
};

uniform mat4 u_projectionMatrix;
uniform mat4 u_worldMatrix;
uniform mat4 u_viewMatrix;
uniform PointLight u_pointLight;
uniform vec3 u_cameraPosition;

out VS_OUT {
    vec2 TextureCoordinate;
    vec3 TangentLightPos;
    vec3 TangentViewPos;
    vec3 TangentFragPos;
    vec3 FragmentPosition;
} vs_out;


void main() {
    vs_out.TextureCoordinate = inTextureCoordinate;
    vs_out.FragmentPosition = vec3(u_worldMatrix * vec4(inVertexPosition, 1.0));
    vec3 T = normalize(vec3(u_worldMatrix * vec4(inTangent,   0.0)));
    vec3 B = normalize(vec3(u_worldMatrix * vec4(cross(inNormal, inTangent), 0.0)));
    vec3 N = normalize(vec3(u_worldMatrix * vec4(inNormal,    0.0)));
    mat3 TBN = transpose(mat3(T, B, N));
    vs_out.TangentLightPos = TBN * u_pointLight.position;
    vs_out.TangentViewPos  = TBN * u_cameraPosition;
    vs_out.TangentFragPos  = TBN * vec3(u_worldMatrix * vec4(inVertexPosition, 1.0));
    gl_Position =  u_projectionMatrix * u_viewMatrix * u_worldMatrix * vec4(inVertexPosition, 1.0);
}

#fragmentShader

#version 330

in VS_OUT {
    vec2 TextureCoordinate;
    vec3 TangentLightPos;
    vec3 TangentViewPos;
    vec3 TangentFragPos;
    vec3 FragmentPosition; 
} fs_in;


struct PointLight {
    vec3 position;
    vec3 color;
    float ambientStrength;
    float specularStrength;
};

out vec4 fragColor;

uniform sampler2D u_texture_sampler;
uniform sampler2D u_normal_texture_sampler;
uniform vec3 u_cameraPosition;
uniform PointLight u_pointLight;

void main()
{   
    vec3 objectColor = (texture(u_texture_sampler, fs_in.TextureCoordinate)).xyz;

    vec3 Normal = (texture(u_normal_texture_sampler, fs_in.TextureCoordinate)).rgb;
    Normal = normalize(Normal * 2.0 - 1.0);

    //ambient light
    vec3 ambient = u_pointLight.ambientStrength * u_pointLight.color;

    //diffuse light
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(fs_in.TangentLightPos - fs_in.TangentFragPos);  
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * u_pointLight.color;

    //specular light
    float shininess = 8.0;
    vec3 viewDir = normalize(fs_in.TangentViewPos - fs_in.TangentFragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    vec3 reflectDir = reflect(-lightDir, norm);  
    float spec = pow(max(dot(Normal, halfwayDir), 0.0), shininess);
    vec3 specular = u_pointLight.specularStrength * spec * u_pointLight.color;  


    vec3 result = (ambient + diffuse + specular) * objectColor;

    fragColor = vec4(result, 1.0);
}

