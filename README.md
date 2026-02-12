# URL Shortener Service

A Spring Boot-based URL shortening service with RESTful APIs, PostgreSQL persistence, and Docker support.

## Features

- ✅ Shorten long URLs to compact short codes
- ✅ Retrieve original URLs using short codes
- ✅ Update existing shortened URLs
- ✅ Delete shortened URLs
- ✅ Track access statistics (view count)
- ✅ Persistent storage with PostgreSQL
- ✅ Database migrations with Flyway
- ✅ Comprehensive unit and integration tests
- ✅ Docker containerization for all environments

## Tech Stack

- **Framework**: Spring Boot 4.0.2
- **Language**: Java 21
- **Database**: PostgreSQL 16
- **Build Tool**: Maven 3.9
- **Testing**: JUnit 5, Mockito, H2 (in-memory)
- **Containerization**: Docker & Docker Compose
- **ORM**: Spring Data JPA with Hibernate
- **Utilities**: Lombok

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/shorten` | Create a new shortened URL |
| `GET` | `/shorten/{shortCode}` | Retrieve original URL (increments access count) |
| `PUT` | `/shorten/{shortCode}` | Update an existing shortened URL |
| `DELETE` | `/shorten/{shortCode}` | Delete a shortened URL |
| `GET` | `/shorten/{shortCode}/stats` | Get statistics without incrementing count |

### Example Requests

**Create a shortened URL:**
```bash
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.example.com/very/long/url"}'
```

**Retrieve original URL:**
```bash
curl http://localhost:8080/shorten/abc123
```

**Get statistics:**
```bash
curl http://localhost:8080/shorten/abc123/stats
```

---

## 🛠️ Development Setup

### Prerequisites
- Docker & Docker Compose
- Java 21 (for running locally without Docker)
- Maven 3.9+ (for building locally)

### Quick Start (Development)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd url-shortner
   ```

2. **Configure environment variables**
   ```bash
   cp .env.dev .env
   ```
   
   Edit `.env` if needed (default values work out of the box).

3. **Start PostgreSQL database**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```
   
   This starts only the PostgreSQL container on port 5432.

4. **Run the application locally**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

5. **Access the application**
   - API: http://localhost:8080
   - Test endpoint: http://localhost:8080/shorten/{shortCode}

6. **Stop database when done**
   ```bash
   docker-compose -f docker-compose.dev.yml down
   ```
   
   To remove data volumes:
   ```bash
   docker-compose -f docker-compose.dev.yml down -v
   ```

### Development Workflow


**Build the project:**
```bash
./mvnw clean package
```

**View database:**
```bash
docker exec -it postgres-local psql -U dev_user -d urlshortner_dev
```

---

## 🚀 Production Deployment

### Option 1: Production with Own PostgreSQL Container

Use this when you want to manage your own PostgreSQL container alongside the application.

#### Setup

1. **Configure environment**
   ```bash
   cp .env.prod .env
   ```

2. **Edit `.env` with secure credentials**
   ```bash
   nano .env
   ```
   
   **Important**: Change `POSTGRES_PASSWORD` to a strong password!

3. **Deploy the stack**
   ```bash
   docker-compose up -d
   ```
   
   This will:
   - Build the application Docker image
   - Start PostgreSQL container with persistent volume
   - Start the application container
   - Set up networking between containers

4. **Verify deployment**
   ```bash
   docker-compose ps
   docker-compose logs -f app
   ```

5. **Access the application**
   - API: http://your-server-ip:8080

#### Management Commands

**View logs:**
```bash
docker-compose logs -f
docker-compose logs -f app      # App logs only
docker-compose logs -f postgres # Database logs only
```

**Restart services:**
```bash
docker-compose restart
docker-compose restart app      # Restart app only
```

**Stop services:**
```bash
docker-compose down             # Stop but keep data
docker-compose down -v          # Stop and remove volumes (⚠️ data loss)
```

**Backup database:**
```bash
docker exec postgres-local pg_dump -U prod_user urlshortner_prod > backup.sql
```

**Restore database:**
```bash
docker exec -i postgres-local psql -U prod_user -d urlshortner_prod < backup.sql
```

---

### Option 2: Production with Managed Database Service

Use this when connecting to AWS RDS, Azure Database, Google Cloud SQL, or other managed PostgreSQL services.

#### Setup

1. **Create managed database instance**
   - AWS RDS PostgreSQL
   - Azure Database for PostgreSQL
   - Google Cloud SQL for PostgreSQL
   - DigitalOcean Managed Databases
   - etc.

2. **Configure environment**
   ```bash
   cp .env.prod.managed .env
   ```

3. **Edit `.env` with your database details**
   ```bash
   nano .env
   ```
   
   Update these values:
   ```env
   POSTGRES_HOST=your-db-instance.region.rds.amazonaws.com
   POSTGRES_DB=urlshortner_prod
   POSTGRES_USER=your_db_user
   POSTGRES_PASSWORD=your_secure_password
   POSTGRES_PORT=5432
   ```

4. **Deploy the application**
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```
   
   This deploys only the application container, connecting to your external database.

