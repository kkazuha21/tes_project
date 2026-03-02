$file = "C:\SharedLab\frontend\src\views\admin\SystemLogs.vue"
$content = Get-Content $file -Raw -Encoding UTF8

# 替换 level 行
$content = $content -replace "    const level = levels\[Math\.floor\(Math\.random\(\) \* levels\.length\)\]", "    // 使用 ID 确定内容，而不是随机`n    const level = levels[(i - 1) % levels.length]"

# 替换 hoursAgo 行  
$content = $content -replace "    const hoursAgo = \(count - i\) \* Math\.random\(\) \* 2 // 每条日志间隔随机 0-2 小时", "    const hoursAgo = (count - i) * 1.5 // 每条日志间隔 1.5 小时"

# 替换 type 行
$content = $content -replace "      type: types\[Math\.floor\(Math\.random\(\) \* types\.length\)\]", "      type: types[(i - 1) % types.length]"

# 替换 message 行
$content = $content -replace "      message: messages\[Math\.floor\(Math\.random\(\) \* messages\.length\)\]", "      message: messages[(i - 1) % messages.length]"

# 替换 user 行
$content = $content -replace "      user: users\[Math\.floor\(Math\.random\(\) \* users\.length\)\]", "      user: users[(i - 1) % users.length]"

# 替换 ip 行
$content = $content -replace "      ip: ``192\.168\.1\.\`\$\{Math\.floor\(Math\.random\(\) \* 255\)\}``", "      ip: ``192.168.1.`${((i - 1) % 255) + 1}``"

[System.IO.File]::WriteAllText($file, $content, (New-Object System.Text.UTF8Encoding $false))
Write-Host "修改完成"
