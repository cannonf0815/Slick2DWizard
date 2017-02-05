@ECHO off
ECHO cleaning up...
DEL .\bin\game\wizard\*.class

REM   -cp <path> Specify where to find user class files (jar files)
ECHO compiling WizardGame.java
javac -cp ./lib/jars/slick.jar;./lib/jars/lwjgl.jar -d ./bin ./src/game/wizard/WizardGame.java

REM set the java.library.path to natives binary (Windows only)
REM and the classpath and run the program
REM important: since ./bin is our output directory it needs to be added to classpath to find
REM the main class game.wizard.WizardGame
ECHO starting Java Application game.wizard.WizardGame
java -Djava.library.path=./lib/natives -cp ./bin;./lib/jars/slick.jar;./lib/jars/lwjgl.jar game.wizard.WizardGame

PAUSE
