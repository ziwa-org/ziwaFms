# Authentication Fix Applied

## What Was Fixed

### Issue
The registration endpoint was returning a 500 Internal Server Error because the `UserService` was creating its own `BCryptPasswordEncoder` instance instead of using the one provided by Spring's dependency injection.

### Fix Applied
Updated `backend/src/main/java/com/example/ziwa/service/UserService.java`:
- Changed from: `private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();`
- Changed to: `private final PasswordEncoder passwordEncoder;`
- This allows Spring to inject the PasswordEncoder bean configured in SecurityConfig

## How to Apply the Fix

### 1. Restart the Backend

Stop the current backend process (Ctrl+C in the terminal where it's running), then restart:

```bash
cd backend
./mvnw spring-boot:run
```

Wait for the message: "Started ZiwaApplication in X seconds"

### 2. Test Registration via Swagger UI

1. Open http://localhost:8080/swagger-ui/index.html

2. Navigate to **Authentication** section

3. Click on **POST /api/auth/register**

4. Click **"Try it out"**

5. Enter the following JSON:
```json
{
  "username": "admin",
  "password": "Admin@123",
  "fullName": "Admin User"
}
```

6. Click **"Execute"**

7. You should now get a **201 Created** response with a JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "admin",
    "fullName": "Admin User",
    "role": "USER"
  }
}
```

### 3. Test Login via Web UI

1. Make sure the frontend is running:
```bash
cd frontend
npm run dev
```

2. Open http://localhost:5173

3. Enter your credentials:
   - Username: `admin`
   - Password: `Admin@123`

4. Click **"Sign In"**

5. You should be redirected to the dashboard!

## Creating Additional Users

### Via Swagger UI (Recommended)

Use the same registration endpoint to create more users:

```json
{
  "username": "manager",
  "password": "Manager@123",
  "role": "MANAGER",
  "fullName": "Farm Manager"
}
```

```json
{
  "username": "user",
  "password": "User@123",
  "role": "USER",
  "fullName": "Regular User"
}
```

### User Roles

- **ADMIN**: Full access to all features
- **MANAGER**: Read/write access to all modules
- **USER**: Read-only access

## Troubleshooting

### Still Getting 500 Error?

1. Check backend logs for the actual error message
2. Verify MySQL is running: `mysql -u php_user -pPhp_pass@2026 -e "SELECT 1;"`
3. Check database exists: `mysql -u php_user -pPhp_pass@2026 -e "SHOW DATABASES LIKE 'ziwa_db';"`
4. Verify tables exist: `mysql -u php_user -pPhp_pass@2026 ziwa_db -e "SHOW TABLES;"`

### Username Already Exists?

If you get a "Username already exists" error, either:
1. Use a different username
2. Delete the existing user from the database:
```bash
mysql -u php_user -pPhp_pass@2026 ziwa_db -e "DELETE FROM users WHERE username='admin';"
```

### Network Error in Frontend?

1. Verify backend is running on port 8080
2. Check `frontend/.env` has: `VITE_API_BASE_URL=http://localhost:8080`
3. Restart the frontend dev server

## Testing the Complete Flow

1. **Register** a user via Swagger UI
2. **Login** via the web UI
3. **Navigate** to different pages (Dashboard, Livestock, Production, etc.)
4. **Logout** and login again to verify token persistence

## Next Steps

Now that authentication is working:
1. ✅ Login page redesigned
2. ✅ Authentication fixed
3. ⏳ Implement Sign Up page in the frontend
4. ⏳ Implement Forgot Password flow
5. ⏳ Add user profile management
6. ⏳ Add role-based UI restrictions

## Files Modified

- `backend/src/main/java/com/example/ziwa/service/UserService.java` - Fixed PasswordEncoder injection
