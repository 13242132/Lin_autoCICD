import requests
import uuid
import json

def test_user_controller():
    """测试UserController中的接口"""
    base_url = 'http://localhost:8090'
    
    # 生成唯一邮箱和用户名用于测试
    test_email = f'test_{uuid.uuid4().hex[:8]}@example.com'
    test_username = f'testuser_{uuid.uuid4().hex[:8]}'
    test_password = 'TestPassword123'
    
    print(f"=== 测试UserController接口开始: 使用邮箱 {test_email} 和用户名 {test_username} ===")
    
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
    
    # 3. 测试用户个人资料接口（使用@CurrentUserId注解）
    print("\n=== 3. 测试用户个人资料接口（使用@CurrentUserId注解） ===")
    headers = {
        'Authorization': f'Bearer {token}'
    }
    
    try:
        response = requests.get(f'{base_url}/api/users/profile', headers=headers)
        print(f'用户个人资料请求URL: {base_url}/api/users/profile')
        print(f'用户个人资料响应状态码: {response.status_code}')
        
        if response.status_code == 200:
            print("✅ 获取用户个人资料成功")
            profile_result = response.json()
            print(f"用户个人资料结果: {json.dumps(profile_result, indent=2)}")
            
            # 检查返回的用户ID是否与注册时获取的一致
            if 'id' in profile_result and str(profile_result['id']) == str(user_id):
                print("✅ @CurrentUserId注解正确获取了用户ID")
            else:
                print("⚠️ 返回的用户ID与注册时不一致")
        else:
            print("❌ 获取用户个人资料失败")
            print(f'用户个人资料响应内容: {response.text}')
            
    except Exception as e:
        print(f'❌ 获取用户个人资料请求发生异常: {str(e)}')
    
    # 4. 测试不带token的请求
    print("\n=== 4. 测试不带token的请求 ===")
    try:
        response = requests.get(f'{base_url}/api/users/profile')
        print(f'无token请求URL: {base_url}/api/users/profile')
        print(f'无token请求响应状态码: {response.status_code}')
        
        if response.status_code == 401:
            print("✅ 无token请求被正确拒绝")
        else:
            print("❌ 无token请求应返回401状态码")
            print(f'无token请求响应内容: {response.text}')
            
    except Exception as e:
        print(f'❌ 无token请求发生异常: {str(e)}')

if __name__ == '__main__':
    test_user_controller()