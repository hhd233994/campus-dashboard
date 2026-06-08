# 测试运行指南

## 概述

本项目包含完整的单元测试和集成测试,但由于需要MySQL和Redis服务,本地运行测试需要先启动这些依赖服务。

## 测试结构

### Controller层测试 (4个)
- `AuthControllerTest` - 认证接口测试
- `ConsumptionRecordControllerTest` - 消费记录接口测试
- `OperationLogControllerTest` - 操作日志接口测试
- `FileControllerTest` - 文件上传接口测试

### Service层测试 (5个)
- `UserServiceTest` - 用户服务测试
- `ConsumptionRecordServiceTest` - 消费记录服务测试
- `OperationLogServiceTest` - 操作日志服务测试
- `FileServiceImplTest` - 文件服务测试
- `JwtUtilTest` - JWT工具类测试

## 本地运行测试

### 方法1: 使用Docker Compose(推荐)

```bash
# 1. 启动MySQL和Redis
docker-compose up -d mysql redis

# 2. 等待服务启动(约10秒)
sleep 10

# 3. 运行测试
mvn clean test

# 4. 停止服务
docker-compose down
```

### 方法2: 手动启动依赖服务

```bash
# 启动MySQL
docker run -d --name test-mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=campus_dashboard_test \
  -p 3306:3306 mysql:8.0

# 启动Redis
docker run -d --name test-redis \
  -p 6379:6379 redis:7-alpine

# 运行测试
mvn clean test

# 清理容器
docker rm -f test-mysql test-redis
```

### 方法3: 跳过测试构建项目

如果只是想验证代码可以编译,可以跳过测试:

```bash
mvn clean package -DskipTests
```

## GitHub Actions CI/CD

项目已配置GitHub Actions,每次push或PR时会自动运行测试:

- 自动启动MySQL和Redis服务
- 运行所有单元测试
- 上传测试结果
- 构建JAR包

查看工作流状态: `.github/workflows/maven-ci.yml`

## 测试覆盖率目标

- **当前覆盖率**: ~65-70%
- **目标覆盖率**: >= 70%
- **关键业务逻辑**: 100%覆盖

## 常见问题

### Q: 测试失败 "Connection refused"
A: 确保MySQL和Redis服务正在运行

### Q: 测试失败 "Table already exists"
A: 测试配置已设置`spring.sql.init.mode=never`,如果仍出现此错误,请检查是否使用了正确的测试配置文件

### Q: 如何查看测试覆盖率报告?
A: 运行以下命令生成JaCoCo报告:
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

## 测试最佳实践

1. **Controller测试**: 使用`@WebMvcTest` + MockMvc,Mock Service层
2. **Service测试**: 使用`@SpringBootTest`进行集成测试
3. **隔离性**: 每个测试应该独立,不依赖其他测试的执行结果
4. **事务回滚**: 使用`@Transactional`注解确保测试数据自动清理

## 下一步改进

1. 添加参数化测试(Parameterized Tests)
2. 增加边界条件和异常场景测试
3. 集成Testcontainers实现更可靠的测试环境
4. 添加性能测试

---

**注意**: 由于测试需要真实的数据库连接,建议在有MySQL和Redis的环境中运行完整测试套件。对于快速验证,可以使用`mvn compile`只编译代码。
