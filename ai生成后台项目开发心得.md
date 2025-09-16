AI 生成后台项目 — 开发心得与技术亮点
一、项目背景
传统后台项目开发需要经历需求分析、数据库设计、代码编写、部署测试等多个环节，周期长且重复劳动多。
本项目的目标是通过自然语言输入，自动生成可运行、可部署、可测试的完整 Spring Boot 后端项目，并通过闭环 AI 修复机制确保交付质量。
￼
二、总体架构
系统整体采用单接口闭环执行模式：

需求文档 + 原型 → AI生成后台代码 → 自动部署 → 自动测试 → AI修复 → 打包返回
• 输入：自然语言需求文档和原型html文件
• 输出：已通过测试、可直接运行的 ZIP 格式项目包

编排模块结构如下：
   @Override
    public byte[] processProject(ProjectGenerateRequest request) throws Exception {
        // 1. Code Generation
        byte[] projectData = codeGenerationService.generateProject(request);

        // 2. Auto Deployment
        boolean deploymentSuccess = deploymentService.deployProject(projectData);
        if (!deploymentSuccess) {
            log.warn("部署失败，尝试AI修复");
            projectData = aiRepairService.repairCode(projectData);
            // 重新尝试部署
            deploymentSuccess = deploymentService.deployProject(projectData);
            if (!deploymentSuccess) {
                log.error("修复后仍然部署失败");
                throw new RuntimeException("项目部署失败，修复后仍无法成功部署");
            }
        }

        // 3. Auto Testing
        boolean testsPassed = autoTestService.runTests(projectData);

        // 4. AI Repair (if tests fail)
        if (!testsPassed) {
            log.warn("测试失败，尝试AI修复");
            projectData = aiRepairService.repairCode(projectData);
            // 重新尝试测试
            testsPassed = autoTestService.runTests(projectData);
            if (!testsPassed) {
                log.error("修复后仍然测试失败");
                throw new RuntimeException("项目测试失败，修复后仍无法成功测试");
            }
        }
        return projectData;
    }

架构分为六大核心模块：
1. 代码生成模块（AI + 模板驱动）
2. 自动部署模块（容器化 & 动态端口管理）
3. 自动测试模块（ai根据接口文档等上下文自动生成测试文件，并进行测试）
4. AI 修复模块（日志驱动二次生成，实现简单错误的修复）
5. 流程编排模块（闭环执行）
6. 打包下载模块（zip 输出）
￼
三、设计亮点
1. 控制器级别的并行处理架构，兼顾速度与稳定性
• 在接口生成阶段，系统并非简单地按接口逐个并行处理（速度快但稳定性差），也没有采取串行处理（稳定但速度慢），而是选择按控制器粒度并行处理的方案。
• 这种设计在性能和稳定性之间找到了最佳平衡：
• 并行提升速度：多个控制器同时生成，显著缩短生成时间。
• 减少逻辑错乱：控制器内部的接口保持上下文一致，避免了跨接口整合逻辑复杂导致的代码错误。
• 该架构在真实生产环境中更具可扩展性，可灵活增加新的控制器类型而不影响现有模块。
   // 1. 使用splitApiResponse方法切分api文档生成ai的回复
            String[] apiResponseParts = fileProcessingService.splitApiResponse(apiDoc.getAiResponse());
            logger.debug("AI response split into {} parts", apiResponseParts.length);


   //2.控制器级别分组
            final String lastPart = apiResponseParts.length > 1 ? apiResponseParts[apiResponseParts.length - 1] : "";

            for (int i = 0; i < apiResponseParts.length - 1; i++) {
                String part = apiResponseParts[i];

                // 提取 controller 名称
                String controller = extractController(part);
                if (controller == null || controller.trim().isEmpty()) {
                    controller = "UnknownController"; // 容错
                }

                groupedParts.computeIfAbsent(controller, k -> new ArrayList<>()).add(part);
            }

            // 2. 并行处理每一组（每个 controller 一组）
            List<CompletableFuture<WorkflowResponse>> futures = new ArrayList<>();

            // 准备共享的附加内容
            String sharedSuffix = "\n" + lastPart + "\n" + ProjectConstants.MAVEN_POM_CONTENT;

 // 3. 等待所有任务完成
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));


