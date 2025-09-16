package com.example.ai.airepair.service.impl;

import com.example.ai.airepair.service.AiRepairService;
import com.example.ai.airepair.constant.EntityImportConstants;
import com.example.ai.airepair.constant.ImportFixConstants;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AiRepairServiceImpl implements AiRepairService {

    @Override
    public byte[] repairCode(byte[] projectData) throws Exception {
        // TODO: Implement AI repair logic
        return projectData;
    }

    @Override
    public String fixImports(String code, List<String> importWhitelist) {
        // 1. 收集已有 import
        Set<String> existingImports = new HashSet<>();
        for (String line : code.split("\n")) {
            if (line.trim().startsWith("import ")) {
                existingImports.add(line.trim());
            }
        }

        // 2. 计算缺失 import
        List<String> missingImports = new ArrayList<>();
        for (String imp : importWhitelist) {
            if (!existingImports.contains(imp)) {
                missingImports.add(imp);
            }
        }

        // 3. 插入到 package 后
        StringBuilder newCode = new StringBuilder();
        boolean inserted = false;
        for (String line : code.split("\n")) {
            newCode.append(line).append("\n");
            if (!inserted && line.startsWith("package ")) {
                for (String imp : missingImports) {
                    newCode.append(imp).append("\n");
                }
                newCode.append("\n");
                inserted = true;
            }
        }
        return newCode.toString();
    }


}