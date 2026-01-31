#!/bin/bash
# MedShare Hub - Database Setup Script
# This script starts the PostgreSQL and Redis containers using Docker Compose.

echo "🚀 Starting MedShare Hub Infrastructure..."

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null
then
    echo "❌ Error: docker-compose could not be found. Please install it first."
    exit 1
fi

# Start the containers
docker-compose up -d

echo "✅ Database (PostgreSQL) and Cache (Redis) are starting in the background."
echo "📋 Use 'docker-compose ps' to check status."
echo "📜 Use 'docker-compose logs -f' to view logs."
