#!/usr/bin/env bash
set -e

echo 'Limpando e construindo o projeto de seller'
./gradlew clean build -x test

echo 'Gerando a imagem do seller-ms...'
docker build -t lucciano01/seller-ms:latest .