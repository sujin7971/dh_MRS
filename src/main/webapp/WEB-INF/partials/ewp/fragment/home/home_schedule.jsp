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
<!-- 오늘의 스케줄 안내 모달 -->
<jsp:include page="/WEB-INF/partials/ewp/modal/meeting/guideForScheduleModal.jsp"></jsp:include>
<!-- 권한 없는 회의 안내 모달 -->
<jsp:include page="/WEB-INF/partials/ewp/modal/meeting/guideForAccessDeniedModal.jsp"></jsp:include>
<script>
//로그인한 사원정보
const date = ("${date}" != "")?"${date}":undefined;
</script>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/partials/fragment/home/dist/home_schedule.bundle.js')}"></script>
