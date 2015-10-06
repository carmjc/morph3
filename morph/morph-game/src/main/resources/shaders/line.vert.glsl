#version 330 core
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec2 vertexUV;

out vec2 UV;

uniform mat4 VP;
uniform mat4 M;

void main() {
	mat4 MVP = VP * M; 
	gl_Position = MVP * vec4(vertexPosition_modelspace, 1);
    	
	UV = vertexUV;
}