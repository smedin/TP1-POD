#!/bin/bash

target_dir="server/target"
client_dir="client/target"

mvn clean package

mkdir -p "executables"

cp "$target_dir/tpe1-g4-server-2024.2Q-bin.tar.gz" "executables/"
cp "$client_dir/tpe1-g4-client-2024.2Q-bin.tar.gz" "executables/"
cd "executables"

tar -xzf "tpe1-g4-server-2024.2Q-bin.tar.gz"
mv ./tpe1-g4-server-2024.2Q/run-server.sh ./tpe1-g4-server-2024.2Q/server.sh
#mv ./tpe1-g4-server-2024.2Q ./server
#rm -r tpe1-g4-server-2024.2Q
rm "tpe1-g4-server-2024.2Q-bin.tar.gz"

tar -xzf "tpe1-g4-client-2024.2Q-bin.tar.gz"
#rm -r tpe1-g4-client-2024.2Q
#mv ./tpe1-g4-client-2024.2Q ./client
rm "tpe1-g4-client-2024.2Q-bin.tar.gz"

chmod +x ./*/*.sh