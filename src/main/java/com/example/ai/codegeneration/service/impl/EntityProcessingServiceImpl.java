package com.example.ai.codegeneration.service.impl;

import com.example.ai.airepair.constant.EntityImportConstants;
import com.example.ai.airepair.constant.DtoImportConstants;
import com.example.ai.airepair.service.AiRepairService;
import com.example.ai.codegeneration.service.EntityProcessingService;
import com.example.ai.flowise.agent.EntityGeneratorService;
import com.example.ai.flowise.agent.dto.WorkflowResponse;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实体处理服务实现类，负责处理AI生成实体的相关逻辑
 */
@Service
public class EntityProcessingServiceImpl implements EntityProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(EntityProcessingServiceImpl.class);
    
    @Autowired
    private AiRepairService aiRepairService;
    


    @Override
    public String processEntities(String entityList, EntityGeneratorService entityGeneratorService, Path tempDir) throws Exception {
        logger.info("开始调用实体生成AI");
        
        try {
            // 调用实体生成AI
            WorkflowResponse entityResponse = entityGeneratorService.generateEntity(entityList);
            logger.info("实体生成AI调用成功");

            // 使用MarkdownCleaner工具类清理实体生成AI响应中的Markdown标记
            String cleanedEntityResponse = com.example.ai.util.MarkdownCleaner.clean(entityResponse.getResponseContent());
            
            // 将清理后的实体生成AI响应写入压缩包，方便调试
            Path entityResponseFile = tempDir.resolve("entity-ai-response.txt");
            Files.writeString(entityResponseFile, cleanedEntityResponse);
            logger.info("实体生成AI响应已写入文件: {}", entityResponseFile);

            // 使用本地方法来切分和写入实体文件
            String[] entityParts = splitEntityResponse(cleanedEntityResponse);
            writeEntityPartsToFiles(entityParts, tempDir.toString());
            
            // 为每个实体生成对应的DTO类
            generateDtoClasses(entityParts, tempDir.toString());
            
            // 为每个DTO生成对应的CRUD接口
            for (String entityPart : entityParts) {
                if (entityPart != null && !entityPart.trim().isEmpty()) {
                    String className = extractClassName(entityPart);
                    if (className != null) {
                        generateCrudInterfaces(className, tempDir);
                    }
                }
            }
            
            // 返回完整的实体内容，供后续步骤使用
            return cleanedEntityResponse;
            
            } catch (Exception e) {
            logger.error("调用实体生成AI失败", e);
            throw e;
        }
    }
    
    /**
     * 为实体数组生成对应的DTO类
     * @param entityParts 实体数组
     * @param targetDir 目标目录
     * @return 是否生成成功
     */
    private boolean generateDtoClasses(String[] entityParts, String targetDir) {
        if (entityParts == null) {
            return false;
        }
        
        try {
            for (String entityPart : entityParts) {
                if (entityPart == null || entityPart.trim().isEmpty()) {
                    continue;
                }
                
                String className = extractClassName(entityPart);
                if (className != null) {
                    try {
                        // 使用JavaParser解析实体类源码
                        generateDtoForEntityContent(entityPart, className, targetDir);
                        logger.info("成功为实体 {} 生成DTO类", className);
                    } catch (Exception e) {
                        logger.error("生成实体 {} 的DTO类失败", className, e);
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.error("生成DTO类失败", e);
            return false;
        }
    }
    
    /**
     * 为单个实体生成QueryDTO类（基于JavaParser解析源码）
     * @param entityContent 实体类内容
     * @param className 实体类名
     * @param outputDir 输出目录
     * @return 是否生成成功
     */
    private boolean generateDtoForEntityContent(String entityContent, String className, String outputDir) {
        try {
            // 使用JavaParser解析实体类内容
            CompilationUnit cu = StaticJavaParser.parse(entityContent);
            
            // 获取类声明
            Optional<ClassOrInterfaceDeclaration> classOpt = cu.getClassByName(className);
            if (!classOpt.isPresent()) {
                logger.error("无法在解析的内容中找到类: {}", className);
                return false;
            }
            
            ClassOrInterfaceDeclaration clazz = classOpt.get();
            
            // 只生成QueryDTO类
            return generateDtoClass(clazz, outputDir);
            
        } catch (Exception e) {
            logger.error("生成QueryDTO类失败", e);
            return false;
        }
    }
    
    /**
     * 生成QueryDTO类的简化方法
     * @param clazz 实体类声明
     * @param outputDir 输出目录
     * @return 是否生成成功
     */
    private boolean generateDtoClass(ClassOrInterfaceDeclaration clazz, String outputDir) {
        try {
            // 获取实体类名
            String entityName = clazz.getNameAsString();
            String queryDtoName = entityName + "QueryDTO";
            
            // 定义QueryDTO包名
            String queryDtoPackage = "com.example.demo.api.querydto";
            
            // 创建QueryDTO类内容
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(queryDtoPackage).append(";");
            sb.append("\n\n");
            
            // 添加类声明，并添加@Data注解
            sb.append("@Data\n");
            sb.append("public class ").append(queryDtoName).append(" {\n\n");
            
            // 获取所有字段
            List<FieldDeclaration> fields = clazz.getFields();
            
            // 定义QueryDTO包含的类型
            Set<String> queryDtoTypes = new HashSet<>();
            queryDtoTypes.add("String");
            queryDtoTypes.add("LocalDateTime");
            queryDtoTypes.add("Long");
            queryDtoTypes.add("long");
            queryDtoTypes.add("Integer");
            queryDtoTypes.add("int");
            queryDtoTypes.add("Boolean");
            queryDtoTypes.add("boolean");
            queryDtoTypes.add("Double");
            queryDtoTypes.add("double");
            queryDtoTypes.add("Float");
            queryDtoTypes.add("float");
            
            // 添加字段
            for (FieldDeclaration field : fields) {
                // 获取字段类型和名称
                Type fieldType = field.getElementType();
                String fieldName = field.getVariables().get(0).getNameAsString();
                
                // 只添加基本类型、String类型和LocalDateTime类型
                String fieldTypeName = fieldType.toString();
                if (fieldTypeName.contains("<") && fieldTypeName.contains(">")) {
                    // 处理泛型类型，如List<String>
                    fieldTypeName = fieldTypeName.substring(0, fieldTypeName.indexOf("<"));
                }
                
                if (!queryDtoTypes.contains(fieldTypeName)) {
                    continue; // 跳过不在QueryDTO类型列表中的字段
                }
                
                // 添加字段
                sb.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n");
            }
            
            sb.append("\n}\n");
            
            // 创建QueryDTO输出目录
            Path packagePath = Paths.get(outputDir, "src/main/java", queryDtoPackage.replace('.', File.separatorChar));
            if (!packagePath.toFile().exists()) {
                Files.createDirectories(packagePath);
            }
            
            // 写入QueryDTO文件
            String queryDtoContentStr = aiRepairService.fixImports(sb.toString(), DtoImportConstants.DTO_IMPORT_WHITELIST);
            Path outputPath = packagePath.resolve(queryDtoName + ".java");
            Files.writeString(outputPath, queryDtoContentStr);
            logger.info("成功生成QueryDTO类: {}", outputPath);
            
            return true;
            
        } catch (Exception e) {
            logger.error("生成QueryDTO类失败", e);
            return false;
        }
    }
    
    /**
     * 处理实体回复，根据// ---ENTITY_BOUNDARY---进行切分
     * @param entityResponse 实体回复内容
     * @return 切分后的字符串数组
     */
    private String[] splitEntityResponse(String entityResponse) {
        if (entityResponse == null || entityResponse.trim().isEmpty()) {
            return new String[0];
        }
        return entityResponse.split("// ---ENTITY_BOUNDARY---");
    }
    
    /**
     * 将切分后的实体写入对应的实体文件
     * @param entityParts 切分后的实体数组
     * @param targetDir 目标目录
     * @return 是否写入成功
     */
    private boolean writeEntityPartsToFiles(String[] entityParts, String targetDir) {
        if (entityParts == null) {
            return false;
        }
        try {
            Path targetPath = Paths.get(targetDir).resolve("src/main/java/com/example/demo/entity");
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            for (String entityPart : entityParts) {
                if (entityPart == null || entityPart.trim().isEmpty()) {
                    continue;
                }
                String className = extractClassName(entityPart);
                if (className != null) {
                    Path filePath = targetPath.resolve(className + ".java");
                    
                    // 使用AI修复导入语句
                      String fixedEntityPart = aiRepairService.fixImports(entityPart.trim(), EntityImportConstants.ENTITY_IMPORT_WHITELIST);
                    Files.writeString(filePath, fixedEntityPart);
                    logger.info("Successfully wrote entity to file: {}", filePath);
                } else {
                    logger.warn("Could not extract class name from part: {}", entityPart.substring(0, Math.min(entityPart.length(), 100)));
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error writing entity files", e);
            return false;
        }
    }
    
    /**
     * 从实体内容中提取类名
     * @param content 实体内容
     * @return 提取的类名
     */
    private String extractClassName(String content) {
        Pattern pattern = Pattern.compile("// Class: (\\w+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("public class (\\w+)");
        matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    @Override
    public boolean generateCrudInterfaces(String entityName, Path tempDir) throws Exception {
        logger.info("开始为实体 {} 生成CRUD接口", entityName);
        
        try {
            // 生成Repository接口
            generateRepositoryInterface(entityName, tempDir);
            
            // 生成Service接口和实现类
            generateServiceInterfaceAndImpl(entityName, tempDir);
            
            // 生成Controller类
            generateControllerClass(entityName, tempDir);
            
            logger.info("成功为实体 {} 生成CRUD接口", entityName);
            return true;
        } catch (Exception e) {
            logger.error("为实体 {} 生成CRUD接口失败", entityName, e);
            return false;
        }
    }
    
    /**
     * 生成Repository接口
     * @param entityName 实体类名称
     * @param tempDir 临时目录
     * @throws Exception 处理过程中可能出现的异常
     */
    private void generateRepositoryInterface(String entityName, Path tempDir) throws Exception {
        String repositoryContent = generateRepositoryContent(entityName);
        
        // 写入文件
        Path repositoryPath = tempDir.resolve("src/main/java/com/example/demo/api/repository");
        if (!Files.exists(repositoryPath)) {
            Files.createDirectories(repositoryPath);
        }
        
        Path repositoryFile = repositoryPath.resolve(entityName + "ApiRepository.java");
        Files.writeString(repositoryFile, repositoryContent);
        logger.info("成功生成Repository接口: {}", repositoryFile);
    }
    
    /**
     * 生成Service接口和实现类
     * @param entityName 实体类名称
     * @param tempDir 临时目录
     * @throws Exception 处理过程中可能出现的异常
     */
    private void generateServiceInterfaceAndImpl(String entityName, Path tempDir) throws Exception {
        // 生成Service接口
        String serviceInterfaceContent = generateServiceInterfaceContent(entityName);
        
        Path servicePath = tempDir.resolve("src/main/java/com/example/demo/api/service");
        if (!Files.exists(servicePath)) {
            Files.createDirectories(servicePath);
        }
        
        Path serviceInterfaceFile = servicePath.resolve(entityName + "ApiService.java");
        Files.writeString(serviceInterfaceFile, serviceInterfaceContent);
        logger.info("成功生成Service接口: {}", serviceInterfaceFile);
        
        // 生成Service实现类
        String serviceImplContent = generateServiceImplContent(entityName);
        
        Path serviceImplPath = tempDir.resolve("src/main/java/com/example/demo/api/service/impl");
        if (!Files.exists(serviceImplPath)) {
            Files.createDirectories(serviceImplPath);
        }
        
        Path serviceImplFile = serviceImplPath.resolve(entityName + "ApiServiceImpl.java");
        Files.writeString(serviceImplFile, serviceImplContent);
        logger.info("成功生成Service实现类: {}", serviceImplFile);
    }
    
    /**
     * 生成Controller类
     * @param entityName 实体类名称
     * @param tempDir 临时目录
     * @throws Exception 处理过程中可能出现的异常
     */
    private void generateControllerClass(String entityName, Path tempDir) throws Exception {
        String controllerContent = generateControllerContent(entityName);
        
        Path controllerPath = tempDir.resolve("src/main/java/com/example/demo/api/controller");
        if (!Files.exists(controllerPath)) {
            Files.createDirectories(controllerPath);
        }
        
        Path controllerFile = controllerPath.resolve(entityName + "ApiController.java");
        Files.writeString(controllerFile, controllerContent);
        logger.info("成功生成Controller类: {}", controllerFile);
    }
    
    /**
     * 生成Repository接口内容
     * @param entityName 实体类名称
     * @return Repository接口内容
     */
    private String generateRepositoryContent(String entityName) {
        return "package com.example.demo.api.repository;\n\n" +
               "import com.example.demo.entity." + entityName + ";\n" +
               "import org.springframework.data.jpa.repository.JpaRepository;\n" +
               "import org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n" +
               "import org.springframework.stereotype.Repository;\n\n" +
               "@Repository\n" +
               "public interface " + entityName + "ApiRepository extends JpaRepository<" + entityName + ", Long>, JpaSpecificationExecutor<" + entityName + "> {\n" +
               "}\n";
    }
    
    /**
     * 生成Service接口内容
     * @param entityName 实体类名称
     * @return Service接口内容
     */
    private String generateServiceInterfaceContent(String entityName) {
        return "package com.example.demo.api.service;\n\n" +
               "import com.example.demo.entity." + entityName + ";\n" +
               "import com.example.demo.api.querydto." + entityName + "QueryDTO;\n" +
               "import java.util.List;\n" +
               "import java.util.Optional;\n\n" +
               "public interface " + entityName + "ApiService {\n" +
               "    List<" + entityName + "> findAll();\n" +
               "    Optional<" + entityName + "> findById(Long id);\n" +
               "    " + entityName + " save(" + entityName + " entity);\n" +
               "    void deleteById(Long id);\n" +
               "    List<" + entityName + "> queryByConditions(" + entityName + "QueryDTO queryDTO);\n" +
               "}\n";
    }
    
    /**
     * 生成Service实现类内容
     * @param entityName 实体类名称
     * @return Service实现类内容
     */
    private String generateServiceImplContent(String entityName) {
        return "package com.example.demo.api.service.impl;\n\n" +
               "import com.example.demo.entity." + entityName + ";\n" +
               "import com.example.demo.api.querydto." + entityName + "QueryDTO;\n" +
               "import com.example.demo.api.repository." + entityName + "ApiRepository;\n" +
               "import com.example.demo.api.service." + entityName + "ApiService;\n" +
               "import org.springframework.beans.factory.annotation.Autowired;\n" +
               "import org.springframework.stereotype.Service;\n" +
               "import org.springframework.data.domain.Page;\n" +
               "import org.springframework.data.domain.Pageable;\n" +
               "import org.springframework.data.jpa.domain.Specification;\n" +
               "import jakarta.persistence.criteria.Predicate;\n" +
               "import java.util.ArrayList;\n" +
               "import java.util.List;\n" +
               "import java.util.Optional;\n" +
               "import java.util.stream.Collectors;\n\n" +
               "@Service\n" +
               "public class " + entityName + "ApiServiceImpl implements " + entityName + "ApiService {\n\n" +
               "    @Autowired\n" +
               "    private " + entityName + "ApiRepository repository;\n\n" +
               "    @Override\n" +
               "    public List<" + entityName + "> findAll() {\n" +
               "        return repository.findAll();\n" +
               "    }\n\n" +
               "    @Override\n" +
               "    public Optional<" + entityName + "> findById(Long id) {\n" +
               "        return repository.findById(id);\n" +
               "    }\n\n" +
               "    @Override\n" +
               "    public " + entityName + " save(" + entityName + " entity) {\n" +
               "        return repository.save(entity);\n" +
               "    }\n\n" +
               "    @Override\n" +
               "    public void deleteById(Long id) {\n" +
               "        repository.deleteById(id);\n" +
               "    }\n\n" +
               "    @Override\n" +
"    public List<" + entityName + "> queryByConditions(" + entityName + "QueryDTO queryDTO) {\n" +
"        Specification<" + entityName + "> spec = (root, query, cb) -> {\n" +
"            List<Predicate> predicates = new ArrayList<>();\n" +
"            // 使用反射获取QueryDTO的所有字段\n" +
"            try {\n" +
"                for (java.lang.reflect.Field field : queryDTO.getClass().getDeclaredFields()) {\n" +
"                    field.setAccessible(true);\n" +
"                    Object value = field.get(queryDTO);\n" +
"                    if (value != null) {\n" +
"                        String fieldName = field.getName();\n" +
"                        // 所有类型都使用等值查询\n" +
"                        predicates.add(cb.equal(root.get(fieldName), value));\n" +
"                    }\n" +
"                }\n" +
"            } catch (IllegalAccessException e) {\n" +
"                throw new RuntimeException(\"获取查询条件失败\", e);\n" +
"            }\n" +
"            return cb.and(predicates.toArray(new Predicate[0]));\n" +
"        };\n" +
"        return repository.findAll(spec);\n" +
"    }\n" +
               "}\n";
    }
    
    /**
     * 生成Controller类内容
     * @param entityName 实体类名称
     * @return Controller类内容
     */
    private String generateControllerContent(String entityName) {
        return "package com.example.demo.api.controller;\n\n" +
               "import com.example.demo.entity." + entityName + ";\n" +
               "import com.example.demo.api.querydto." + entityName + "QueryDTO;\n" +
               "import com.example.demo.api.service." + entityName + "ApiService;\n" +
               "import org.springframework.beans.factory.annotation.Autowired;\n" +
               "import org.springframework.http.ResponseEntity;\n" +
               "import org.springframework.web.bind.annotation.*;\n" +
               "import java.util.List;\n" +
               "import java.util.Optional;\n\n" +
               "@RestController\n" +
               "@RequestMapping(\"/api/v2/" + entityName.toLowerCase() + "s\")\n" +
               "public class " + entityName + "ApiController {\n\n" +
               "    @Autowired\n" +
               "    private " + entityName + "ApiService service;\n\n" +
               "    @GetMapping\n" +
               "    public List<" + entityName + "> findAll() {\n" +
               "        return service.findAll();\n" +
               "    }\n\n" +
               "    @GetMapping(\"/{id}\")\n" +
               "    public ResponseEntity<" + entityName + "> findById(@PathVariable Long id) {\n" +
               "        return service.findById(id)\n" +
               "            .map(ResponseEntity::ok)\n" +
               "            .orElse(ResponseEntity.notFound().build());\n" +
               "    }\n\n" +
               "    @PostMapping\n" +
               "    public " + entityName + " save(@RequestBody " + entityName + " entity) {\n" +
               "        return service.save(entity);\n" +
               "    }\n\n" +
               "    @DeleteMapping(\"/{id}\")\n" +
               "    public ResponseEntity<Void> deleteById(@PathVariable Long id) {\n" +
               "        service.deleteById(id);\n" +
               "        return ResponseEntity.noContent().build();\n" +
               "    }\n\n" +
               "    @PostMapping(\"/query\")\n" +
               "    public List<" + entityName + "> queryByConditions(@RequestBody " + entityName + "QueryDTO queryDTO) {\n" +
               "        return service.queryByConditions(queryDTO);\n" +
               "    }\n" +
               "}\n";
    }
}
