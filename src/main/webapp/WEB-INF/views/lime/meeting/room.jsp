<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- 
* 회의 페이지
 --%>
<!DOCTYPE HTML>
<html lang="en" class="min-w-0">
<head>
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<meta http-equiv="Content-Security-Policy" 
	content="default-src 'self'; 
	img-src 'self' localhost:* data: blob:;
	media-src 'self' data: blob:;
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>L-MRS</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">   
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/> 
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingroom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
    <!-- script -->
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
    <script src="/resources/front-end-assets/js/main/comm/timer-module.js"></script>
    <script src="/resources/library/checksum/checksum.min.js"></script>
    <script src="/resources/library/checksum/algorithms/bsd16.js"></script>
    <script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
    
    <%-- WebSocket --%>
	<script src="/resources/library/websocket/sockjs.min.js"></script>
	<script src="/resources/library/websocket/stomp.js"></script>
	<%-- WebSocket --%>
	
	<%--토스트 --%>
	<link rel="stylesheet" href="/resources/library/toast/toastr.min.css">
	<script src="/resources/library/toast/toastr.min.js"></script>
	
	<link rel="stylesheet" href="/resources/library/toastr/toastr.min.css">
	<script src="/resources/library/toastr/toastr.min.js"></script>
	<%--토스트 --%>
	
	<link rel="stylesheet" href="/resources/core-assets/modules/limePad/limePad.css">
</head>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<body class="meetingroom">
    <div class="topDiv">
    	<c:if test='${userRole.hasRole("ROLE_GENERAL")}'>
    	<div id="logoBtn" class="btn btnCi">
            <div class="ciSvg">
                <?xml version="1.0" encoding="UTF-8"?>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 411.26 220">
                    <g id="a"/>
                            <g id="b">
                            <g id="c">
                            <g>
                                <path class="d" d="M325.13,64.62c36.9,2.19,34.45-53.98,0-55-36.9-2.19-34.45,53.98,0,55Z"/>
                                <path class="d" d="M86.13,64.62c34.45-1.03,36.9-57.19,0-55-34.45,1.03-36.9,57.19,0,55Z"/>
                                <path class="d" d="M206.13,55c35.68,.52,35.68-55.53,0-55-35.68-.52-35.68,55.53,0,55Z"/>
                                <path class="d" d="M373.63,113.77c5.51-37.81-44.95-39.62-70-44.76-27.36-3.77-21.09,29.8-50.08,17.97-1.25-33.02-49.12-22.4-69.92-23.97-13.68,0-24.88,10.57-25.91,23.98-48.09,7.67-5.96-35.98-94.09-9.48-16.58,3.18-27.87,20-26,36.27-50.61,22.36-49.98,57.01,.77,79.07,5.77-28.35,51.77-7.36,69.23-6.35,26.76,5.08,19.64,38.89,49.91,32.77-.45-34.9,96.23-35.11,95.82,.03,45.27-.26,8.95-36.05,94.27-41.27,12.18-2.36,22.4,4.06,25.22,14.82,50.71-22.07,51.43-56.71,.77-79.07ZM85.13,173.62c-34.45-1.03-36.9-57.19,0-55,34.45,1.03,36.9,57.19,0,55Zm120.82,13.74c-35.68,.52-35.68-55.53,0-55,35.68-.52,35.68,55.53,0,55Zm120.18-13.74c-36.9,2.19-34.45-53.98,0-55,36.9-2.19,34.45,53.98,0,55Z"/>
                            </g>
                        </g>
                    </g>
                </svg>
            </div>
        </div>
        </c:if>
       	<div class="toolbar">
            <div id="toolCon" class="docuToolDiv d-none">
               	<div class="tool tBookmark" title="책갈피"><i class="fas fa-bookmark"></i></div>
               	<div class="recoveryToolCon d-flex">
              		<div class="tool tUndo disabled" title="되돌리기"><i class="fas fa-undo"></i></div>
               		<div class="tool tRedo disabled" title="다시하기"><i class="fas fa-redo"></i></div>
               	</div>
               	<hr>
            </div>
		</div>
		
        <!-- 
            문서컨트롤 상황별 class
                컨트롤 off = c0
                내화면 & 내 컨트롤 = cs1
                내화면 + 상대방 컨트롤 = c2
        -->
        <div class="btn btnControl c0" title="화면공유"><span></span></div>
        <input type="file" id="photoInput" name="photo" capture="camera" accept="image/*" class="d-none"/>
        <div class="btn btnCamera" title="사진" id="cameraBtn"></div>
        <div id="voiceBtn" class="btn btnRecord" title="녹음"></div>
		<c:if test='${userRole.hasRole("ROLE_GENERAL")}'>
		<div id="archiveBtn" class="btn btnFileFolder" title="내 파일함"></div>
		</c:if>
        <div class="btn btnExpand" title="전체화면"></div>
        <div class="btn btnContract" title="화면축소" style="display:none"></div>
        <div class="btn btnFinish" title="회의종료"><span class="btnTit">회의종료</span><span class="finishCount"></span></div>
    
    </div>
	<div class="f2Div"></div>
    <div class="f3Div"></div>
    <div class="bodyDiv">
        <div class="infoSection">
	        <div class="overflow-auto">
	            <div class="mSummary">
	                <div id="levelBadge" class="mTab">
						<div id="securityInfoBtn" class="mSecurity" title="보안등급 안내"></div>
	                    <div id="roomName" class="mPlace"><c:out value="${schedule.placeName }"/></div>
	                    <div id="scheduleTime" class="mTime" title="회의시간"></div>                    
	                </div>
	                <div id="title" class="mTitle"></div>
	                <%-- <div class="mAgendar"><c:out value="${schedule.meeting.contents }"/></div> --%>
	                <div id="manageBtn" class="display-flex ml-auto">
	                	<button id="extBtn" class="btn btn-xs btn-blue-border margin-top-4 margin-left-8">회의시간연장</button>
	                	<button id="reportBtn" class="btn btn-xs btn-blue-border margin-top-4 margin-left-8">회의록작성</button>
	                </div>
	            </div>
	            <div class="mBodyWrap">
	                <section class="userListSection">
	                    <div class="summaryDiv">
	                   	 	<div class="tit">참석자</div>
	                        <!-- <div class="mTot">예정 <span id="totalCount">0</span> /&nbsp;</div> -->
	                        <div class="mOutsider">참석&nbsp;<span id="attendCount" class="colorRed">0</span></div>
	                        <!-- <div class="mSign">사인&nbsp;<span id="signCount" class="colorRed">00</span></div> -->
	                        <!-- <div class="btn-help margin-left-auto userIndexBtn" title="HELP"></div> -->
	                        <div id="inviteBtn" class="btn-add addUserBtn margin-left-8" title="참석자 추가"></div>
	                    </div>
	                    <ul id="userList" class="userListDiv overflow-auto">
	                        
	                    </ul>
	                </section>
	
	                <div class="fileSection">
	                    <div class="fTitDiv">
	                        <div class="tit">첨부파일</div>
	                        <div id="fileMngBtn" class="btn-add" title="첨부파일 추가"></div>
	                        <div id="memoBtn" class="btn-add-memo" title="메모장 열기"></div>
	                    </div>
	                    <ol id="fileListBox">
	                    </ol>
	                </div>
	            </div>
            </div>
            <div class="openCloseBtn iClose"></div>
        </div>
        <div class="docuLeftSection text-center">
            <!--썸네일 영역-->
            <ul class="list thumbnailDiv overflow-auto">
                
            </ul>
            <!-- 북마크 영역-->
            <ul class="list bookmarkDiv">
                
            </ul>
        </div>
        <div id="docuSection" class="docuSection">
        	<div id="padSection">
        	<%--문서 뷰어 영역--%>
        	</div>
        </div>
    </div>
    
    <div class="bottomDiv">
        <div class="thumbnailBtnDiv">
			<div class="btnThumbnail disabled" title="전체 썸네일">
				<i class="fas fa-copy"></i>
			</div>
			<div class="btnBookmark disabled" title="북마크">
				<i class="fas fa-bookmark"></i>
			</div>
		</div>
		<div class="docuPagingDiv d-none">
			<i class="fas fa-step-backward"  id="backward" title="문서 처음으로"></i> 
			<i class="fas fa-caret-left" id="prev" title="이전 페이지"></i> 
			<input type="text" class="currentPage" id="currentPage" minlength="1" maxlength="3">
			<div class="tot" id="tot">/ 0</div>
			<i class="fas fa-caret-right" id="next" title="다음 페이지"></i> 
			<i class="fas fa-step-forward" id="forward" title="문서 끝으로"></i>
		</div>     
    </div>

