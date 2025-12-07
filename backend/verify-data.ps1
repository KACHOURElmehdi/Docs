# Verify Database Data
$baseUrl = "http://localhost:8080/api"

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "   VERIFYING DATABASE DATA" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# Login
$loginBody = @{
    email = "admin@test.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "Login successful!" -ForegroundColor Green
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
}

# Get categories
Write-Host "`nFetching categories..." -ForegroundColor Yellow
try {
    $categories = Invoke-RestMethod -Uri "$baseUrl/categories" -Method Get -Headers $headers
    if ($categories.totalElements) {
        Write-Host "Total categories: $($categories.totalElements)" -ForegroundColor Green
        Write-Host "`nFirst 10 categories:" -ForegroundColor Cyan
        $categories.content | Select-Object -First 10 | ForEach-Object {
            Write-Host "  - $($_.name): $($_.description)" -ForegroundColor White
        }
    } elseif ($categories.Count) {
        Write-Host "Total categories: $($categories.Count)" -ForegroundColor Green
        Write-Host "`nFirst 10 categories:" -ForegroundColor Cyan
        $categories | Select-Object -First 10 | ForEach-Object {
            Write-Host "  - $($_.name): $($_.description)" -ForegroundColor White
        }
    } else {
        Write-Host "No categories found or empty result" -ForegroundColor Yellow
        Write-Host "Raw response: $categories" -ForegroundColor Gray
    }
} catch {
    Write-Host "Failed to fetch categories: $($_.Exception.Message)" -ForegroundColor Red
}

# Get tags
Write-Host "`nFetching tags..." -ForegroundColor Yellow
try {
    $tags = Invoke-RestMethod -Uri "$baseUrl/tags" -Method Get -Headers $headers
    if ($tags.totalElements) {
        Write-Host "Total tags: $($tags.totalElements)" -ForegroundColor Green
        Write-Host "`nFirst 10 tags:" -ForegroundColor Cyan
        $tags.content | Select-Object -First 10 | ForEach-Object {
            Write-Host "  - $($_.name)" -ForegroundColor White
        }
    } elseif ($tags.Count) {
        Write-Host "Total tags: $($tags.Count)" -ForegroundColor Green
        Write-Host "`nFirst 10 tags:" -ForegroundColor Cyan
        $tags | Select-Object -First 10 | ForEach-Object {
            Write-Host "  - $($_.name)" -ForegroundColor White
        }
    } else {
        Write-Host "No tags found or empty result" -ForegroundColor Yellow
        Write-Host "Raw response: $tags" -ForegroundColor Gray
    }
} catch {
    Write-Host "Failed to fetch tags: $($_.Exception.Message)" -ForegroundColor Red
}

# Test creating a new tag
Write-Host "`n`nTesting tag creation..." -ForegroundColor Yellow
$newTagBody = @{
    name = "Test Tag - $(Get-Date -Format 'HHmmss')"
    color = "#FF5733"
} | ConvertTo-Json

try {
    $newTag = Invoke-RestMethod -Uri "$baseUrl/tags" -Method Post -Body $newTagBody -Headers $headers -ContentType "application/json"
    Write-Host "SUCCESS! Tag created: $($newTag.name) (ID: $($newTag.id))" -ForegroundColor Green
} catch {
    Write-Host "FAILED to create tag: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
}

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "   VERIFICATION COMPLETE" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan
