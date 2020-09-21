#!/bin/bash
echo "Usage: $0 <message>"
echo "Sending message: $1"
echo "Using command: http://localhost:8001/send/$1"
curl http://localhost:8001/send/$1 