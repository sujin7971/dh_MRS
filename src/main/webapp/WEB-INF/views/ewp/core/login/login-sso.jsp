<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
	 EWP SSO 로그인 페이지
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
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
    <%-- script --%>
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/common.js"></script>
</head>
<c:url value="/login" var="loginProcessingUrl"/>
<body class="index">
    <form id="memberLoginForm" action="${loginProcessingUrl}" method="POST">
    <%-- input name 변경하지 말 것 --%>
        <input type="hidden" name="username" value = "${loginId}">
        <input name="loginType" type="hidden" value="SSO">
        <input name="${_csrf.parameterName}" type="hidden" value="${_csrf.token}">
    </form>
</body>
<script>
$(() => {
	const $loginForm = $("#memberLoginForm");
	$loginForm.submit();
});

</script>
</html>