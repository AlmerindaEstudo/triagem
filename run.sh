#!/bin/bash
clear
echo "Compilando o projeto..."
mkdir -p out
javac -d out src/*.java

echo "Executando o sistema de triagem..."
cd out
java HospitalTriagemGUI
