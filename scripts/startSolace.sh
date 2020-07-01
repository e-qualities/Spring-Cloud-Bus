#!/bin/bash
SCRIPT_DIR=$(dirname "$0")
docker-compose -f "$SCRIPT_DIR/solace-docker-template/PubSubStandard_singleNode.yml" up -d