￼
2. 闭环执行与自愈能力
• 部署失败 → 自动提取日志 → AI 分析并修复代码 → 重新部署
• 支持多次迭代修复，直到通过测试或达到最大重试次数
价值：
系统具备自愈能力，减少人工介入，提升交付成功率。
 @Override
    public byte[] processProject(ProjectGenerateRequest request) throws Exception {
        // 1. Code Generation
        byte[] projectData = codeGenerationService.generateProject(request);

        // 2. Auto Deployment
        boolean deploymentSuccess = deploymentService.deployProject(projectData);
        if (!deploymentSuccess) {
            log.warn("部署失败，尝试AI修复");
            projectData = aiRepairService.repairCode(projectData);
            // 重新尝试部署
            deploymentSuccess = deploymentService.deployProject(projectData);
            if (!deploymentSuccess) {
                log.error("修复后仍然部署失败");
                throw new RuntimeException("项目部署失败，修复后仍无法成功部署");
            }
        }

        // 3. Auto Testing
        boolean testsPassed = autoTestService.runTests(projectData);

        // 4. AI Repair (if tests fail)
        if (!testsPassed) {
            log.warn("测试失败，尝试AI修复");
            projectData = aiRepairService.repairCode(projectData);
            // 重新尝试测试
            testsPassed = autoTestService.runTests(projectData);
            if (!testsPassed) {
                log.error("修复后仍然测试失败");
                throw new RuntimeException("项目测试失败，修复后仍无法成功测试");
            }
        }

        return projectData;
    }
            
￼
3. 模块化 AI 路由，精准匹配不同接口类型
• 系统能根据接口类型（如 auth、CRUD、其他）自动将请求路由到专门的 AI 模块，保证生成代码的专业性与准确性。
• 这种模块化设计让身份验证逻辑和业务 CRUD 逻辑分离处理，减少相互干扰，保证了一个模块的ai实现一个类型的接口，
而不会因为其他模块的ai相关提示词而影响代码生成质量。
• 同时保留了灵活扩展能力，未来可以无缝增加更多接口类型 AI（如支付、文件处理、消息队列等）。

// 为每一个ai模块的回复使用独立的处理逻辑，避免因为不同模块的ai回复而导致的代码生成错误。
/**
 * 解析器注册表：通过静态方法提供全局访问
 * 实例仍由 Spring 管理，避免 new 导致 @Autowired 失效
 */
@Component
public class WorkflowParserRegistry {

    ：：：：：：
    ：：：：：：
    ：：：：：：

    /**
     * 应用启动后，将所有 Spring Bean 注册到静态 Map
     */
    @PostConstruct
    public void init() {
        PARSERS.clear(); // 安全起见

        for (Map.Entry<String, WorkflowResponseParser> entry : parsers.entrySet()) {
            String beanName = entry.getKey();
            WorkflowResponseParser parser = entry.getValue();

            if ("crudWorkflowResponseParser".equals(beanName)) {
                PARSERS.put("crud", parser);
            } else if ("authWorkflowResponseParser".equals(beanName)) {
                PARSERS.put("auth", parser);
            }
            // 新增 AI 类型在这里添加判断
        }

        System.out.println("✅ WorkflowParserRegistry loaded parsers: " + PARSERS.keySet());
    }

    /**
     * 根据 AI 类型获取解析器
     */
    public static WorkflowResponseParser getParser(String aiType) {
        if (aiType == null) return null;
        return PARSERS.get(aiType.trim().toLowerCase());
    }

    /**
     * 检查是否支持该类型
     */
    public static boolean supports(String aiType) {
        return getParser(aiType) != null;
    }
}

