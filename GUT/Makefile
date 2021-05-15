UNAME := $(shell uname)

os = windows
delimeter = ;

ifeq ($(UNAME), Darwin)
os = macosx
delimeter = :
endif

jar:
	cd bin; jar cfe ../gamedata.jar GUT *.class

all:
	javac -cp "src$(delimeter)../../Engine/java/greenpixel.jar" -d bin src/GUT.java 

run: all
	java -cp "bin/.$(delimeter)../../Engine/java/greenpixel.jar" GUT
