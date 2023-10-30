<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 보조 진행자 선택 --%>
<div id="assistantModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">보조 진행자 지정</div>
            <div class="modalBody flex-direction-column">
                <ul>
                    <!-- <li><input type="radio" id="wl1" name="wl"><label for="wl1">홍길동 2직급(나) 전략기획부</label></li> -->
                </ul>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</div>
                <div class="btn btn-md btn-blue" data-modal-btn="OK">확 인</div>
            </div>
        </div>
    </div>
</div>
<script>
let $selectAssistant;

function showSelectAssistantModal(list, callback){
	$selectAssistant = $("#assistantModal");
	$selectListCon = $selectAssistant.find("ul");
	$selectListCon.html("");
	
	$selectListCon.append(makeSelectAssistantTemplate(null));
	
	const listMap = list.reduce((acc, attendee) => {
		acc[attendee.attendKey] = attendee;
		return acc;
	}, {});
	for(let i = 0; i < list.length; i++){
		$selectListCon.append(makeSelectAssistantTemplate(list[i]));
	}
	$selectAssistant.find(".btn-blue").on("click", function(){
		let attendKey = $("input[name='assistantRadio']:checked").val();
		callback(listMap[attendKey]);
		$selectAssistant.hide();
		$selectAssistant.find(".btn").unbind("click");
	});
	$selectAssistant.find(".btn-silver").click(function() {
		$selectAssistant.hide();
		$selectAssistant.find(".btn").unbind("click");
	});
	$selectAssistant.show();
}

/* 보조 진행자 지정 모달창에 참가자 목록 생성 */
function makeSelectAssistantTemplate(attendee) {
	let $li = $("<li></li>");
	let $input = $('<input type="radio">');
	let $label = $('<label></label>');
	if(attendee != null){
		$input.val(attendee.attendKey);
		$input.attr("id", "att"+attendee.attendKey);
		$input.attr("name", "assistantRadio");
		$label.html(attendee.user.nameplate);
		$label.attr("for", "att"+attendee.attendKey);
	}else{
		$input.val(0);
		$input.attr("id", "att0");
		$input.attr("name", "assistantRadio");
		$label.html("지정취소");
		$label.attr("for", "att0");
	}
	$li.append($input);
	$li.append($label);
	return $li;
}
</script>