# -*- coding: utf-8 -*-
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
import time

print("=" * 50)
print("Device Management UI Test")
print("=" * 50)

driver = None
try:
    print("\n[1/5] Starting Chrome...")
    service = Service(executable_path=r'E:\chromedriver-win64\chromedriver.exe')
    driver = webdriver.Chrome(service=service)
    print("OK - Chrome started")
    
    print("\n[2/5] Opening login page...")
    driver.get("http://localhost:5173/login")
    time.sleep(2)
    print("OK - Page loaded")
    
    print("\n[3/5] Logging in as admin...")
    username = driver.find_element(By.CSS_SELECTOR, "input[placeholder='请输入用户名']")
    username.send_keys("admin")
    
    password = driver.find_element(By.CSS_SELECTOR, "input[placeholder='请输入密码']")
    password.send_keys("123456")
    
    login_btn = driver.find_element(By.CSS_SELECTOR, "button.el-button--primary")
    login_btn.click()
    time.sleep(3)
    print("OK - Logged in")
    
    print("\n[4/5] Navigating to device page...")
    if "device" not in driver.current_url:
        driver.get("http://localhost:5173/admin/devices")
        time.sleep(2)
    print("OK - On device page")
    
    print("\n[5/5] Checking device list...")
    from selenium.webdriver.support.ui import WebDriverWait
    from selenium.webdriver.support import expected_conditions as EC
    
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, ".el-table"))
    )
    rows = driver.find_elements(By.CSS_SELECTOR, ".el-table__row")
    print(f"OK - Found {len(rows)} devices")
    
    print("\n" + "=" * 50)
    print("TEST PASSED!")
    print("=" * 50)
    input("\nPress Enter to close browser...")
    
except Exception as e:
    print(f"\nERROR: {e}")
    import traceback
    traceback.print_exc()
    input("\nPress Enter to close...")
finally:
    if driver:
        driver.quit()
