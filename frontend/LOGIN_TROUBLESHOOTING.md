# Login Troubleshooting Guide

## Network Error: "Please check your connection"

This error occurs when the frontend cannot connect to the backend API. Here's how to fix it:

### Step 1: Check if Backend is Running

Open a terminal and run:
```bash
curl http://localhost:8080/api/auth/login
```

If you get a response (even an error), the backend is running. If you get "Connection refused", start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

### Step 2: Verify Backend URL

Check your `frontend/.env` file:
```
VITE_API_BASE_URL=http://localhost:8080
```

Make sure this matches where your backend is running.

### Step 3: Create a Test User

The backend needs at least one user to exist. Create one using curl:

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123",
    "fullName": "Test User"
  }'
```

Note: If you get an error, the database might not be set up. Check `backend/INFRASTRUCTURE_SETUP.md`.

### Step 4: Test Login

Try logging in with the test user:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123"
  }'
```

You should get a response with a JWT token.

### Step 5: Use These Credentials in the UI

Now try logging in through the web interface:
- Username: `testuser`
- Password: `Test@123`

## Common Issues

### CORS Errors
If you see CORS errors in the browser console, check `backend/src/main/java/com/example/ziwa/config/SecurityConfig.java` and ensure CORS is properly configured.

### Database Connection Issues
Check `backend/src/main/resources/application.properties` for database configuration. Make sure MySQL is running and the database exists.

### Port Already in Use
If port 8080 is already in use, either:
1. Stop the other application using port 8080
2. Change the backend port in `application.properties`
3. Update `VITE_API_BASE_URL` in `frontend/.env` to match

## Quick Test Credentials

After setting up the database, you can use:
- Username: `testuser`
- Password: `Test@123`

## Sign Up & Forgot Password

These features are currently placeholders and will show alerts. They need to be implemented:
- Sign Up: Should navigate to a registration page
- Forgot Password: Should navigate to a password reset flow

## Need More Help?

1. Check backend logs for errors
2. Check browser console (F12) for frontend errors
3. Verify database is running and accessible
4. Review `backend/GETTING_STARTED.md` for setup instructions
