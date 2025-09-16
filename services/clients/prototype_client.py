#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
原型生成客户端
"""

import requests
import json
import logging
from typing import Dict, Any, Optional
from ..config.flowise_config import FlowiseConfig

logger = logging.getLogger(__name__)

class PrototypeGeneratorClient:
    """原型生成客户端"""
    
    def __init__(self):
        self.workflow_type = "prototype_generator"
        self.config = FlowiseConfig.get_workflow_config(self.workflow_type)
        self.url = FlowiseConfig.get_workflow_url(self.workflow_type)
        self.session = requests.Session()
        self.session.headers.update(FlowiseConfig.REQUEST_CONFIG["headers"])
    
    def generate_prototype(self, input_content: str) -> Optional[Dict[str, Any]]:
        """生成原型文件
        
        Args:
            input_content: 输入内容（可以是项目描述或需求文档）
            
        Returns:
            包含生成原型信息的字典，失败时返回None
        """
        try:
            logger.info(f"开始生成原型文件，工作流ID: {self.config['id']}")
            logger.info(f"输入内容长度: {len(input_content)} 字符")
            
            # 构建请求数据
            payload = {
                "question": input_content
            }
            
            # 发送请求
            response = self.session.post(
                self.url,
                json=payload,
                timeout=self.config.get("timeout", 800)
            )
            
            response.raise_for_status()
            result = response.json()
            
            # 解析响应
            return self._parse_response(result)
            
        except requests.exceptions.Timeout:
            logger.error("原型生成请求超时")
            return None
        except requests.exceptions.RequestException as e:
            logger.error(f"原型生成请求失败: {str(e)}")
            return None
        except Exception as e:
            logger.error(f"原型生成过程中发生未知错误: {str(e)}")
            return None
    
    def _parse_response(self, response: Dict[str, Any]) -> Optional[Dict[str, Any]]:
        """解析Flowise响应
        
        Args:
            response: Flowise API响应
            
        Returns:
            解析后的原型信息
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
                prototype_data = json.loads(response['text'])
                file_name = prototype_data.get('name', '原型文件.html')
                file_content = prototype_data.get('file', prototype_data.get('content', ''))
                
                if not file_content:
                    logger.warning("生成的原型文件内容为空，使用原始响应")
                    file_content = response['text']
                
                logger.info(f"原型文件生成成功: {file_name}, 内容长度: {len(file_content)} 字符")
                
                return {
                    'success': True,
                    'file_name': file_name,
                    'file_content': file_content,
                    'file_type': self._determine_file_type(file_name, file_content),
                    'workflow_id': self.config['id'],
                    'workflow_name': self.config['name']
                }
                
            except json.JSONDecodeError:
                # 如果不是JSON格式，直接使用文本内容
                logger.info("响应不是JSON格式，直接使用文本内容")
                file_content = response['text']
                file_name = self._generate_filename(file_content)
                
                return {
                    'success': True,
                    'file_name': file_name,
                    'file_content': file_content,
                    'file_type': self._determine_file_type(file_name, file_content),
                    'workflow_id': self.config['id'],
                    'workflow_name': self.config['name']
                }
                
        except Exception as e:
            logger.error(f"解析原型响应时发生错误: {str(e)}")
            return None
    
    def _determine_file_type(self, file_name: str, file_content: str) -> str:
        """根据文件名和内容确定文件类型
        
        Args:
            file_name: 文件名
            file_content: 文件内容
            
        Returns:
            文件类型
        """
        # 根据文件扩展名判断
        if file_name.endswith('.html'):
            return 'html'
        elif file_name.endswith('.css'):
            return 'css'
        elif file_name.endswith('.js'):
            return 'javascript'
        elif file_name.endswith('.json'):
            return 'json'
        elif file_name.endswith('.md'):
            return 'markdown'
        
        # 根据内容判断
        content_lower = file_content.lower().strip()
        if content_lower.startswith('<!doctype html') or content_lower.startswith('<html'):
            return 'html'
        elif content_lower.startswith('{') and content_lower.endswith('}'):
            return 'json'
        elif '```' in file_content or file_content.startswith('#'):
            return 'markdown'
        
        # 默认为文本
        return 'text'
    
    def _generate_filename(self, content: str) -> str:
        """根据内容生成合适的文件名
        
        Args:
            content: 文件内容
            
        Returns:
            生成的文件名
        """
        file_type = self._determine_file_type('', content)
        
        if file_type == 'html':
            return '原型页面.html'
        elif file_type == 'css':
            return '样式文件.css'
        elif file_type == 'javascript':
            return '脚本文件.js'
        elif file_type == 'json':
            return '配置文件.json'
        elif file_type == 'markdown':
            return '原型文档.md'
        else:
            return '原型文件.txt'
    
    def validate_input(self, input_content: str) -> bool:
        """验证输入参数
        
        Args:
            input_content: 输入内容
            
        Returns:
            验证是否通过
        """
        if not input_content or not input_content.strip():
            logger.error("输入内容不能为空")
            return False
        
        if len(input_content.strip()) < 10:
            logger.error("输入内容过短，至少需要10个字符")
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