import os
import logging
import concurrent.futures
from project_api_client import ProjectAPIClient
from datetime import datetime
import glob

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(threadName)s - %(message)s',
    handlers=[logging.FileHandler('parallel_execution.log'), logging.StreamHandler()]
)
logger = logging.getLogger(__name__)

class ParallelProcessor:
    def __init__(self, max_workers=2):
        self.max_workers = max_workers
        self.client = ProjectAPIClient()

    def process_file(self, json_path, extract_root):
        """处理单个JSON参数文件"""
        try:
            logger.info(f"开始处理文件: {json_path}")
            result = self.client.run(json_path, extract_root)
            logger.info(f"文件处理完成: {json_path}")
            return {
                'status': 'success',
                'file': json_path,
                'output_dir': result
            }
        except Exception as e:
            logger.error(f"文件处理失败 {json_path}: {str(e)}", exc_info=True)
            return {
                'status': 'failed',
                'file': json_path,
                'error': str(e)
            }

    def run_parallel(self, json_files, extract_root, batch_size=2, delay_seconds=60):
        """并行处理多个JSON文件（分批执行）"""
        if not json_files:
            logger.warning("未提供JSON文件列表")
            return []

        logger.info(f"启动并行处理，共{len(json_files)}个文件，每批{batch_size}个任务，批处理延迟{delay_seconds}秒")
        all_results = []

        # 分批处理任务
        for i in range(0, len(json_files), batch_size):
            batch_files = json_files[i:i+batch_size]
            batch_num = i // batch_size + 1
            logger.info(f"开始处理第{batch_num}批任务，共{len(batch_files)}个文件")

            # 使用线程池执行当前批次
            with concurrent.futures.ThreadPoolExecutor(
                max_workers=batch_size,
                thread_name_prefix=f'Batch{batch_num}'
            ) as executor:
                future_to_file = {
                    executor.submit(self.process_file, file_path, extract_root): file_path
                    for file_path in batch_files
                }

                # 收集当前批次结果
                batch_results = []
                for future in concurrent.futures.as_completed(future_to_file):
                    file_path = future_to_file[future]
                    try:
                        batch_results.append(future.result())
                    except Exception as e:
                        logger.error(f"批处理任务失败 {file_path}: {str(e)}")
                        batch_results.append({
                            'status': 'error',
                            'file': file_path,
                            'error': str(e)
                        })

                all_results.extend(batch_results)
                logger.info(f"第{batch_num}批任务处理完成")

                # 非最后一批时添加延迟
                if i + batch_size < len(json_files):
                    logger.info(f"批处理间隔延迟{delay_seconds}秒...")
                    time.sleep(delay_seconds)

        return all_results

if __name__ == "__main__":
    import sys
    import time  
    if len(sys.argv) < 2:
        print("用法: python parallel_executor.py <json_file1> <json_file2> ...")
        print(f"示例: python parallel_executor.py backend_params_*.json")
        sys.exit(1)

    # 解析命令行参数
    json_files = sys.argv[1:]
    extract_root = r'd:\code\flowise\Lin_autoCICD-main\extract_project'
    batch_size = 2  # 每次并行处理2个任务
    delay_seconds = 60  # 批次间延迟60秒

    # 验证文件存在性并处理通配符
    valid_files = []
    for pattern in json_files:
        # 移除通配符转义字符并解析模式
        cleaned_pattern = pattern.replace('\\*', '*').replace('\\?', '?')
        for file_path in glob.glob(cleaned_pattern):
            if os.path.isfile(file_path):
                valid_files.append(file_path)
            else:
                logger.warning(f"文件不存在，跳过: {file_path}")

    if not valid_files:
        logger.error("没有有效的JSON文件可供处理")
        sys.exit(1)

    # 执行并行处理
    processor = ParallelProcessor()
    results = processor.run_parallel(valid_files, extract_root, batch_size, delay_seconds)

    # 打印执行摘要
    print("\n===== 并行处理结果摘要 ====")
    success = sum(1 for r in results if r['status'] == 'success')
    failed = len(results) - success
    print(f"总文件数: {len(valid_files)}, 成功: {success}, 失败: {failed}")

    if failed > 0:
        print("失败文件列表:")
        for r in results:
            if r['status'] != 'success':
                print(f"- {r['file']}: {r.get('error', '未知错误')}")
        sys.exit(1)
    else:
        print("所有文件处理成功!")
        sys.exit(0)