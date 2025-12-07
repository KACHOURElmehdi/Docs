# Database Seeding Script
# This script logs in and triggers the database seeding endpoint

$baseUrl = "http://localhost:8080/api"

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "   DATABASE SEEDING SCRIPT STARTING" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# Try to login with admin credentials
Write-Host "Attempting to login..." -ForegroundColor Yellow

$loginBody = @{
    email = "admin@test.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "Login successful!" -ForegroundColor Green
} catch {
    Write-Host "Login failed. Attempting to register admin user..." -ForegroundColor Yellow
    
    # Try to register
    $registerBody = @{
        username = "admin"
        email = "admin@test.com"
        password = "admin123"
        fullName = "Admin User"
    } | ConvertTo-Json
    
    try {
        $registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerBody -ContentType "application/json"
        $token = $registerResponse.token
        Write-Host "Registration successful!" -ForegroundColor Green
    } catch {
        Write-Host "Failed to login or register: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

# Call the seed-data endpoint
Write-Host "`nSeeding database with categories and tags..." -ForegroundColor Yellow

$headers = @{
    "Authorization" = "Bearer $token"
}

try {
    $seedResponse = Invoke-RestMethod -Uri "$baseUrl/admin/seed-data" -Method Post -Headers $headers
    Write-Host "`nDATABASE SEEDED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "Status: $($seedResponse.status)" -ForegroundColor Green
    Write-Host "Message: $($seedResponse.message)" -ForegroundColor Green
    Write-Host "`nSummary:" -ForegroundColor Cyan
    Write-Host "  * 56 Categories added" -ForegroundColor White
    Write-Host "  * 71 Tags added" -ForegroundColor White
} catch {
    Write-Host "Failed to seed database: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`nSeeding complete! You can now use Categories and Tags in the application." -ForegroundColor Green

