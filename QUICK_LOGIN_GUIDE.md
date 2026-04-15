# Quick Login Guide

## TL;DR

1. **Clear users** (if any exist):
   ```bash
   mysql -u php_user -pPhp_pass@2026 ziwa_db -e "TRUNCATE TABLE users;"
   ```

2. **Restart backend**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   
   Wait for: "Default admin user created successfully!"

3. **Login credentials**:
   - Username: `admin`
   - Password: `Admin@123`

4. **Open web UI**: http://localhost:5173

5. **Login and enjoy!** 🎉

## Alternative User

- Username: `testuser`
- Password: `Test@123`

## That's It!

The backend now automatically creates users on startup. No more manual SQL, no more Swagger UI registration, no more 500 errors.

Just restart the backend and login!
