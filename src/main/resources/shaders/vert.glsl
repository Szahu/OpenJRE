#version 330
layout (location=0) in vec3 position;
out vec4 vertexColor;
//			0, 3, 1, 1, 2, 3
const vec4 color[4] = vec4[4](  vec4(1.0f, 0.0f, 0.0f, 1.0f),
                                vec4(0.0f, 1.0f, 0.0f, 1.0f),
                                vec4(0.0f, 0.0f, 0.6f, 1.0f),
                                vec4(0.0f, 0.0f, 0.6f, 1.0f)
                                );

void main()
{
    gl_Position = vec4(position, 1.0);
    vertexColor = color[gl_VertexID];
}