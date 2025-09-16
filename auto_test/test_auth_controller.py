import requests
import uuid
import json

def test_auth_endpoints():
    """测试AuthController的register和login接口"""
    base_url = 'http://localhost:8090/api/auth'
    
    # 生成唯一邮箱和用户名用于测试
    test_email = f'test_{uuid.uuid4().hex[:8]}@example.com'
    test_username = f'testuser_{uuid.uuid4().hex[:8]}'
    test_password = 'TestPassword123'
    
    print(f"=== 测试开始: 使用邮箱 {test_email} 和用户名 {test_username} ===")
    
    # 1. 测试注册接口
    print("\n=== 1. 测试注册接口 ===")
    register_data = {
        'username': test_username,
        'email': test_email,
        'password': test_password,
        'confirmPassword': test_password
    }
    
    try:
        response = requests.post(f'{base_url}/register', data=register_data)
        print(f'注册请求URL: {base_url}/register')
        print(f'注册请求数据: {register_data}')
        print(f'注册响应状态码: {response.status_code}')
        print(f'注册响应内容: {response.text}')
        
        if response.status_code == 201:
            print("✅ 注册成功")
            register_result = response.json()
            print(f"注册结果: {json.dumps(register_result, indent=2)}")
        else:
            print("❌ 注册失败")
            return
            
    except Exception as e:
        print(f'❌ 注册请求发生异常: {str(e)}')
        return
    
    # 2. 测试登录接口
    print("\n=== 2. 测试登录接口 ===")
    login_data = {
        'username': test_username,
        'password': test_password,
        'rememberMe': 'false'
    }
    
    try:
        response = requests.post(f'{base_url}/login', data=login_data)
        print(f'登录请求URL: {base_url}/login')
        print(f'登录请求数据: {login_data}')
        print(f'登录响应状态码: {response.status_code}')
        print(f'登录响应内容: {response.text}')
        
        if response.status_code == 200:
            print("✅ 登录成功")
            login_result = response.json()
            print(f"登录结果: {json.dumps(login_result, indent=2)}")
            
            # 检查返回的token
            if 'token' in login_result:
                print(f"✅ 获取到token: {login_result['token'][:20]}...")
            else:
                print("⚠️ 响应中没有token字段")
        else:
            print("❌ 登录失败")
            
    except Exception as e:
        print(f'❌ 登录请求发生异常: {str(e)}')
    
    # 3. 测试使用错误密码登录
    print("\n=== 3. 测试使用错误密码登录 ===")
    wrong_login_data = {
        'username': test_username,
        'password': 'WrongPassword123',
        'rememberMe': 'false'
    }
    
    try:
        response = requests.post(f'{base_url}/login', data=wrong_login_data)
        print(f'错误密码登录请求URL: {base_url}/login')
        print(f'错误密码登录请求数据: {wrong_login_data}')
        print(f'错误密码登录响应状态码: {response.status_code}')
        print(f'错误密码登录响应内容: {response.text}')
        
        if response.status_code == 401 or response.status_code == 400:
            print("✅ 错误密码登录失败，符合预期")
        else:
            print("❌ 错误密码登录应返回401或400状态码")
            
    except Exception as e:
        print(f'❌ 错误密码登录请求发生异常: {str(e)}')

if __name__ == '__main__':
    test_auth_endpoints()