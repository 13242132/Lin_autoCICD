#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
记忆管理功能测试启动脚本
"""

import subprocess
import time
import sys
import os
from pathlib import Path

def start_spring_boot_app():
    """启动Spring Boot应用"""
    project_dir = Path(__file__).parent
    pom_xml = project_dir / "pom.xml"
    
    if not pom_xml.exists():
        print("错误: 未找到pom.xml文件，请确保在项目根目录下运行此脚本")
        return False
    
    try:
        print("正在启动Spring Boot应用...")
        # 使用Maven启动Spring Boot应用
        process = subprocess.Popen(
            ["mvn", "spring-boot:run"],
            cwd=project_dir,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        
        # 等待应用启动
        print("等待应用启动...")
        time.sleep(30)  # 给应用足够的时间启动
        
        # 检查进程是否仍在运行
        if process.poll() is not None:
            stdout, stderr = process.communicate()
            print(f"应用启动失败:")
            print(f"标准输出: {stdout}")
            print(f"错误输出: {stderr}")
            return False
        
        print("Spring Boot应用已启动")
        return process
        
    except Exception as e:
        print(f"启动Spring Boot应用时发生错误: {str(e)}")
        return False

def run_api_test():
    """运行API测试"""
    try:
        print("\n开始运行API测试...")
        test_script = Path(__file__).parent / "test_memory_api.py"
        
        if not test_script.exists():
            print("错误: 未找到test_memory_api.py文件")
            return False
        
        # 运行测试脚本
        result = subprocess.run(
            [sys.executable, str(test_script)],
            cwd=Path(__file__).parent,
            capture_output=True,
            text=True
        )
        
        print("测试输出:")
        print(result.stdout)
        
        if result.stderr:
            print("测试错误:")
            print(result.stderr)
        
        return result.returncode == 0
        
    except Exception as e:
        print(f"运行API测试时发生错误: {str(e)}")
        return False

def main():
    """主函数"""
    print("=" * 60)
    print("记忆管理功能测试")
    print("=" * 60)
    
    # 启动Spring Boot应用
    app_process = start_spring_boot_app()
    if not app_process:
        print("无法启动Spring Boot应用，测试终止")
        return
    
    try:
        # 运行API测试
        test_success = run_api_test()
        
        if test_success:
            print("\n测试成功完成!")
        else:
            print("\n测试失败!")
            
    finally:
        # 终止Spring Boot应用
        print("\n正在关闭Spring Boot应用...")
        app_process.terminate()
        try:
            app_process.wait(timeout=10)
        except subprocess.TimeoutExpired:
            app_process.kill()
        
        print("测试完成")

if __name__ == "__main__":
    main()