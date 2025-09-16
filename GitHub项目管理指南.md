# GitHub项目管理指南

## 目录
1. [GitHub基础设置](#github基础设置)
2. [创建和管理仓库](#创建和管理仓库)
3. [基本Git命令](#基本git命令)
4. [分支管理策略](#分支管理策略)
5. [协作工作流程](#协作工作流程)
6. [Issue和项目管理](#issue和项目管理)
7. [CI/CD集成](#cicd集成)
8. [最佳实践](#最佳实践)

## GitHub基础设置

### 1. 创建GitHub账户
- 访问 [GitHub官网](https://github.com/)
- 点击"Sign up"注册账户
- 完成邮箱验证
- 设置个人资料和头像

### 2. 安装Git
- 下载并安装 [Git](https://git-scm.com/downloads)
- 配置Git用户信息：
  ```bash
  git config --global user.name "您的姓名"
  git config --global user.email "您的邮箱"
  ```

### 3. 生成SSH密钥
```bash
# 生成SSH密钥
ssh-keygen -t ed25519 -C "您的邮箱"

# 启动ssh-agent
eval "$(ssh-agent -s)"

# 添加SSH私钥到ssh-agent
ssh-add ~/.ssh/id_ed25519

# 复制公钥到剪贴板
clip < ~/.ssh/id_ed25519.pub
```

### 4. 将SSH公钥添加到GitHub
- 登录GitHub
- 点击右上角头像 → Settings
- 左侧菜单点击"SSH and GPG keys"
- 点击"New SSH key"
- 粘贴刚才复制的公钥

## 创建和管理仓库

### 1. 创建新仓库
- 点击GitHub主页的"+" → "New repository"
- 填写仓库信息：
  - Repository name: 仓库名称
  - Description: 仓库描述
  - Public/Private: 公开或私有
  - Initialize with README: 初始化README文件
- 点击"Create repository"

### 2. 克隆现有仓库
```bash
# 使用HTTPS
git clone https://github.com/username/repository.git

# 使用SSH（推荐）
git clone git@github.com:username/repository.git
```

### 3. 仓库设置
- 点击仓库的"Settings"标签
- 常用设置：
  - General: 仓库名称、描述、默认分支等
  - Branches: 分支保护规则
  - Options: 功能启用/禁用
  - Collaborators: 协作者管理
  - Webhooks: 自动化钩子

## 基本Git命令

### 1. 初始化本地仓库
```bash
# 在项目目录中
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin git@github.com:username/repository.git
git push -u origin main
```

### 2. 日常操作
```bash
# 查看状态
git status

# 添加文件到暂存区
git add filename
git add .  # 添加所有文件

# 提交更改
git commit -m "提交信息"

# 推送到远程仓库
git push
git push origin main  # 推送到指定分支

# 拉取远程更改
git pull
git pull origin main  # 拉取指定分支

# 查看提交历史
git log
git log --oneline  # 简洁显示
```

### 3. 撤销操作
```bash
# 撤销工作区更改
git checkout -- filename

# 撤销暂存区更改
git reset HEAD filename

# 撤销提交（保留更改）
git reset --soft HEAD~1

# 撤销提交（删除更改）
git reset --hard HEAD~1

# 撤销已推送的提交
git revert HEAD  # 创建新提交来撤销
```

## 分支管理策略

### 1. 基本分支操作
```bash
# 查看所有分支
git branch -a

# 创建新分支
git branch feature-branch

# 切换分支
git checkout feature-branch

# 创建并切换分支
git checkout -b feature-branch

# 删除本地分支
git branch -d feature-branch

# 删除远程分支
git push origin --delete feature-branch
```

### 2. Git Flow工作流
```
main
├── develop
│   ├── feature/*
│   ├── release/*
│   └── hotfix/*
└── tags
```

#### 主要分支：
- **main**: 主分支，始终保持可发布状态
- **develop**: 开发分支，集成了所有功能开发

#### 支持分支：
- **feature**: 功能分支，从develop分出，完成后合并回develop
- **release**: 发布分支，从develop分出，测试完成后合并到main和develop
- **hotfix**: 紧急修复分支，从main分出，修复后合并到main和develop

### 3. GitHub Flow工作流
```
main
└── feature/*
```
- 简化的工作流，只有main和功能分支
- 适合持续部署的项目

### 4. 分支保护设置
- 在GitHub仓库设置中
- 点击"Branches" → "Add branch protection rule"
- 设置保护规则：
  - Branch name pattern: main
  - Require pull request reviews before merging
  - Require status checks to pass before merging
  - Include administrators

## 协作工作流程

### 1. Fork和Pull Request工作流
1. **Fork项目**：在GitHub上点击"Fork"按钮
2. **克隆本地仓库**：
   ```bash
   git clone git@github.com:your-username/repository.git
   cd repository
   ```
3. **添加上游仓库**：
   ```bash
   git remote add upstream git@github.com:original-owner/repository.git
   ```
4. **创建功能分支**：
   ```bash
   git checkout -b feature-branch
   ```
5. **进行更改并提交**：
   ```bash
   git add .
   git commit -m "添加新功能"
   ```
6. **推送到您的Fork**：
   ```bash
   git push origin feature-branch
   ```
7. **创建Pull Request**：
   - 在GitHub上点击"New pull request"
   - 选择源分支和目标分支
   - 填写PR描述
   - 点击"Create pull request"
8. **响应审查意见**：
   - 根据反馈进行修改
   - 推送更新，PR会自动更新
9. **合并PR**：
   - 维护者审查通过后合并
   - 合并后可以删除功能分支

### 2. 协作分支工作流
1. **被添加为协作者**：
   - 仓库所有者在Settings → Collaborators中添加您
2. **克隆仓库**：
   ```bash
   git clone git@github.com:owner/repository.git
   ```
3. **创建功能分支**：
   ```bash
   git checkout -b feature-branch
   ```
4. **进行更改并提交**：
   ```bash
   git add .
   git commit -m "添加新功能"
   ```
5. **推送到远程仓库**：
   ```bash
   git push origin feature-branch
   ```
6. **创建Pull Request**：
   - 在GitHub上创建PR
   - 等待审查和合并

### 3. 代码审查最佳实践
- **提供清晰的PR描述**：
  - 说明更改的目的
  - 列出主要更改点
  - 提及相关Issue
- **进行有意义的审查**：
  - 关注代码质量和逻辑
  - 提供建设性反馈
  - 使用GitHub的评论功能
- **及时响应审查意见**：
  - 感谢审查者
  - 解释设计决策
  - 进行必要的修改

## Issue和项目管理

### 1. 创建和管理Issue
- **创建Issue**：
  - 点击仓库的"Issues"标签
  - 点击"New issue"
  - 填写标题和描述
  - 添加标签、里程碑和指派人
- **Issue模板**：
  - 在仓库根目录创建`.github/ISSUE_TEMPLATE/`目录
  - 添加不同类型的Issue模板文件
- **Issue链接**：
  - 在PR描述中使用`#issue-number`引用Issue
  - 在提交信息中使用`#issue-number`自动关联

### 2. 项目看板
- **创建项目看板**：
  - 点击仓库的"Projects"标签
  - 点击"New project"
  - 选择模板或创建自定义看板
- **看板列**：
  - To Do: 待处理
  - In Progress: 进行中
  - In Review: 审查中
  - Done: 已完成
- **管理项目卡片**：
  - 将Issue或PR添加到看板
  - 拖动卡片更新状态
  - 设置截止日期和指派人

### 3. 里程碑
- **创建里程碑**：
  - 点击Issues → Milestones
  - 点击"New milestone"
  - 设置标题、描述和截止日期
- **关联Issue**：
  - 在创建或编辑Issue时选择里程碑
  - 查看里程碑进度

### 4. 标签管理
- **创建标签**：
  - 点击Issues → Labels
  - 点击"New label"
  - 设置名称、描述和颜色
- **常用标签**：
  - `bug`: 错误报告
  - `enhancement`: 功能增强
  - `question`: 问题咨询
  - `documentation`: 文档改进
  - `good first issue`: 适合新手

## CI/CD集成

### 1. GitHub Actions
- **创建工作流文件**：
  - 在仓库中创建`.github/workflows/`目录
  - 添加YAML格式的工作流文件
- **示例工作流**：
  ```yaml
  name: CI/CD Pipeline
  
  on:
    push:
      branches: [ main ]
    pull_request:
      branches: [ main ]
  
  jobs:
    test:
      runs-on: ubuntu-latest
      steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
      - name: Run tests
        run: mvn test
    
    build:
      needs: test
      runs-on: ubuntu-latest
      steps:
      - uses: actions/checkout@v2
      - name: Build project
        run: mvn package
  ```

### 2. 常用CI/CD场景
- **代码质量检查**：
  - 使用SonarQube、ESLint等工具
  - 设置质量门禁
- **自动化测试**：
  - 单元测试
  - 集成测试
  - 端到端测试
- **自动部署**：
  - 部署到测试环境
  - 部署到生产环境
  - 发布Docker镜像

### 3. 环境管理
- **配置环境**：
  - 在仓库设置中添加环境
  - 设置环境保护规则
- **环境变量**：
  - 在仓库设置中配置secrets
  - 在工作流中使用`${{ secrets.SECRET_NAME }}`

### 4. 第三方CI/CD集成
- **Jenkins**：
  - 使用GitHub Webhook触发构建
  - 配置GitHub插件
- **Travis CI**：
  - 连接GitHub账户
  - 配置`.travis.yml`文件
- **CircleCI**：
  - 连接GitHub仓库
  - 配置`.circleci/config.yml`

## 最佳实践

### 1. 仓库组织
- **README文件**：
  - 项目描述
  - 安装说明
  - 使用方法
  - 贡献指南
- **LICENSE文件**：
  - 选择合适的开源许可证
  - 常见选择：MIT、Apache 2.0、GPL
- **.gitignore文件**：
  - 忽略临时文件和依赖
  - 使用[gitignore.io](https://gitignore.io/)生成模板

### 2. 提交信息规范
- **约定式提交**：
  ```
  type(scope): description
  
  # 可选的详细描述
  ```
- **常用类型**：
  - `feat`: 新功能
  - `fix`: 修复
  - `docs`: 文档更改
  - `style`: 代码格式
  - `refactor`: 重构
  - `test`: 测试相关
  - `chore`: 构建或辅助工具变动

### 3. 代码审查清单
- **代码质量**：
  - 代码是否符合项目规范
  - 是否有明显的错误
  - 是否有性能问题
- **测试覆盖**：
  - 是否有足够的测试
  - 测试是否通过
- **文档更新**：
  - 是否需要更新文档
  - README是否需要更新

### 4. 安全考虑
- **敏感信息**：
  - 不要提交密码、API密钥等
  - 使用GitHub Secrets管理敏感信息
- **依赖安全**：
  - 定期更新依赖
  - 使用GitHub Dependabot自动检查
- **访问控制**：
  - 设置适当的仓库权限
  - 使用分支保护规则

### 5. 性能优化
- **仓库大小**：
  - 使用Git LFS管理大文件
  - 定期清理无用文件
- **提交历史**：
  - 定期合并分支
  - 使用交互式rebase整理历史
- **网络优化**：
  - 使用浅克隆（`--depth 1`）
  - 使用Git协议而不是HTTPS

### 6. 团队协作
- **沟通**：
  - 使用Issue讨论问题
  - 在PR中提供清晰的上下文
- **责任分工**：
  - 明确代码审查者
  - 设置里程碑和截止日期
- **知识共享**：
  - 记录决策过程
  - 创建Wiki页面

## 总结

GitHub是一个强大的项目管理平台，通过合理使用其功能，可以大大提高开发效率和代码质量。从基本的仓库管理到复杂的CI/CD流程，GitHub提供了完整的工具链来支持现代软件开发流程。

记住，最好的工作流程是适合您团队的工作流程。根据项目规模、团队结构和开发需求，选择合适的策略和工具，并随着项目的发展不断优化。