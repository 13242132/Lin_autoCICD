import requests
import json
import zipfile
import os
import io
import logging
from datetime import datetime

# 配置日志以便调试
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class ProjectAPIClient:
    def __init__(self, api_url='http://localhost:8080/api/v2/project/generate-full'):
        self.api_url = api_url
        self.session = requests.Session()
        self.session.timeout = 900  # 5分钟超时设置

    def load_parameters(self, json_path):
        """从JSON文件加载项目生成参数"""
        try:
            with open(json_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except FileNotFoundError:
            logger.error(f"参数文件不存在: {json_path}")
            raise
        except json.JSONDecodeError:
            logger.error(f"JSON格式错误: {json_path}")
            raise

    def generate_project(self, params):
        """调用API生成项目并返回ZIP数据"""
        try:
            response = self.session.post(self.api_url, json=params)
            response.raise_for_status()  # 自动处理HTTP错误状态码
            return response.content
        except requests.exceptions.RequestException as e:
            logger.error(f"API请求失败: {str(e)}")
            raise

    def extract_zip(self, zip_data, extract_path):
        """解压ZIP数据到指定目录"""
        os.makedirs(extract_path, exist_ok=True)
        with zipfile.ZipFile(io.BytesIO(zip_data), 'r') as zip_ref:
            zip_ref.extractall(extract_path)
        logger.info(f"成功解压到: {extract_path}")

    def run(self, params_path, extract_path):
        """运行完整流程: 加载参数 -> 生成项目 -> 解压"""
        start_time = datetime.now()
        logger.info("开始项目生成流程")

        params = self.load_parameters(params_path)
        zip_data = self.generate_project(params)
        
        # 生成带时间戳的唯一解压目录
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        unique_extract_path = os.path.join(extract_path, timestamp)
        extracted_files = self.extract_zip(zip_data, unique_extract_path)

        duration = (datetime.now() - start_time).total_seconds()
        logger.info(f"项目生成流程完成，耗时{duration:.2f}秒，共解压{len(extracted_files)}个文件到 {unique_extract_path}")
        return extracted_files

if __name__ == "__main__":
    # 配置文件路径
    PARAMS_FILE = 'd:\code\flowise\Lin_autoCICD-main\backend_params_20250828_183450.json'
    EXTRACT_DIR = 'd:\code\flowise\Lin_autoCICD-main\extract_project'

    client = ProjectAPIClient()
    try:
        client.run(PARAMS_FILE, EXTRACT_DIR)
        print("项目生成成功！")
    except Exception as e:
        print(f"执行失败: {str(e)}")
        exit(1)