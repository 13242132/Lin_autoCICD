#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Flowise工作流配置文件
"""

class FlowiseConfig:
    """Flowise配置类"""
    
    # Flowise服务基础配置
    BASE_URL = "http://localhost:3000/api/v1/prediction"
    AUTH_TOKEN = "B1O4BXKWOTP_47B1zPKFyR-K6ytVG2vf87nTgXsrDds"
    
    # 工作流ID配置
    WORKFLOWS = {
        "requirements_document": {
            "id": "43cd9fa1-1a4d-4e2e-af5a-8a140c044747",
            "name": "需求文档生成器",
            "description": "根据项目描述生成详细的需求文档",
            "timeout": 1200,
            "output_format": "json"
        },
        "prototype_generator": {
            "id": "c3e7a768-6553-4668-8448-e4c043c98f5a",
            "name": "原型生成器",
            "description": "根据需求生成项目原型文件",
            "timeout": 1200,
            "output_format": "text"
        }
    }
    
    # 请求配置
    REQUEST_CONFIG = {
        "headers": {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {AUTH_TOKEN}"
        },
        "default_timeout": 300,
        "max_retries": 3,
        "retry_delay": 5
    }
    
    @classmethod
    def get_workflow_url(cls, workflow_type: str) -> str:
        """获取工作流的完整URL"""
        if workflow_type not in cls.WORKFLOWS:
            raise ValueError(f"未知的工作流类型: {workflow_type}")
        
        workflow_id = cls.WORKFLOWS[workflow_type]["id"]
        return f"{cls.BASE_URL}/{workflow_id}"
    
    @classmethod
    def get_workflow_config(cls, workflow_type: str) -> dict:
        """获取工作流配置"""
        if workflow_type not in cls.WORKFLOWS:
            raise ValueError(f"未知的工作流类型: {workflow_type}")
        
        return cls.WORKFLOWS[workflow_type]
    
    @classmethod
    def get_available_workflows(cls) -> list:
        """获取所有可用的工作流类型"""
        return list(cls.WORKFLOWS.keys())

        