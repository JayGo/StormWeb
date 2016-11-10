var addr = "";

function GetRequest() {
	var url = location.search; // »ñÈ¡urlÖÐ"?"·ûºóµÄ×Ö´®
	// alert(url);
	var theRequest = new Object();
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);
		strs = str.split("&");
		for (var i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
		}
	}
	return theRequest;
}

function sleep(d) {
	for (var t = Date.now(); Date.now() - t <= d;);
}

function play(playerId, cilpUrl, rtmpUrl) {
	flowplayer(playerId, "player/swf/flowplayer-3.2.18.swf", {
		clip: {
			url: cilpUrl,
			provider: 'rtmp',
			live: true,
		},
		plugins: {
			rtmp: {
				url: 'player/swf/flowplayer.rtmp-3.2.13.swf',
				netConnectionUrl: rtmpUrl
			}
		}
	});
}

function submit(effect) {
	if (effect != null && effect != "none") {
		$.ajax({
			type: "GET",
			url: "./api/v1/camera/startEffect?effect=" + effect + "&addr=" + addr,
			dataType: "json",
			contentType: "application/json",
			async: true,
			success: function(data, textStatus, jqXHR) {
				if (data.result == true) {
					var msgs = data.message.split(",");
					var rtmpAddr = msgs[0];
					var rtmpAddrs = rtmpAddr.split("/");
					var streamId = rtmpAddrs[rtmpAddrs.length - 1];
					initialPlayer(rtmpAddrs, streamId);
				}
			},
			error: function(data, textStatus, jqXHR) {
				alert("error:" + textStatus);
			}
		});
	}
}

function checkEffect(effectValue) {
	// alert();
	switch (effectValue) {
		case 'gray':
			{
				// alert("gray");
				if (!$("#canny-parameter-list").hasClass("hidden")) {
					$("#canny-parameter-list").addClass("hidden");
				}
				if ($("#gray-parameter-list").hasClass("hidden")) {
					$("#gray-parameter-list").removeClass("hidden");
				}

				//	$("#dialog-form .parameter-list").append(createLi("R", "INPUT_PARA"));
				//	$("#dialog-form .parameter-list").append(createLi("G", "INPUT_PARA"));
				//	$("#dialog-form .parameter-list").append(createLi("B", "INPUT_PARA"));
				break;
			}
		case 'canny_edge':
			// alert("cannyEdge");
			if (!$("#gray-parameter-list").hasClass("hidden")) {
				$("#gray-parameter-list").addClass("hidden");
			}
			if ($("#canny-parameter-list").hasClass("hidden")) {
				$("#canny-parameter-list").removeClass();
			}

			break;
		case 'color_histogram':
			// alert("colorHistogram");
			break;
		case 'foreground_extraction':
			// alert("foregroundExtraction");
			break;
		default:
			alert("no effect selected!");
	}
}

function addCameraToBox(addr, effect, parameters, id) {
	// alert("addCameraToBox is invoked!");
	var previewImgAddr = "";

	var box = document.querySelector("#stream-box-template");

	box.content.querySelector("li").id = "box" + id;
	box.content.querySelector(".ellipsis").innerHTML = addr+ "-" + id;
	box.content.querySelector(".tag.ellipsis").innerHTML = "处理效果: " + effect;
	box.content.querySelector("a").id = "player" + id;

	$("#stream-list-contentbox").append(box.content.cloneNode(true));

	var paraSection = $("#box" + id + " .boxarea .mes p");
	var paraItem = document.querySelector("#parameters-list-template");

	for (var i in parameters) {
		// alert("shuchu:" + i + ":" + parameters[i]);
		paraItem.content.querySelector("label").for = i + "";
		paraItem.content.querySelector("label").innerHTML = i;
		paraItem.content.querySelector("input").name = i;
		paraItem.content.querySelector("input").value = parameters[i];
		paraItem.content.querySelector("input").setAttribute("disabled", "true");
		paraSection.append(paraItem.content.cloneNode(true));
	}

	play("player" + id, 'hks', 'rtmp://live.hkstv.hk.lxdns.com/live');
}

