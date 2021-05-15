all:
	javac -classpath src -d bin src/Convert.java 

run: all
	java -cp bin/. Convert $(SRC) $(DEST)
