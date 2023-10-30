<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div id="guideForScheduleModal" class="modal" data-modal="guideForSchedule" style="display: none">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">오늘의 스케줄</div>
            <!-- <div class="modalBody flex-direction-column"></div> -->
			<ul class="modalInfo margin-top-16">
				<li>오늘의 스케줄은 내가 등록한 사용신청 중 오늘 날짜에 해당하는 스케줄 입니다.</li>
				<li>캘린더에서 날짜를 변경 선택하면 해당일의 스케줄을 확인할 수 있습니다.</li>
				<li>다른 사용자가 등록한 전자회의에 내가 참석자로 등록되었다면 해당 스케줄도 표출됩니다.</li>
			</ul>
			<div class="modalBtnDiv">
				<button class="btn btn-md btn-blue" data-modal-action="OK">확 인</button>
			</div>
        </div>
    </div>
</div>