function initialPlayer(rtmpAddr, streamId) {

	var li = document.createElement("li");
	li.className = "video-item";
	li.id = "video-item" + streamId;

	var divMargin = document.createElement("div");
	divMargin.className = "videomargin";
	divMargin.id = "vmargin" + streamId;

	var divBox = document.createElement("div");
	divBox.className = "videobox";
	divBox.id = "videobox" + streamId;

	var span = document.createElement("div");
	span.className = "videoname";
	span.id = "video" + streamId;
	span.innerHTML = streamId + "画面";

	var a = document.createElement("a");
	a.id = "player" + streamId;
	a.style.width = "320px";
	a.style.height = "240px";

	// divBox.appendChild(span);
	// divBox.appendChild(a);
	// divBox.appendChild(span);

	li.appendChild(divMargin);
	li.appendChild(divBox);

	$(".videosection .video-list").append(li);

	var playerId = "player" + streamId;

	// play(playerId, rtmpAddr);
}

function commitChangeParas(obj) {
	// alert(obj.nodeName);
	var p = obj.parentNode;
	var childs = p.children;
	// alert(childs.length);
	$(obj).prev().removeClass("hidden");
	for (var i = 0; i < childs.length; i++) {
		if (childs[i].nodeName == "INPUT") {
			childs[i].setAttribute("disabled", "true");
		}
	}
	$(obj).addClass("hidden");
	alert("参数修改已提交！");
}

function changeParas(obj) {
	// alert(obj.nodeName);
	var p = obj.parentNode;
	var childs = p.children;
	// alert(childs.length);
	$(obj).next().removeClass("hidden");
	for (var i = 0; i < childs.length; i++) {
		if (childs[i].nodeName == "INPUT") {
			childs[i].removeAttribute("disabled");
		}
	}
	$(obj).addClass("hidden");
}

function switchCamera(element) {
	var targetId = element.getAttribute("id");
	// alert("we're going to: "+targetId+".html");
	// alert("切换至摄像头"+nowCamId);
	// updateRawCameraBox();
	// updateEffectCameraBox();
	// window.location.href = targetId+".html";
}

function addCameraToList(addr, id) {
	var item = document.querySelector("#camli-template");
	var name = "摄像头" + id;
	item.content.querySelector(".camlist-li").id = id;
	item.content.querySelector("span").innerHTML = name;
	item.content.querySelector(".camlist-li").setAttribute("onclick", "switchCamera(this)");
	item.content.querySelector(".camlist-li").setAttribute("selected", "false");
	item.content.querySelector(".camlist-li .switch-cam").href = id + ".html";
	item.content.querySelector(".camlist-li .hidden").innerHTML = addr;

	$(".left-menu .leftnav-camlist .camlist-ul").append(item.content.cloneNode(true));
	cameraCount++;
}

function initCameraList() {
	$.ajax({
		async: false,
		type: "GET",
		url: "./api/v1/camera/allCamerasList",
		dataType: "json",
		contentType: "application/json",
		success: function(data, textStatus, jqXHR) {
			var cameraInfosJSONStr = JSON.stringify(data);

			if (data == "") {
				alert("Camera list is empty!");
			} else {

				for (var i = 0; i < data.length; i++) {
					var id = data[i].id;
					var addr = data[i].addr;

					// 填充左侧列表
					addCameraToList(addr, id);
				}
			}
		},
		error: function(data, textStatus, jqXHR) {
			alert("error:" + textStatus);
		}
	});
}

function clearCameraBox() {
	var boxListLength = $(".play-list li").length;
	for (var i = 1; i < boxListLength; i++) {
		$("#box" + i).remove();
		streamCount--;
		// alert("streamCount: "+streamCount);
	}

}

function updateEffectCameraBox() {
	// clearCameraBox();
	$.ajax({
		async: false,
		type: "GET",
		url: "./api/v1/camera/allEffects?id=" + nowCamId,
		dataType: "json",
		contentType: "application/json",
		success: function(data, textStatus, jqXHR) {
			if (data != undefined) {
				for (var i = 0; i < data.length; i++) {
					var camId = data[i].camId;
					var playerId = data[i].playerId;
					var rtmpUrl = data[i].rtmpUrl;
					var effectType = data[i].effectType;
					var parameters = data[i].parameters;
					// alert(camId + "\\" + playerId + "\\" + rtmpUrl + "\\" + effectType + "\\" + parameters);
					var paraDic = {};
					// alert(JSON.stringify(parameters));
					for (var j = 0; j < parameters.entry.length; j++) {
						// alert(parameters.entry[j].key + " : " + parameters.entry[j].value);
						paraDic[parameters.entry[j].key] = parameters.entry[j].value;
					}
					addCameraToBox(rtmpUrl, effectType, paraDic, streamCount);
					streamCount++;
					// alert("streamCount: "+streamCount);
					// sleep(1800);
				}
			} else {
				alert("Error:" + data.message);
			}
		},
		error: function(data, textStatus, jqXHR) {
			alert("wrong in updateEffectCameraBox");
			alert("error:" + textStatus);
		}
	});
}

