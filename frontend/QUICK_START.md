# Quick Start Guide

## Current Status

The login page has been redesigned with a modern split-screen layout featuring:
- Dark green theme matching the Energy Management Dashboard
- Company logo (Ziwa Dairy)
- Password visibility toggle
- Remember me checkbox
- Sign Up and Forgot Password buttons (currently placeholders)

## Known Issue: Network Error on Login

You're seeing "Network error. Please check your connection" because there's a mismatch between the password hashing in the database and what the backend expects.

## Quick Fix

### Option 1: Use Swagger UI to Register (Recommended)

1. Make sure the backend is running:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. Open Swagger UI in your browser:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

3. Find the "Authentication" section and click on "POST /api/auth/register"

4. Click "Try it out" and enter:
   ```json
   {
     "username": "admin",
     "password": "Admin@123",
     "fullName": "Admin User"
   }
   ```

5. Click "Execute"

6. If successful, you'll get a response with a JWT token

7. Now try logging in through the web UI with:
   - Username: `admin`
   - Password: `Admin@123`

### Option 2: Manual Database Insert

If registration fails, you can insert a user directly. Contact the backend developer for the correct BCrypt hash format.

## Testing the Frontend

1. Start the frontend dev server:
   ```bash
   cd frontend
   npm run dev
   ```

2. Open http://localhost:5173 in your browser

3. You should see the new login page design

## Features Implemented

✅ Modern split-screen login design
✅ Dark green color scheme
✅ Company logo SVG
✅ Password visibility toggle
✅ Remember me checkbox
✅ Error message display with helpful hints
✅ Responsive design (mobile-friendly)

## Features Pending

⏳ Sign Up page (currently shows alert)
⏳ Forgot Password flow (currently shows alert)
⏳ User registration through UI

## Troubleshooting

See `LOGIN_TROUBLESHOOTING.md` for detailed troubleshooting steps.

## Next Steps

1. Fix the password hashing issue in the backend
2. Implement Sign Up page
3. Implement Forgot Password flow
4. Add form validation feedback
5. Add loading states and animations
