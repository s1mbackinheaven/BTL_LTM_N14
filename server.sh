#!/bin/bash

echo "🚀 Starting Game Server..."

# Compile project
echo "🔨 Compiling project..."
mvn clean compile

# Chạy server
echo "🖥️ Starting server on port 3009..."
java -cp target/classes com.oop.game.server.ServerMain
