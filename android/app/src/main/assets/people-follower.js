	var user_fan_data;
	var url = decodeURI(window.location.href);
 	var argsIndex = url .split("?Info=");
 
 	var arg = argsIndex[1];

 
 $.post("http://47.95.220.161:8080/userattention/getUserFans",{userNo:arg},function(data){
	 user_fan_data=data;
	 var div_1 =document.getElementById('fans_list');
	 for(i=0;i<user_fan_data.length;i++)
	 {
	 	let d1=user_fan_data[i];
		var fans_userNo={
			userNo:d1.fanNo
		}
		// console.log(fans_userNo);
		 $.post("http://47.95.220.161:8080/users/getUserInfo",fans_userNo,function(data){
			 // console.log(data);
			 // console.log(data.userNa);
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
	 