￼
4. 实体优先生成策略，提升稳定性与效率
• 早期版本中，实体由接口生成 AI 统一生成，如果接口文档未涉及用户管理，User 实体就缺失，导致 auth AI 无法正常生成登录注册功能。
• 现在采用实体优先生成方案：
• 在接口生成前，由专门的实体生成 AI 统一生成所有实体。
• 接口生成 AI 可以直接依赖这些实体，保证业务逻辑完整性。
• 实体生成与接口生成并行执行，相比之前顺序执行，不仅提升了生成稳定性，还显著提高了生成速度。
• 这一策略消除了依赖链上的风险，使得生成项目的整体可运行性大幅提升。运行系统而不是“半成品”。

 // 5.2 将切分后的实体类别交给实体生成ai来进行实现。
            if (apiResponseParts.length > 0) {
                // 获取最后一部分作为实体列表
                String entityList = apiResponseParts[apiResponseParts.length - 1];
                logger.info("开始调用实体生成AI");
                try {
                    WorkflowResponse entityResponse = entityGeneratorService.generateEntity(entityList);
                    logger.info("实体生成AI调用成功");

                    // 使用新的服务方法来切分和写入实体文件
                    String[] entityParts = fileProcessingService.splitEntityResponse(entityResponse.getResponseContent());
                    fileProcessingService.writeEntityPartsToFiles(entityParts, tempDir.toString());
                } catch (Exception e) {
                    logger.error("调用实体生成AI失败", e);
                }
            } else {
                logger.warn("没有找到API响应部分来生成实体");
            }
￼
5. 基于标记的AI响应切分机制，确保解析的高鲁棒性
• 在早期设计中，系统尝试通过定义强类型的Java对象来直接反序列化AI的JSON响应，以实现字段级别的精确映射。
• 然而，该方案在实践中暴露出严重的稳定性问题：AI模型的微小“幻觉”、格式偏差或标点错误，都可能导致整个反序列化过程失败，这对于一个追求高可用的自动化生成平台是不可接受的。
• 为此，系统创新性地采用了基于文本标记的切分策略，实现了从“精确但脆弱”到“灵活且稳定”的关键转变：
• 统一字符串接收：系统不再依赖复杂的对象映射，而是以最基础的String类型接收AI的完整回复，从根本上杜绝了因格式问题导致的解析崩溃。
• 约定分隔符：与AI模型约定使用---API_SEPARATOR---作为接口定义的分隔符，并将响应的最后一个部分视为实体定义列表。
• 稳定切分：通过简单的字符串分割操作，即可将AI的响应稳定地切分为多个独立部分，确保了后续处理流程的可靠性。

 /**
     * 处理AI回复，根据---API_SEPARATOR---进行切分
     * @param aiResponse AI回复内容
     * @return 切分后的字符串数组
     */
    String[] splitApiResponse(String aiResponse);

    /**
     * 处理实体回复，根据// ---ENTITY_BOUNDARY---进行切分
     * @param entityResponse 实体回复内容
     * @return 切分后的字符串数组
     */
    String[] splitEntityResponse(String entityResponse);


四、开发心得
1. 首先是服务的结构设计
直接让ai来上手设计代码的话，容易出现不容易扩展以及维护的局面，因为ai更倾向于生成单一结构的代码，
而不是一个项目的多个模块，所以在设计之初，就需要考虑好服务的结构，以及模块之间的依赖关系，这样才能在后续的开发中，方便扩展以及维护。

2. 关于提示词工程
提示词的设计需要考虑结构化，比如说，首先定义ai的角色，以及简要描述一下ai的任务，设计ai的输出结构。在本项目中的接口实现ai就采用了这个策略，严格要求ai输出控制器层，实体层等等，这样如果ai输出的代码有问题，可以类似于传统项目的针对性修改，而不会过多地影响其他层面的内容。

