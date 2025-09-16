import zipfile
import os
import shutil

# 配置路径
ZIP_FILE_PATH = r"d:\code\flowise\autoCICD-langchain4j\zip\code.zip"
EXTRACT_DIR = r"d:\code\flowise\autoCICD-langchain4j\auto_test\extracted_project"

# 创建提取目录，如果存在则先删除
if os.path.exists(EXTRACT_DIR):
    shutil.rmtree(EXTRACT_DIR)
os.makedirs(EXTRACT_DIR)

# 解压缩文件
print(f"正在解压缩文件: {ZIP_FILE_PATH}")
try:
    with zipfile.ZipFile(ZIP_FILE_PATH, 'r') as zip_ref:
        zip_ref.extractall(EXTRACT_DIR)
    print(f"成功解压缩到: {EXTRACT_DIR}")

    # 列出提取目录中的文件
    print("\n提取的文件结构:")
    for root, dirs, files in os.walk(EXTRACT_DIR):
        level = root.replace(EXTRACT_DIR, '').count(os.sep)
        indent = ' ' * 4 * level
        print(f"{indent}{os.path.basename(root)}/")
        subindent = ' ' * 4 * (level + 1)
        for file in files:
            print(f"{subindent}{file}")

except Exception as e:
    print(f"解压缩失败: {e}")