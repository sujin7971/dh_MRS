<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="Content-Security-Policy" 
	content="default-src 'self'; 
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>스마트 회의시스템</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />     
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingtime.css">
    <!-- script -->
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
</head>

<body class="mm12">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<c:set var = "loginType" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}"/>
<div class="wrapper">
    <div class="titDiv timetable">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">배정현황</div>
        <%-- <div class="viewOptionBtn">
            <div class="radioDiv2">
                <label for="vOptionC">
                    <input type="radio" id="vOptionC" name="vOption" onclick="location.href='/lime/meeting/assign/status/list?holdingDate=${holdingDate}'">
                    <span>목록보기</span>
                </label>
                <label for="vOptionP" >
                    <input type="radio" id="vOptionP" name="vOption" onclick="location.href='/lime/meeting/assign/status/timetable?holdingDate=${holdingDate}'" disabled checked>
                    <span>타임테이블</span>
                </label>
            </div>
        </div>
       	<div class="comment">배정 담당자 : <span id="managerName"></span> <span class="interphone" id="managerTel"></span></div> --%>
	</div>

    <div id="searchBox" class="scdSrchDiv">    
    <form id="searchForm" class="align-items-center">    
        <%-- <div class="row w-10">
            <div class="item mobileOnly"><span>사업소</span></div>
            <div class="answer w-100">
                <div class="selectDiv ">
                    <label id="officeLabel" for="officeSelect" class="ellipsis">본 사</label>
                    <select id="officeSelect" title="선택 구분">
                        <c:forEach items="${officeBook}" var="code">
                        	<option value="${ code.key }">${ code.value }</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </div> --%>
        <div class="row radio d-none">
            <div class="item mobileOnly"><span>장소구분</span></div>
            <div class="answer">
                <div class="radioDiv">
                    <label for="placeDiv1"><input type="radio" name="roomType" id="placeDiv1" value="MEETING_ROOM" checked><span><span class="dot"></span>회의실</span></label>
                    <label for="placeDiv2"><input type="radio" name="roomType" id="placeDiv2" value="EDU_ROOM" ><span><span class="dot"></span>강의실</span></label>
                    <label for="placeDiv3"><input type="radio" name="roomType" id="placeDiv3" value="HALL" ><span><span class="dot"></span>강 당</span></label>
                </div>
            </div>
        </div>      
        <div class="row ml-2">
			<div class="answer date">
				<div data-input="date" id="dateDiv">
			 		<input type="text" id="dateInput" class="width100p input-md" name="holdingDate" readonly></div>
			</div>
		</div>
	</form>
    </div>
	<div id="roomContainer" class="d-flex flex-row w-100 overflow-auto">
		
	</div>
	<div class="pageBtnDiv scheduleCalendarBtnDiv">
        <div class="f1">
            <button type="button" class="btn btn-lg btn-silver" id="cancelBtn">취 소</button>
            <button type="button" class="btn btn-lg btn-silver" id="initBtn">초기화</button>
            <button type="button" class="btn btn-lg btn-blue" id="nextBtn">다 음</button>
        </div>
        <div class="f2">
            <div id="reserveRoomNameInput" class="selectedMeetingRoom"></div>
            <div id="reserveScheduleInput" class="selectedMeetingTime"></div>
        </div>        
    </div>
</div>
<div id="datePickerModal" class="modal">
	<div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody flex-direction-column align-items-center">
               <div class="calendarDiv" id="datepicker"></div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</button>
                <button type="button" class="btn btn-md btn-blue" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<%-- 예약된 회의정보 --%>
<div id="infoReservedMeeting" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="xBtn" data-modal-btn="CLOSE"></div>
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody flex-direction-column align-items-center">
                <div class="f1">
                    <p id="titleInput" class="meetingTitle colorBlue"></p>
                </div>
                <div class="f2">
                    <div class="row">
                        <div class="item">일 시</div>
                        <div class="answer">
                            <span id="holdingDateInput"></span>
                            <span id="scheduleInput"></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item">장 소</div>
                        <div class="answer" id="roomNameInput"></div>
                    </div>
                    <div class="row">
                        <div class="item">주관자</div>
                        <div class="answer colorBlue">
                            <span id="hostInput"></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item">신청자</div>
                        <div class="answer">
                            <span id="writerNameplateInput"></span>
                            <br>
                            <span id="writerTelInput" class="interphone"></span>
                            <span id="writerMailInput"></span>
                        </div>
                    </div>
                </div>
            </div>
            <!-- <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver">확 인</button>
            </div> -->
        </div>
    </div>
</div>
<script>
const OPEN_TIME = '${OPEN_TIME}';
const CLOSE_TIME = '${CLOSE_TIME}';
const INTERVAL_MINUTE = '${INTERVAL_MINUTE}';
const roomType = ("${roomType}" == "")?"MEETING_ROOM":"${roomType}";
const roomId = ("${roomId}" == "")?undefined:"${roomId}";
const holdingDate = "${holdingDate}";
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/meeting/assign/assign_status_timetable.js"></script>
</body>
</html>