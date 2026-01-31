# MedShare Hub - Database Setup Script (PowerShell)
# This script starts the PostgreSQL and Redis containers using Docker Compose.

Write-Host "🚀 Starting MedShare Hub Infrastructure..." -ForegroundColor Cyan

# Check if docker-compose is installed
if (!(Get-Command "docker-compose" -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Error: docker-compose could not be found. Please install it first." -ForegroundColor Red
    exit
}

# Start the containers
docker-compose up -d

Write-Host "✅ Database (PostgreSQL) and Cache (Redis) are starting in the background." -ForegroundColor Green
Write-Host "📋 Use 'docker-compose ps' to check status."
Write-Host "📜 Use 'docker-compose logs -f' to view logs."
