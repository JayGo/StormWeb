/**
 * 
 */
var cameraCount = 0;

function startPlay(element) {
	var addr = element.getAttribute("id");
	//$(".camlist-li").attr("selected", "false");
	//element.setAttribute("selected", "true");
	window.open("surveillance.html?addr=" + addr);
}