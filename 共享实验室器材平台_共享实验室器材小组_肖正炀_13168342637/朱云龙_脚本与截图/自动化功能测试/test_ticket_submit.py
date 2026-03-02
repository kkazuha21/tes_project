#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
报修管理模块自动化功能测试
测试报修单的提交、查询、更新等核心功能
"""

import requests
import json
import time

BASE_URL = "http://localhost:8080/api"

class TicketAutomatedTest:
    def __init__(self):
        self.admin_token = None
        self.student_token = None
        self.created_ticket_id = None
        
    def login(self, username, password):
        """用户登录"""
        url = f"{BASE_URL}/auth/login"
        data = {"username": username, "password": password}
        try:
            response = requests.post(url, json=data, timeout=5)
            if response.status_code == 200:
                token = response.json().get('token')
                print(f"✓ 登录成功: {username}, Token: {token[:20]}...")
                return token
            else:
                print(f"✗ 登录失败: {username}, 状态码: {response.status_code}")
                return None
        except Exception as e:
            print(f"✗ 登录异常: {e}")
            return None
    
    def test_create_ticket(self):
        """测试用例1: 创建报修单"""
        print("\n=== 测试用例1: 创建报修单 ===")
        
        if not self.student_token:
            print("✗ 未登录，无法创建报修单")
            return False
            
        url = f"{BASE_URL}/tickets"
        headers = {"Authorization": f"Bearer {self.student_token}"}
        data = {
            "deviceId": 13,
            "title": "自动化测试-设备故障",
            "content": "这是自动化测试创建的报修单，请忽略",
            "severity": "HIGH"
        }
        
        try:
            response = requests.post(url, json=data, headers=headers, timeout=5)
            if response.status_code == 200:
                result = response.json()
                self.created_ticket_id = result.get('id')
                print(f"✓ 创建报修单成功，ID: {self.created_ticket_id}")
                print(f"  标题: {result.get('title')}")
                print(f"  状态: {result.get('status')}")
                return True
            else:
                print(f"✗ 创建报修单失败，状态码: {response.status_code}")
                print(f"  响应: {response.text}")
                return False
        except Exception as e:
            print(f"✗ 创建报修单异常: {e}")
            return False
    
    def test_get_my_tickets(self):
        """测试用例2: 查询我的报修单"""
        print("\n=== 测试用例2: 查询我的报修单 ===")
        
        if not self.student_token:
            print("✗ 未登录，无法查询报修单")
            return False
            
        url = f"{BASE_URL}/tickets/my"
        headers = {"Authorization": f"Bearer {self.student_token}"}
        
        try:
            response = requests.get(url, headers=headers, timeout=5)
            if response.status_code == 200:
                tickets = response.json()
                print(f"✓ 查询成功，共 {len(tickets)} 条报修单")
                if len(tickets) > 0:
                    print(f"  最新报修: {tickets[0].get('title')}")
                return True
            else:
                print(f"✗ 查询失败，状态码: {response.status_code}")
                return False
        except Exception as e:
            print(f"✗ 查询异常: {e}")
            return False
    
    def test_get_ticket_detail(self):
        """测试用例3: 查询报修单详情"""
        print("\n=== 测试用例3: 查询报修单详情 ===")
        
        if not self.student_token or not self.created_ticket_id:
            print("✗ 无有效报修单ID")
            return False
            
        url = f"{BASE_URL}/tickets/{self.created_ticket_id}"
        headers = {"Authorization": f"Bearer {self.student_token}"}
        
        try:
            response = requests.get(url, headers=headers, timeout=5)
            if response.status_code == 200:
                ticket = response.json()
                print(f"✓ 查询详情成功")
                print(f"  ID: {ticket.get('id')}")
                print(f"  标题: {ticket.get('title')}")
                print(f"  内容: {ticket.get('content')}")
                print(f"  严重程度: {ticket.get('severity')}")
                print(f"  状态: {ticket.get('status')}")
                return True
            else:
                print(f"✗ 查询详情失败，状态码: {response.status_code}")
                return False
        except Exception as e:
            print(f"✗ 查询详情异常: {e}")
            return False
    
    def test_admin_get_all_tickets(self):
        """测试用例4: 管理员查询所有报修单"""
        print("\n=== 测试用例4: 管理员查询所有报修单 ===")
        
        if not self.admin_token:
            print("✗ 管理员未登录")
            return False
            
        url = f"{BASE_URL}/tickets"
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        
        try:
            response = requests.get(url, headers=headers, timeout=5)
            if response.status_code == 200:
                tickets = response.json()
                print(f"✓ 管理员查询成功，共 {len(tickets)} 条报修单")
                return True
            else:
                print(f"✗ 查询失败，状态码: {response.status_code}")
                return False
        except Exception as e:
            print(f"✗ 查询异常: {e}")
            return False
    
    def test_update_ticket_status(self):
        """测试用例5: 更新报修单状态（管理员操作）"""
        print("\n=== 测试用例5: 更新报修单状态 ===")
        
        if not self.admin_token or not self.created_ticket_id:
            print("✗ 管理员未登录或无有效报修单ID")
            return False
            
        url = f"{BASE_URL}/tickets/{self.created_ticket_id}/status"
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        params = {"status": "PROCESSING"}
        
        try:
            response = requests.put(url, headers=headers, params=params, timeout=5)
            if response.status_code == 200:
                result = response.json()
                print(f"✓ 更新状态成功")
                print(f"  新状态: {result.get('status')}")
                return True
            else:
                print(f"✗ 更新状态失败，状态码: {response.status_code}")
                print(f"  响应: {response.text}")
                return False
        except Exception as e:
            print(f"✗ 更新状态异常: {e}")
            return False
    
    def test_student_cannot_get_all_tickets(self):
        """测试用例6: 学生无权查询所有报修单（权限测试）"""
        print("\n=== 测试用例6: 权限测试 - 学生不能查询所有报修单 ===")
        
        if not self.student_token:
            print("✗ 学生未登录")
            return False
            
        url = f"{BASE_URL}/tickets"
        headers = {"Authorization": f"Bearer {self.student_token}"}
        
        try:
            response = requests.get(url, headers=headers, timeout=5)
            if response.status_code == 403:
                print(f"✓ 权限验证成功，学生无法访问（403 Forbidden）")
                return True
            else:
                print(f"✗ 权限验证失败，期望403，实际: {response.status_code}")
                return False
        except Exception as e:
            print(f"✗ 权限测试异常: {e}")
            return False
    
    def run_all_tests(self):
        """运行所有测试用例"""
        print("=" * 60)
        print("报修管理模块自动化功能测试")
        print("=" * 60)
        
        # 准备：登录用户
        print("\n【准备阶段】用户登录")
        self.admin_token = self.login("admin", "123456")
        self.student_token = self.login("student1", "123456")
        
        if not self.admin_token or not self.student_token:
            print("\n✗ 登录失败，测试终止")
            return
        
        # 执行测试
        results = []
        results.append(("创建报修单", self.test_create_ticket()))
        time.sleep(0.5)
        
        results.append(("查询我的报修单", self.test_get_my_tickets()))
        time.sleep(0.5)
        
        results.append(("查询报修单详情", self.test_get_ticket_detail()))
        time.sleep(0.5)
        
        results.append(("管理员查询所有报修单", self.test_admin_get_all_tickets()))
        time.sleep(0.5)
        
        results.append(("更新报修单状态", self.test_update_ticket_status()))
        time.sleep(0.5)
        
        results.append(("权限测试", self.test_student_cannot_get_all_tickets()))
        
        # 统计结果
        print("\n" + "=" * 60)
        print("测试结果汇总")
        print("=" * 60)
        
        passed = sum(1 for _, result in results if result)
        total = len(results)
        
        for test_name, result in results:
            status = "✓ 通过" if result else "✗ 失败"
            print(f"{test_name:20s} {status}")
        
        print("-" * 60)
        print(f"总计: {total} 个测试用例")
        print(f"通过: {passed} 个")
        print(f"失败: {total - passed} 个")
        print(f"通过率: {passed/total*100:.1f}%")
        print("=" * 60)

if __name__ == "__main__":
    tester = TicketAutomatedTest()
    tester.run_all_tests()
