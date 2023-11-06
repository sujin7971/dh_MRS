<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 
* 회의 스케줄 페이지
- 회의관리프로그램 메인 페이지 역할
 --%>
<!DOCTYPE HTML>
<div class="titDiv align-items-center">
       <div id="calendarTit" class="pageTit"></div>
       <div id="scheduleHelpBtn" class="help"></div>
   </div>
<div class="container">
	<div class="calendarDiv" id="datepicker">
		<!-- <div id="addBtn" class="addBtnDiv" title="사용신청"><i class="fas fa-plus"></i></div> -->
	</div>
	<div id="cardBox">
		<%--회의카드--%>
		<div class="cardDiv placeholder-wave placeholder">
			<div class="mPropertyDiv">
				<div class="mDivision placeholder"></div>
				<div class="electDoc placeholder" title="전자회의"></div>
				<div class="myTab placeholder"></div>
				<div class="cancelTab placeholder"></div>
				<div class="regTab placeholder"></div>
				<div class="attend mandatory placeholder"></div>
				<div class="attend option placeholder"></div>
				<div class="attend finishIn placeholder"></div>
				<div class="attend finishNo placeholder"></div>
				<div class="count placeholder"></div>
			</div>
			<div class="timePlaceDiv">
				<div class="meetTime placeholder"><span></span></div>
				<div class="meetPlace placeholder"></div>
			</div>
			<div class="pubSecDiv placeholder w-20">
				<span></span>
			</div>
			<div class="meetTit placeholder w-80"></div>
			<div class="meetHost placeholder w-60"><span class="placeholder w-20"></span><span class="placeholder w-20"></span><span class="placeholder w-20"></span></div>
		</div>
	</div>
	<%--회의추가--%>
	<div data-btn="assign" class="cardDiv addCard" title="사용신청"></div>
</div>
<div data-btn="assign" class="floatingAddBtn" title="사용신청"></div>
<div id="scheduleHelpModal" class="modal">
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
				<div class="btn btn-md btn-blue" data-modal-btn="OK">확 인</div>
			</div>
        </div>
    </div>
</div>
<script>
//로그인한 사원정보
const date = ("${date}" != "")?"${date}":undefined;
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/home/home_schedule.js"></script>
