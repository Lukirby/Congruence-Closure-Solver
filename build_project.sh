#!/bin/bash

# Set the source directory
SRC_DIR="project"

# Set the output directory
OUT_DIR="project/compiled"

mkdir -p $OUT_DIR

# Compile all Java files
javac -d $OUT_DIR $(find $SRC_DIR -type f -name "*.java" ! -path "$SRC_DIR/tests/*")

# Check if compilation was successful
if [ $? -eq 0 ]; then
  echo "Compilation successful!"
else
  echo "Compilation failed!"
fi
