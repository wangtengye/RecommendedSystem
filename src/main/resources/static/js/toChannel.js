$(function(){
	if($.session.get('userName')==undefined) {
		window.location.href="sign.html";
	}
	$("#userName").html($.session.get('userName'));
	var type = GetQueryString("type");
	var name = GetQueryString("name");
	getTypes(type,name);
	loadChannel(type);//获得频道信息

	$("#selectAll").click(function(){//绑定全选
		if($(this).is(':checked')){
			$("input[name='checkname[]']").each(function(){
				$(this).attr("checked",true);
			})
		}
		else{
			$("input[name='checkname[]']").each(function(){
				$(this).attr("checked",false);
			})
		}

	})
});

function GetQueryString(name)//匹配url参数type
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var result = window.location.search.substr(1).match(reg);
     return result?decodeURIComponent(result[2]):null;
}

function loadType(type,name){//获得类别信息
	$('#'+type).addClass("active");
	$("#typeName").html(name);
}

function getTypes(type,name) {
	var url='/getChannelTypes';
	$.getJSON(url,function (data) {
		var list = data.data;
		var len = list.length;
		for(var i=0;i<len;i++) {
			var html = "<a href='channel.html?type="+list[i]+"&name="+list[i]+"' class='list-group-item' id='"+list[i]+"'>"+list[i]+"</a>";
			$('#list').append(html);
		}
		loadType(type,name);
	});
}

function loadChannel(type){//获得频道信息
	var url = "getChannels/"+type;
	$.getJSON(url,function(data){
		list = data.data;
		var length = list.length;
		var cType = list[0].type;
		for (var i = 0; i < length; i++) {
			var channel = list[i];//list中的每个频道
			var tr = "<tr onclick='selectC(this)'><td><input type='checkbox' id='"+channel.id
				+"' name='checkname[]' value='"+channel.id+"'>"+
				"</td><td>"+channel.id+
				"</td><td>"+channel.channelName+
				"</td><td>"+cType+
				"</td><td>"+channel.clickTime+
				"</td></tr>";
			$("#tbody").append(tr);
		};
	});
}

function selectC(obj) {
	var $tr = $(obj);
	var channelId = $tr.find('td').eq(1).html();
	var name = $tr.find('td').eq(2).html();
	var type = $tr.find('td').eq(3).html();
	$.ajax({
		url:"click",
		type:"POST",
		data:JSON.stringify({channelId: channelId, userId: $.session.get('userId')}),
		contentType:"application/json",
		success:function(msg){
			$(".alert").remove();
			//播放
			window.location.href='play.html?name='+name+'&type='+type+'&id='+channelId;
		},
		error:function(msg){
			$(".alert").remove();
			$(".container").prepend("<div class='alert alert-danger'>失败！错误原因："+msg.status+"</div>");
		}
	});
}