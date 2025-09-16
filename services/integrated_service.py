#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
整合服务

将需求文档生成和原型生成整合在一起，处理文档格式并为后台调用准备参数
"""

import logging
import json
import re
from typing import Dict, Any, Optional, Tuple
from datetime import datetime

from .clients import RequirementsDocumentClient, PrototypeGeneratorClient

logger = logging.getLogger(__name__)

class IntegratedFlowiseService:
    """整合的Flowise服务"""
    
    def __init__(self):
        self.requirements_client = RequirementsDocumentClient()
        self.prototype_client = PrototypeGeneratorClient()
    
    def generate_project_documents(self, project_description: str) -> Dict[str, Any]:
        """生成完整的项目文档（需求文档 + 原型）
        
        Args:
            project_description: 项目描述
            
        Returns:
            包含需求文档和原型的完整结果
        """
        result = {
            'success': False,
            'requirements_document': None,
            'prototype': None,
            'backend_params': None,
            'error': None,
            'metrics': {
                'requirements_length': 0,
                'prototype_input_length': 0,
                'prototype_output_length': 0,
                'processing_time': 0
            }
        }
        
        start_time = datetime.now()
        
        try:
            logger.info("开始生成项目文档")
            
            # 1. 生成需求文档
            logger.info("步骤1: 生成需求文档")
            requirements_result = self.requirements_client.generate_requirements_document(project_description)
            
            if not requirements_result or not requirements_result.get('success'):
                result['error'] = '需求文档生成失败'
                return result
            
            # 清理需求文档内容
            cleaned_requirements = self._clean_requirements_content(requirements_result['file_content'])
            requirements_result['file_content'] = cleaned_requirements
            result['requirements_document'] = requirements_result
            result['metrics']['requirements_length'] = len(cleaned_requirements)
            
            logger.info(f"需求文档生成成功，长度: {result['metrics']['requirements_length']} 字符")
            
            # 2. 使用需求文档生成原型
            logger.info("步骤2: 基于需求文档生成原型")
            prototype_input = self._prepare_prototype_input(cleaned_requirements, project_description)
            result['metrics']['prototype_input_length'] = len(prototype_input)
            
            logger.info(f"原型生成输入长度: {result['metrics']['prototype_input_length']} 字符")
            
            prototype_result = self.prototype_client.generate_prototype(prototype_input)
            
            if not prototype_result or not prototype_result.get('success'):
                result['error'] = '原型生成失败'
                return result
            
            result['prototype'] = prototype_result
            result['metrics']['prototype_output_length'] = len(prototype_result['file_content'])
            
            logger.info(f"原型生成成功，输出长度: {result['metrics']['prototype_output_length']} 字符")
            
            # 3. 准备后台调用参数
            logger.info("步骤3: 准备后台调用参数")
            backend_params = self._prepare_backend_params(requirements_result, prototype_result, project_description)
            result['backend_params'] = backend_params
            
            # 保存后台参数到文件以便查看
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            params_file = f"backend_params_{timestamp}.json"
            try:
                with open(params_file, 'w', encoding='utf-8') as f:
                    json.dump(backend_params, f, ensure_ascii=False, indent=2)
                logger.info(f"后台参数已保存到文件: {params_file}")
            except Exception as e:
                logger.warning(f"保存后台参数文件失败: {str(e)}")
            
            # 4. 计算处理时间
            end_time = datetime.now()
            result['metrics']['processing_time'] = (end_time - start_time).total_seconds()
            
            result['success'] = True
            logger.info(f"项目文档生成完成，总耗时: {result['metrics']['processing_time']:.2f} 秒")
            
            return result
            
        except Exception as e:
            logger.error(f"生成项目文档时发生错误: {str(e)}")
            result['error'] = f'处理过程中发生错误: {str(e)}'
            return result
    
    def _clean_requirements_content(self, content: str) -> str:
        """清理需求文档内容，移除markdown代码块标记
        
        Args:
            content: 原始内容
            
        Returns:
            清理后的内容
        """
        try:
            # 移除开头和结尾的```json标记
            cleaned = content.strip()
            
            # 移除开头的```json或```
            if cleaned.startswith('```json'):
                cleaned = cleaned[7:].strip()
            elif cleaned.startswith('```'):
                cleaned = cleaned[3:].strip()
            
            # 移除结尾的```
            if cleaned.endswith('```'):
                cleaned = cleaned[:-3].strip()
            
            # 如果内容是JSON格式，尝试解析并重新格式化
            if cleaned.startswith('{') and cleaned.endswith('}'):
                try:
                    json_data = json.loads(cleaned)
                    # 如果有file字段，使用file内容
                    if 'file' in json_data and json_data['file']:
                        cleaned = json_data['file']
                    # 否则重新格式化JSON
                    else:
                        cleaned = json.dumps(json_data, ensure_ascii=False, indent=2)
                except json.JSONDecodeError:
                    # 如果JSON解析失败，保持原内容
                    pass
            
            logger.info(f"需求文档内容清理完成，原长度: {len(content)}, 清理后长度: {len(cleaned)}")
            return cleaned
            
        except Exception as e:
            logger.warning(f"清理需求文档内容时发生错误: {str(e)}，使用原内容")
            return content
    
    def _split_prototype_content(self, raw_content: str) -> list:
        """基于filename数量切分原型内容，提取多个JSON对象
        
        Args:
            raw_content: 原始原型内容
            
        Returns:
            解析后的文件列表
        """
        try:
            logger.info(f"开始切分原型内容，原始长度: {len(raw_content)} 字符")
            
            # 清理内容，移除markdown代码块标记
            cleaned_content = re.sub(r'```json\s*', '', raw_content)
            cleaned_content = re.sub(r'```\s*', '', cleaned_content)
            
            # 找到所有"file_name"字段的位置
            file_name_positions = []
            for match in re.finditer(r'"file_name"\s*:', cleaned_content):
                file_name_positions.append(match.start())
            
            logger.info(f"找到 {len(file_name_positions)} 个file_name字段")
            
            if len(file_name_positions) == 0:
                logger.warning("未找到任何file_name字段")
                return []
            
            files = []
            
            for i, pos in enumerate(file_name_positions):
                try:
                    # 从当前file_name位置向前查找最近的左大括号
                    start_pos = pos
                    while start_pos > 0 and cleaned_content[start_pos] != '{':
                        start_pos -= 1
                    
                    # 确定结束位置：下一个file_name的起始位置或内容末尾
                    if i + 1 < len(file_name_positions):
                        # 从下一个file_name位置向前查找最近的右大括号
                        next_pos = file_name_positions[i + 1]
                        end_search_pos = next_pos
                        while end_search_pos > pos and cleaned_content[end_search_pos] != '}':
                            end_search_pos -= 1
                        end_pos = end_search_pos + 1
                    else:
                        # 最后一个文件，查找到内容末尾的最后一个右大括号
                        end_pos = len(cleaned_content)
                        while end_pos > pos and cleaned_content[end_pos - 1] != '}':
                            end_pos -= 1
                    
                    # 提取JSON块
                    json_block = cleaned_content[start_pos:end_pos].strip()
                    
                    # 清理可能的多余字符
                    json_block = json_block.strip().rstrip(',')
                    
                    logger.info(f"提取第 {i+1} 个JSON块，长度: {len(json_block)} 字符")
                    
                    # 尝试解析JSON
                    try:
                        obj = json.loads(json_block)
                        if 'file_name' in obj and 'content' in obj:
                            files.append(obj)
                            logger.info(f"成功解析第 {i+1} 个JSON对象: {obj.get('file_name', '未知文件')}")
                        else:
                            logger.warning(f"第 {i+1} 个JSON对象缺少必要字段")
                    except json.JSONDecodeError as e:
                        logger.warning(f"解析第 {i+1} 个JSON块失败: {e}")
                        logger.debug(f"失败的JSON块内容: {json_block[:200]}...")
                        
                        # 尝试修复常见问题
                        try:
                            # 修复转义字符问题
                            fixed_block = json_block.replace('\\n', '\\\\n').replace('\\"', '\\\\"')
                            obj = json.loads(fixed_block)
                            if 'file_name' in obj and 'content' in obj:
                                files.append(obj)
                                logger.info(f"修复后成功解析第 {i+1} 个JSON对象: {obj.get('file_name', '未知文件')}")
                        except json.JSONDecodeError as e2:
                            logger.warning(f"修复后仍无法解析第 {i+1} 个JSON块: {e2}")
                            continue
                            
                except Exception as e:
                    logger.error(f"处理第 {i+1} 个文件块时发生错误: {str(e)}")
                    continue
                
            logger.info(f"原型内容切分完成，成功解析 {len(files)} 个文件")
            return files
            
        except Exception as e:
            logger.error(f"切分原型内容时发生错误: {str(e)}")
            return []
    
    def _prepare_prototype_input(self, requirements_content: str, project_description: str) -> str:
        """准备原型生成的输入内容
        
        Args:
            requirements_content: 需求文档内容
            project_description: 项目描述
            
        Returns:
            原型生成的输入内容
        """
        prototype_input = f"""
