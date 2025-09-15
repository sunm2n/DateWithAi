#!/bin/bash

echo "🚀 Starting DateWithAI Database Services..."
echo ""

# .env 파일 확인
if [[ ! -f .env ]]; then
    echo "⚠️  .env file not found. Using default values."
fi

# Docker Compose로 컨테이너 시작
echo "📦 Starting containers..."
docker-compose --env-file .env up -d

# 헬스체크 대기
echo "⏳ Waiting for services to be healthy..."
timeout=60
counter=0

while [ $counter -lt $timeout ]; do
    if docker-compose ps | grep -q "healthy"; then
        break
    fi
    echo "   Waiting... ($counter/$timeout seconds)"
    sleep 2
    counter=$((counter + 2))
done

# 서비스 상태 확인
echo ""
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "✅ Database setup complete!"
echo ""
echo "📡 Connection Information:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🐘 PostgreSQL (with PGVector)"
echo "   Host: localhost"
echo "   Port: 5432"
echo "   Database: datewithai-db"
echo "   Username: root"
echo "   Password: 1234"
echo ""
echo "🔴 Redis"
echo "   Host: localhost"
echo "   Port: 6379"
echo "   Password: (none)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🛠️  Management Commands:"
echo "   Stop services:    docker-compose down"
echo "   View logs:        docker-compose logs -f"
echo "   Restart:          docker-compose restart"
echo "   Access DB:        docker exec -it datewithai-db psql -U root -d datewithai-db"
echo "   Access Redis:     docker exec -it datewithai-redis redis-cli"