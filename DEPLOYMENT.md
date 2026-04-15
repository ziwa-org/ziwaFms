# Deployment Guide

## Frontend - Vercel

### Prerequisites
1. Install Vercel CLI: `npm i -g vercel`
2. Or use Vercel dashboard at https://vercel.com

### Deploy via CLI
```bash
cd frontend
vercel
```

### Deploy via GitHub
1. Push your code to GitHub
2. Go to https://vercel.com/new
3. Import your repository
4. Vercel will auto-detect Vite configuration
5. Add environment variable:
   - `VITE_API_BASE_URL`: `https://ziwa-backend.onrender.com`
6. Click Deploy

### Environment Variables (Vercel)
| Variable | Value | Notes |
|----------|-------|-------|
| `VITE_API_BASE_URL` | `https://ziwa-backend.onrender.com` | Backend API URL |

---

## Backend - Render

### Prerequisites
1. Create a Render account at https://render.com
2. Connect your GitHub repository

### Deploy via Dashboard
1. Go to https://dashboard.render.com/new
2. Select "Web Service"
3. Connect your GitHub repository
4. Configure:
   - **Name**: `ziwa-backend`
   - **Region**: Oregon (or nearest to you)
   - **Runtime**: Java 17
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/ziwa-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free

5. Add Environment Variables:
   | Variable | Value | Notes |
   |----------|-------|-------|
   | `SPRING_PROFILES_ACTIVE` | `prod` | Activates production settings |
   | `DATABASE_URL` | MySQL connection string | e.g., `jdbc:mysql://host:3306/ziwa_db` |
   | `DATABASE_USERNAME` | Your DB username | |
   | `DATABASE_PASSWORD` | Your DB password | |
   | `JWT_SECRET` | Secure random string | Min 32 characters |
   | `CORS_ORIGINS` | `https://your-frontend.vercel.app` | Your Vercel frontend URL |

### Database Setup
Render supports MySQL via:
1. **Render Managed MySQL** (recommended for free tier)
   - Create a new MySQL instance in Render dashboard
   - Use the connection details for DATABASE_URL
2. **External MySQL** (e.g., PlanetScale, AWS RDS)
   - Use your existing MySQL connection string

### Deploy via render.yaml
The `render.yaml` file is already configured. Simply push to GitHub and Render will auto-deploy.

---

## Important Security Notes

### Before Going Live
1. Change `JWT_SECRET` to a secure random value (min 32 characters)
2. Update database credentials
3. Update `CORS_ORIGINS` to your actual frontend domain
4. Review all default passwords in `application.properties`

### Production Checklist
- [ ] Set all environment variables in Render
- [ ] Enable HTTPS on both frontend and backend
- [ ] Verify CORS is configured with correct origins
- [ ] Test authentication flow in production
- [ ] Set `spring.jpa.show-sql=false` (already done in prod)
- [ ] Remove debug endpoints if any

---

## Troubleshooting

### CORS Errors
Ensure `CORS_ORIGINS` in Render matches your frontend URL exactly (including protocol).

### Database Connection Failed
- Verify `DATABASE_URL` format: `jdbc:mysql://host:port/database`
- Check database is accessible from Render's network
- Verify credentials are correct

### Build Failed
- Ensure Java 17 is selected in Render
- Check Maven wrapper is executable: `chmod +x mvnw`
