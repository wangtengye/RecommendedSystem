/**
 * Created by Jack on 2017/7/3.
 */
$(function () {
    if($.session.get('userName')==undefined) {
        window.location.href="sign.html";
    }
    var type = GetQueryString("type");
    var name = GetQueryString("name");
    $('#type').text(type);
    $('#channelName').text(name);

});
function GetQueryString(name)//匹配url参数type
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var result = window.location.search.substr(1).match(reg);
    return result?decodeURIComponent(result[2]):null;
}

function back() {
    $.ajax({
        url:"close",
        type:"POST",
        data:JSON.stringify({channelId: GetQueryString("id"), userId: $.session.get('userId')}),
        contentType:"application/json",
        success:function(){
            $(".alert").remove();
            window.location.href='index.html';
        },
        error:function(msg){
            $(".alert").remove();
            $(".container").prepend("<div class='alert alert-danger'>失败！错误原因："+msg.status+"</div>");
        }
    });
}