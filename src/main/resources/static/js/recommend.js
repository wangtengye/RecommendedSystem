$(function(){
    if($.session.get('userName')==undefined) {
        window.location.href="sign.html";
    }
    $("#userName").html($.session.get('userName'));//session
    var userId = $.session.get('userId');
    show10();
});

function show_event(e){//捕获关闭
    if(!e){
        var e = window.event;
    }
    var targ = e.target;
    var v_id = targ.id;
    console.log(v_id);
    if(v_id=="message_close"){
        console.log("close");
        clearTimeout(t);//感觉没用。。。
        testMessager();
    }
}


function show10(){//显示10秒
    var url='recommendtopk/'+$.session.get('userId')+'/10';
    $.getJSON(url,function(msg){
        switch(msg.status){
            case 0:{
                $(".alert").remove();
                $("#main").prepend("<div class='alert alert-danger'>失败！错误原因："+msg.error+"</div>");
                break;
            };
            case 1:{
                $(".alert").remove();
                /*var channel = msg.data;
                var type = channel.type;
                var channelName = channel.channelName;
                var channelId = channel.id;
                $.messager.show('频道推荐','此时您可能想看：<u>'+channel+'</u>', 10000);
                $("#message").click(function(){
                    window.location.href='play.html?name='+channelName+'&type='+type+'&id='+channelId;
                });
                setTimeout("testMessager()",60000);//10秒后又开始自动推荐倒计时*/
                var vm = new Vue({
                    el: '#rec',
                    data: {
                        msg: msg.data
                    }
                })
                break;
            };
            default:{
                $(".alert").remove();
                break;
            }
        };
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


function exit() {
    if(confirm("是否退出")){
        $.session.remove('userName');
        $.session.remove('userId');
        window.location.href="sign.html";
    };
}




function choose(type){//选择类别
    window.location.href="channel.html?type="+type+'&name='+type;
/*    switch(type){
        case 1:{
            window.location.href="channel.html?type='+type&name=体育";
            break;
        };
        case 2:{
            window.location.href="channel.html?type=动画&name=动画";
            break;
        };
        case 3:{
            window.location.href="channel.html?type=3&name=类别三";
            break;
        };
        case 4:{
            window.location.href="channel.html?type=4&name=类别四";
            break;
        };
        default:break;
    }*/
}