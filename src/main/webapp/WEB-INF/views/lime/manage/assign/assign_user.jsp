<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 사용자 파일함 페이지
 --%>
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
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
    <!-- script -->
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
</head>

<body class="mm15">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/lime/manage/assign/assign.jsp"></jsp:include>
</body>
<script>
const pageType = 'user';
const $userTab = document.querySelector("#userTab");
$userTab.classList.add("focus");
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/manage/assign/assign_searcher.js"></script>
</html>