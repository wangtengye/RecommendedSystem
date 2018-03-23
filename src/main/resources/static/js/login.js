$(function(){//相当于$(document.ready(function(){}))

	$("#inputPwd").keyup(function(){//登录检查
		var pwd = $("#inputPwd").val();
		if(pwd.length<8){
			$("#pwdHint").html("密码须大于8位");
		}
		else{
			$("#pwdHint").html("");
		}
	});

	$("#inputPwd2").keyup(function(){//注册检查
		var pwd = $("#inputPwd").val();
		var pwd2 = $("#inputPwd2").val();
		if(pwd!=pwd2){
			$("#pwdHint").html("两次密码不同");
		}
		else{
			$("#pwdHint").html("");
		}
	});

	$("#signin").click(function(){//登录
		var userName = $("#inputName").val();
		var password = $('#inputPwd').val();
		$.ajax({
			url:"login_auth",
			type:"POST",
			data:JSON.stringify({username: userName, password: password}),
			contentType:"application/json",
			success:function(msg){
				msg = eval('('+msg+')');
				switch(msg.status){
					case 0:{
						$(".alert").remove();
						$(".container").append("<div class='alert alert-danger'>失败！错误原因："+msg.error+"</div>");
						break;
					};
					case 1:{
						$(".alert").remove();
						var userName = msg.data.userName;
						var userId = msg.data.userId;
						$.session.set('userId',userId);
						$.session.set('userName',userName);
						window.location.href="index.html";
						break;
					};
					default:{
						$(".alert").remove();
						break;
					};
				}
			},
			error:function(msg){
				$(".alert").remove();
				$(".container").append("<div class='alert alert-danger'>失败！错误原因："+msg.status+"</div>");
			}
		})
	});

	$("#login").click(function(){//注册
		var userName = $("#inputName").val();
		var password = $("#inputPwd").val();
		var password2 = $("#inputPwd2").val();
		if(password.length<8){
			$(".alert").remove();
			$(".container").append("<div class='alert alert-danger'>密码必须不少于8位！</div>");
			return false;
		}
		else{
			$(".alert").remove();
		}
		if(password!=password2){
			$(".alert").remove();
			$(".container").append("<div class='alert alert-danger'>两次密码不同！</div>");
			return false;
		}
		else{
			$(".alert").remove();
		}
		var json = {
			username:userName,
			password:password,
			password2:password2,
		};

		$.ajax({
			url:"register_auth",
			type:"POST",
			data:JSON.stringify(json),
			contentType:"application/json",
			success:function(msg){
				msg = eval('('+msg+')');
				switch(msg.status){
					case 0:{
						$(".alert").remove();
						$(".container").append("<div class='alert alert-danger'>失败！错误原因："+msg.error+"</div>");
						break;
					};
					case 1:{
						var userName = msg.data.username;
						var userId = msg.data.id;
						$.session.set('userId',userId);
						$.session.set('userName',userName);
						window.location.href="index.html";
						break;
					};
					default:break;
				};
			},
			error:function(msg){
				$(".alert").remove();
				$(".container").append("<div class='alert alert-danger'>失败！错误原因："+msg.status+"</div>");
			}
		});
	});
});

function toLogin(){//去注册
	$(".alert").remove();
	$("#signin").hide();
	$("#to-login").hide();
	$("#login").show();
	$("#to-signin").show();
	$("#title").html("请注册");
	//两次密码
	$("#inputPwd2").css("display","inline");
}
function toSignin(){//去登录
	$(".alert").remove();
	$("#signin").show();
	$("#to-login").show();
	$("#login").hide();
	$("#to-signin").hide();
	$("#title").html("请登录");
	//二次密码
	$("#inputPwd2").css("display","none");
}