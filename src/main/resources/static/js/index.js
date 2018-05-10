$(function(){
    if($.session.get('userName')==undefined) {
        window.location.href="sign.html";
    }
    $("#userName").html($.session.get('userName'));//session
    showTypes();
    testMessager();
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

function showUp(){//显示10秒
    var url='recommend/'+$.session.get('userId');
    $.getJSON(url,function(msg){
        switch(msg.status){
            case 0:{
                $(".alert").remove();
                $("#main").prepend("<div class='alert alert-danger'>失败！错误原因："+msg.error+"</div>");
                break;
            };
            case 1:{
                $(".alert").remove();
                var channel = msg.data;
                var type = channel.type;
                var channelName = channel.channelName;
                var channelId = channel.id;
                $.messager.show('频道推荐','此时您可能想看：<u>'+channelName+'</u>', 10000);
                $("#message").click(function(){
                    window.location.href='play.html?name='+channelName+'&type='+type+'&id='+channelId;
                });
                setTimeout("testMessager()",60000);//10秒后又开始自动推荐倒计时
                break;
            };
            default:{
                $(".alert").remove();
                break;
            }
        };
    });
}

function testMessager() {//测试推荐
    setTimeout("showUp()",60000);
}
function exit() {
    if(confirm("是否退出")){
        $.session.remove('userName');
        $.session.remove('userId');
        window.location.href="sign.html";
    };
}

function showTypes() {
    var url='/getChannelTypes';
    $.getJSON(url,function (data) {
        var list = data.data;
        var len = list.length;
        for(var i=0;i<len;i++) {
            if(i==0)
            {
                var html = '<div class="col-xs-6 col-sm-4 placeholder" id="forth" onclick="choose(\''+ list[i] +'\')">' +
                '<img src="img/'+(i+1)+'.png" width="100" height="100" class="img-responsive" alt="">' +
                '<h4>'+list[i]+'</h4>' +
                '</div>';
                $('#types').append(html);
                }
            else
            {
                var html1 = '<div class="col-xs-6 col-sm-4 placeholder" >' +
                '<img src="img/transparent.png" height="100" class="img-responsive" alt="">' +
                '<h4>-</h4>' +
                '</div>';
                var html2 = '<div class="col-xs-6 col-sm-4 placeholder" id="forth" onclick="choose(\''+ list[i] +'\')">' +
                '<img src="img/'+(i+1)+'.png" width="100" height="100" class="img-responsive" alt="">' +
                '<h4>'+list[i]+'</h4>' +
                '</div>';
                $('#types').append(html1+html2);
                }

        }
    });
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