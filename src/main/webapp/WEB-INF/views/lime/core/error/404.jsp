<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
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
	<title>L-MRS</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">    
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingtime.css">
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
</head>
<c:url value="페이지가 존재하지 않거나, 사용할 수 없는 페이지입니다. 입력하신 주소가 정확한지 다시 한번 확인해 주세요." var="errorMessage"/>
<c:if test='${message ne "null" && message ne "" }'>
<c:url value="${message}" var="errorMessage"/>
</c:if>
<body class="systemInfo">
<div class="wrapper errorPage display-flex align-items-center justify-content-center br">	
	<section class="text-secondary">
		<div class="tit">404</div>
		<div class="subTit">PAGE NOT FOUND</div>
		<pre>
		<span class="d-block">${errorMessage}</span>
		</pre>
		<div class="btnDiv">
			<span onclick="location.href='/home'">HOME</span>
			<span>|</span>
			<span onclick="backClose()">BACK</span>
		</div>
	</section>
</div>
<script>
function backClose(){
	history.back();
	window.close();	
}
</script>
</html>