<jsp:include page="/WEB-INF/views/lime/common/modal/room/roomModal.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/lime/common/modal/room/fileModal.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/lime/common/modal/room/selectAssistantModal.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/lime/common/modal/room/finishModal.jsp"></jsp:include>
</body>
<script>
/* 서버로부터 전달받아 사용할 글로벌 변수 */
const sessionId = "${sessionId}";
const loginType = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}";
//로그인한 사원번호
const loginId = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userId}";
const officeCode = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.officeCode}";
//로그인한 유저권한
const userRole = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.getAuthorities()}";
let authorities = "${authorityCollection.getAuthorities()}";
const scheduleId = "${scheduleId}";
const meetingId = "${meetingId}";
const OPEN_TIME = "${OPEN_TIME}";
const CLOSE_TIME = "${CLOSE_TIME}";
const INTERVAL_MINUTE = "${INTERVAL_MINUTE}";

toastr.options = {
  "closeButton": false,
  "debug": false,
  "newestOnTop": false,
  "progressBar": true,
  "positionClass": "toast-center",
  "preventDuplicates": false,
  "onclick": null,
  "showDuration": "500",
  "hideDuration": "1000",
  "timeOut": "5000",
  "extendedTimeOut": "1000",
  "showEasing": "swing",
  "hideEasing": "linear",
  "showMethod": "fadeIn",
  "hideMethod": "fadeOut",
}
/* 서버로부터 전달받아 사용할 글로벌 변수 */
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/meeting/room/core-module.js"></script>
</html>