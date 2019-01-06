var scripts = document.getElementsByTagName('script'); 
var currentScript = scripts[scripts.length - 1];
function getRootPath(){
    var pos=currentScript.src.indexOf("/js/sort.js");
    var rootPath=currentScript.src.substring(0,pos);
    return rootPath;
}
//排序字段
function sort(id) { 
	var sortAsc = true; 
	var field=$("#orderBy").val();
	if (field!=null && field.indexOf(id)==0 && field.indexOf("asc")>0){ 
		sortAsc = false; 
	}
	$("#orderBy").val(id+" "+(sortAsc?"asc":"desc"));
	$("#doSearch").click();
}
var blankImg=getRootPath()+"/images/s.gif";
$(document).ready(function(){
	var orderBy=$("#orderBy").val();
	var sortField=orderBy?orderBy.split(" ")[0]:"";
	var sortClass=orderBy?("sort-"+orderBy.split(" ")[1]):"";
	
	$("[data-sort-id]").each(function(){
		var sortId=$(this).attr("data-sort-id");
		if(sortId){
			$(this).click(function(){
				sort(sortId);
			});
			$(this).append("<img class='x-grid3-sort-icon "+(sortField==sortId?sortClass:"")+"' src='"+blankImg+"'>");
		}
	});
});