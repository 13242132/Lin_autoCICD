import logging
import os
import zipfile
import uuid
from concurrent.futures import ThreadPoolExecutor
from flask import Flask, request, jsonify
from services.complete_project_service import CompleteProjectService
from datetime import datetime

# --- 配置 ---
# 指定固定的输出目录和提取目录
OUTPUT_DIRECTORY = r"d:\code\flowise\Lin_autoCICD-main\zip"
EXTRACT_DIRECTORY = r"d:\code\flowise\Lin_autoCICD-main\extract_project"
MAX_CONCURRENT_REQUESTS = 5


# --- Flask 应用初始化 ---
app = Flask(__name__)

# --- 线程池初始化 ---
executor = ThreadPoolExecutor(max_workers=MAX_CONCURRENT_REQUESTS)
tasks = {}  # 存储任务ID与Future对象的映射

# --- 日志配置 ---
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# --- 服务层初始化 ---
# 在应用上下文中创建服务实例，以便复用
with app.app_context():
    try:
        project_service = CompleteProjectService()
        logging.info("CompleteProjectService 初始化成功")
    except ImportError:
        project_service = None
        logging.error("无法导入 CompleteProjectService，请确保在项目根目录下启动服务。")

def _generate_project_task(project_description, project_name):
    """在后台线程中执行项目生成任务"""
    try:
        # 调用核心服务生成项目
        generation_result = project_service.generate_complete_project(
            project_description=project_description,
            project_name=project_name,
        )
        
        # 处理并保存结果
        if generation_result.get('success'):
            zip_data = generation_result['zip_data']
            filename = generation_result['filename']
            
            # 确保输出目录存在
            os.makedirs(OUTPUT_DIRECTORY, exist_ok=True)
            
            saved_path = project_service.save_project_zip(zip_data, filename, OUTPUT_DIRECTORY)
            absolute_path = os.path.abspath(saved_path)
            
            # 自动解压zip文件到指定目录
            os.makedirs(EXTRACT_DIRECTORY, exist_ok=True)
            extract_path = os.path.join(EXTRACT_DIRECTORY, project_name)
            os.makedirs(extract_path, exist_ok=True)
            
            try:
                with zipfile.ZipFile(saved_path, 'r') as zip_ref:
                    zip_ref.extractall(extract_path)
                logging.info(f"项目 '{project_name}' 已成功解压到: {extract_path}")
            except Exception as e:
                logging.error(f"解压项目 '{project_name}' 时出错: {e}")
                
            logging.info(f"项目 '{project_name}' 已成功生成并保存到: {absolute_path}")
            
            return {
                "status": "completed",
                "message": "项目生成成功。",
                "file_path": absolute_path,
                "extract_path": extract_path
            }
        else:
            error_message = generation_result.get('message', '未知错误')
            logging.error(f"项目 '{project_name}' 生成失败: {error_message}")
            return {
                "status": "failed",
                "error": f"项目生成失败: {error_message}"
            }
    except Exception as e:
        logging.exception(f"处理请求时发生意外错误: {e}")
        return {
            "status": "failed",
            "error": f"服务器内部错误: {e}"
        }

@app.route('/api/v1/generate-project', methods=['POST'])
def generate_project_endpoint():
    """
    API端点，接收项目描述并生成完整的项目代码。
    """
    if not project_service:
        return jsonify({'success': False, 'message': '服务未正确初始化，请检查日志。'}), 500

    # 1. 从请求中获取JSON数据
    data = request.get_json()
    if not data or 'project_description' not in data:
        return jsonify({'success': False, 'message': '请求体中缺少 project_description 字段'}), 400

    project_description = data['project_description']
    if not project_description.strip():
        return jsonify({'success': False, 'message': 'project_description 不能为空'}), 400

    # 2. 生成一个项目名称（基于描述的开头、时间戳和UUID，确保唯一性）
    project_name = "test_project"
    timestamp = datetime.now().strftime('%Y%m%d%H%M%S')
    unique_id = uuid.uuid4().hex[:8]  # 取UUID的前8位作为唯一标识
    project_name = f"{project_name}_{timestamp}_{unique_id}"

    logging.info(f"接收到新请求，项目名称: {project_name}")

    try:
        # 提交任务到线程池并获取任务ID
        task_id = str(uuid.uuid4())
        future = executor.submit(_generate_project_task, project_description, project_name)
        tasks[task_id] = future
        
        return jsonify({
            "message": "项目生成任务已提交",
            "taskId": task_id
        }), 202
            
    except Exception as e:
        logging.error(f"提交项目生成任务时出错: {str(e)}")
        return jsonify({"error": f"提交项目生成任务时出错: {str(e)}"}), 500

@app.route('/api/v1/task-status/<task_id>', methods=['GET'])
def get_task_status(task_id):
    """查询任务状态"""
    try:
        if task_id not in tasks:
            return jsonify({"error": "任务不存在"}), 404
        
        future = tasks[task_id]
        
        if future.running():
            return jsonify({
                "taskId": task_id,
                "status": "running",
                "message": "任务正在执行中"
            }), 200
        elif future.done():
            try:
                result = future.result(timeout=0)  # 不阻塞获取结果
                return jsonify({
                    "taskId": task_id,
                    "status": result.get("status", "completed"),
                    "result": result
                }), 200
            except Exception as e:
                return jsonify({
                    "taskId": task_id,
                    "status": "failed",
                    "error": f"任务执行出错: {str(e)}"
                }), 500
        else:
            return jsonify({
                "taskId": task_id,
                "status": "pending",
                "message": "任务等待执行中"
            }), 200
            
    except Exception as e:
        logging.error(f"查询任务状态时出错: {str(e)}")
        return jsonify({"error": f"查询任务状态时出错: {str(e)}"}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """健康检查端点"""
    return jsonify({'status': 'ok'}), 200

def main():
    """
    启动Flask Web服务器
    """
    # 确保输出目录存在
    if not os.path.exists(OUTPUT_DIRECTORY):
        os.makedirs(OUTPUT_DIRECTORY)
        logging.info(f"已创建输出目录: {OUTPUT_DIRECTORY}")

    logging.info(f"Flask服务器将在 http://127.0.0.1:5000 上启动")
    logging.info(f"项目将保存在: {os.path.abspath(OUTPUT_DIRECTORY)}")
    logging.info("使用 POST http://127.0.0.1:5000/api/v1/generate-project 来生成项目")
    app.run(host='127.0.0.1', port=5000, debug=False)

if __name__ == '__main__':
    main()