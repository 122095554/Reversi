JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Reversi.java \
	ReversiBoard.java \
	TwoPlayerPlayable.java \
	Player.java \
	COLOR.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class