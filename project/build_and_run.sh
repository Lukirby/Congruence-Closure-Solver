#!/bin/bash

# Set the source and output directories
SRC_DIR=.
OUT_DIR=out

# Create the output directory if it doesn't exist
if [ ! -d "$OUT_DIR" ]; then
    mkdir -p "$OUT_DIR"
fi

# Compile the Java files
javac -d "$OUT_DIR" -sourcepath "$SRC_DIR" 
"$SRC_DIR/project/HelloWorld.java" 
"$SRC_DIR/project/classes/CongruenceClosureAlgorithm.java" 
"$SRC_DIR/project/preprocessing/FormulaReader.java" 
"$SRC_DIR/project/preprocessing/TermsParser.java"

# Check if the compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the compiled Java program
java -cp "$OUT_DIR" project.HelloWorld