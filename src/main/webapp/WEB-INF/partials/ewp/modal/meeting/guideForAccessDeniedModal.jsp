<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div id="guideForAccessDeniedModal" class="modal" data-modal="guideForAccessDenied" style="display: none">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">권한 없음</div>
            <div class="modalBody flex-column">
                <div class="commonMent">
                    <p class="colorRed">상세내용을 확인하실 수 없습니다.</p>
                </div>
            </div>
			<ul class="modalInfo margin-top-16">
				<li>사용신청에 등록된 사용자가 아니거나, 보안설정에 따라 공개대상이 아닌경우에는 상세내용 확인불가</li>
				<li>종료된 기밀회의는 상세내용 확인불가</li>
			</ul>
			<div class="modalBtnDiv">
				<button type="button" class="btn btn-md btn-blue" data-modal-action="CLOSE">확 인</button>
			</div>
        </div>
    </div>
</div>