基于以下需求文档生成项目原型：

## 项目描述
{project_description}

## 详细需求文档
{requirements_content}

## 原型要求
请生成一个完整的HTML原型页面，包含：
1. 现代化的UI设计
2. 响应式布局
3. 主要功能界面
4. 交互元素和导航
5. 符合需求文档中描述的功能结构
"""
        
        return prototype_input
    
    def _prepare_backend_params(self, requirements_result: Dict[str, Any], 
                              prototype_result: Dict[str, Any], 
                              project_description: str) -> Dict[str, Any]:
        """准备后台调用参数
        按照ProjectGenerateRequest格式
        
        Args:
            requirements_result: 需求文档生成结果
            prototype_result: 原型生成结果
            project_description: 项目描述
            
        Returns:
            后台调用参数
        """
        # 使用默认项目名称和数据库类型
        project_name = "AI生成项目"
        database_type = "mysql"
        
        # 准备文件列表 - 按照FileParameter格式
        files = []
        
        # 添加需求文档
        files.append({
            "fileName": requirements_result['file_name'],
            "content": requirements_result['file_content'],
            "type": "requirements_document"
        })
        
        # 使用正则表达式切分原型内容，提取多个文件
        prototype_files = self._split_prototype_content(prototype_result['file_content'])
        
        if prototype_files:
            # 如果成功切分出多个文件，添加每个文件
            for prototype_file in prototype_files:
                file_name = prototype_file.get('file_name', 'prototype.html')
                file_content = prototype_file.get('content', '')
                files.append({
                    "fileName": file_name,
                    "content": file_content,
                    "type": "prototype_file"
                })
            logger.info(f"成功添加 {len(prototype_files)} 个原型文件到后台参数")
        else:
            # 如果切分失败，使用原始原型文件
            files.append({
                "fileName": prototype_result['file_name'],
                "content": prototype_result['file_content'],
                "type": "prototype_file"
            })
            logger.warning("原型切分失败，使用原始原型文件")
        
        # 构建后台参数 - 按照ProjectGenerateRequest格式
        # 注意：不包含metadata字段，因为ProjectGenerateRequest类不支持该字段
        backend_params = {
            "projectName": project_name,
            "projectDescription": project_description,
            "databaseType": database_type,
            "files": files
        }
        
        # 记录元数据信息到日志中
        logger.info(f"后台参数准备完成:")
        logger.info(f"  - 项目名称: {project_name}")
        logger.info(f"  - 数据库类型: {database_type}")
        logger.info(f"  - 文件数量: {len(files)}")
        logger.info(f"  - 需求文档长度: {len(requirements_result['file_content'])} 字符")
        logger.info(f"  - 原型内容长度: {len(prototype_result['file_content'])} 字符")
        
        return backend_params
    
    def save_generated_files(self, result: Dict[str, Any], output_dir: str = '.') -> Dict[str, str]:
        """保存生成的文件
        
        Args:
            result: 生成结果
            output_dir: 输出目录
            
        Returns:
            保存的文件路径
        """
        saved_files = {}
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        
        try:
            if result.get('requirements_document'):
                req_file = f"{output_dir}/requirements_{timestamp}.md"
                with open(req_file, 'w', encoding='utf-8') as f:
                    f.write(result['requirements_document']['file_content'])
                saved_files['requirements'] = req_file
                logger.info(f"需求文档已保存: {req_file}")
            
            if result.get('prototype'):
                proto_result = result['prototype']
                file_ext = {
                    'html': '.html',
                    'css': '.css',
                    'javascript': '.js',
                    'json': '.json',
                    'markdown': '.md'
                }.get(proto_result['file_type'], '.txt')
                
                proto_file = f"{output_dir}/prototype_{timestamp}{file_ext}"
                with open(proto_file, 'w', encoding='utf-8') as f:
                    f.write(proto_result['file_content'])
                saved_files['prototype'] = proto_file
                logger.info(f"原型文件已保存: {proto_file}")
            
            if result.get('backend_params'):
                params_file = f"{output_dir}/backend_params_{timestamp}.json"
                with open(params_file, 'w', encoding='utf-8') as f:
                    json.dump(result['backend_params'], f, ensure_ascii=False, indent=2)
                saved_files['backend_params'] = params_file
                logger.info(f"后台参数已保存: {params_file}")
            
        except Exception as e:
            logger.error(f"保存文件时发生错误: {str(e)}")
        
        return saved_files
    

    
    def get_service_info(self) -> Dict[str, Any]:
        """获取服务信息
        
        Returns:
            服务配置信息
        """
        return {
            'service_name': 'IntegratedFlowiseService',
            'version': '1.0.0',
            'requirements_workflow': self.requirements_client.get_workflow_info(),
            'prototype_workflow': self.prototype_client.get_workflow_info(),
            'capabilities': [
                '需求文档生成',
                '原型生成',
                '内容清理和格式化',
                '后台参数准备',
                '文件保存'
            ]
        }