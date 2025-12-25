# CampusCard Deployment Guide

Complete guide for deploying CampusCard to production environments.

## Table of Contents

- [Pre-Deployment Checklist](#pre-deployment-checklist)
- [Environment Setup](#environment-setup)
- [Deployment Options](#deployment-options)
- [Docker Deployment](#docker-deployment)
- [Cloud Deployment](#cloud-deployment)
- [Database Setup](#database-setup)
- [SSL/TLS Configuration](#ssltls-configuration)
- [Monitoring and Logging](#monitoring-and-logging)
- [Backup Strategy](#backup-strategy)
- [Troubleshooting](#troubleshooting)

---

## Pre-Deployment Checklist

Before deploying to production, ensure you have completed:

### Security

- [ ] Changed default admin password
- [ ] Generated strong JWT secret (min 256-bit)
- [ ] Configured secure database passwords
- [ ] Set up MinIO with secure credentials
- [ ] Configured CORS for production domain only
- [ ] Enabled HTTPS/SSL
- [ ] Reviewed and updated banned words list
- [ ] Configured rate limiting appropriately
- [ ] Removed all development/debug settings

### Configuration

- [ ] Created production `.env` file with all required variables
- [ ] Configured email SMTP settings
- [ ] Set correct allowed origins for CORS
- [ ] Configured database connection pool
- [ ] Set up file storage (MinIO production instance)
- [ ] Configured logging levels
- [ ] Set up monitoring and alerting

### Testing

- [ ] Run all unit tests: `./mvnw test`
- [ ] Run all integration tests
- [ ] Perform security audit
- [ ] Load testing completed
- [ ] Manual end-to-end testing
- [ ] Verified email sending works
- [ ] Tested file upload functionality

### Documentation

- [ ] Updated README.md with production URLs
- [ ] Documented deployment process
- [ ] Created runbook for common issues
- [ ] Documented backup and recovery procedures
- [ ] Created monitoring dashboard

---

## Environment Setup

### Production Environment Variables

Create a production `.env` file with secure values:

```bash
# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# Database (Production PostgreSQL)
DB_HOST=your-production-db-host.com
DB_PORT=5432
DB_NAME=campuscard_prod
DB_USERNAME=campuscard_prod_user
DB_PASSWORD=CHANGE_THIS_STRONG_PASSWORD_123!

# JWT Configuration
JWT_SECRET=CHANGE_THIS_TO_VERY_LONG_RANDOM_STRING_AT_LEAST_256_BITS
JWT_EXPIRATION_MS=86400000

# MinIO (Production Object Storage)
MINIO_ENDPOINT=https://minio.your-domain.com
MINIO_ACCESS_KEY=CHANGE_THIS_MINIO_ACCESS_KEY
MINIO_SECRET_KEY=CHANGE_THIS_MINIO_SECRET_KEY
MINIO_BUCKET_NAME=campuscard-prod

# Admin Account
ADMIN_EMAIL=admin@eng.psu.edu.eg
ADMIN_PASSWORD=CHANGE_THIS_SECURE_ADMIN_PASSWORD_456!
ADMIN_FIRST_NAME=System
ADMIN_LAST_NAME=Administrator
ADMIN_NATIONAL_ID=30000000000001
ADMIN_FACULTY_ID=1
ADMIN_DEPARTMENT_ID=1

# Email Configuration (Production SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=noreply@your-domain.com
MAIL_PASSWORD=CHANGE_THIS_EMAIL_PASSWORD
MAIL_FROM=noreply@your-domain.com

# CORS (Production domain only)
ALLOWED_ORIGINS=https://campuscard.your-domain.com

# Rate Limiting (Production)
RATELIMIT_LOGIN_CAPACITY=5
RATELIMIT_LOGIN_REFILL_DURATION=15m
RATELIMIT_SIGNUP_CAPACITY=3
RATELIMIT_SIGNUP_REFILL_DURATION=1h

# File Upload Limits
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB

# Frontend URL
FRONTEND_URL=https://campuscard.your-domain.com
```

### Generating Secure Secrets

**JWT Secret** (256-bit minimum):
```bash
openssl rand -base64 32
```

**Database Password**:
```bash
openssl rand -base64 24
```

**MinIO Credentials**:
```bash
# Access Key (20 characters)
openssl rand -hex 10

# Secret Key (40 characters)
openssl rand -base64 30
```

---

## Deployment Options

### Option 1: Docker Deployment (Recommended)

**Pros**:
- Consistent environment
- Easy rollback
- Simple scaling
- Isolated dependencies

**Cons**:
- Requires Docker knowledge
- Additional resource overhead

### Option 2: Traditional Server Deployment

**Pros**:
- No Docker overhead
- Direct system access
- Familiar to sysadmins

**Cons**:
- Environment inconsistencies
- Manual dependency management
- Complex rollback

### Option 3: Cloud Platform (AWS, Azure, GCP)

**Pros**:
- Managed infrastructure
- Auto-scaling
- High availability
- Managed databases

**Cons**:
- Higher costs
- Vendor lock-in
- Learning curve

---

## Docker Deployment

### Production Docker Setup

#### 1. Create Production Dockerfile

**Backend** (`CampusCard/Dockerfile.prod`):
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/CampusCard-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend** (`frontend/Dockerfile.prod`):
```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 2. Create Production Docker Compose

**`docker-compose.prod.yml`**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: campuscard-postgres-prod
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME}"]
      interval: 10s
      timeout: 5s
      retries: 5

  minio:
    image: minio/minio:latest
    container_name: campuscard-minio-prod
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY}
    volumes:
      - minio_data:/data
    ports:
      - "9000:9000"
      - "9001:9001"
    command: server /data --console-address ":9001"
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  backend:
    build:
      context: ./CampusCard
      dockerfile: Dockerfile.prod
    container_name: campuscard-backend-prod
    env_file:
      - .env
    depends_on:
      postgres:
        condition: service_healthy
      minio:
        condition: service_healthy
    ports:
      - "8080:8080"
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile.prod
    container_name: campuscard-frontend-prod
    depends_on:
      - backend
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./ssl:/etc/nginx/ssl:ro
    restart: unless-stopped

volumes:
  postgres_data:
    driver: local
  minio_data:
    driver: local
```

#### 3. Deploy with Docker Compose

```bash
# Build and start services
docker compose -f docker-compose.prod.yml up -d --build

# View logs
docker compose -f docker-compose.prod.yml logs -f

# Check status
docker compose -f docker-compose.prod.yml ps

# Stop services
docker compose -f docker-compose.prod.yml down

# Update and restart
docker compose -f docker-compose.prod.yml up -d --build --force-recreate
```

#### 4. Nginx Configuration

**`frontend/nginx.conf`**:
```nginx
server {
    listen 80;
    listen [::]:80;
    server_name campuscard.your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name campuscard.your-domain.com;

    # SSL Configuration
    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Frontend
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # Backend API Proxy
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Gzip Compression
    gzip on;
    gzip_vary on;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/json;
    gzip_min_length 1000;

    # Caching
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## Cloud Deployment

### AWS Deployment

#### Architecture

```
┌─────────────────┐
│   Route 53      │ (DNS)
└────────┬────────┘
         │
┌────────▼────────┐
│   CloudFront    │ (CDN)
└────────┬────────┘
         │
┌────────▼────────┐
│  Application    │
│  Load Balancer  │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
┌───▼──┐  ┌──▼───┐
│ ECS  │  │ ECS  │ (Docker containers)
└───┬──┘  └──┬───┘
    │        │
    └────┬───┘
         │
┌────────▼────────┐
│  RDS PostgreSQL │
└─────────────────┘

┌─────────────────┐
│       S3        │ (MinIO alternative)
└─────────────────┘
```

#### Services Used

- **ECS (Elastic Container Service)**: Run Docker containers
- **RDS (Relational Database Service)**: Managed PostgreSQL
- **S3**: Object storage (instead of MinIO)
- **ALB (Application Load Balancer)**: Load balancing
- **CloudFront**: CDN for frontend
- **Route 53**: DNS management
- **Certificate Manager**: SSL/TLS certificates
- **CloudWatch**: Monitoring and logging

#### Deployment Steps

1. **Create RDS PostgreSQL Instance**:
   - Engine: PostgreSQL 16
   - Instance class: db.t3.medium (or larger)
   - Storage: 100 GB SSD
   - Multi-AZ: Yes (for high availability)
   - Backup retention: 7 days

2. **Create S3 Bucket**:
   ```bash
   aws s3 mb s3://campuscard-prod-files
   aws s3api put-bucket-cors --bucket campuscard-prod-files --cors-configuration file://cors.json
   ```

3. **Build and Push Docker Images**:
   ```bash
   # Login to ECR
   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

   # Build and push backend
   docker build -t campuscard-backend:latest -f CampusCard/Dockerfile.prod CampusCard/
   docker tag campuscard-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/campuscard-backend:latest
   docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/campuscard-backend:latest

   # Build and push frontend
   docker build -t campuscard-frontend:latest -f frontend/Dockerfile.prod frontend/
   docker tag campuscard-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/campuscard-frontend:latest
   docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/campuscard-frontend:latest
   ```

4. **Create ECS Task Definitions and Services**

5. **Configure ALB and Target Groups**

6. **Set up CloudFront Distribution**

7. **Configure Route 53 DNS**

### Azure Deployment

Use Azure equivalents:
- **Azure Container Instances** or **Azure Kubernetes Service** (AKS)
- **Azure Database for PostgreSQL**
- **Azure Blob Storage**
- **Azure Application Gateway**
- **Azure CDN**
- **Azure DNS**

### Google Cloud Deployment

Use GCP equivalents:
- **Cloud Run** or **Google Kubernetes Engine** (GKE)
- **Cloud SQL for PostgreSQL**
- **Cloud Storage**
- **Cloud Load Balancing**
- **Cloud CDN**
- **Cloud DNS**

---

## Database Setup

### Production Database Configuration

#### PostgreSQL Configuration

**`postgresql.conf`** optimizations:
```ini
# Connections
max_connections = 100

# Memory
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB

# WAL
wal_level = replica
max_wal_size = 1GB
min_wal_size = 80MB

# Query Planning
random_page_cost = 1.1
effective_io_concurrency = 200

# Logging
logging_collector = on
log_directory = 'pg_log'
log_filename = 'postgresql-%Y-%m-%d.log'
log_rotation_age = 1d
log_min_duration_statement = 1000  # Log slow queries (>1s)
```

#### Database Initialization

```bash
# Connect to PostgreSQL
psql -h <DB_HOST> -U <DB_USERNAME> -d postgres

# Create database
CREATE DATABASE campuscard_prod;

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE campuscard_prod TO campuscard_prod_user;

# Exit and run migrations
\q

# Migrations run automatically on Spring Boot startup
```

---

## SSL/TLS Configuration

### Let's Encrypt (Free SSL)

```bash
# Install Certbot
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d campuscard.your-domain.com

# Auto-renewal (cron job)
sudo crontab -e
# Add: 0 0 * * * certbot renew --quiet
```

### Manual SSL Certificate

1. Obtain SSL certificate from CA
2. Place files in `ssl/` directory:
   - `fullchain.pem`
   - `privkey.pem`
3. Mount in Docker Compose (already configured above)

---

## Monitoring and Logging

### Application Monitoring

**Spring Boot Actuator** endpoints:
```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

Access metrics:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`

### Logging Configuration

**Logback** configuration (`logback-spring.xml`):
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/campuscard/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/campuscard/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### Monitoring Tools

**Prometheus + Grafana** (Recommended):
1. Install Prometheus to scrape `/actuator/prometheus`
2. Install Grafana for visualization
3. Import Spring Boot dashboard

**ELK Stack** (Elasticsearch, Logstash, Kibana):
1. Configure Logstash to collect logs
2. Index logs in Elasticsearch
3. Visualize in Kibana

---

## Backup Strategy

### Automated Backups

**Daily database backup** (cron job):
```bash
#!/bin/bash
# /opt/scripts/backup-database.sh

BACKUP_DIR="/backup/campuscard"
DATE=$(date +%Y%m%d_%H%M%S)
DB_HOST="localhost"
DB_NAME="campuscard_prod"
DB_USER="campuscard_prod_user"

# Create backup
PGPASSWORD=$DB_PASSWORD pg_dump -h $DB_HOST -U $DB_USER $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# Compress
gzip $BACKUP_DIR/db_backup_$DATE.sql

# Delete backups older than 7 days
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +7 -delete

# Upload to S3 (optional)
aws s3 cp $BACKUP_DIR/db_backup_$DATE.sql.gz s3://campuscard-backups/
```

**Cron schedule**:
```bash
# Daily at 2 AM
0 2 * * * /opt/scripts/backup-database.sh
```

---

## Troubleshooting

### Common Issues

**Issue**: Application won't start
- Check logs: `docker compose logs backend`
- Verify database connection
- Check environment variables

**Issue**: Database connection timeout
- Verify PostgreSQL is running
- Check firewall rules
- Verify credentials

**Issue**: File upload fails
- Check MinIO/S3 configuration
- Verify bucket exists
- Check file size limits

---

**Last Updated**: December 24, 2025  
**Maintained By**: CampusCard DevOps Team
