The goal of this project was to build a Compiler in Java that would take code written in a language similar to C (Block) parse it then compile it into TAM assembly which would get executed by a TAM Machine, to execute them in TAM assembly. 

The Parser and Lexer are in the MiniCParser.G4 file and MiniCLexer.G4 file respectively. The Abstract tree is then built in the ASTBuilder.java file. 

To run the Compiler, you can use the following commands : 
- ant generate
- ant compile
- java -cp "bin/cls:tools/commons-lang3-3.7.jar:tools/commons-text-1.2.jar:tools/antlr-4.13.1-complete.jar:$CLASSPATH" fr.n7.stl.minic.Driver target.txt (where target.txt is the Block code you want to execute. To see example you can find some in the TestBon folder at the root)

