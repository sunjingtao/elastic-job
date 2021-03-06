+++
toc = true
date = "2017-01-07T16:14:21+08:00"
title = "运维平台"
weight = 11
prev = "/02-guide/cloud-concepts/"
next = "/02-guide/cloud-restful-api/"
+++

运维平台内嵌于elastic-job-cloud-scheduler的jar包中, 无需额外启动WEB付服务器, 可通过修改配置文件中http_port参数来调整启动端口,默认端口为8899，访问地址为http://{your_scheduler_ip}:8899。

## 登录

提供两种账户，管理员及访客，管理员拥有全部操作权限，访客仅拥有察看权限。默认管理员用户名和密码是root/root，访客用户名和密码是guest/guest，可通过conf\auth.properties修改管理员及访客用户名及密码。

## 功能列表

* 作业APP管理（发布、修改、查看）

* 作业管理（注册、修改、查看以及删除）

* 作业状态查看（待运行、运行中、待失效转移）

* 作业历史查看（运行轨迹、执行状态、历史dashboard）

## 设计理念

运维平台采用纯静态HTML+JS方式与后台的RESTful  API交互，通过读取作业注册中心展示作业配置和状态，数据库展现作业运行轨迹及执行状态，或更新作业注册中心数据修改作业配置。
