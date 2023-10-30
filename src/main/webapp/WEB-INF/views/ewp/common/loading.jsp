<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
	 로딩 페이지
--%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
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
	<%-- Favicon --%>
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<%-- CSS --%>
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
    <%-- script --%>
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/common.js"></script>
	
	<script src="/resources/front-end-assets/js/main/comm/util-package.js"></script>
	<script src="/resources/front-end-assets/js/main/comm/ajax/ajax-package.js"></script>
	<script src="/resources/front-end-assets/js/main/comm/modal-module.js"></script>
	
	<%-- 로딩 --%>
	<link rel="stylesheet" href="/resources/library/loadingModal/css/jquery.loadingModal.css">
	<script src="/resources/library/loadingModal/js/jquery.loadingModal.js"></script>
	<%-- 로딩 --%>
</head>
<c:url value="/login" var="loginProcessingUrl"/>
<c:url value="/login" var="loginProcessingUrl"/>
<body class="index">
    <div class="loginContainer">
        <div class="sys_tit">
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
            <div class="ciTit">B-PLMS
            <c:if test='${version eq "test" }'>
             TEST SERVER
            </c:if>
            </div>
            <div class="ciSubTit">BELLOCK PAPERLESS MEETING SYSTEM</div>
        </div>
        <div class="copyright">Copyright © BELLOCK. All Rights Reserved.</div>
    </div>
</body>
<script>
const meetingKey = "${meetingKey}";
$(function(){
	$("body").loadingModal({
		text: '회의를 준비중입니다...',
		animation: 'threeBounce'
	});
	checkReady();
	function checkReady(){
		setTimeout(async function(){
			const authorities = await AjaxCall.meeting.getMeetingAuthorityForUser(meetingKey);
			if(authorities && authorities.length != 0){
				location.reload();
			}else{
				checkReady();
			}
		}, 1000);
	}
});
</script>
</html>