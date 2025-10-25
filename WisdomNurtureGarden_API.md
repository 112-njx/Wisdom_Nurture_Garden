
# Wisdom Nurture Garden — 接口文档（完整版）

> 更新时间：2025-10-25 06:11:15  
> 技术栈：Spring Boot + MyBatis Plus + WebSocket + JWT  
> 说明：该文档根据 Controller + ServiceImpl 自动生成，适用于前后端联调。

---

## 概要
- 所有请求与响应均使用 `application/json` 格式  
- 登录/注册无需 Token，其余接口需在 Header 中携带：  
  ```
  Authorization: Bearer <token>
  ```
- 性别：1=男，2=女  
- 角色：1=子女端，2=老人端  
- 打卡评分范围：1~3，数值越高代表状态越好

---

## 用户模块 `/api/users`

### 用户注册

**POST /api/users/register**  
注册新用户。支持子女端与老人端。

#### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | ✅ | 用户名 |
| password | string | ✅ | 密码（明文，服务端自动加密） |
| gender | int | ❌ | 性别（1男，2女） |
| img | string | ❌ | 头像 URL |
| role | int | ✅ | 角色（1子女端，2老人端） |

#### 响应
```json
{ "code": 200, "message": "注册成功" }
```

---

### 用户登录

**POST /api/users/login**  
用户名+密码登录。

#### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | ✅ | 用户名 |
| password | string | ✅ | 密码 |
| role | int | ✅ | 角色（1子女端，2老人端） |

#### 响应
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "jwt-token",
    "user": {
      "id": 1,
      "name": "张三",
      "gender": 1,
      "role": 1,
      "img": "https://example.com/avatar.jpg"
    }
  }
}
```

---

### 微信授权登录

**POST /api/wechat/login**  
通过微信 code 获取 openid 并登录或注册。

#### 请求参数
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | string | ✅ | 微信登录 code |
| nickname | string | ✅ | 微信昵称 |
| avatarUrl | string | ✅ | 头像地址 |

---

### 微信用户完善信息

**POST /api/wechat/update**  
绑定用户名、密码与角色信息。

#### 请求参数
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | ✅ | 用户 ID |
| name | string | ✅ | 用户名 |
| password | string | ✅ | 密码 |
| role | int | ✅ | 角色（1子女端，2老人端） |
| gender | int | ❌ | 性别（1男，2女） |

---

## 绑定模块 `/api/bind`

### 子女绑定老人

**POST /api/bind**  
子女输入老人的用户名和密码完成绑定。

#### 请求参数
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elderName | string | ✅ | 老人用户名 |
| elderPassword | string | ✅ | 老人密码 |

#### 响应
```json
{ "code": 200, "message": "绑定成功" }
```

---

### 查询绑定信息

**GET /api/bind/child**  
根据子女 token 查询绑定老人信息。

**GET /api/bind/elder**  
根据老人 token 查询绑定子女信息。

#### 响应示例
```json
{
  "id": 1,
  "childId": 2,
  "elderId": 5,
  "createTime": "2025-10-24T12:00:00"
}
```

---

## 打卡模块 `/api/checkin`

### 老人每日打卡

**POST /api/checkin**  
老人端上报每日健康状况。每天仅允许一次。

#### 请求参数
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| mood | int | ✅ | 心情评分（1-3） |
| sleepQuality | int | ✅ | 睡眠质量评分（1-3） |
| appetite | int | ✅ | 食欲评分（1-3） |

#### 响应
```json
{ "code": 200, "message": "打卡成功" }
```

---

### 子女查看今日打卡

**GET /api/checkin/today**  
子女端查询所绑定老人的今日打卡记录。

#### 响应示例
```json
[
  {
    "id": 1,
    "elderId": 5,
    "mood": 3,
    "sleepQuality": 2,
    "appetite": 3,
    "createTime": "2025-10-25T08:00:00"
  }
]
```

---

## 求助模块 `/api/help`

### 老人一键求助

**POST /api/help**  
老人端发出紧急求助。系统通过 WebSocket 实时通知绑定的子女。

#### 请求参数
| 字段 | 类型 | 说明 |
|------|------|------|
| message | string | 求助信息内容（可选） |

#### 响应
```json
{ "code": 200, "message": "求助已发送" }
```

---

## 数据表结构

| 表名 | 字段 | 说明 |
|------|------|------|
| users | id, name, gender, password, img, role, openid, createTime, updateTime | 用户基本信息 |
| binding | id, child_id, elder_id, create_time | 绑定关系 |
| checkin | id, elder_id, mood, sleep_quality, appetite, create_time | 每日打卡记录 |
| help_request | id, elder_id, child_id, create_time | 求助记录 |

---

## 附录
- 所有密码均采用 `MD5` 加密存储  
- WebSocket 推送类：`HelpWebSocketServer.sendToChild(childId, message)`  
- JWT 工具类：`JwtUtil.generateToken(userId, username, role)`  
- 微信登录依赖接口：`https://api.weixin.qq.com/sns/jscode2session`

---
