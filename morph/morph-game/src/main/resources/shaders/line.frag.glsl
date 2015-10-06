#version 330 core
out vec4 color;
in vec2 UV;

uniform sampler2D myTextureSampler;
uniform vec4 constColor;

void main(){
    color = texture(myTextureSampler, UV) * constColor;
}