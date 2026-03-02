import sys
import traceback

# 写入文件而不是打印到控制台
with open('diagnose_result.txt', 'w', encoding='utf-8') as f:
    f.write("=== Python 环境诊断 ===\n\n")
    f.write(f"Python 版本: {sys.version}\n")
    f.write(f"Python 路径: {sys.executable}\n\n")
    
    f.write("--- 测试 selenium 导入 ---\n")
    try:
        import selenium
        f.write(f"✓ selenium 导入成功\n")
        f.write(f"  版本: {selenium.__version__}\n")
        f.write(f"  路径: {selenium.__file__}\n")
    except Exception as e:
        f.write(f"✗ selenium 导入失败: {e}\n")
        f.write(traceback.format_exc())
        f.write("\n解决方案: pip install selenium\n")
    
    f.write("\n--- 测试 webdriver 导入 ---\n")
    try:
        from selenium import webdriver
        f.write(f"✓ webdriver 导入成功\n")
    except Exception as e:
        f.write(f"✗ webdriver 导入失败: {e}\n")
        f.write(traceback.format_exc())
    
    f.write("\n--- 测试 ChromeDriver ---\n")
    try:
        import subprocess
        result = subprocess.run(['chromedriver', '--version'], 
                              capture_output=True, text=True, timeout=5)
        if result.returncode == 0:
            f.write(f"✓ ChromeDriver 可用\n")
            f.write(f"  版本: {result.stdout.strip()}\n")
        else:
            f.write(f"✗ ChromeDriver 执行失败\n")
            f.write(f"  stderr: {result.stderr}\n")
    except FileNotFoundError:
        f.write("✗ ChromeDriver 未找到（不在 PATH 中）\n")
    except Exception as e:
        f.write(f"✗ ChromeDriver 检查失败: {e}\n")
        f.write(traceback.format_exc())
    
    f.write("\n=== 诊断完成 ===\n")

print("诊断结果已写入 diagnose_result.txt")
