$(function(){
	if($.session.get('userName')==undefined) {
		window.location.href="sign.html";
	}
	$("#userName").html($.session.get('userName'));
	var userId = $.session.get('userId');
	loadHistory(userId);//获取历史记录
});

function loadHistory(userId){//获取历史记录
	var url = "getHistory/"+userId;
	$.getJSON(url,function(result){
        var app4 = new Vue({
            el: '#tbody',
            data: {
                channels:result.data
            }
        })
	});
}

function selectC(obj) {
	var $tr = $(obj);
	var channelId = $tr.find('td').eq(0).html();
	var name = $tr.find('td').eq(1).html();
	var type = $tr.find('td').eq(2).html();
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