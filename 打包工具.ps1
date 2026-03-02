# SharedLab 项目打包脚本
$ErrorActionPreference = "Stop"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "    SharedLab 项目打包工具" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$rootPath = "C:\SharedLab"
$outputPath = Join-Path $rootPath "打包输出"

if (Test-Path $outputPath) {
    Write-Host "清理旧的打包输出目录..." -ForegroundColor Yellow
    Remove-Item $outputPath -Recurse -Force
}
New-Item -ItemType Directory -Path $outputPath | Out-Null

# 1. 创建源码包
Write-Host ""
Write-Host "[ 1/2 ] 正在创建源码包..." -ForegroundColor Green

$sourcePath = Join-Path $outputPath "SharedLab-Source"
New-Item -ItemType Directory -Path $sourcePath | Out-Null

Write-Host "  - 复制后端源码..." -ForegroundColor Gray
$backendSource = Join-Path $sourcePath "backend"
New-Item -ItemType Directory -Path $backendSource | Out-Null
Copy-Item (Join-Path $rootPath "backend\src") (Join-Path $backendSource "src") -Recurse
Copy-Item (Join-Path $rootPath "backend\pom.xml") $backendSource
Copy-Item (Join-Path $rootPath "backend\mvnw.cmd") $backendSource -ErrorAction SilentlyContinue
Copy-Item (Join-Path $rootPath "backend\insert-test-data.sql") $backendSource -ErrorAction SilentlyContinue

Write-Host "  - 复制前端源码..." -ForegroundColor Gray
$frontendSource = Join-Path $sourcePath "frontend"
New-Item -ItemType Directory -Path $frontendSource | Out-Null
Copy-Item (Join-Path $rootPath "frontend\src") (Join-Path $frontendSource "src") -Recurse
Copy-Item (Join-Path $rootPath "frontend\index.html") $frontendSource
Copy-Item (Join-Path $rootPath "frontend\package.json") $frontendSource
Copy-Item (Join-Path $rootPath "frontend\vite.config.js") $frontendSource

Write-Host "  - 复制数据库文件..." -ForegroundColor Gray
if (Test-Path (Join-Path $rootPath "database")) {
    $databaseSource = Join-Path $sourcePath "database"
    Copy-Item (Join-Path $rootPath "database") $databaseSource -Recurse
}

Write-Host "  - 复制文档..." -ForegroundColor Gray
$docsSource = Join-Path $sourcePath "docs"
New-Item -ItemType Directory -Path $docsSource | Out-Null
$localPath = Join-Path $rootPath "local"
if (Test-Path $localPath) {
    Get-ChildItem $localPath -Filter "*.md" | ForEach-Object {
        Copy-Item $_.FullName -Destination $docsSource
    }
}

Write-Host "  - 压缩源码包..." -ForegroundColor Gray
$sourceZip = Join-Path $outputPath "SharedLab-Source.zip"
Compress-Archive -Path $sourcePath -DestinationPath $sourceZip -CompressionLevel Optimal
$sourceSize = [math]::Round((Get-Item $sourceZip).Length / 1MB, 2)
Write-Host "  ✓ 源码包创建完成: $sourceSize MB" -ForegroundColor Green

# 2. 创建部署包
Write-Host ""
Write-Host "[ 2/2 ] 正在创建部署包..." -ForegroundColor Green

$deployPath = Join-Path $outputPath "SharedLab-Deploy"
New-Item -ItemType Directory -Path $deployPath | Out-Null

Write-Host "  - 打包后端部署文件..." -ForegroundColor Gray
$backendDeploy = Join-Path $deployPath "backend"
New-Item -ItemType Directory -Path $backendDeploy | Out-Null

$jarFile = Join-Path $rootPath "backend\target\shared-lab-platform-1.0.0.jar"
if (Test-Path $jarFile) {
    Copy-Item $jarFile $backendDeploy
    Write-Host "    ✓ JAR文件已复制" -ForegroundColor Gray
} else {
    Write-Host "    ✗ 警告: 未找到JAR文件" -ForegroundColor Red
}

$appPropPath = Join-Path $rootPath "backend\src\main\resources\application.properties"
Copy-Item $appPropPath $backendDeploy

$backendDeployMd = Join-Path $rootPath "BACKEND_DEPLOY.md"
if (Test-Path $backendDeployMd) {
    Copy-Item $backendDeployMd (Join-Path $backendDeploy "README.md")
}

Write-Host "  - 打包前端部署文件..." -ForegroundColor Gray
$frontendDeploy = Join-Path $deployPath "frontend"
$frontendDistPath = Join-Path $rootPath "frontend\dist"
if (Test-Path $frontendDistPath) {
    Copy-Item $frontendDistPath $frontendDeploy -Recurse
    Write-Host "    ✓ 前端构建文件已复制" -ForegroundColor Gray
} else {
    Write-Host "    ✗ 警告: 未找到前端构建文件" -ForegroundColor Red
}

$frontendDeployMd = Join-Path $rootPath "FRONTEND_DEPLOY.md"
if (Test-Path $frontendDeployMd) {
    Copy-Item $frontendDeployMd (Join-Path $deployPath "frontend\README.md")
}

$deployReadme = Join-Path $rootPath "DEPLOY_README.md"
if (Test-Path $deployReadme) {
    Copy-Item $deployReadme (Join-Path $deployPath "README.md")
}

$dbInitSql = Join-Path $rootPath "backend\insert-test-data.sql"
if (Test-Path $dbInitSql) {
    Copy-Item $dbInitSql (Join-Path $deployPath "database-init.sql")
}

Write-Host "  - 压缩部署包..." -ForegroundColor Gray
$deployZip = Join-Path $outputPath "SharedLab-Deploy.zip"
Compress-Archive -Path $deployPath -DestinationPath $deployZip -CompressionLevel Optimal
$deploySize = [math]::Round((Get-Item $deployZip).Length / 1MB, 2)
Write-Host "  ✓ 部署包创建完成: $deploySize MB" -ForegroundColor Green

# 完成
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "    打包完成！" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "输出目录: $outputPath" -ForegroundColor White
Write-Host ""
Write-Host "生成的文件:" -ForegroundColor White
Write-Host "  1. SharedLab-Source.zip  - 项目源码包 ($sourceSize MB)" -ForegroundColor Yellow
Write-Host "  2. SharedLab-Deploy.zip  - 项目部署包 ($deploySize MB)" -ForegroundColor Yellow
Write-Host ""
Write-Host "提交说明:" -ForegroundColor White
Write-Host "  - 材料1（项目源码）: 提交 SharedLab-Source.zip" -ForegroundColor Cyan
Write-Host "  - 材料2（项目部署文件）: 提交 SharedLab-Deploy.zip" -ForegroundColor Cyan
Write-Host ""

explorer.exe $outputPath

Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

