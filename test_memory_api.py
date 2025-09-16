#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
记忆管理API测试脚本
"""

import requests
import json
import logging
import time
from typing import Dict, Any, Optional, List

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class MemoryApiClient:
    """记忆管理API客户端"""
    
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            "Content-Type": "application/json"
        })
    
    def save_chat_memory(self, project_id: str, user_message: str, ai_response: str, memory_type: str = "CHAT") -> Dict[str, Any]:
        """保存聊天记忆
        
        Args:
            project_id: 项目ID
            user_message: 用户消息
            ai_response: AI回复
            memory_type: 记忆类型
            
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/save"
            payload = {
                "projectId": project_id,
                "userMessage": user_message,
                "aiResponse": ai_response,
                "type": memory_type
            }
            
            logger.info(f"保存聊天记忆: {project_id}")
            response = self.session.post(url, json=payload)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"保存结果: {result}")
            return result
            
        except Exception as e:
            logger.error(f"保存聊天记忆失败: {str(e)}")
            return {"success": False, "error": str(e)}
    
    def save_batch_chat_memory(self, memories: List[Dict[str, str]]) -> Dict[str, Any]:
        """批量保存聊天记忆
        
        Args:
            memories: 记忆列表，每个元素包含projectId, userMessage, aiResponse, type字段
            
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/save-batch"
            
            logger.info(f"批量保存聊天记忆: {len(memories)} 条")
            response = self.session.post(url, json=memories)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"批量保存结果: {result}")
            return result
            
        except Exception as e:
            logger.error(f"批量保存聊天记忆失败: {str(e)}")
            return {"success": False, "error": str(e)}
    
    def query_chat_memory(self, project_id: str, limit: int = 10, memory_type: str = None) -> Dict[str, Any]:
        """查询聊天记忆
        
        Args:
            project_id: 项目ID
            limit: 查询数量限制
            memory_type: 记忆类型过滤
            
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/query"
            payload = {
                "projectId": project_id,
                "limit": limit
            }
            
            if memory_type:
                payload["type"] = memory_type
            
            logger.info(f"查询聊天记忆: {project_id}, 限制: {limit}")
            response = self.session.post(url, json=payload)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"查询到 {len(result[0].get('memories', []))} 条记录")
            return result
            
        except Exception as e:
            logger.error(f"查询聊天记忆失败: {str(e)}")
            return [{"projectId": project_id, "sessionId": None, "memories": []}]
    
    def get_recent_chat_history(self, project_id: str, limit: int = 10) -> Dict[str, Any]:
        """获取最近的聊天历史
        
        Args:
            project_id: 项目ID
            limit: 查询数量限制
            
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/project/{project_id}/recent"
            params = {"limit": limit}
            
            logger.info(f"获取最近聊天历史: {project_id}, 限制: {limit}")
            response = self.session.get(url, params=params)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"获取到 {len(result[0].get('memories', []))} 条历史记录")
            return result
            
        except Exception as e:
            logger.error(f"获取最近聊天历史失败: {str(e)}")
            return [{"projectId": project_id, "sessionId": None, "memories": []}]
    
    def get_recent_session_history(self, project_id: str, session_id: str, limit: int = 10) -> Dict[str, Any]:
        """获取最近会话历史
        
        Args:
            project_id: 项目ID
            session_id: 会话ID
            limit: 查询数量限制
            
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/project/{project_id}/session/recent"
            params = {"limit": limit, "sessionId": session_id}
            
            logger.info(f"获取最近会话历史: {project_id}/{session_id}, 限制: {limit}")
            response = self.session.get(url, params=params)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"获取到 {len(result[0].get('memories', []))} 条会话记录")
            return result
            
        except Exception as e:
            logger.error(f"获取最近会话历史失败: {str(e)}")
            return [{"projectId": project_id, "sessionId": session_id, "memories": []}]
    
    def delete_project_memory(self, project_id: str) -> Dict[str, Any]:
        """删除项目记忆
        
        Args:
            project_id: 项目ID
            
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/project/{project_id}"
            
            logger.info(f"删除项目记忆: {project_id}")
            response = self.session.delete(url)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"删除结果: {result}")
            return result
            
        except Exception as e:
            logger.error(f"删除项目记忆失败: {str(e)}")
            return {"success": False, "error": str(e)}
    
    def clean_expired_memory(self) -> Dict[str, Any]:
        """清理过期记忆
        
        Returns:
            API响应结果
        """
        try:
            url = f"{self.base_url}/api/memory/clean-expired"
            
            logger.info("清理过期记忆")
            response = self.session.post(url)
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"清理结果: {result}")
            return result
            
        except Exception as e:
            logger.error(f"清理过期记忆失败: {str(e)}")
            return {"success": False, "error": str(e)}


def run_memory_api_tests():
    """运行记忆管理API测试"""
    client = MemoryApiClient()
    
    # 测试项目ID
    test_project_id = f"test_project_{int(time.time())}"
    
    print("=" * 60)
    print("开始记忆管理API测试")
    print("=" * 60)
    
    # 1. 测试保存单条聊天记忆
    print("\n1. 测试保存单条聊天记忆")
    save_result = client.save_chat_memory(
        project_id=test_project_id,
        user_message="你好，我想创建一个电商网站",
        ai_response="好的，我可以帮你创建一个电商网站。请告诉我你的具体需求。",
        memory_type="CHAT"
    )
    print(f"保存结果: {save_result}")
    
  
    # 3. 测试查询聊天记忆
    print("\n3. 测试查询聊天记忆")
    query_result = client.query_chat_memory(project_id=test_project_id, limit=10)
    print(f"查询结果数量: {len(query_result[0].get('memories', []))}")
    for i, memory in enumerate(query_result[0].get('memories', [])[:3]):
        print(f"  记录 {i+1}: {memory.get('userMessage', 'N/A')}")
    
    # 4. 测试获取最近聊天历史
    print("\n4. 测试获取最近聊天历史")
    history_result = client.get_recent_chat_history(project_id=test_project_id, limit=5)
    print(f"历史记录数量: {len(history_result[0].get('memories', []))}")
    
    # 5. 测试获取最近会话历史
    print("\n5. 测试获取最近会话历史")
    session_id = "session_001"
    session_history_result = client.get_recent_session_history(
        project_id=test_project_id, 
        session_id=session_id, 
        limit=5
    )
    print(f"会话记录数量: {len(session_history_result[0].get('memories', []))}")
    
    # 6. 测试删除项目记忆
    print("\n6. 测试删除项目记忆")
    delete_result = client.delete_project_memory(project_id=test_project_id)
    print(f"删除结果: {delete_result}")
    
    # 7. 验证删除后查询结果为空
    print("\n7. 验证删除后查询结果为空")
    after_delete_query = client.query_chat_memory(project_id=test_project_id, limit=10)
    print(f"删除后查询结果数量: {len(after_delete_query[0].get('memories', []))}")
    
    # 8. 测试清理过期记忆
    print("\n8. 测试清理过期记忆")
    clean_result = client.clean_expired_memory()
    print(f"清理结果: {clean_result}")
    
    print("\n" + "=" * 60)
    print("记忆管理API测试完成")
    print("=" * 60)


if __name__ == "__main__":
    run_memory_api_tests()