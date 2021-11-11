#version 330
in vec4 vertexColor;
out vec4 fragColor;
void main()
{
    fragColor = vertexColor + vec4(0.1f, 0.2f, 0.3f, 0.0f);
}