5. **Verify deployment**
   ```bash
   docker-compose -f docker-compose.prod.yml ps
   docker-compose -f docker-compose.prod.yml logs -f
   ```

#### Management Commands

**View logs:**
```bash
docker-compose -f docker-compose.prod.yml logs -f
```

**Restart application:**
```bash
docker-compose -f docker-compose.prod.yml restart
```

**Stop application:**
```bash
docker-compose -f docker-compose.prod.yml down
```

**Update application:**
```bash
git pull
docker-compose -f docker-compose.prod.yml up -d --build
```

---

## 📊 Monitoring & Health Checks

### Application Health Check

The application includes health check endpoints:

```bash
# Docker health check (configured in docker-compose)
curl http://localhost:8080/

# Check container health
docker inspect --format='{{.State.Health.Status}}' url-shortner-app
```

### Database Health Check

```bash
# Check PostgreSQL container health
docker inspect --format='{{.State.Health.Status}}' postgres-local

# Manual connection test
docker exec -it postgres-local pg_isready -U prod_user
```

---

## 🔒 Security Recommendations

### For Production Deployments:

1. **Strong Passwords**: Use strong, randomly generated passwords
   ```bash
   openssl rand -base64 32
   ```

2. **Environment Variables**: Never commit `.env` files to version control
   ```bash
   echo ".env*" >> .gitignore
   ```

3. **Database Access**: Restrict PostgreSQL access to application only
   - Remove `ports:` section from postgres service in docker-compose.yml
   - Use Docker network isolation

4. **HTTPS**: Use a reverse proxy (nginx, Caddy, Traefik) with SSL/TLS

5. **Firewall**: Configure firewall rules to allow only necessary ports

6. **Updates**: Regularly update base images and dependencies

---

## 📁 Project Structure

```
url-shortner/
├── src/
│   ├── main/
│   │   ├── java/com/arnavbansal2764/url_shortner/
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── service/             # Business logic
│   │   │   └── UrlShortnerApplication.java
│   │   └── resources/
│   │       ├── db/migration/        # Flyway migrations
│   │       └── application.yml      # Spring configuration
│   └── test/                        # Unit and integration tests
├── docker-compose.yml               # Production with PostgreSQL
├── docker-compose.dev.yml           # Development (DB only)
├── docker-compose.prod.yml          # Production with managed DB
├── Dockerfile                       # Multi-stage Docker build
├── .env.dev                         # Development environment
├── .env.prod                        # Production environment (own DB)
├── .env.prod.managed               # Production environment (managed DB)
└── pom.xml                         # Maven configuration
```

---

## 🧪 Testing

**Run all tests:**
```bash
./mvnw test
```

**Run specific test class:**
```bash
./mvnw test -Dtest=ShortenerServiceTest
```

**Run with coverage:**
```bash
./mvnw test jacoco:report
```

**Test files:**
- `ShortCodeGeneratorServiceTest.java` - Short code generation tests
- `ShortenerServiceTest.java` - Business logic tests
- `ShortnerControllerTest.java` - API integration tests

---

## 🐳 Docker Commands Reference

### Development
```bash
# Start database only
docker-compose -f docker-compose.dev.yml up -d

# Stop database
docker-compose -f docker-compose.dev.yml down

# View database logs
docker-compose -f docker-compose.dev.yml logs -f
```

### Production (Own PostgreSQL)
```bash
# Start full stack
docker-compose up -d

# Rebuild after code changes
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production (Managed Database)
```bash
# Start application only
docker-compose -f docker-compose.prod.yml up -d

# Rebuild and restart
docker-compose -f docker-compose.prod.yml up -d --build

# Stop application
docker-compose -f docker-compose.prod.yml down
```

---

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default (Dev) | Required |
|----------|-------------|---------------|----------|
| `POSTGRES_DB` | Database name | urlshortner_dev | ✅ |
| `POSTGRES_USER` | Database user | dev_user | ✅ |
| `POSTGRES_PASSWORD` | Database password | dev_password | ✅ |
| `POSTGRES_HOST` | Database host | localhost | ✅ |
| `POSTGRES_PORT` | Database port | 5432 | ✅ |
| `SPRING_PROFILES_ACTIVE` | Spring profile | dev | ❌ |

### Application Profiles

- **dev**: Development mode (verbose logging, dev tools)
- **prod**: Production mode (optimized, minimal logging)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License.

---

## 🆘 Troubleshooting

### Common Issues

**Port already in use:**
```bash
# Check what's using port 5432 or 8080
lsof -i :5432
lsof -i :8080

# Change port in .env file
POSTGRES_PORT=5433
```

**Database connection failed:**
```bash
# Check PostgreSQL is running
docker ps

# Check logs
docker-compose logs postgres

# Verify credentials in .env match application.yml
```

**Application won't start:**
```bash
# Check application logs
docker-compose logs app

# Rebuild image
docker-compose up -d --build

# Check Java version
java -version  # Should be 21
```

**Migrations failed:**
```bash
# Check migration files
ls src/main/resources/db/migration/

# Manually repair
./mvnw flyway:repair

# Or reset database (⚠️ development only)
docker-compose down -v
docker-compose up -d
```

---

## 📧 Support

For issues and questions, please open an issue on the GitHub repository.
