# WeChat Mini-Program Mall Backend

基于 Spring Boot 3 的微信小程序电商商城后端系统，提供完整的电商功能 API 接口。

## 功能特性

- **商品管理**: 商品分类、商品列表、商品详情、搜索功能
- **订单管理**: 创建订单、订单列表、订单详情、订单状态管理
- **支付集成**: 微信支付接口（待实现）

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

## API 接口

### 认证相关,暂时不启用

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/profile` - 获取用户信息

### 商品相关

- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/categories` - 获取商品分类

### 订单相关

- `GET /api/orders/list` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单

## 配置说明

### 应用配置

默认配置在 `application.properties` 中，主要配置项：

```properties
# 服务器端口
server.port=9898
# JWT 配置，暂时不启用
jwt.secret=your-jwt-secret-key-change-this-in-production
jwt.expiration=86400000  # 24小时
```

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

# api test

# 1.获取商品列表

```
curl --location 'http://localhost:9898/api/v1/products' \
--header 'Content-Type: application/json' \
--data '{
    "page":1,
    "pageSize":10,
    "categoryId":1,
    "minRating":3.2,
    "search":"小米",
    "sortBy":"newest"
}'
```

# 2.获取订单列表

```
curl --location 'http://localhost:9898/api/v1/orders/list' \
--header 'Content-Type: application/json' \
--data '{
    "page":1,
    "pageSize":10
}'
```

# 3.保存订单

```
curl --location 'http://localhost:9898/api/v1/orders' \
--header 'Content-Type: application/json' \
--data '{
    "address": {
        "id": "temp",
        "name": "2",
        "phone": "13534342323",
        "province": "2",
        "city": "2",
        "district": "2",
        "address": "sd",
        "postalCode": "232321",
        "isDefault": false
    },
    "items": [
        {
            "id": 3,
            "productId": 3,
            "name": "小米14 Ultra",
            "price": 6499,
            "quantity": "1",
            "image": "http://localhost:9898/api/v1/images/xiaomi-14-ultra.jpg",
            "stock": 45
        }
    ],
    "paymentMethod": "simulated"
}'
```
# 4.获取订单详情
```
curl --location 'http://localhost:9898/api/v1/orders/1'
```