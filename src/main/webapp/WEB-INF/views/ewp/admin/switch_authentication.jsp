<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- 테스트 페이지 --%>
<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html">
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
<%-- 촬영된 사진을 바로 보여주기 위해 img src에 blob: 추가 --%>
<meta http-equiv="Content-Security-Policy"
	content="default-src 'self'; 
	img-src 'self' 'unsafe-inline' data: blob: data: ;
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' 'unsafe-inline' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
<title>스마트 회의시스템</title>
<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
<meta name="author" content="BPLMS">
<meta name="viewport"
	content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />   
<%-- Favicon --%>
<link rel="shortcut icon"
	href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
<%-- CSS --%>
<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
<link rel="stylesheet"
	href="/resources/meetingtime/ewp/css/jquery-ui.css">
<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
<link rel="stylesheet"
	href="/resources/meetingtime/ewp/css/meetingtime.css">
<%-- script --%>
<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
<script src="/resources/meetingtime/ewp/js/jquery-ui.js"></script>
<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
<%-- 공통 스크립트 패키지 --%>
<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
<script src="/resources/meetingtime/ewp/js/jquery-ui.js"></script>
<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>

<link rel="stylesheet"
	href="/resources/front-end-assets/css/main/global-styles.css">
<link rel="stylesheet"
	href="/resources/front-end-assets/css/main/custom.css">
<link rel="stylesheet"
	href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
</head>
<body class="index">
	<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
    <div class="loginContainer">
        <div class="sys_tit">
            <div class="ciEWP"><img src="/resources/meetingtime/ewp/img/ci_ewp.png" alt="한국동서발전 로고"></div>
            <div class="ciTit">인증 정보 변경
            </div>
        </div>
        <article>
            <div class="loginBox">
               	<div class="mb-3 w-100 d-flex flex-row">
                   <input type="radio" name="loginType" value="SSO" id="ssoRadio" class="btn-check" checked><label class="btn py-2 flex-1" for="ssoRadio">SSO</label>
                   <input type="radio" name="loginType" value="PLTE" id="plteRadio" class="btn-check"><label class="btn py-2 flex-1" for="plteRadio">PLTE</label>
                   </div>
                   <div class="inputDiv inputId">
                       <label>ID</label>
                       <input type="text" name="username" class="input-lg" placeholder="사번" maxlength="8">
                   </div>
                   <a class="btn btn-lg btn-blue btnLogin transition" id="switchAuthenticaionBtn">인증 정보 변경</a>
              </div>
        </article>
    </div>
</body>
<script type="module" src="/resources/front-end-assets/js/ewp/page/user/dev/switch-authentication.js"></script>
</html>