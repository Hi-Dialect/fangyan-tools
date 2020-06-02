	var user_attention_data;
	var url = decodeURI(window.location.href);
 	var argsIndex = url .split("?Info=");
 
 	var arg = argsIndex[1];

 
 
 $.post("http://47.95.220.161:8080/userattention/getUserAttentions",{userNo:arg},function(data){
	 user_attention_data=data;
	 console.log(user_attention_data);
	 var div_1 =document.getElementById('Attention_list');
	 for(i=0;i<user_attention_data.length;i++)
	 {
	 	let d1=user_attention_data[i];
		var attention_userNo={
			userNo:d1.starNo
		}
		console.log(attention_userNo);
		 $.post("http://47.95.220.161:8080/users/getUserInfo",attention_userNo,function(data){
			let div_2=document.createElement('a');
			div_2.className="listItem";
			div_2.href="people-other-info.html?info="+data.userNo;
			div_2.innerHTML="<div class='image'>"+
								"<img src='"+data.userImg+"' alt='avatar'>"+
							"</div>"+
							"<div class='text'>"+
								"<div>"+
									"<strong>"+data.userNa+"</strong>"+
									"<div class='text-muted'>"+
										"<i class='icon ion-ios-pin mr-1'></i>"+
										"上海市"+
									"</div>"+
								"</div>"+
							"</div>";
			div_1.appendChild(div_2);
		});

	 	
	 	
	 }

	 });
	 