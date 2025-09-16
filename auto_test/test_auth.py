import unittest
import requests
import uuid
import unittest

class TestAuthSystem(unittest.TestCase):
    def setUp(self):
        self.base_url = 'http://localhost:8090/api/auth'
        # 生成唯一邮箱用于测试
        self.test_email = f'test_{uuid.uuid4().hex[:8]}@example.com'
        self.test_password = 'TestPassword123'
        print(f"=== 测试开始: 使用邮箱 {self.test_email} ===")

    def test_registration(self):
        """测试正常注册功能"""
        print("\n=== 1. 测试正常注册 ===")
        # 注册用户
        register_data = {
            'email': self.test_email,
            'password': self.test_password,
            'username': 'testuser'
        }
        response = requests.post(f'{self.base_url}/register', json=register_data)
        print(f'注册响应状态码: {response.status_code}')
        print(f'注册响应内容: {response.text}')
        
        self.assertEqual(response.status_code, 201)
        response_json = response.json()
        self.assertTrue('id' in response_json)
        self.assertTrue('email' in response_json)
        self.assertEqual(response_json['email'], self.test_email)

    def test_login_success(self):
        """测试登录成功功能"""
        print("\n=== 2. 测试登录成功 ===")
        # 先注册用户
        register_data = {
            'email': self.test_email,
            'password': self.test_password,
            'username': 'testuser'
        }
        print(f'注册数据: {register_data}')
        register_response = requests.post(f'{self.base_url}/register', json=register_data)
        print(f'注册响应状态码: {register_response.status_code}')
        print(f'注册响应内容: {register_response.text}')
        
        # 确保注册成功
        self.assertEqual(register_response.status_code, 201)
        
        # 使用正确密码登录
        login_data = {
            'email': self.test_email,
            'password': self.test_password
        }
        login_url = f'{self.base_url}/login'
        print(f'登录URL: {login_url}')
        print(f'登录数据: {login_data}')
        
        try:
            response = requests.post(login_url, json=login_data)
            print(f'登录响应状态码: {response.status_code}')
            print(f'登录响应头: {response.headers}')
            print(f'登录响应内容: {response.text}')
        except Exception as e:
            print(f'登录请求发生异常: {str(e)}')
            raise
        
        # 检查是否是由于邮箱或密码错误导致的401
        if response.status_code == 401:
            print("\n调试信息: 登录失败，可能的原因:")
            print(f"1. 邮箱不存在: {self.test_email}")
            print(f"2. 密码不匹配: {self.test_password}")
            print("3. 服务器端认证逻辑问题")
            
            # 尝试获取用户信息来验证邮箱是否存在
            try:
                # 假设我们有一个获取用户信息的接口
                user_info_url = f'{self.base_url}/users/by-email/{self.test_email}'
                user_response = requests.get(user_info_url)
                print(f'验证邮箱是否存在响应状态码: {user_response.status_code}')
                print(f'验证邮箱是否存在响应内容: {user_response.text}')
            except Exception as e:
                print(f'验证邮箱是否存在请求发生异常: {str(e)}')
        
        elif response.status_code != 200:
            print(f"\n调试信息: 登录返回意外状态码 {response.status_code}")
            print(f"响应内容: {response.text}")
        
        self.assertEqual(response.status_code, 200)
        response_json = response.json()
        self.assertTrue('token' in response_json)
        self.assertTrue('id' in response_json)
        self.assertTrue('email' in response_json)
        self.assertEqual(response_json['email'], self.test_email)

    def test_get_all_users(self):
        """测试获取所有用户功能"""
        print("\n=== 3. 测试获取所有用户 ===")
        
        # 先注册一个用户，确保有数据
        register_data = {
            'email': self.test_email,
            'password': self.test_password,
            'username': 'testuser'
        }
        register_response = requests.post(f'{self.base_url}/register', json=register_data)
        print(f'注册响应状态码: {register_response.status_code}')
        
        # 发送获取所有用户的请求
        response = requests.get(f'{self.base_url}/users')
        print(f'获取所有用户响应状态码: {response.status_code}')
        print(f'获取所有用户响应内容: {response.text}')
        
        # 验证响应
        self.assertEqual(response.status_code, 200)
        response_json = response.json()
        self.assertIsInstance(response_json, list)
        self.assertTrue(len(response_json) > 0)
        
        # 检查我们注册的用户是否在列表中
        user_found = False
        for user in response_json:
            if user['email'] == self.test_email:
                user_found = True
                break
        self.assertTrue(user_found, f'注册的用户 {self.test_email} 未在用户列表中找到')

if __name__ == '__main__':
    unittest.main()