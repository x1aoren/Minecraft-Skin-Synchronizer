# SkinSynchronizer

## 插件简介
SkinSynchronizer 是一个支持 Spigot/Paper/Folia 及 Velocity 的我的世界皮肤同步插件，能在离线模式下自动为玩家恢复正版皮肤。

## 功能特性
- 离线模式下自动同步正版皮肤
- 支持 Spigot/Paper/Folia 服务器端
- 支持 Velocity 代理端（无需子服重复安装）
- 皮肤数据本地缓存，减少API请求
- 异步处理，性能友好

## 安装方法
### Bukkit/Spigot/Paper/Folia
1. 构建或下载 `bukkit/target/bukkit-*.jar`。
2. 放入服务端 `plugins` 目录。
3. 启动服务器，自动生成配置文件。

### Velocity
1. 构建或下载 `velocity/target/velocity-*.jar`。
2. 放入 Velocity 代理的 `plugins` 目录。
3. 启动代理，自动生成配置文件。

## 配置说明
`config.yml` 示例：
```yaml
cache-file: skin-cache.dat
api-timeout: 5000 # 毫秒
cache-expire-minutes: 1440 # 皮肤缓存有效期（分钟）
```
- `cache-file`：皮肤缓存文件名
- `api-timeout`：Mojang API 超时时间（毫秒）
- `cache-expire-minutes`：皮肤缓存有效期，单位分钟

## 兼容性
- Minecraft 1.20 ~ 1.21
- Spigot/Paper/Folia
- Velocity 3.x

## 构建方法
1. 安装 JDK 17 及以上
2. Maven 构建：
   ```sh
   mvn clean package
   ```
3. 生成的 jar 在各自模块的 `target/` 目录下

## 注意事项
- 需保证服务器能访问 Mojang API
- 频繁重启或大量新玩家可能导致 API 频率限制
- Folia 环境下所有网络请求均为异步

## 开源协议
MIT 