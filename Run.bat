javac src/*.java -d bin

cd bin

jar cfm ../versions/EvoSim.jar ../data/compiler/manifest.txt *.class textures

start "" java -jar ../versions/EvoSim.jar