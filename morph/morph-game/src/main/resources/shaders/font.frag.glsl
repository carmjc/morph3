#version 330 core
out vec4 color;
in vec2 UV;

uniform sampler2D myTextureSampler;
uniform vec4 constColor;
uniform vec4 fontData[255];
uniform int charData;

void main() {
	if (UV.x <= fontData[charData][0] + fontData[charData][2] && UV.y <= fontData[charData][1] + fontData[charData][3]) {
    	color = texture(myTextureSampler, UV) * constColor;
    } else {
    	color = vec4(0, 0, 0, 0);
    }
}