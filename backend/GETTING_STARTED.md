# Getting Started with Ziwa Dairy Farm API

## Application Status

✅ **Application is running successfully!**

- Server: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- API Docs: http://localhost:8080/api-docs
- Database: MySQL (localhost:3306/ziwa_db)

## Quick Start

### 1. Create an Admin User

First, register an admin user to access the API:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123",
    "role": "ADMIN",
    "fullName": "System Administrator"
  }'
```

### 2. Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }'
```

Save the token from the response. You'll need it for authenticated requests.

### 3. Use the Token for API Calls

Include the token in the Authorization header:

```bash
curl -X GET http://localhost:8080/api/cows \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Available Modules

### 1. Livestock Management (`/api/cows`)
- Register and manage cows
- Track breeding records
- Update cow status (ACTIVE, SOLD, DECEASED)

### 2. Milk Production (`/api/production`)
- Record daily milk production (morning & evening)
- View production trends
- Identify top producers
- Analyze cow productivity

### 3. Health Management (`/api/health`)
- Record vaccinations, treatments, and checkups
- Track withdrawal periods
- View upcoming vaccinations
- Monitor cows in withdrawal

### 4. Financial Management (`/api/financial/transactions`)
- Record income and expenses
- Calculate profit/loss
- View category breakdowns
- Analyze financial trends

### 5. Analytics & Dashboard (`/api/analytics`)
- Comprehensive dashboard with key metrics
- Production period comparisons
- Cross-module insights

### 6. Authentication (`/api/auth`)
- User registration
- Login with JWT tokens
- Token refresh
- Role-based access control (ADMIN, MANAGER, USER)

## Using Swagger UI

The easiest way to explore and test the API is through Swagger UI:

1. Open http://localhost:8080/swagger-ui/index.html in your browser
2. Click "Authorize" button at the top
3. Enter your JWT token in the format: `Bearer YOUR_TOKEN_HERE`
4. Click "Authorize" and "Close"
5. Now you can test any endpoint directly from the browser

## Example Workflow

### Register a Cow
```bash
curl -X POST http://localhost:8080/api/cows \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tagId": "COW-001",
    "breed": "Holstein",
    "dateOfBirth": "2020-01-15",
    "acquisitionDate": "2021-03-20",
    "status": "ACTIVE"
  }'
```

### Record Milk Production
```bash
curl -X POST http://localhost:8080/api/production \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cowId": 1,
    "date": "2026-04-10",
    "morningQuantity": 15.5,
    "eveningQuantity": 14.2,
    "notes": "Normal production"
  }'
```

### View Dashboard
```bash
curl -X GET http://localhost:8080/api/analytics/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## API Documentation

Complete API documentation is available at:
- Interactive: http://localhost:8080/swagger-ui/index.html
- JSON: http://localhost:8080/api-docs
- Markdown: `backend/docs/API_DOCUMENTATION.md`

## Security Features

- JWT-based authentication
- Role-based access control (RBAC)
- Password hashing with BCrypt
- XSS input sanitization
- Rate limiting on authentication endpoints (5 requests/minute)
- CSRF protection disabled (stateless API)

## Database

The application uses MySQL with the following configuration:
- Host: localhost:3306
- Database: ziwa_db
- User: php_user
- Password: Php_pass@2026

Database schema is automatically created/updated by Hibernate on startup.

## Testing

Run the test suite:
```bash
cd backend
./mvnw test
```

## Stopping the Application

To stop the running application, press `Ctrl+C` in the terminal where it's running.

## Next Steps

1. Create your admin user
2. Explore the API using Swagger UI
3. Register some cows
4. Record milk production
5. Add health records
6. Track financial transactions
7. View the dashboard for insights

## Support

For detailed API documentation, see `backend/docs/API_DOCUMENTATION.md`
