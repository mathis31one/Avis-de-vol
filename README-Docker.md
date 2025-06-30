# Avis de Vol - Docker Compose Setup

This Docker Compose setup launches the complete Avis de Vol application stack including:
- PostgreSQL database
- Spring Boot backend (Java 16)
- Angular frontend (served with Nginx)

## Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM

## Quick Start

1. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

2. **Start in background (detached mode):**
   ```bash
   docker-compose up -d --build
   ```

3. **View logs:**
   ```bash
   # All services
   docker-compose logs -f
   
   # Specific service
   docker-compose logs -f backend
   docker-compose logs -f frontend
   docker-compose logs -f postgres
   ```

4. **Stop all services:**
   ```bash
   docker-compose down
   ```

5. **Stop and remove volumes (clean slate):**
   ```bash
   docker-compose down -v
   ```

## Access Points

- **Frontend (Angular):** http://localhost:80 or http://localhost:4200
- **Backend API:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/actuator/health
- **PostgreSQL:** localhost:5432 (username: username, password: password)

## Services

### Frontend (Angular + Nginx)
- Built from `./avis-de-vol-cs`
- Runs on port 80 (and 4200 for convenience)
- Nginx serves the built Angular application
- Automatically routes API calls to backend

### Backend (Spring Boot)
- Built from `./avis-de-vol-ss`
- Runs on port 8080
- Connects to PostgreSQL database
- Includes health checks via Spring Actuator

### Database (PostgreSQL)
- PostgreSQL 15
- Database: `avis_db`
- Credentials: username/password
- Data persisted in Docker volume

## Development

### Make changes and rebuild:
```bash
# Rebuild specific service
docker-compose build backend
docker-compose build frontend

# Restart specific service
docker-compose restart backend
docker-compose restart frontend
```

### Access container shells:
```bash
# Backend container
docker-compose exec backend bash

# Frontend container
docker-compose exec frontend sh

# Database container
docker-compose exec postgres psql -U username -d avis_db
```

## Test Login

After startup, you can test the application:

1. Go to http://localhost:80
2. Register a new user or use these test credentials:
   - Email: test@example.com
   - Password: password123

## Troubleshooting

### Backend not connecting to database:
- Check if PostgreSQL is ready: `docker-compose logs postgres`
- Backend will automatically retry connections

### Frontend can't reach backend:
- Verify backend is healthy: http://localhost:8080/actuator/health
- Check network connectivity between containers

### Port conflicts:
- If ports 80, 4200, 8080, or 5432 are in use, stop the conflicting services or modify the docker-compose.yml port mappings

### Performance issues:
- Ensure Docker has enough memory allocated (recommend 4GB+)
- Use `docker system prune` to clean up unused resources

## Production Deployment

For production:
1. Update environment variables
2. Use proper SSL certificates
3. Configure proper CORS origins
4. Use secrets for sensitive data
5. Set up proper logging and monitoring
