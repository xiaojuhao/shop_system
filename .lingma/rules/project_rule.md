**添加规则文件可帮助模型精准理解你的编码偏好，如框架、代码风格等**
**规则文件只对当前工程生效，单文件限制10000字符。如果无需将该文件提交到远程 Git 仓库，请将其添加到 .gitignore**

# 技术框架
springboot
jdk8

# 框架模块说明
## shop-common: 公共模块
## shop-service: 业务模块
## shop-web: web模块
## shop-admin: 后台管理模块
## shop-dao: 数据访问模块
    dataobject: 存放entity对象，跟数据库表保持一直
    mapper: 存放mapper接口，用于数据访问
    datasource: 存放数据源配置,一般不需要改动
    foundation: 存放一些注解和工具类，通常不需要改动
    query: 封装一些复杂的公用查询方法，一般都是基于数据库做的一些二次封装，里面组合mapper层里面的方法，提供复杂的查询接口
