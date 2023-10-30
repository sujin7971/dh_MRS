// 로그인 페이지 tab
function fn_tabOpen(id,t){
	$(t).parent().children().removeClass("active");
	$(t).addClass("active");
	$("#"+id).parent().children().removeClass("open").addClass("close");
	$("#"+id).removeClass("close").addClass("open");
};

// 모달창 X  버튼으로 닫기
$(function(){ 
    $(".modal .xBtn").click(function(){
        $(".modal").hide();
    });
});
$(function(){ 
    $(".modalCancel").click(function(){
        $(".modal").hide();
    });
});

//select ui
$(document).ready(function(){ 
    var select = $('.selectDiv select');
    select.change(function(){
        var select_name = $(this).children('option:selected').text();
        $(this).siblings("label").text(select_name);
    });
});