import requests
import json
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api"

class BookingFlowTest:
    def __init__(self):
        self.admin_token = None
        self.student_token = None
        self.booking_id = None
        
    def login(self, username, password):
        """用户登录"""
        data = {"username": username, "password": password}
        resp = requests.post(f"{BASE_URL}/auth/login", json=data)
        if resp.status_code == 200:
            return resp.json()["token"]
        else:
            print(f"❌ 登录失败 [{username}]: {resp.status_code} - {resp.text}")
            return None
    
    def test_1_login(self):
        """测试1: 用户登录"""
        print("\n=== 测试1: 用户登录 ===")
        
        # 管理员登录
        self.admin_token = self.login("admin", "123456")
        if self.admin_token:
            print(f"✅ 管理员登录成功")
        
        # 学生登录
        self.student_token = self.login("student1", "123456")
        if self.student_token:
            print(f"✅ 学生登录成功")
        
        return self.admin_token and self.student_token
    
    def test_2_get_available_devices(self):
        """测试2: 查询可用设备"""
        print("\n=== 测试2: 查询可用设备 ===")
        
        headers = {"Authorization": f"Bearer {self.student_token}"}
        resp = requests.get(f"{BASE_URL}/devices?status=AVAILABLE", headers=headers)
        
        if resp.status_code == 200:
            devices = resp.json()
            print(f"✅ 查询成功，找到 {len(devices)} 个可用设备")
            if len(devices) > 0:
                print(f"   设备示例: ID={devices[0].get('id')}, 名称={devices[0].get('name')}")
                return True
        else:
            print(f"❌ 查询失败: {resp.status_code} - {resp.text}")
        return False
    
    def test_3_create_booking(self):
        """测试3: 创建预约"""
        print("\n=== 测试3: 创建预约 ===")
        
        headers = {"Authorization": f"Bearer {self.student_token}"}
        
        # 使用未来时间避免冲突
        start_time = datetime(2029, 3, 15, 10, 0, 0)
        end_time = datetime(2029, 3, 15, 12, 0, 0)
        
        data = {
            "deviceId": 13,
            "startTime": start_time.strftime("%Y-%m-%dT%H:%M:%S"),
            "endTime": end_time.strftime("%Y-%m-%dT%H:%M:%S")
        }
        
        resp = requests.post(f"{BASE_URL}/bookings", json=data, headers=headers)
        
        if resp.status_code == 200:
            result = resp.json()
            self.booking_id = result.get("id")
            print(f"✅ 预约创建成功: ID={self.booking_id}")
            print(f"   设备ID: {result.get('deviceId')}")
            print(f"   时间: {result.get('startTime')} 至 {result.get('endTime')}")
            print(f"   状态: {result.get('status')}")
            return True
        else:
            print(f"❌ 预约创建失败: {resp.status_code} - {resp.text}")
            return False
    
    def test_4_get_my_bookings(self):
        """测试4: 查询我的预约"""
        print("\n=== 测试4: 查询我的预约 ===")
        
        headers = {"Authorization": f"Bearer {self.student_token}"}
        resp = requests.get(f"{BASE_URL}/bookings/my", headers=headers)
        
        if resp.status_code == 200:
            bookings = resp.json()
            print(f"✅ 查询成功，共有 {len(bookings)} 个预约")
            
            # 验证刚创建的预约是否在列表中
            found = any(b.get("id") == self.booking_id for b in bookings)
            if found:
                print(f"   ✅ 找到刚创建的预约 (ID={self.booking_id})")
            else:
                print(f"   ⚠️ 未找到刚创建的预约")
            return True
        else:
            print(f"❌ 查询失败: {resp.status_code} - {resp.text}")
            return False
    
    def test_5_get_device_bookings(self):
        """测试5: 查询设备预约"""
        print("\n=== 测试5: 查询设备预约 ===")
        
        headers = {"Authorization": f"Bearer {self.student_token}"}
        resp = requests.get(f"{BASE_URL}/bookings?deviceId=13", headers=headers)
        
        if resp.status_code == 200:
            bookings = resp.json()
            print(f"✅ 查询成功，设备13共有 {len(bookings)} 个预约")
            return True
        else:
            print(f"❌ 查询失败: {resp.status_code} - {resp.text}")
            return False
    
    def test_6_admin_get_all_bookings(self):
        """测试6: 管理员查询指定设备的所有预约"""
        print("\n=== 测试6: 管理员查询设备预约 ===")
        
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        resp = requests.get(f"{BASE_URL}/bookings?deviceId=13", headers=headers)
        
        if resp.status_code == 200:
            bookings = resp.json()
            print(f"✅ 管理员查询成功，设备13共有 {len(bookings)} 个预约")
            return True
        else:
            print(f"❌ 查询失败: {resp.status_code} - {resp.text}")
            return False
    
    def test_7_cancel_booking(self):
        """测试7: 取消预约"""
        print("\n=== 测试7: 取消预约 ===")
        
        if not self.booking_id:
            print("⚠️ 跳过测试：没有可取消的预约")
            return False
        
        headers = {"Authorization": f"Bearer {self.student_token}"}
        resp = requests.put(f"{BASE_URL}/bookings/{self.booking_id}/cancel", headers=headers)
        
        if resp.status_code == 200:
            result = resp.json()
            print(f"✅ 预约取消成功: ID={self.booking_id}")
            print(f"   状态: {result.get('status')}")
            return True
        else:
            print(f"❌ 取消失败: {resp.status_code} - {resp.text}")
            return False
    
    def test_8_permission_test(self):
        """测试8: 权限测试 - 查询其他用户的预约"""
        print("\n=== 测试8: 权限测试 ===")
        
        # 学生尝试取消其他用户的预约（假设ID为1的预约不是student1的）
        headers = {"Authorization": f"Bearer {self.student_token}"}
        resp = requests.put(f"{BASE_URL}/bookings/1/cancel", headers=headers)
        
        if resp.status_code in [403, 404]:
            print(f"✅ 权限验证成功：学生无法取消他人预约 ({resp.status_code})")
            return True
        else:
            print(f"❌ 权限验证失败：应返回403或404，实际返回 {resp.status_code}")
            return False
    
    def run_all_tests(self):
        """运行所有测试"""
        print("=" * 60)
        print("开始预约管理模块自动化功能测试")
        print("=" * 60)
        
        tests = [
            self.test_1_login,
            self.test_2_get_available_devices,
            self.test_3_create_booking,
            self.test_4_get_my_bookings,
            self.test_5_get_device_bookings,
            self.test_6_admin_get_all_bookings,
            self.test_7_cancel_booking,
            self.test_8_permission_test
        ]
        
        results = []
        for test in tests:
            try:
                result = test()
                results.append(result if result is not None else False)
            except Exception as e:
                print(f"❌ 测试异常: {str(e)}")
                results.append(False)
        
        # 统计结果
        passed = sum(1 for r in results if r)
        total = len(results)
        
        print("\n" + "=" * 60)
        print(f"测试完成: {passed}/{total} 通过")
        print("=" * 60)
        
        return passed == total

if __name__ == "__main__":
    test = BookingFlowTest()
    success = test.run_all_tests()
    exit(0 if success else 1)
