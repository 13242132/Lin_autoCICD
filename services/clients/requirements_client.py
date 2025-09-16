#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
需求文档生成客户端
"""

import requests
import json
import logging
from typing import Dict, Any, Optional
from ..config.flowise_config import FlowiseConfig

logger = logging.getLogger(__name__)

class RequirementsDocumentClient:
    """需求文档生成客户端"""
    
    def __init__(self):
        self.workflow_type = "requirements_document"
        self.config = FlowiseConfig.get_workflow_config(self.workflow_type)
        self.url = FlowiseConfig.get_workflow_url(self.workflow_type)
        self.session = requests.Session()
        self.session.headers.update(FlowiseConfig.REQUEST_CONFIG["headers"])
    
    def generate_requirements_document(self, project_description: str) -> Optional[Dict[str, Any]]:
        """生成需求文档
        
        Args:
            project_description: 项目描述
            
        Returns:
            包含生成文档信息的字典，失败时返回None
        """
        try:
            logger.info(f"开始生成需求文档，工作流ID: {self.config['id']}")
            logger.info(f"项目描述: {project_description[:100]}...")
            
            # 构建请求数据
            payload = {
                "question": project_description
            }
            
            # 发送请求
            response = self.session.post(
                self.url,
                json=payload,
                timeout=self.config.get("timeout", 600)
            )
            
            response.raise_for_status()
            result = response.json()
            
            # 解析响应
            return self._parse_response(result)
            
        except requests.exceptions.Timeout:
            logger.error("需求文档生成请求超时")
            return None
        except requests.exceptions.RequestException as e:
            logger.error(f"需求文档生成请求失败: {str(e)}")
            return None
        except Exception as e:
            logger.error(f"需求文档生成过程中发生未知错误: {str(e)}")
            return None
    
    def _parse_response(self, response: Dict[str, Any]) -> Optional[Dict[str, Any]]:
        """解析Flowise响应
        
        Args:
            response: Flowise API响应
            
        Returns:
            解析后的文档信息
        """
        try:
            if 'error' in response:
                logger.error(f"Flowise返回错误: {response['error']}")
                return None
            
            if 'text' not in response or not response['text']:
                logger.error("Flowise响应中缺少text字段或为空")
                return None
            
            # 尝试解析JSON格式的响应
            try:
                document_data = json.loads(response['text'])
                file_name = document_data.get('name', '需求文档.md')
                file_content = document_data.get('file', '')
                
                if not file_content:
                    logger.warning("生成的需求文档内容为空")
                    return None
                
                logger.info(f"需求文档生成成功: {file_name}, 内容长度: {len(file_content)} 字符")
                
                return {
                    'success': True,
                    'file_name': file_name,
                    'file_content': file_content,
                    'file_type': 'markdown',
                    'workflow_id': self.config['id'],
                    'workflow_name': self.config['name']
                }
                
            except json.JSONDecodeError:
                # 如果不是JSON格式，直接使用文本内容
                logger.info("响应不是JSON格式，直接使用文本内容")
                return {
                    'success': True,
                    'file_name': '需求文档.md',
                    'file_content': response['text'],
                    'file_type': 'markdown',
                    'workflow_id': self.config['id'],
                    'workflow_name': self.config['name']
                }
                
        except Exception as e:
            logger.error(f"解析需求文档响应时发生错误: {str(e)}")
            return None
    
    def validate_input(self, project_description: str) -> bool:
        """验证输入参数
        
        Args:
            project_description: 项目描述
            
        Returns:
            验证是否通过
        """
        if not project_description or not project_description.strip():
            logger.error("项目描述不能为空")
            return False
        
        if len(project_description.strip()) < 10:
            logger.error("项目描述过短，至少需要10个字符")
            return False
        
        return True
    
    def get_workflow_info(self) -> Dict[str, Any]:
        """获取工作流信息
        
        Returns:
            工作流配置信息
        """
        return {
            'workflow_type': self.workflow_type,
            'workflow_id': self.config['id'],
            'workflow_name': self.config['name'],
            'description': self.config['description'],
            'url': self.url,
            'timeout': self.config.get('timeout', 600)
        }