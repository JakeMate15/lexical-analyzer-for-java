#!/bin/bash

# Compilación del programa Java
javac Lexical.java

if [ $# -lt 1 ]; then
    echo "Se requiere al menos un argumento: nombre de archivo de entrada."
    exit 1
fi

archivo_entrada="$1"

# Ejecución del programa Java
java Lexical "$archivo_entrada"

# Compilación y ejecución del programa C++
g++ Analizador.cpp
./a.out
