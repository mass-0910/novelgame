javac -d bin -encoding UTF-8 src/*.java
jar cvfm NovelGame.jar META-INF/MANIFEST.MF -C bin .
exewrap -g -c "2020 shakeo" -C "TRIMAKERS SOFTWARE" -e NOLOG -j NovelGame.jar -o NovelGame.exe