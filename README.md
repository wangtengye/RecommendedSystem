软件工程大作业
基于协同过滤算法的推荐系统

----------
## 理解流程
- `Application.customize`可自定义启动端口
- `index.js`里面调用`jquery.session`判断`userName`是否存在来判断用户是否已经通过验证，进而决定是否重定向到登录页面
- `springboot`自动映射`/`到`index.html`，所以不用显示写出路径映射
- 登录和注册用的是同一个HTML文件，`login.js`根据是登录还是注册显示不同的组件
- 类的总体说明
    - `AddDataController`：纯粹是为了增添数据库的数据，与用户逻辑无关
    - `LoginController`：用户的登录和注册
    - `UserController`：其他的所有逻辑，包括频道的加载，用户推荐等。注意的是频道id和用户id相同时，只有一条观看历史，这是因为history表的联合主键为频道id和用户id，所以用一个户观看同个频道时，只会更新时间，不会插入记录。
    - `LoginRequest`，`RegisterRequest`：分别对应将其登录，注册上传的json解析为对象，方便处理
    - `HistoryReturn`，`LoginReturn`：作为`Message`的数据返回给前端，再通过`jqery`取值操作`dom`
    - `Message`：返回给前端的对象，包括状态，错误信息，数据。这里采用了泛型，挺好的设计方案。
    - `mapper`下的所有类：对应不同表的操作，可以具体查看下`JpaRepository`的用法
    - `model`下除了`UserChannelPK`外都对应一张表，`UserChannelPK`对应`history`表的联合主键，重写`equals`和`hashCode`是为了主键相等时的判断（从主键定义上来讲讲应该如此做）。
  
## 重构
- 前端界面采用`Vue.js`重构，之前前端界面采用`jQuery`操作`dom`，通过字符串拼接`<tr>`元素等，不仅会出错，而且不利于`HTML`界面的总体理解，现用`Vue.js`进行重写。`Vue.js`是一个精简的`MVVM`。从技术角度讲，`Vue.js` 专注于 `MVVM` 模型的 `ViewModel` 层。它通过双向数据绑定把 `View` 层和 `Model` 层连接了起来，通过对数据的操作就可以完成对页面视图的渲染，不再需要引用相应的`DOM`对象，可以说数据和`View`是分离的。
以用户查看观看历史为例子，
重构前的样子：
```
	<tbody id="tbody">
					
	</tbody>
	
	for (var i = 0; i < length; i++) {
			var channel = list[i];//list中的每个频道
			$("#tbody").append("<tr onclick='selectC(this)'><td>"+channel.channelId+"</td><td>"+channel.channelName+"</td><td>"+channel.type+"</td><td>"+channel.startTime+"</td><td>"+channel.lastTime+"</td></tr>");
	};
```
重构后的样子：
```
	<tbody id="tbody">
		<tr onclick='selectC(this)' v-for="channel in channels">
			<td>{{channel.channelId}}</td>
			<td>{{channel.channelName}}</td>
			<td>{{channel.type}}</td>
			<td>{{channel.startTime}}</td>
			<td>{{channel.lastTime}}</td>
		</tr>;
	</tbody>
	
	var app4 = new Vue({
	    el: '#tbody',
	    data: {
	        channels:result.data
	    }
	})
```
可以看出重构后代码逻辑更加清晰，避免字符串拼接时产生错误。
## 发现的bugs
- 注册界面上的记住我只是显示而已，没有实际作用
- `ChannelRepository`的`findByType`没有作用,发现原因：中文乱码。指定数据库连接编码解决。
赶快修复呀