软件工程大作业
基于协同过滤算法的推荐系统

----------
## 理解流程
- `Application.customize`可自定义启动端口
- `index.js`里面调用`jquery.session`判断`userName`是否存在来判断用户是否已经通过验证，进而决定是否重定向到登录页面
- `springboot`自动映射`/`到`index.html`，所以不用显示写出路径映射
- 登录和注册用的是同一个HTML文件，`login.js`根据是登录还是注册显示不同的组件

## 发现的bugs
- 注册界面上的记住我只是显示而已，没有实际作用
- `ChannelRepository`的`findByType`没有作用,发现原因：中文乱码。指定数据库连接编码解决。
