print("=" * 50)
print("测试开始 - 验证Python环境")
print("=" * 50)

print("\n[1/3] 检查Python版本...")
import sys
print(f"Python版本: {sys.version}")
print("✓ Python正常")

print("\n[2/3] 检查Selenium库...")
try:
    import selenium
    print(f"Selenium版本: {selenium.__version__}")
    print("✓ Selenium已安装")
except ImportError as e:
    print("✗ Selenium未安装!")
    print("  请运行: pip install selenium")
    input("\n按任意键退出...")
    sys.exit(1)

print("\n[3/3] 检查ChromeDriver...")
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
import os

chromedriver_path = r'E:\chromedriver-win64\chromedriver.exe'
print(f"ChromeDriver路径: {chromedriver_path}")

if os.path.exists(chromedriver_path):
    print("✓ ChromeDriver文件存在")
else:
    print("✗ ChromeDriver文件不存在")
    print("\n尝试使用系统PATH中的ChromeDriver...")

print("\n[测试] 尝试启动Chrome浏览器...")
driver = None
try:
    if os.path.exists(chromedriver_path):
        service = Service(executable_path=chromedriver_path)
        driver = webdriver.Chrome(service=service)
    else:
        driver = webdriver.Chrome()
    
    print("✓ Chrome浏览器启动成功!")
    print("\n正在访问测试页面...")
    driver.get("https://www.baidu.com")
    print(f"页面标题: {driver.title}")
    print("\n测试成功! 5秒后自动关闭浏览器...")
    
    import time
    time.sleep(5)
    
except Exception as e:
    print(f"\n✗ 启动失败: {type(e).__name__}")
    print(f"错误详情: {str(e)}")
    print("\n可能的原因:")
    print("  1. ChromeDriver版本与Chrome浏览器不匹配")
    print("  2. Chrome浏览器未安装")
    print("  3. 需要更新ChromeDriver")
    import traceback
    traceback.print_exc()
finally:
    if driver:
        driver.quit()
    print("\n=" * 50)
    print("环境检查完成")
    print("=" * 50)
    input("\n按任意键退出...")
