package com.example.ai.airepair.service;

import com.example.ai.airepair.dto.FileReadRequest;
import com.example.ai.airepair.dto.FileReadResponse;
import com.example.ai.airepair.dto.FileOperationRequest;
import com.example.ai.airepair.dto.FileOperationResponse;

public interface FileReadService {
    
    /**
     * 读取指定路径下的文件内容
     * @param request 文件读取请求参数
     * @return 文件读取响应结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    FileReadResponse readFiles(FileReadRequest request) throws Exception;
    
    /**
     * 读取单个文件内容
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws Exception 处理过程中可能抛出的异常
     */
    String readSingleFile(String filePath) throws Exception;
    
    /**
     * 检查文件是否存在
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    boolean fileExists(String filePath);
    
    /**
     * 列出指定路径下的所有子文件和目录
     * @param path 路径
     * @param includeSubdirectories 是否包含子目录
     * @return 文件和目录列表
     * @throws Exception 处理过程中可能抛出的异常
     */
    String listAllFiles(String path, boolean includeSubdirectories) throws Exception;
    
    /**
     * 统一文件操作接口
     * @param request 统一文件操作请求参数
     * @return 统一文件操作响应结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    FileOperationResponse executeFileOperation(FileOperationRequest request) throws Exception;
}