var list;
var id_name;
var id_sex;
var id_img1;
//个人信息
var url = decodeURI(window.location.href);
		var argsIndex = url .split("?Info=");
		
		var arg = argsIndex[1];
		if(arg){
			var str1 = JSON.parse(arg);					
			var id_img=document.getElementById('user_img');
				id_img1=document.createElement('img');		
				id_img1.src=str1.userImg;
				id_img1.alt='avatar';
				id_img1.className="imageBlock circle xlarge";
			var id_p=document.createElement('p');
				id_p.style="font-size: 5px";
				id_p.textContent="点击更换头像";
				id_img.appendChild(id_img1);
				id_img.appendChild(id_p);
			
				id_name=document.getElementById('user_name');
			id_name.value=str1.userNa;
			id_sex=document.getElementById('exampleFormControlSelect1');
			$(id_sex).children('option').eq(str1.sex).prop('selected', true);
			console.log(str1.sex);
			
			var id_signature=document.getElementById('user_signature');
			var signature="感谢各位的关注❤<br>土生土长上海人带你领略上海话的美"
				signature=signature.replace(/<br>/g,"\n");
				id_signature.value=signature;
			var id_city=document.getElementById('showCityPicker');
				id_city.value="上海";
			
		}
		else{

			var id_img=document.getElementById('user_img');
			var id_img1=document.createElement('img');		
				id_img1.src="img/sample/avatar.jpg";
				id_img1.alt='avatar';
				id_img1.className="imageBlock circle xlarge";
			var id_p=document.createElement('p');
				id_p.style="font-size: 5px";
				id_p.textContent="点击更换头像";
				id_img.appendChild(id_img1);
				id_img.appendChild(id_p);
		}
		
//所在地城市

function deleteLabel(list,a)
{

	for(var i=0;i<list.length;i++){
		
	
		if(list[i].labelNa==a){
			
			list.splice(i,1);
			break;
		}			
	}
}

(function($, doc) {
			$.init();
			$.ready(function() {
				var _getParam = function(obj, param) {
					return obj[param] || '';
				};
				var cityPicker = new $.PopPicker({
					layer: 2
				});
				cityPicker.setData(cityData);
				var showCityPickerButton = doc.getElementById('showCityPicker');
				var cityResult = doc.getElementById('cityResult');
				showCityPickerButton.addEventListener('tap', function(event) {
					
					cityPicker.show(function(items) {

						showCityPickerButton.value=items[0].text;
						//返回 false 可以阻止选择框的关闭
						//return false;
					});
				}, false);
			});
		})(mui, document);

//添加标签
var WS = function(opt){
	$.get("http://47.95.220.161:8080//labels/getAllLabels",function(data){
		var regexp = opt.regexp || /\S/,
			el = $(opt.el), 
				
				holder = $('<span class="words-split"></span>'),
				add = $('<a href="javascript:void(0)" class="words-split-add">+</a>'),								
				count;
				
				
				
		list=data;
		count=(list.length>6?6:list.length);
		console.log(list);
	
		
		// list.remove(0);
		// console.log(typeof list[0]);
		// console.log(list[0]);
		for (var i = 0; i < count; i++) {
			holder.append( $('<a href="javascript:void(0)" class="fm-button">'+list[i].labelNa+'<em> </em></a>') );
		}
		
	
		el.hide().after( holder );
		
		holder.after(add);
	
		holder.on('click','a>em',function(){	//刪除
			$(this).parent().remove();
			//在数组中删除
			deleteLabel(list,$(this).parent().text().replace(/[ ]/g,""));
			console.log(list);
			//对数据库操作
			el.val( holder.text().match(/\S+/g).join(',') )
		});
		
		//点击加号
		add.on('click',function(){				//添加预处理
			$(this).hide();
			holder.append( $('<input type="text" class="lbl-input" />') )
		});
		
		//点击空白处
		holder.on('blur','.lbl-input',function(){	//验证添加字段
		
			var t = $(this),
				v = t.val().toString();
			if( regexp.test(v) ){
				t.remove();
				add.show();
				//将添加的加入list中
				list.push({labelNa:v});
				holder.append( $('<a href="javascript:void(0)" class="fm-button">'+v+'<em> </em></a>') );
				el.val( holder.text().match(/\S+/g).join(',') )
			}else if(!v){
				t.remove();
				add.show();
			}else{
				alert('输入少于10字');
			}
			// console.log(list);
			if(list[2].labelId)
			{
				//不传
				console.log("true");
			}
			else{
				console.log("false")
				//传 编辑
			}
		});
		//响应键盘
		holder.on('keydown','.lbl-input',function(e){
			switch(e.keyCode){
				case 13:
				case 27: $(this).trigger('blur');
			}
		});	
	});
}
	//给标签做正则表达
	WS({el:'#staticPath2',regexp:/^[\u4e00-\u9fa5a\w]{1,20}$/});
	
	
	//保存按钮
	function save()
	{
		// id_name=document.getElementById('user_name');
		
		
		var user={
			userNo:1,
			userNa:id_name.value,
			userImg:id_img1.src,
			phone:1,
			QQNum:1,
			WeChatNum:1,
			sex:$(id_sex).val()
			
		}
		console.log(user);

		
		$.post("http://47.95.220.161:8080/users/edit",user,function(data){
		});
		
	}
	
