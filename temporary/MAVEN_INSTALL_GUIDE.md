# Maven 安装和启动指南

## 问题说明

Maven Wrapper 下载失败，通常是因为：
1. PowerShell 执行策略限制
2. 网络连接问题
3. 防火墙/代理设置

## 解决方案

### 方案1：安装 Maven（推荐）

#### 步骤1：下载 Maven
访问：https://maven.apache.org/download.cgi
下载：`apache-maven-3.9.5-bin.zip`

#### 步骤2：解压
将下载的 zip 文件解压到：`C:\Program Files\Apache\maven`

#### 步骤3：配置环境变量
1. 右键 "此电脑" → "属性" → "高级系统设置" → "环境变量"
2. 在"系统变量"中点击"新建"：
   - 变量名：`MAVEN_HOME`
   - 变量值：`C:\Program Files\Apache\maven`
3. 编辑"系统变量"中的 `Path`，添加：
   - `%MAVEN_HOME%\bin`
4. 点击"确定"保存

#### 步骤4：验证安装
打开**新的**命令行窗口，输入：
```bash
mvn -version
```

应该显示 Maven 版本信息。

#### 步骤5：重新启动项目
关闭所有命令行窗口，双击运行：
```
start-dev.bat
```

---

### 方案2：使用 IDE 运行（最简单）

#### 使用 IntelliJ IDEA

1. **打开项目**
   - File → Open → 选择 `C:\SharedLab\backend` 文件夹
   - IDE 会自动识别为 Maven 项目并下载依赖

2. **配置数据库**
   - 打开 `src/main/resources/application.properties`
   - 修改数据库连接信息：
     ```properties
     spring.datasource.username=你的用户名
     spring.datasource.password=你的密码
     ```

3. **运行后端**
   - 找到 `SharedLabApplication.java`
   - 右键 → Run 'SharedLabApplication'

4. **运行前端**
   - 打开终端（Terminal）
   - 输入：
     ```bash
     cd ../frontend
     npm install
     npm run dev
     ```

#### 使用 VS Code

1. **安装扩展**
   - Java Extension Pack
   - Spring Boot Extension Pack

2. **打开项目**
   - File → Open Folder → 选择 `C:\SharedLab\backend`

3. **运行后端**
   - 按 F5 或点击 Run → Start Debugging

4. **运行前端**（新终端）
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

---

### 方案3：手动下载 Maven Wrapper JAR

如果网络问题导致自动下载失败：

1. **手动下载文件**
   访问：https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
   
2. **创建目录**
   ```bash
   mkdir C:\SharedLab\backend\.mvn\wrapper
   ```

3. **放置文件**
   将下载的 `maven-wrapper-3.2.0.jar` 文件重命名为 `maven-wrapper.jar`
   放到：`C:\SharedLab\backend\.mvn\wrapper\`

4. **重新运行**
   ```bash
   cd C:\SharedLab\backend
   mvnw.cmd spring-boot:run
   ```

---

### 方案4：在线开发环境

如果本地环境配置困难，可以使用：
- **Gitpod**: https://gitpod.io/
- **GitHub Codespaces**: https://github.com/features/codespaces
- **CodeSandbox**: https://codesandbox.io/

这些平台提供预配置的开发环境，无需本地安装任何工具。

---

## 常见问题

### Q: Maven 下载依赖很慢怎么办？
A: 配置国内镜像，编辑 `C:\Users\你的用户名\.m2\settings.xml`：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/central</url>
  </mirror>
</mirrors>
```

### Q: PowerShell 执行策略错误？
A: 以管理员身份运行 PowerShell，执行：
```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Q: Java 版本不对？
A: 项目需要 Java 17+，检查版本：
```bash
java -version
```

如果版本低于 17，请下载安装 Java 17 或更高版本。

---

## 推荐的启动顺序

1. ✅ **确保 MySQL 已启动**
2. ✅ **导入数据库**：运行 `database/init.sql`
3. ✅ **配置连接**：修改 `application.properties`
4. ✅ **启动后端**：使用上述任一方案
5. ✅ **启动前端**：`npm install && npm run dev`
6. ✅ **访问**：http://localhost:5173

---

如有问题，请查看：
- 后端日志窗口的错误信息
- `backend/logs/` 目录下的日志文件
