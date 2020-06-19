   var user_data;
   var user_made_data;
   var user_like_data;
   var user_no=1;
   var us1={userNo:user_no};
   data=JSON.stringify(us1);
   
   var post1=$.post("http://47.95.220.161:8080/videos/getMadeByUserNo",us1,function(data){
   	user_made_data=data;
   		// console.log('成功, 收到的数据: ' + JSON.stringify(data));
		
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
   $.post("http://47.95.220.161:8080/videos/getLikeVdoByUserNo",us1,function(data){
   	user_like_data=data;
   		// console.log('成功, 收到的数据: ' + JSON.stringify(data));
		let div_5=document.getElementById('like_1');
		$(div_5).empty();
		for(i=0;i<user_like_data.length;i++)
		{
			
			let d1=user_like_data[i];
			let div_4=document.createElement('div');
			div_4.className="col-6 mt-2";
			
			// 视频信息
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
   //获得用户信息
   $.when(post1).done(function(){
   	   $.post("http://47.95.220.161:8080/users/getUserInfo",{userNo:1},function(data){
   	   	   user_data=data;
   	   		// console.log('成功, 收到的数据: ' + JSON.stringify(user_data));
   	   		
   	   		
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
   	   					"</div>";
		//个人标签
   	    let div_2=document.getElementById('signature');
   	   		div_2.innerHTML="感谢各位的关注❤️<br>"+
   	   					"土生土长上海人带你领略上海话的美";
   	   	let div_3=document.getElementById('info');
   	   		div_3.innerHTML="<div class='col text-center'>"+
   	   						"<strong><div class='text-muted'>发布</div></strong>"+
   	   						"<h5>"+user_made_data.length+"</h5>"+
   	   					"</div>"+
   	   					"<div class='col text-center' id='attention_people'>"+
   	   						"<strong><div class='text-muted' >关注</div></strong>"+
   	   						"<h5>"+user_data.attentionNum+"</h5>"+
   	   					"</div>"+
   	   					"<div class='col text-center' id='fans_people'>"+
   	   						"<strong><div class='text-muted'>粉丝</div></strong>"+
   	   						"<h5>"+user_data.fansNum+"</h5>"+
   	   					"</div>"
   	   	});
   })
   
	
	//弹出选项框
	$(document).on('click','.toggleSidebar1',function(e){
			   
	    $(".set_").fadeToggle(200);
			   // console.log($(e.target).is('.overlay'));
			   
			   if($(e.target).is('.overly')){
				  
			   }
			   else{
				   if($(".set_ .sidebar1").hasClass("is-active")){
				       $(".set_ .sidebar1").removeClass("is-active");
				       $(".set_ .sidebar1").addClass("is-passive");
				   }
				   else{
				       $(".set_ .sidebar1").addClass("is-active");
				   }
			   }
				e.stopPropagation();       
	});
	
	//编辑资料
	$(document).on('click','#setuesrInfo',function(e){
		edit_info();
	});
	$(document).on('click','#attention_people',function(e){
		var url="people-following.html?Info="+user_no;
		window.open(url);
	});
	$(document).on('click','#fans_people',function(e){
		var url="people-follower.html?Info="+user_no;
		window.open(url);
	});
	
	
	function edit_info()
	{
		var index=JSON.stringify(user_data);
		var url="people-info-set.html?Info="+index;
		
		window.open(url);
	}