import requests
import uuid
import json

def test_current_user_id():
    """测试@CurrentUserId注解功能"""
    base_url = 'http://localhost:8090'
    
    # 生成唯一邮箱和用户名用于测试
    test_email = f'test_{uuid.uuid4().hex[:8]}@example.com'
    test_username = f'testuser_{uuid.uuid4().hex[:8]}'
    test_password = 'TestPassword123'
    
    print(f"=== 测试@CurrentUserId注解功能开始: 使用邮箱 {test_email} 和用户名 {test_username} ===")
    
    # 1. 测试注册接口
    print("\n=== 1. 测试注册接口 ===")
    register_data = {
        'username': test_username,
        'email': test_email,
        'password': test_password,
        'confirmPassword': test_password
    }
    
    try:
        response = requests.post(f'{base_url}/api/auth/register', data=register_data)
        print(f'注册请求URL: {base_url}/api/auth/register')
        print(f'注册响应状态码: {response.status_code}')
        
        if response.status_code == 201:
            print("✅ 注册成功")
            register_result = response.json()
            user_id = register_result.get('id')
            print(f"用户ID: {user_id}")
        else:
            print("❌ 注册失败")
            print(f'注册响应内容: {response.text}')
            return
            
    except Exception as e:
        print(f'❌ 注册请求发生异常: {str(e)}')
        return
    
    # 2. 测试登录接口获取token
    print("\n=== 2. 测试登录接口获取token ===")
    login_data = {
        'username': test_username,
        'password': test_password,
        'rememberMe': 'false'
    }
    
    token = None
    try:
        response = requests.post(f'{base_url}/api/auth/login', data=login_data)
        print(f'登录请求URL: {base_url}/api/auth/login')
        print(f'登录响应状态码: {response.status_code}')
        
        if response.status_code == 200:
            print("✅ 登录成功")
            login_result = response.json()
            token = login_result.get('token')
            if token:
                print(f"✅ 获取到token: {token[:20]}...")
            else:
                print("⚠️ 响应中没有token字段")
                return
        else:
            print("❌ 登录失败")
            print(f'登录响应内容: {response.text}')
            return
            
    except Exception as e:
        print(f'❌ 登录请求发生异常: {str(e)}')
        return
    
    # 3. 测试使用@CurrentUserId注解的接口
    print("\n=== 3. 测试使用@CurrentUserId注解的接口 ===")
    headers = {
        'Authorization': f'Bearer {token}'
    }
    
    # 测试获取用户信息接口
    try:
        response = requests.get(f'{base_url}/api/demo/user-info', headers=headers)
        print(f'用户信息请求URL: {base_url}/api/demo/user-info')
        print(f'用户信息响应状态码: {response.status_code}')
        
        if response.status_code == 200:
            print("✅ 获取用户信息成功")
            user_info_result = response.text
            print(f"用户信息结果: {user_info_result}")
            
            # 检查返回的用户ID是否与注册时获取的一致
            if str(user_id) in user_info_result:
                print("✅ @CurrentUserId注解正确获取了用户ID")
            else:
                print("⚠️ 返回的用户ID与注册时不一致")
        else:
            print("❌ 获取用户信息失败")
            print(f'用户信息响应内容: {response.text}')
            
    except Exception as e:
        print(f'❌ 获取用户信息请求发生异常: {str(e)}')
    
    # 4. 测试添加记录接口
    print("\n=== 4. 测试添加记录接口 ===")
    record_data = {
        'content': '测试记录内容',
        'type': 'test'
    }
    
    try:
        response = requests.post(f'{base_url}/api/demo/record', headers=headers, json=record_data)
        print(f'添加记录请求URL: {base_url}/api/demo/record')
        print(f'添加记录响应状态码: {response.status_code}')
        
        if response.status_code == 200:
            print("✅ 添加记录成功")
            record_result = response.text
            print(f"添加记录结果: {record_result}")
        else:
            print("❌ 添加记录失败")
            print(f'添加记录响应内容: {response.text}')
            
    except Exception as e:
        print(f'❌ 添加记录请求发生异常: {str(e)}')
    
    # 5. 测试不带token的请求
    print("\n=== 5. 测试不带token的请求 ===")
    try:
        response = requests.get(f'{base_url}/api/demo/user-info')
        print(f'无token请求URL: {base_url}/api/demo/user-info')
        print(f'无token请求响应状态码: {response.status_code}')
        
        if response.status_code == 401:
            print("✅ 无token请求被正确拒绝")
        else:
            print("❌ 无token请求应返回401状态码")
            print(f'无token请求响应内容: {response.text}')
            
    except Exception as e:
        print(f'❌ 无token请求发生异常: {str(e)}')

if __name__ == '__main__':
    test_current_user_id()