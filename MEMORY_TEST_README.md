# 记忆管理功能测试

本项目提供了记忆管理功能的API测试脚本，用于验证记忆管理功能是否正常工作。

## 文件说明

- `test_memory_api.py` - 记忆管理API测试脚本，包含所有API接口的测试用例
- `run_memory_test.py` - 测试启动脚本，用于启动Spring Boot应用并运行API测试

## 测试内容

测试脚本包含以下API接口的测试：

1. **保存聊天记忆** (`/api/memory/save`)
   - 测试单条聊天记忆的保存功能

2. **批量保存聊天记忆** (`/api/memory/save-batch`)
   - 测试多条聊天记忆的批量保存功能

3. **查询聊天记忆** (`/api/memory/query`)
   - 测试聊天记忆的查询功能

4. **获取最近聊天历史** (`/api/memory/project/{projectId}/recent`)
   - 测试获取项目最近聊天记录的功能

5. **获取最近会话历史** (`/api/memory/project/{projectId}/session/recent`)
   - 测试获取会话最近聊天记录的功能

6. **删除项目记忆** (`/api/memory/project/{projectId}`)
   - 测试删除项目聊天记忆的功能

7. **清理过期记忆** (`/api/memory/clean-expired`)
   - 测试清理过期记忆的功能

## 使用方法

### 方法一：使用启动脚本（推荐）

1. 确保已安装Python 3.x
2. 确保已安装Maven
3. 在项目根目录下运行：

```bash
python run_memory_test.py
```

此脚本会自动启动Spring Boot应用，等待应用启动完成后运行API测试，测试完成后自动关闭应用。

### 方法二：手动运行

1. 首先启动Spring Boot应用：

```bash
mvn spring-boot:run
```

2. 等待应用启动完成（通常需要30秒左右）

3. 在另一个终端中运行API测试脚本：

```bash
python test_memory_api.py
```

## 测试结果

测试脚本会输出每个API接口的测试结果，包括：

- 保存结果
- 查询结果数量
- 删除结果
- 清理结果

如果所有测试通过，会显示"测试成功完成!"，否则会显示"测试失败!"。

## 注意事项

1. 确保Redis服务已启动并正确配置
2. 确保测试环境的Redis连接配置正确（在`application-test.yml`中）
3. 测试过程中会创建和删除测试数据，不会影响生产环境数据
4. 如果测试失败，请检查Spring Boot应用的日志输出以获取更多信息

## 故障排除

### 应用启动失败

- 检查Maven是否正确安装
- 检查项目依赖是否完整
- 检查端口8080是否被占用

### API测试失败

- 检查Spring Boot应用是否正常启动
- 检查Redis服务是否正常运行
- 检查Redis连接配置是否正确
- 查看测试脚本输出的错误信息

### Redis连接问题

- 确保Redis服务已启动
- 检查`application-test.yml`中的Redis配置
- 确保网络连接正常，可以访问Redis服务器