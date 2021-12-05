# Bubble-Line及系统相关代码
本仓库主要存放与论文《Bubble-Line：多元线状空间数据抽象可视化》有关的算法实现、实验平台、实验问卷数据。

## 文件夹说明
### bubble-line-vis
- bubble-line算法实现代码及前端模块
- 安装使用
1. 安装相关依赖包
```
npm install
```
2. 运行前端服务器
```
npm run serve
```
3. 如需打包
```
npm run build
```
### server
- 平台后端部分，基于SpringBoot+Mybatis+Maven+Mysql，要求jdk1.8及以上，主要涉及数据处理，如归一化，墨卡托投影等
- 安装使用
- 安装jdk1.8及以上，mysql5.5及以上，maven3.6.3及以上
- 使用maven安装依赖后，运行web-application模块\web-application\src\main\java\cn\hfut\web里的WebApplication.java启动平台后端，端口号、数据库连接密码修改请打开Bubble-Line\server\web-application\src\main\resources里的application.yml文件

### data
涉及到的数据及爬虫代码，链家网房价爬虫代码用python编写，安装python环境即可运行

### experiment 
实验相关文件