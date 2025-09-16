#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
完整项目生成服务

集成integrated_service和后端接口，提供完整的项目生成流程
"""

import logging
import json
import requests
from typing import Dict, Any, Optional
from datetime import datetime

from .integrated_service import IntegratedFlowiseService

logger = logging.getLogger(__name__)

class CompleteProjectService:
    """完整项目生成服务"""
    
    def __init__(self, backend_base_url: str = "http://localhost:8080"):
        self.integrated_service = IntegratedFlowiseService()
        self.backend_base_url = backend_base_url
        self.session = requests.Session()
        
    def check_backend_health(self) -> bool:
        """检查后端服务健康状态"""
        try:
            response = self.session.get(
                f"{self.backend_base_url}/api/v2/project/health",
                timeout=20
            )
            if response.status_code == 200:
                health_data = response.json()
                logger.info(f"后端服务健康状态: {health_data}")
                return True
            else:
                logger.error(f"后端服务健康检查失败: {response.status_code}")
                return False
        except Exception as e:
            logger.error(f"后端服务健康检查异常: {str(e)}")
            return False
    
    def generate_complete_project(self, project_description: str, 
                                project_name: str = None,
                                database_type: str = "mysql") -> Dict[str, Any]:
        """生成完整项目
        
        Args:
            project_description: 项目描述
            project_name: 项目名称（可选）
            database_type: 数据库类型（默认mysql）
            
        Returns:
            包含项目生成结果的字典
        """
        result = {
            'success': False,
            'message': '',
            'zip_data': None,
            'filename': None,
            'metadata': {
                'generated_at': datetime.now().isoformat(),
                'project_name': project_name or "AI生成项目",
                'database_type': database_type,
                'processing_steps': []
            }
        }
        
        try:
            logger.info(f"开始完整项目生成流程: {project_description[:100]}...")
            
            # 步骤1: 检查后端服务
            result['metadata']['processing_steps'].append("检查后端服务健康状态")
            if not self.check_backend_health():
                result['message'] = '后端服务不可用，请检查后端服务是否正常运行'
                return result
            
            # 步骤2: 生成项目文档和后端参数
            result['metadata']['processing_steps'].append("生成需求文档和原型")
            logger.info("步骤2: 生成项目文档和后端参数")
            
            documents_result = self.integrated_service.generate_project_documents(project_description)
            
            if not documents_result.get('success'):
                result['message'] = f"文档生成失败: {documents_result.get('error', '未知错误')}"
                return result
            
            backend_params = documents_result.get('backend_params')
            if not backend_params:
                result['message'] = '后端参数生成失败'
                return result
            
            # 更新项目名称和数据库类型
            if project_name:
                backend_params['projectName'] = project_name
            if database_type:
                backend_params['databaseType'] = database_type
            
            result['metadata']['backend_params_summary'] = {
                'files_count': len(backend_params.get('files', [])),
                'project_name': backend_params.get('projectName'),
                'database_type': backend_params.get('databaseType')
            }
            
            logger.info(f"后端参数准备完成，包含 {len(backend_params.get('files', []))} 个文件")
            
            # 步骤3: 调用后端接口生成项目zip
            result['metadata']['processing_steps'].append("调用后端接口生成项目")
            logger.info("步骤3: 调用后端接口生成项目zip")
            
            zip_result = self._call_backend_generate_project(backend_params)
            
            if zip_result.get('success'):
                result['success'] = True
                result['message'] = '项目生成成功'
                result['zip_data'] = zip_result['zip_data']
                result['filename'] = zip_result.get('filename', f"{backend_params.get('projectName', 'project')}.zip")
                result['metadata']['zip_size'] = len(zip_result['zip_data'])
                result['metadata']['processing_steps'].append("项目生成完成")
                
                logger.info(f"项目生成成功，ZIP文件大小: {len(zip_result['zip_data'])} 字节")
            else:
                result['message'] = f"后端项目生成失败: {zip_result.get('message', '未知错误')}"
                
        except Exception as e:
            logger.error(f"完整项目生成过程中发生错误: {str(e)}")
            result['message'] = f'项目生成过程中发生错误: {str(e)}'
            result['metadata']['processing_steps'].append(f"错误: {str(e)}")
        
        return result
    
    def _call_backend_generate_project(self, backend_params: Dict[str, Any]) -> Dict[str, Any]:
        """调用后端接口生成项目
        
        Args:
            backend_params: 后端参数
            
        Returns:
            后端调用结果
        """
        try:
            logger.info(f"调用后端接口: {self.backend_base_url}/api/v2/project/generate-full")
            logger.debug(f"请求参数: {json.dumps(backend_params, ensure_ascii=False, indent=2)[:500]}...")
            
            # 调整后台生成项目的超时时间为1200秒（20分钟）
            response = self.session.post(
                f"{self.backend_base_url}/api/v2/project/generate-full",
                json=backend_params,
                headers={"Content-Type": "application/json"},
                timeout=1200  # 1200秒超时（20分钟）
            )
            
            if response.status_code == 200:
                # 检查响应内容类型
                content_type = response.headers.get('Content-Type', '')
                if 'application/octet-stream' in content_type or 'application/zip' in content_type:
                    # ZIP文件响应
                    filename = self._extract_filename_from_headers(response.headers)
                    logger.info(f"后端项目生成成功，ZIP文件: {filename}, 大小: {len(response.content)} 字节")
                    
                    return {
                        'success': True,
                        'message': '后端项目生成成功',
                        'zip_data': response.content,
                        'filename': filename,
                        'content_type': content_type
                    }
                else:
                    # 可能是错误响应
                    logger.error(f"后端返回非ZIP内容: {content_type}")
                    return {
                        'success': False,
                        'message': f'后端返回非ZIP内容: {response.text[:200]}'
                    }
            else:
                logger.error(f"后端接口调用失败: {response.status_code} - {response.text}")
                return {
                    'success': False,
                    'message': f'后端接口调用失败: {response.status_code} - {response.text}'
                }
                
        except requests.exceptions.Timeout:
            logger.error("后端接口调用超时")
            return {
                'success': False,
                'message': '后端接口调用超时，请稍后重试'
            }
        except Exception as e:
            logger.error(f"调用后端接口时发生错误: {str(e)}")
            return {
                'success': False,
                'message': f'调用后端接口时发生错误: {str(e)}'
            }
    
    def _extract_filename_from_headers(self, headers) -> str:
        """从响应头中提取文件名"""
        content_disposition = headers.get('Content-Disposition', '')
        if 'filename=' in content_disposition:
            # 提取filename=后面的内容
            filename_part = content_disposition.split('filename=')[1]
            # 移除可能的引号
            filename = filename_part.strip('"').strip("'")
            return filename
        return 'generated-project.zip'
    
    def save_project_zip(self, zip_data: bytes, filename: str, output_dir: str = '.') -> str:
        """保存项目ZIP文件
        
        Args:
            zip_data: ZIP文件数据
            filename: 文件名
            output_dir: 输出目录
            
        Returns:
            保存的文件路径
        """
        try:
            import os
            
            # 确保输出目录存在
            os.makedirs(output_dir, exist_ok=True)
            
            # 构建完整文件路径
            file_path = os.path.join(output_dir, filename)
            
            # 写入ZIP文件
            with open(file_path, 'wb') as f:
                f.write(zip_data)
            
            logger.info(f"项目ZIP文件已保存: {file_path}")
            return file_path
            
        except Exception as e:
            logger.error(f"保存ZIP文件时发生错误: {str(e)}")
            raise
    
    def get_service_info(self) -> Dict[str, Any]:
        """获取服务信息"""
        return {
            'service_name': 'CompleteProjectService',
            'version': '1.0.0',
            'backend_url': self.backend_base_url,
            'integrated_service_info': self.integrated_service.get_service_info(),
            'capabilities': [
                '完整项目生成流程',
                '后端服务健康检查',
                '文档和原型生成',
                '后端参数准备',
                '后端接口调用',
                'ZIP文件处理'
            ]
        }