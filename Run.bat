javac src/*.java -d bin

cd bin

jar cfm ../versions/EvoSim.jar ../data/compiler/manifest.txt code ../data/textures

java -jar ../versions/EvoSim.jar