function updateRawCameraBox() {
	$.ajax({
		async: false,
		type: "GET",
		url: "./api/v1/camera/rawRtmp?id=" + nowCamId,
		dataType: "json",
		contentType: "application/json",
		success: function(data, textStatus, jqXHR) {

			if (data != undefined) {
				var rtmpAddr = data.rtmpUrl + "-" + data.playerId;
				// alert("rawRtmp is: "+rtmpAddr);
				$("#box0 h3").html(rtmpAddr);
			} else {
				alert("Error:" + data.message);
			}
		},
		error: function(data, textStatus, jqXHR) {
			alert("wrong in updateRawCameraBox");
			alert("error:" + textStatus);
		}
	});
	play("player0", 'hks', 'rtmp://live.hkstv.hk.lxdns.com/live');
}

function getNowPageCamId() {
	var pathName = window.location.pathname;
	var strs1 = new Array();
	strs1 = pathName.split("/");
	var pageName = strs1[strs1.length - 1];

	if (pageName == "")
		return pageName;

	var strs2 = new Array();
	strs2 = pageName.split(".");
	return strs2[0];
}

var nowCamId = 0;
var streamCount = 1;

jQuery(document).ready(function($) {

	//addr = $(".camlist-li").css("selected","true").attr("id");

	var Request = GetRequest();
	addr = Request['addr'];

	// alert(getNowPageCamId());
	var resCamId = getNowPageCamId();
	if (resCamId != "")
		nowCamId = resCamId;
	alert("Now camId: " + resCamId);

	initCameraList();
	updateRawCameraBox();
	updateEffectCameraBox();

	// addCameraToList('rtmp://live.hkstv.hk.lxdns.com/live',"摄像头"+0);

	// addCameraToBox('rtmp://live.hkstv.hk.lxdns.com/live', null, null, streamCount);
	// streamCount++;

	//	if(addr) {
	//		$.ajax({
	//			type: "GET",
	//			url: "./api/v1/camera/play?addr="+addr,
	//			dataType: "json",
	//			contentType: "application/json",
	//			async: false,
	//			success: function(data, textStatus, jqXHR) {
	//				if (data.result == true) {
	//					var addrs = data.message;
	//					var splitAddrs = addrs.split(",");
	//					for(var i = 0; i < splitAddrs.length; i++) {
	//						var rtmpAddr = splitAddrs[i];
	//						var rtmpAddrs = rtmpAddr.split("/");
	//						var streamId = rtmpAddrs[rtmpAddrs.length - 1];
	//						initialPlayer(rtmpAddr, streamId);
	//					}	
	//				}
	//				else {
	//					alert("Error:" + data.message);
	//				}
	//			},
	//			error: function(data, textStatus, jqXHR) {
	//				alert("error:" + textStatus);
	//			}
	//		});
	//	}
	//	
	//	$.ajax({
	//		type : "GET",
	//		url : "./api/v1/camera/allCameraLists",
	//		dataType : "json",
	//		contentType : "application/json",
	//		async : true,
	//		success : function(data, textStatus, jqXHR) {
	//			var cameraInfosJSONStr = JSON.stringify(data);
	//
	//			if (data == "") {
	//				alert("Camera list is empty!");
	//			} else {
	//
	//				for (var i = 0; i < data.length; i++) {
	//					var name = "摄像头" + i;
	//					var addr = data[i].addr;
	//
	//					// 填充左侧列表
	//					addCameraToList(addr, name);
	//				}
	//			}
	//		},
	//		error : function(data, textStatus, jqXHR) {
	//			alert("error:" + textStatus);
	//		}
	//	});

	$("#close").click(function() {
		clearCameraBox();
	});

	$("#dialog-form").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"添加": function() {
				var effect = $("#check-effect").val();
				var paraUl = $("#dialog-form ul:not(.hidden)");
				var paraNum = $("#dialog-form ul:not(.hidden) li").length;
				var paraDic = {};
				var paraDicStrForJava = "{\"entry\":[";
				var paraLis = paraUl.children("li");
				for (var i = 0; i < paraLis.length; i++) {
					var childs = paraLis[i].children;
					var input;
					for (var j = 0; j < childs.length; j++) {
						if (childs[j].nodeName == "INPUT") {
							input = childs[j];
							break;
						}
					}
					var key = input.name;
					var val = input.value;
					paraDic[key] = val;
					paraDicStrForJava += "{\"key\":\""+key+"\",\"value\":"+val+"}";
					if(i<paraLis.length-1) {
						paraDicStrForJava += ",";
					}
				}
				paraDicStrForJava += "]}";
				// alert(paraDicStrForJava);
				// alert(JSON.stringify(paraDic));
				var rtmpUrl = "rtmp://"+nowCamId;

				$.ajax({
					type: "POST",
					url: "./api/v1/camera/addEffect",
					dataType: "json",
					contentType: "application/json",
					data: JSON.stringify({
						"camId": nowCamId,
						"playerId": streamCount,
						"rtmpUrl": rtmpUrl,
						"effectType": effect,
						"parameters": JSON.parse(paraDicStrForJava)
					}),
					async: false,
					success: function(data, textStatus, jqXHR) {
						if (data.result == true) {

							addCameraToBox(rtmpUrl, effect, paraDic, streamCount);
							streamCount++;
						} else {
							alert("Error:" + data.message);
						}
					},
					error: function(data, textStatus, jqXHR) {
						alert("error:" + textStatus);
					}
				});

				$(this).dialog("close");
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		},
		close: function() {
			// allFields.val("").removeClass("ui-state-error");
		}
	});
	// 添加效果按钮，弹出效果选择对话框
	$("#add_new").click(function() {
			$("#dialog-form").dialog("open");
		}

	);
	$("#dialog-form #check-effect").change(function() {
		checkEffect($("#check-effect").val());
	});

	// 添加按钮，目的是添加摄像头，同../js/index.js的功能
	$("#add").click(function() {
		var addAddr = prompt("输入要添加的设备地址：");
		var name = "摄像头" + cameraCount;
		// 填充左侧摄像头列表
		// addCameraToList(addAddr, name);

		if (addAddr) {
			// 提交用户输入的地址
			$.ajax({
				type: "POST",
				url: "./api/v1/camera/add",
				dataType: "json",
				contentType: "application/json",
				data: JSON.stringify({
					"addr": addAddr + "",
					"id": cameraCount
				}),
				async: false,
				success: function(data, textStatus, jqXHR) {
					if (data.result == true) {
						var id = cameraCount;
						// 填充左侧摄像头列表
						addCameraToList(addAddr, id);
					} else {
						alert("Error:" + data.message);
					}
				},
				error: function(data, textStatus, jqXHR) {
					alert("error:" + textStatus);
				}
			});
		} else {
			alert("Invalid Video Address!");
		}
	});

	//	$("#close").click(function() {
	//		if (addr) {
	//			$.ajax({
	//				type: "GET",
	//				url: "./api/v1/camera/close?addr=" + addr,
	//				dataType: "json",
	//				contentType: "application/json",
	//				async: true,
	//				success: function(data, textStatus, jqXHR) {
	//					alert("Info:" + data.message);
	//				},
	//				error: function(data, textStatus, jqXHR) {
	//					alert("error:" + textStatus);
	//				}
	//			});
	//		}
	//	});

	// 删除视频
	$(document).on("click",
		".left-menu .leftnav-camlist .camlist-ul .camlist-li .delete",
		function() {
			var topic = $(this).parent().attr("id");
			var srcAddr = $(this).parent().attr("text");

			$.ajax({
				type: "post",
				url: "./api/v1/camera/delete",
				dataType: "json",
				contentType: "application/json",
				data: JSON.stringify({
					"topic": topic + "",
					"addr": srcAddr + ""
				}),
				async: true,
				success: function(data, textStatus, jqXHR) {
					alert("成功删除:\n\n" + JSON.stringify(data));
				},
				error: function(data, textStatus, jqXHR) {
					alert("error:" + textStatus);
				}
			});

			$(this).parent().remove();
		});

});