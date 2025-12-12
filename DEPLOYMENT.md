# DocClassifier Backend - Render Deployment Guide

## 🚀 Quick Deploy to Render

### Prerequisites
- GitHub account
- Render account (free tier available)
- Your code pushed to GitHub

### Step 1: Push Your Code to GitHub

```bash
git add .
git commit -m "Add Docker configuration for Render deployment"
git push origin main
```

### Step 2: Deploy on Render

#### Option A: Deploy with Render Blueprint (render.yaml)

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Blueprint"**
3. Connect your GitHub repository
4. Render will automatically detect `render.yaml` and create all services

#### Option B: Manual Deployment

1. **Create MySQL Database**
   - Click **"New +"** → **"PostgreSQL"** (or use external MySQL)
   - Name: `docclassifier-db`
   - Plan: Free or Starter
   - Save the **Internal Database URL**

2. **Create RabbitMQ Instance**
   - Use [CloudAMQP](https://www.cloudamqp.com/) free tier
   - Or deploy RabbitMQ on another platform
   - Save the connection details

3. **Deploy Backend**
   - Click **"New +"** → **"Web Service"**
   - Connect your GitHub repository
   - Configure:
     - **Name**: `docclassifier-backend`
     - **Region**: Choose closest to you
     - **Branch**: `main`
     - **Root Directory**: `backend`
     - **Environment**: `Docker`
     - **Dockerfile Path**: `./Dockerfile`
     - **Plan**: Free or Starter

### Step 3: Configure Environment Variables

In your Render web service settings, add these environment variables:

#### Required Variables:

```env
# Server
PORT=8080

# Database (Use your Render PostgreSQL or external MySQL)
DATABASE_URL=jdbc:mysql://your-db-host:3306/docdb?createDatabaseIfNotExist=true
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# JWT Secret (GENERATE A STRONG SECRET!)
JWT_SECRET=your-very-long-and-secure-random-string-here

# RabbitMQ (Use CloudAMQP URL or your RabbitMQ instance)
RABBITMQ_HOST=your-rabbitmq-host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your-rabbitmq-user
RABBITMQ_PASSWORD=your-rabbitmq-password

# Hibernate
DDL_AUTO=update
SHOW_SQL=false

# CORS (Update with your frontend URL)
ALLOWED_ORIGINS=https://your-frontend.vercel.app,http://localhost:3000

# File Upload
LOCAL_STORAGE_PATH=/app/storage
```

#### Generate Secure JWT Secret:

```bash
# On Windows PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))

# On Linux/Mac
openssl rand -base64 64
```

### Step 4: Database Setup

If using PostgreSQL on Render (recommended for free tier):

1. Update `pom.xml` dependencies (PostgreSQL is already included)
2. Use this DATABASE_URL format:
   ```
   DATABASE_URL=jdbc:postgresql://your-db-url/dbname
   ```
3. The `spring.jpa.database-platform` will auto-detect PostgreSQL

If using external MySQL:
- Use the MySQL URL format as shown above
- Ensure the database is accessible from Render

### Step 5: Deploy!

1. Click **"Create Web Service"**
2. Render will:
   - Pull your code from GitHub
   - Build the Docker image
   - Deploy your application
3. Monitor the deployment logs
4. Your backend will be available at: `https://your-service-name.onrender.com`

### Step 6: Verify Deployment

Test your backend:

```bash
# Health check (if you have actuator)
curl https://your-service.onrender.com/actuator/health

# API test
curl https://your-service.onrender.com/api/auth/test
```

## 🔧 Troubleshooting

### Build Fails
- Check Java version matches (Java 21)
- Verify all dependencies in `pom.xml`
- Check Render build logs

### Database Connection Issues
- Verify DATABASE_URL format
- Check database credentials
- Ensure database allows connections from Render IP ranges

### File Upload Issues
- Storage is ephemeral on Render free tier
- Consider using AWS S3 or Cloudinary for production
- Update `FileStorageService` to use cloud storage

### RabbitMQ Connection Issues
- Verify RabbitMQ host and credentials
- Check CloudAMQP connection limits (free tier: 20 connections)

## 📊 Production Recommendations

### Database
- **For Production**: Use Render PostgreSQL Starter plan or external managed database
- **Free Tier**: Limited to 1GB storage, database may sleep

### File Storage
- Implement S3-compatible storage:
  - AWS S3
  - Cloudflare R2
  - Backblaze B2
  - DigitalOcean Spaces

### RabbitMQ
- **CloudAMQP**: Free tier available (Little Lemur plan)
- **Alternative**: Use Redis for simple message queuing

### Monitoring
- Enable Render metrics
- Add application health checks
- Set up error tracking (Sentry, etc.)

### Performance
- Upgrade to Starter plan ($7/month) for:
  - No cold starts
  - Custom domains
  - Better performance

## 🌐 Frontend Deployment (Vercel)

1. Deploy frontend to Vercel:
   ```bash
   cd frontend
   vercel --prod
   ```

2. Set environment variable:
   ```
   NEXT_PUBLIC_API_URL=https://your-backend.onrender.com/api
   ```

3. Update CORS in backend environment variables with your Vercel URL

## 🔐 Security Checklist

- [ ] Changed JWT_SECRET from default
- [ ] Updated database credentials
- [ ] Configured CORS with specific origins
- [ ] Disabled SQL logging in production (SHOW_SQL=false)
- [ ] Using HTTPS for all connections
- [ ] Set up database backups
- [ ] Configured rate limiting (if applicable)

## 📝 Auto-Deploy Setup

Enable auto-deploy from GitHub:

1. In Render Dashboard → Your Web Service
2. Go to **Settings** → **Build & Deploy**
3. Enable **"Auto-Deploy"**
4. Choose branch: `main`
5. Every push to main will trigger automatic deployment

## 💰 Cost Estimate

**Free Tier (Good for testing):**
- Backend: Free ($0)
- Database: Free PostgreSQL on Render ($0)
- RabbitMQ: CloudAMQP Free ($0)
- **Total: $0/month**

**Production Setup:**
- Backend: Starter ($7/month)
- Database: Render PostgreSQL Starter ($7/month)
- RabbitMQ: CloudAMQP Bunny ($9/month)
- **Total: ~$23/month**

## 🆘 Support

- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com/)
- Check deployment logs in Render Dashboard
- Review application logs for errors

## 🎉 Next Steps

After successful deployment:
1. Test all API endpoints
2. Upload a test document
3. Verify file storage works
4. Test user authentication
5. Monitor application performance
6. Set up CI/CD pipeline
7. Configure custom domain (optional)

---

**Deployment Date**: December 11, 2025
**Backend Version**: 0.0.1-SNAPSHOT
**Java Version**: 21
