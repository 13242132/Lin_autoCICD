#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Flowise客户端包

提供与Flowise工作流交互的客户端类
"""

from .requirements_client import RequirementsDocumentClient
from .prototype_client import PrototypeGeneratorClient

__all__ = [
    'RequirementsDocumentClient',
    'PrototypeGeneratorClient'
]

__version__ = '1.0.0'
__author__ = 'AutoCICD Team'
__description__ = 'Flowise工作流客户端包，提供需求文档生成和原型生成功能'