3. 关于ai的上下文
为了减少ai的幻觉或者说ai的自作聪明问题，提供准确的上下文描述是很有必要的，比如说，在接口实现ai中，我会要求ai根据接口文档，实体类的定义，以及要求使用相关pom完整文件，来生成对应的代码，而不是根据一些随机的提示词来生成代码。这样，能够帮助ai了解如何局限自己使用的工具，而不会引入外部依赖来进行开发导致项目构建失败。

4. 关于ai模型的选择
我仅仅使用了qwen plus 和 deepseek chat模型。qwen模型的话，主要特点是输出token的速度快，但在处理代码生成部分的时候，就会出现一些提示词中的重点标识部分，比如关于qwen会对输出的规范性控制能力较差一些，总是无法输出规范的代码。而deepseek chat模型的话，主要特点是输出的代码规范度要高一些，质量高一些，但是在输出token的速度上就会慢一些。所以我使用deepseek来实现接口代码，qwen来实现接口文档和实体代码的编写。
另外，经过测试发现，deepseek chat模型在判断问题的时候表现会差一些，比如刚开始使用chat模型来作为监督者的角色，智能分配接口给对应的ai模块，但是却发现chat模型在判断接口类型的时候错误率比较高，在判断一个auth 接口的时候，却常常判断错误成为其他类型接口。而R1模型和qwen模型在判断接口类型的时候，错误率都比较低，综合考虑输出效率，使用了qwen模型作为监督者。所以，我能了解到不能简单的去判断一个模型适合编程 还是 聊天，而应该判断这个模型是否能够胜任一个任务来进行模型的选择。

5. 关于并行任务的颗粒度权衡
在并行化架构设计时，并不是简单地“能并行就并行”。项目早期我尝试过接口级别的并行，虽然理论上吞吐量高，但实践中发现整合难度大、错误率高。最终选定控制器级并行这一中间颗粒度，既能提升生成速度，又能保持上下文一致性和代码的稳定性。这一经验让我更加意识到，AI 协作型系统的并行粒度设计，应该根据上下文依赖强度而不是纯粹的任务数量来决定。

6. 关于开发工作流的版本控制
在项目开发的初期，我没有像管理项目一样去管理工作流的版本问题，而是直接修改提示词或者工作流结构，在测试过程中不断修改提示词来解决问题，却可能发现生成项目在某个模块的代码稳定性提升，但是在总体代码的稳定性却会降低，这时候考虑回滚到之前的版本，却因为没有完善的版本控制，仅记录了提示词的迭代过程。却忽略了工作流也存在结构和模型选择带来的结果稳定性影响。现在，我通过导出对应工作流的json文件来作为版本的控制，有效地管理了整个项目后台生成服务的版本。

7. 与ai协作完成整个项目的任务部分
在让AI自主开发了一段时间后，我确实认识到了ai现在强大的独立开发能力。很多时候，只要需求说清楚，AI能一口气把大部分功能都写出来，甚至包括一些我没想到的细节。
但在让ai自主开发一些功能的时候也出现了一些问题：AI虽然能“写代码”，却不太理解“为什么这么写”。它会在没有提示的情况下修改我已经写好的配置，删除某些我认为必要的校验逻辑，或者为了“完成任务”而强行拼凑代码，导致后续集成出错。更常见的是，当出现一个小bug时，AI总是往错误的方向反复尝试，却抓不住真正的问题所在。
这让我意识到：现在的AI更像是一个能力很强但却缺乏一些工程性的经验，现如今还是无法独立完成成熟的项目。
如果我把整个项目丢给AI让它从头做到尾，结果往往是代码看似完整，实则结构混乱、风格不一、难以维护。相反，当我先定好项目的整体结构，划分清楚模块职责，再让AI去填充每一个具体的Controller、Service或Entity时，生成的代码质量明显更高，也更容易整合。
所以我现在的做法是：我不再指望AI独立完成项目，而是由我来进行任务的拆解，思考实现项目的流程和架构，然后再把任务交给ai来完成。