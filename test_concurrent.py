import requests
import threading
import time
import json

def send_request(index, description):
    url = "http://127.0.0.1:5000/api/v1/generate-project"
    payload = {
        "project_description": description
    }
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.post(url, json=payload, headers=headers)
        print(f"Request {index}: {response.status_code} - {response.json()}")
        
        # 如果任务已提交，等待任务完成
        if response.status_code == 202:
            task_id = response.json().get("taskId")
            if task_id:
                # 轮询任务状态直到完成
                while True:
                    status_url = f"http://127.0.0.1:5000/api/v1/task-status/{task_id}"
                    status_response = requests.get(status_url)
                    if status_response.status_code == 200:
                        status_data = status_response.json()
                        if status_data.get("status") in ["completed", "failed"]:
                            print(f"Request {index} task completed with status: {status_data.get('status')}")
                            break
                        else:
                            print(f"Request {index} task is still running, waiting...")
                            time.sleep(60)  # 等60秒后再次检查
                    else:
                        print(f"Request {index} failed to get task status: {status_response.status_code}")
                        break
    except Exception as e:
        print(f"Request {index} failed: {e}")

if __name__ == "__main__":
    # 定义项目描述列表
    project_descriptions = [
        "在线学习平台",
        "博客系统",
        "任务管理系统",
        "新闻聚合应用",
        "高校就业系统",
        "在线考试系统"
    ]
    
    # 每次并行执行两个任务
    batch_size = 2
    
    # 分批执行任务
    for i in range(0, len(project_descriptions), batch_size):
        batch = project_descriptions[i:i+batch_size]
        threads = []
        
        print(f"开始执行第 {i//batch_size + 1} 批任务")
        
        # 创建并启动线程来发送并发请求
        for j, description in enumerate(batch):
            thread_index = i + j + 1
            thread = threading.Thread(target=send_request, args=(thread_index, description))
            threads.append(thread)
            thread.start()
        
        # 等待当前批次的所有线程完成
        for thread in threads:
            thread.join()
        
        # 固定延迟30秒
        time.sleep(60)
        print(f"第 {i//batch_size + 1} 批任务完成，等待60秒...")
    
    print("所有请求 completed")