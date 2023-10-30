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
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
    <!-- script -->
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
</head>

<body class="mm12">
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<div class="wrapper">
    <div class="titDiv timetable">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">배정현황</div>
        <div class="viewOptionBtn">
            <div class="radioDiv2">
                <label for="vOptionC">
                    <input type="radio" id="vOptionC" name="vOption" onclick="location.href='/ewp/meeting/assign/status/list?holdingDate=${holdingDate}'" disabled checked>
                    <span>목록보기</span>
                </label>
                <label for="vOptionP" >
                    <input type="radio" id="vOptionP" name="vOption" onclick="location.href='/ewp/meeting/assign/status/timetable?holdingDate=${holdingDate}'">
                    <span>타임테이블</span>
                </label>
            </div>
        </div>
       	<div class="comment">배정 담당자 : <span id="managerName"></span> <span class="interphone" id="managerTel"></span></div>
	</div>

    <div id="searchBox" class="scdSrchDiv">        
        <!-- 사업소 선택 row는 로그인계정의 사업소별로 표출 예정이니 관리자에게만 표출. 관리자의 사업소가 기본 checked -->
        <div class="row w-10">
            <div class="item mobileOnly"><span>사업소</span></div>
            <div class="answer w-100">
                <div class="selectDiv ">
                    <label id="officeLabel" for="officeSelect" class="ellipsis"></label>
                    <select id="officeSelect" title="선택 구분">
                        <c:forEach items="${officeBook}" var="code">
                            <option value="${ code.key }">${ code.value }</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </div>
        <div class="row radio">
            <div class="item mobileOnly"><span>장소구분</span></div>
            <div class="answer">
                <div class="radioDiv">
                    <label for="placeDiv1"><input type="radio" name="roomType" id="placeDiv1" value="MEETING_ROOM" checked><span><span class="dot"></span>회의실</span></label>
                    <label for="placeDiv2"><input type="radio" name="roomType" id="placeDiv2" value="EDU_ROOM" ><span><span class="dot"></span>강의실</span></label>
                    <label for="placeDiv3"><input type="radio" name="roomType" id="placeDiv3" value="HALL" ><span><span class="dot"></span>강 당</span></label>
                </div>
            </div>
        </div>        
        <div class="row w-20">
            <div class="item mobileOnly"><span>장소선택</span></div>
            <div class="answer w-100">
                <div class="selectDiv">
                    <label id="roomLabel" for="roomSelect" class="ellipsis"></label>
                    <select id="roomSelect" title="선택 구분">
                    </select>
                </div>
            </div>
        </div>
        <div class="row">
		<div class="item"><span>기 간</span></div>
		<div class="answer date">
			<div data-input="startDate" id="startDateDiv">
		 		<input type="text" id="startDateInput" class="width100p input-md cursor-pointer bg-white" readonly></div>
	 		<span class="period">~</span>
	     	<div data-input="endDate" id="endDateDiv">
		     	<input type="text" id="endDateInput" class="width100p input-md cursor-pointer bg-white" readonly></div>
		 	</div>
		</div>


        <div class="srchBtnDiv">
            <!--초기화 버튼은 2개. 모바일용, pc용-->
            <button class="btn btn-md btn-white mobileReset" id="resetMbBtn">초기화</button>
            <button class="btn btn-md btn-blue srch" id="searchBtn">검 색</button>
            <button class="btn btn-md btn-silver reset" id="resetBtn">초기화</button>
        </div>
    </div>

	<div id="resultBox" class="bodyDiv tableContainer">
        <div class="tableDiv">
            <div class="tableTitDiv">
                <div class="tableTit"><span class="placeholder w-100">-----</span></div>
                <div class="tableBtn"><button class="btn btn-blue btn-sm disabled" onclick="location.href='/ewp/meeting/assign/post'">사용신청</button></div>
            </div>
            <div class="listHeaderDiv">
                <div class="row">
                    <div class="item no">No</div>
                    <div class="item dateTime justify-content-start">사용일시</div>
                    <div class="item title">제 목</div>
                    <div class="item host justify-content-start">주관자</div>
                    <div class="item status">상 태</div>
                    <div class="item regDate justify-content-start">등록일</div>
                </div>
            </div>
            <div class="listBodyDiv">
                <div class="row placeholder-wave">
                    <div class="item no"><span class="placeholder w-100"></span></div>
                    <div class="item dateTime">
                            <span class="date placeholder w-40"></span>
                            <span class="time placeholder w-40"></span>
                    </div>
                    <div class="item title"><span class="placeholder w-100"></span></div>
                    <div class="item host"><span class="placeholder w-100"></span></div>
                    <div class="item status"><span class="placeholder w-100"></span></div>
                    <div class="item regDate"><span class="placeholder w-100"></span></div>
                </div>
            </div>
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
<script>
const officeCode = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.officeCode}";
const roomType = ("${roomType}" == "")?"MEETING_ROOM":"${roomType}";
const roomKey = ("${roomKey}" == "")?undefined:"${roomKey}";
const startDate = "${startDate}";
const endDate = ("${endDate}" == startDate)?moment(startDate).add(7 ,"d").format("YYYY-MM-DD"):"${endDate}";
</script>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/page/meeting/assign/dist/assign_status_list.bundle.js')}"></script>
</body>
</html>