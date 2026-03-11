# MyDict IntelliJ Plugin

<p align="center">
  <img src="src/main/resources/META-INF/pluginIcon.svg" width="80" alt="MyDict Plugin Icon"/>
</p>

<p align="center">
  <a href="https://plugins.jetbrains.com/plugin/YOUR_PLUGIN_ID"><img src="https://img.shields.io/jetbrains/plugin/v/YOUR_PLUGIN_ID.svg" alt="Version"/></a>
  <a href="https://plugins.jetbrains.com/plugin/YOUR_PLUGIN_ID"><img src="https://img.shields.io/jetbrains/plugin/d/YOUR_PLUGIN_ID.svg" alt="Downloads"/></a>
  <img src="https://img.shields.io/badge/IDEA-2024.1%2B-blue.svg" alt="IDEA 2024.1+"/>
  <img src="https://img.shields.io/badge/JDK-17%2B-orange.svg" alt="JDK 17+"/>
</p>

> IntelliJ IDEA 对 [MyDict Spring Boot Starter](https://github.com/canjiemo/mydict-spring-boot-starter) 的 IDE 支持插件。
> 无需编译，实时识别 `@MyDict` 注解生成的描述字段和访问器方法，体验与 Lombok 插件一致。

---

## 功能

- **实时代码补全**：自动提示 `@MyDict` 注解生成的 `xxxDesc` 字段及对应的 getter/setter
- **类型检查**：编写 `test.getStatusDesc()` 不再报红，无需先编译
- **启动检测**：项目启动时检测注解处理器是否已正确配置，未配置时给出提示

## 效果预览

```java
@Data
public class Order {

    @MyDict(type = "order_status")
    private Integer status;

    @MyDict(type = "pay_type")
    private Integer payType;
}
```

打开任意调用处，插件会自动补全：

```java
order.getStatusDesc();   // ✅ 直接提示，无需编译
order.getPayTypeDesc();  // ✅ 直接提示，无需编译
```

## 安装

### 方式一：JetBrains Plugin Marketplace（推荐）

IDEA → `Settings` → `Plugins` → 搜索 **MyDict** → Install

### 方式二：手动安装

1. 前往 [Releases](https://github.com/canjiemo/mydict-intellij-plugin/releases) 下载最新 `.zip`
2. IDEA → `Settings` → `Plugins` → 右上角齿轮 → `Install Plugin from Disk`
3. 选择下载的 zip 文件，重启 IDEA

## 使用前提

使用本插件需配合 [MyDict Spring Boot Starter](https://github.com/canjiemo/mydict-spring-boot-starter) 使用。

**Maven 依赖：**

```xml
<dependency>
    <groupId>io.github.canjiemo</groupId>
    <artifactId>mydict-spring-boot-starter</artifactId>
    <version>1.0.5-jdk21</version>
</dependency>
```

**注解处理器配置（使用 Lombok 时需同时声明）：**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
            <path>
                <groupId>io.github.canjiemo</groupId>
                <artifactId>mydict-processor</artifactId>
                <version>1.0.5-jdk21</version>
            </path>
        </annotationProcessorPaths>
        <fork>true</fork>
        <compilerArgs>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
            <arg>-J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## 字段命名规则

| 原字段名 | camelCase | 生成字段名 | getter |
|---------|-----------|-----------|--------|
| `status` | `true`（默认） | `statusDesc` | `getStatusDesc()` |
| `status` | `false` | `status_desc` | `getStatus_desc()` |
| `userName` | 忽略 | `userNameDesc` | `getUserNameDesc()` |
| `user_status` | 忽略 | `user_status_desc` | `getUser_status_desc()` |
| `USER_STATUS` | 忽略 | `USER_STATUS_DESC` | `getUSER_STATUS_DESC()` |

## 兼容性

| 项目 | 要求 |
|------|------|
| IntelliJ IDEA | 2024.1+（Ultimate / Community） |
| JDK | 17+ |
| Java 插件 | 已启用（IDEA 默认内置） |
| Lombok 插件 | 如项目使用 `@Data` / `@Getter` 则需安装 |

## 相关项目

- [mydict-spring-boot-starter](https://github.com/canjiemo/mydict-spring-boot-starter) — 核心库，提供注解 + 编译期代码生成 + Spring Boot 自动配置

## License

[Apache 2.0](LICENSE)
