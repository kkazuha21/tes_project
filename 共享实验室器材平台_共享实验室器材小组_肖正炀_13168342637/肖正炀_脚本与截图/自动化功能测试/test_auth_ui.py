from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
import time

# 简单 UI 自动化脚本示例
print("=" * 50)
print("开始执行登录自动化测试...")
print("=" * 50)

driver = None
try:
    print("\n[1/5] 正在启动 Chrome 浏览器...")
    # 指定 ChromeDriver 路径
    service = Service(executable_path=r'E:\chromedriver-win64\chromedriver.exe')
    driver = webdriver.Chrome(service=service)
    print("✓ 浏览器启动成功")
    
    print("\n[2/5] 正在访问登录页面: http://localhost:5173/login")
    driver.get("http://localhost:5173/login")
    print("✓ 页面加载完成")
    
    # 等待页面加载
    time.sleep(2)
    
    print("\n[3/5] 正在填写登录信息...")
    # Element Plus 使用 placeholder 定位
    username_field = driver.find_element(By.CSS_SELECTOR, "input[placeholder='请输入用户名']")
    username_field.send_keys("admin")
    print("  - 用户名: admin")
    
    password_field = driver.find_element(By.CSS_SELECTOR, "input[placeholder='请输入密码']")
    password_field.send_keys("123456")
    print("  - 密码: ******")
    
    print("\n[4/5] 正在点击登录按钮...")
    # 点击登录按钮
    login_button = driver.find_element(By.CSS_SELECTOR, "button.el-button--primary")
    login_button.click()
    
    print("\n[5/5] 等待页面跳转...")
    time.sleep(3)
    
    # 验证跳转
    current_url = driver.current_url
    print(f"  当前 URL: {current_url}")
    
    if "dashboard" in current_url or "admin" in current_url:
        print("\n" + "=" * 50)
        print("✓ 测试通过！成功登录并跳转到管理页面")
        print("=" * 50)
    else:
        print("\n" + "=" * 50)
        print(f"✗ 测试失败：未跳转到预期页面")
        print(f"  预期 URL 包含: dashboard 或 admin")
        print(f"  实际 URL: {current_url}")
        print("=" * 50)
    
    print("\n按任意键关闭浏览器...")
    input()
    
except Exception as e:
    print("\n" + "=" * 50)
    print(f"✗ 测试失败: {type(e).__name__}")
    print(f"  错误详情: {str(e)}")
    print("=" * 50)
    print("\n可能的原因:")
    print("  1. 前端服务未启动 (http://localhost:5173)")
    print("  2. 页面元素 ID 或选择器不匹配")
    print("  3. ChromeDriver 版本与 Chrome 浏览器不匹配")
    import traceback
    print("\n详细错误信息:")
    traceback.print_exc()
finally:
    if driver:
        print("\n正在关闭浏览器...")
        driver.quit()
        print("✓ 浏览器已关闭")
    print("\n测试执行完毕")
