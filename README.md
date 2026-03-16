# WeChat Mini-Program Mall Backend

基于 Spring Boot 3 的微信小程序电商商城后端系统，提供完整的电商功能 API 接口。

## 功能特性

- **用户管理**: 用户注册、登录、个人信息管理
- **商品管理**: 商品分类、商品列表、商品详情、搜索功能
- **购物车**: 添加商品、修改数量、删除商品
- **订单管理**: 创建订单、订单列表、订单详情、订单状态管理
- **支付集成**: 微信支付接口（待实现）
- **文件上传**: 图片上传功能
- **权限控制**: JWT 令牌认证，管理员/用户角色分离
- **API 文档**: 集成 Swagger/OpenAPI 文档

## 技术栈

- **后端框架**: Spring Boot 3.2.5
- **Java 版本**: 17
- **安全认证**: Spring Security + JWT
- **API 文档**: SpringDoc OpenAPI 3.0
- **构建工具**: Maven
- **JSON 处理**: Gson, Fastjson
- **开发工具**: Lombok

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+（可选，当前使用内存数据）

### 安装与运行

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd test-shopping-mall-backend
   ```

2. **构建项目**
   ```bash
   mvn clean package
   ```

3. **运行项目**
   ```bash
   mvn spring-boot:run
   ```
   或者直接运行打包后的 JAR 文件：
   ```bash
   java -jar target/wechat-miniprogram-mall-backend-1.0.0.jar
   ```

4. **访问应用**
   - 应用地址: http://localhost:8080
   - API 文档: http://localhost:8080/swagger-ui.html
   - OpenAPI 文档: http://localhost:8080/v3/api-docs

## API 接口

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/profile` - 获取用户信息

### 商品相关
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/search` - 搜索商品
- `GET /api/categories` - 获取商品分类

### 购物车相关
- `GET /api/cart` - 获取购物车列表
- `POST /api/cart` - 添加商品到购物车
- `PUT /api/cart/{id}` - 更新购物车商品数量
- `DELETE /api/cart/{id}` - 删除购物车商品

### 订单相关
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}/status` - 更新订单状态

### 文件上传
- `POST /api/upload` - 上传图片文件

### 管理员接口
- `POST /api/admin/login` - 管理员登录
- `GET /api/admin/products` - 管理员获取商品列表
- `POST /api/admin/products` - 管理员添加商品
- `PUT /api/admin/products/{id}` - 管理员更新商品
- `DELETE /api/admin/products/{id}` - 管理员删除商品

## 项目结构

```
src/main/java/com/mall/
├── MallBackendApplication.java          # 应用主类
├── config/                              # 配置类
│   ├── SecurityConfig.java              # 安全配置
│   ├── WebConfig.java                   # Web配置
│   └── JwtAuthenticationFilter.java     # JWT过滤器
├── controller/                          # 控制器层
│   ├── AuthController.java              # 认证控制器
│   ├── ProductController.java           # 商品控制器
│   ├── CartController.java              # 购物车控制器
│   ├── OrderController.java             # 订单控制器
│   ├── UploadController.java            # 文件上传控制器
│   └── AdminController.java             # 管理员控制器
├── dto/                                 # 数据传输对象
│   ├── ProductRequest.java              # 商品请求DTO
│   ├── ProductResponse.java             # 商品响应DTO
│   ├── OrderResponse.java               # 订单响应DTO
│   └── ...
├── service/                             # 服务层
│   ├── AuthService.java                 # 认证服务
│   ├── ProductService.java              # 商品服务
│   ├── CartService.java                 # 购物车服务
│   ├── OrderService.java                # 订单服务
│   └── JwtService.java                  # JWT服务
├── model/                               # 数据模型
│   ├── User.java                        # 用户模型
│   ├── Product.java                     # 商品模型
│   ├── CartItem.java                    # 购物车项模型
│   ├── Order.java                       # 订单模型
│   └── Category.java                    # 分类模型
└── utils/                               # 工具类
    ├── JwtUtil.java                     # JWT工具
    └── FileUtil.java                    # 文件工具
```

## 配置说明

### 应用配置
默认配置在 `application.properties` 中，主要配置项：

```properties
# 服务器端口
server.port=8080

# JWT 配置
jwt.secret=your-jwt-secret-key-change-this-in-production
jwt.expiration=86400000  # 24小时

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# 静态资源访问
spring.web.resources.static-locations=classpath:/static/,file:./uploads/
```

### 安全配置
- 默认管理员账号: `admin` / `admin123`
- JWT 令牌在请求头中传递: `Authorization: Bearer <token>`
- 公开接口: 登录、注册、商品列表、商品详情
- 需要认证的接口: 购物车、订单、用户信息

## 开发指南

### 添加新功能
1. 在 `model` 包中创建实体类
2. 在 `dto` 包中创建请求/响应DTO
3. 在 `service` 包中实现业务逻辑
4. 在 `controller` 包中创建API接口
5. 更新API文档注解

### 运行测试
```bash
mvn test
```

### 代码规范
- 使用 Lombok 减少样板代码
- 遵循 RESTful API 设计规范
- 使用统一的响应格式
- 添加必要的API文档注解

## 部署

### 生产环境部署
1. 修改 `application.properties` 中的配置
2. 配置数据库连接（如果需要）
3. 设置安全的 JWT 密钥
4. 配置 SSL/TLS 证书
5. 使用生产环境配置文件：`application-prod.properties`

### Docker 部署
```bash
# 构建 Docker 镜像
docker build -t mall-backend .

# 运行容器
docker run -p 8080:8080 -d mall-backend
```

## 常见问题

### 1. 端口被占用
修改 `application.properties` 中的 `server.port` 配置

### 2. JWT 认证失败
检查请求头中的 Authorization 格式是否正确

### 3. 文件上传失败
检查上传目录权限和文件大小限制

### 4. 跨域问题
已在 `WebConfig.java` 中配置跨域支持

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件至 [your-email@example.com]

---

**注意**: 本项目为演示用途，生产环境使用前请进行充分的安全测试和性能优化。