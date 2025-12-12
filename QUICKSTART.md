# 🚀 Quick Start: Deploy to Render in 5 Minutes

## Step-by-Step Guide

### 1. Prerequisites ✅
- [ ] Push your code to GitHub
- [ ] Create a free account on [Render](https://render.com)
- [ ] Create a free CloudAMQP account for RabbitMQ

### 2. Set Up External Services (5 min)

#### A. CloudAMQP (Free RabbitMQ)
1. Go to [cloudamqp.com](https://www.cloudamqp.com/)
2. Sign up for free
3. Create a new instance (Little Lemur plan - FREE)
4. Copy the connection details:
   - Host
   - Username
   - Password
   - Port (usually 5672)

#### B. Database Options

**Option 1: Render PostgreSQL (Recommended - Free)**
- Render will create it automatically with the blueprint
- Or manually: Dashboard → New PostgreSQL → Create

**Option 2: External MySQL**
- Use [PlanetScale](https://planetscale.com/) (Free tier)
- Or [Aiven MySQL](https://aiven.io/) (Free tier)

### 3. Deploy to Render

#### Method 1: One-Click Deploy with render.yaml (Easiest)

1. **Push code to GitHub**:
   ```bash
   git add .
   git commit -m "Ready for Render deployment"
   git push origin main
   ```

2. **Create Web Service on Render**:
   - Go to [Render Dashboard](https://dashboard.render.com/)
   - Click **"New +"** → **"Web Service"**
   - Click **"Connect GitHub"**
   - Select your repository
   - Render will detect your Dockerfile

3. **Configure the service**:
   - **Name**: `docclassifier-backend`
   - **Region**: Choose your region
   - **Branch**: `main`
   - **Root Directory**: `backend`
   - **Environment**: Docker
   - **Dockerfile Path**: `./Dockerfile`
   - **Plan**: Free

4. **Add Environment Variables** (Click "Advanced" → "Add Environment Variable"):

   Copy these one by one:

   ```
   PORT=8080
   ```

   ```
   DATABASE_URL=<your-postgres-or-mysql-url>
   ```

   ```
   DB_USERNAME=<your-db-username>
   ```

   ```
   DB_PASSWORD=<your-db-password>
   ```

   ```
   JWT_SECRET=<generate-with-command-below>
   ```

   ```
   RABBITMQ_HOST=<your-cloudamqp-host>
   ```

   ```
   RABBITMQ_PORT=5672
   ```

   ```
   RABBITMQ_USERNAME=<your-cloudamqp-username>
   ```

   ```
   RABBITMQ_PASSWORD=<your-cloudamqp-password>
   ```

   ```
   DDL_AUTO=update
   ```

   ```
   SHOW_SQL=false
   ```

   ```
   ALLOWED_ORIGINS=http://localhost:3000
   ```

5. **Generate JWT Secret**:
   
   PowerShell (Windows):
   ```powershell
   -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | % {[char]$_})
   ```

   Or use: https://www.grc.com/passwords.htm (Copy the 63 random alpha-numeric)

6. **Click "Create Web Service"**

7. **Wait for deployment** (5-10 minutes first time)
   - Render will build your Docker image
   - Deploy the container
   - Monitor logs in real-time

### 4. Verify Deployment ✅

Once deployed, test your backend:

```bash
# Replace with your Render URL
curl https://your-app.onrender.com/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### 5. Get Your Backend URL

Your backend is now live at:
```
https://your-app-name.onrender.com
```

### 6. Deploy Frontend (Optional)

If deploying frontend to Vercel:

```bash
cd frontend
npm install -g vercel
vercel --prod
```

Set environment variable in Vercel:
```
NEXT_PUBLIC_API_URL=https://your-backend.onrender.com/api
```

Update CORS in Render:
```
ALLOWED_ORIGINS=https://your-frontend.vercel.app,http://localhost:3000
```

## 🎯 Important Notes

### Free Tier Limitations
- ⚠️ Service sleeps after 15 min of inactivity
- ⏱️ Cold start takes ~30-60 seconds
- 💾 File storage is ephemeral (uploaded files lost on restart)
- ✅ Perfect for testing and development

### Production Ready
Upgrade to Starter plan ($7/month) for:
- ✅ No cold starts
- ✅ Always online
- ✅ Custom domains
- ✅ Better performance

## 🆘 Troubleshooting

### Build Failed?
- Check build logs in Render dashboard
- Verify Java version matches (21)
- Ensure all files committed to git

### Can't Connect to Database?
- Verify DATABASE_URL format
- Check credentials
- For Render PostgreSQL, use internal URL

### Application Crashes?
- Check application logs in Render
- Verify all environment variables set
- Check RabbitMQ credentials

### Health Check Failing?
- Wait for build to complete (10-15 min first time)
- Check logs for errors
- Verify actuator is accessible: `/actuator/health`

## 📊 Monitor Your App

In Render Dashboard you can:
- View real-time logs
- Check metrics (CPU, Memory)
- See deployment history
- Monitor health checks

## 🎉 You're Done!

Your backend is now:
- ✅ Deployed on Render
- ✅ Connected to database
- ✅ Using RabbitMQ for async processing
- ✅ Secured with JWT
- ✅ Auto-deploying on git push

Test your API and start using it!

---

**Need help?** Check DEPLOYMENT.md for detailed instructions.
