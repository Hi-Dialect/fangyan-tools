
	//处理url传来的信息
	var url = decodeURI(window.location.href);
		var argsIndex = url .split("?info=");

		var arg = argsIndex[1];

	var user_no=1;
   var user_data;
   var user_made_data;
   var user_like_data;
   var If_attention=false;
   var other_url={
	   userNo:arg
   };
   layout1();
   function layout1(){
	   
	   //查看是否关注过
	  var post1=$.post("http://47.95.220.161:8080/userattention/getUserAttentions",{userNo:user_no},function(data){
			console.log(data);
			If_attention=false;	

	   		for(var i=0;i<data.length;i++)
	   		{
				
	   			if(data[i].starNo==arg)
				{
					If_attention=true;
					break;
				}	   				

					
	   		}
			console.log(If_attention);
	   });
	   
	//获取用户信息
	$.when(post1,post2).done(function(){
		$.post("http://47.95.220.161:8080/users/getUserInfo",other_url,function(data){
		    user_data=data;	
			let div_1=document.getElementById('user2');
			div_1.innerHTML="<div>"+
							"<img src='"+user_data.userImg+"' alt='avatar' class='imageBlock circle large toggleSidebar1'>"+
						"</div>"+
						"<div class='ml-1 mt-1'>"+
							"<h5><strong>"+ user_data.userNa +"</strong></h5>"+
							"<div class='text-muted'>"+
								"<span class='iconify' data-icon='ion:location-sharp' data-inline='false'></span>"+
								"上海"+
							"</div>"
						"</div>"
		let div_2=document.getElementById('signature');
			div_2.innerHTML="感谢各位的关注❤️<br>"+
						"土生土长上海人带你领略上海话的美";
		let div_3=document.getElementById('info');
			div_3.innerHTML="<div class='col text-center'>"+
							"<strong><div class='text-muted'>发布</div></strong>"+
							"<h5>"+user_data.attentionNum+"</h5>"+
						"</div>"+
						"<div class='col text-center' id='attention_people'> "+
							"<strong><div class='text-muted'>关注</div></strong>"+
							"<h5>"+user_data.attentionNum+"</h5>"+
						"</div>"+
						"<div class='col text-center'id='fans_people'>"+
							"<strong><div class='text-muted'>粉丝</div></strong>"+
							"<h5>"+user_data.fansNum+"</h5>"+
						"</div>"
		let div_4=document.getElementById('attention');
		var attention_botton="<div class='col-6'>"+
								"<a href='javascript:;' class='btn btn-primary btn-block'onclick=attent()> ";
								
			if(If_attention) attention_botton+="取消关注";
			else attention_botton+="<i class='icon ion-md-add'></i> 关注";
			attention_botton+="</a>"+							
							   "</div>"+
								"<div class='col-6'>"+
									"<a href='javascript:;' class='btn btn-danger btn-block'>"+
										"<i class='icon ion-ios-mail'></i> 私信"+
									"</a>"+
								"</div>";
			div_4.innerHTML=attention_botton;	
		});  
	})
   	//获得制作的视频的信息
   	var post2=$.post("http://47.95.220.161:8080/videos/getMadeByUserNo",other_url,function(data){
   		user_made_data=data;
   
   
   			let div_3=document.getElementById('release_1');
   			$(div_3).empty();
   			for(i=0;i<user_made_data.length;i++)
   			{
   				let d1=user_made_data[i];
   				let div_4=document.createElement('div');
   				div_4.className="col-6 mt-2";
   				
   				var Vedios_info="<a href="+d1.vdoPath+" class='postItem'>"+
   					   " <div class='imageWrapper'>"+
   						   " <img src="+d1.vdoImg+" alt='image' class='image'>";
   					//判断是否有标签
   					if(d1.videoLabels.length){
   						Vedios_info+="<div class='badge badge-warning'>"+d1.videoLabels[0].labelNa+"</div>"; 
   					}
   					Vedios_info+=" </div>"+
   						"<h2 class='title'>"+d1.vdoNa+"</h2>"+
   					"</a>";
   				
   				div_4.innerHTML=Vedios_info;
   				div_3.appendChild(div_4);
   				
   			}
   		
   			
   	});
   	//获得喜欢的视频的信息
   	$.post("http://47.95.220.161:8080/videos/getLikeVdoByUserNo",other_url,function(data){ 
   		
   		user_like_data=data;
   		let div_5=document.getElementById('like_1');
   		$(div_5).empty();
   		for(i=0;i<user_like_data.length;i++)
   		{
   			
   			let d1=user_like_data[i];
   			let div_4=document.createElement('div');
   			div_4.className="col-6 mt-2";
   
   			var Vedios_info="<a href="+d1.vdoPath+" class='postItem'>"+
   				   " <div class='imageWrapper'>"+
   					   " <img src="+d1.vdoImg+" alt='image' class='image'>";
   				//判断是否有标签
   				if(d1.videoLabels.length){
   					Vedios_info+="<div class='badge badge-warning'>"+d1.videoLabels[0].labelNa+"</div>"; 
   				}
   				Vedios_info+=" </div>"+
   					"<h2 class='title'>"+d1.vdoNa+"</h2>"+
   				"</a>";
   			
   			div_4.innerHTML=Vedios_info;
   	   
   			div_5.appendChild(div_4);
   			
   		}
   	});
   
   };
	//改变关注
	function attent(){
	   var temp={
	   		fanNo:user_no,
			starNo:arg
	   }	
	   if(If_attention)
	   {		   
		   $.post("http://47.95.220.161:8080/userattention/cancelAtten",temp,function(data){ });
		   
	   }
	   else{
		   $.post("http://47.95.220.161:8080/userattention/addAttention",temp,function(data){ });   
		   
	   }
	   setTimeout(function(){
		   layout1();
	   },100);
   }
  
   $(document).on('click','#attention_people',function(e){
   	var url="people-following.html?Info="+user_no;
   	window.open(url);
   });
   $(document).on('click','#fans_people',function(e){
   	var url="people-follower.html?Info="+user_no;
   	window.open(url);
   });
   

   
   

	
		

