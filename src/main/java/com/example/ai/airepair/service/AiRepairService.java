package com.example.ai.airepair.service;

import java.util.List;

public interface AiRepairService {



    /**
     * 修复项目代码
     * @param projectData 项目代码字节数组
     * @return 修复后的项目代码字节数组
     * @throws Exception
     */
    byte[] repairCode(byte[] projectData) throws Exception;
    
    /**
     * 修复代码中的导入语句
     * @param code 需要修复的代码
     * @param importWhitelist 导入白名单
     * @return 修复后的代码
     */
    String fixImports(String code, List<String> importWhitelist);
}