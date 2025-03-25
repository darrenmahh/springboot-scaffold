# springboot-scaffold

## 分支

1. ### main:

   全部功能

2. ### login:

   注册、登录、日志（未进入数据库）

3. ### logSql：

   login+日志数据库







## 账号密码

### 超级管理员：

shimilywt 000000



### 管理员：

admin111111



### 用户

userr 222222

![image-20250321155552968](C:\Users\马冲\AppData\Roaming\Typora\typora-user-images\image-20250321155552968.png)

loginRoleTest1  123456



## 笔记

```
sequenceDiagram
    participant 用户
    participant 控制器 as 控制器(Controller)
    participant 切面 as 日志切面(LogAspect)
    participant 服务 as 业务服务(Service)
    participant 日志服务 as 日志服务(LogService)
    participant 数据库 as 数据库(Database)
    
    用户->>控制器: 1. 发送请求(例如:登录)
    Note over 控制器: 控制器方法上有@LogOperation注解
    控制器->>切面: 2. 进入方法(触发切面)
    
    切面->>切面: 3. 创建LogInfo对象
    切面->>切面: 4. 记录请求信息(URL,方法等)
    切面->>切面: 5. 记录开始时间
    
    切面->>服务: 6. 调用业务方法
    服务->>数据库: 7. 执行业务操作
    数据库-->>服务: 8. 返回业务结果
    服务-->>切面: 9. 返回业务结果
    
    切面->>切面: 10. 记录响应结果
    切面->>切面: 11. 记录结束时间和耗时
    
    切面->>日志服务: 12. 异步保存日志
    日志服务->>数据库: 13. 将日志写入数据库
    
    切面-->>控制器: 14. 返回业务结果
    控制器-->>用户: 15. 向用户返回响应
```

