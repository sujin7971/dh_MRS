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
	<title>B-PLMS 권한관리</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />   
	<%-- Favicon --%>
	<link rel="shortcut icon"
		href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<%-- CSS --%>
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet"
		href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet"
		href="/resources/meetingtime/lime/css/meetingtime.css">
	<%-- script --%>
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	<%-- 공통 스크립트 패키지 --%>
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<link rel="stylesheet"
		href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet"
		href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet"
		href="/resources/front-end-assets/css/lime/lime-version-styles.css">
</head>
<body class="mm5">
	<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
	<div class="wrapper">
		<div class="titDiv">
			<div class="backBtnDiv">
				<i class="fal fa-long-arrow-left" onclick="history.back()"
					title="뒤로"></i>
			</div>
			<div class="pageTit">
				<span>[관리자]</span>
			</div>
		</div>
		<div class="h-100 justify-content-center d-flex flex-row">
			<aside id="sidebar" class="col-4 col-sm-3 col-md-3 col-xxl-2 d-flex flex-column p-3 mr-2 bg-white">
				<span class="fs-4">권한 목록</span>
				<hr>
				<ul class="nav nav-pills flex-column">
					<li>
						<h3 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">계층 관리자</h3>
					</li>
					<!-- <li>
						<a href="#" data-role="sys-master" class="nav-link">최고 관리자</a>
					</li> -->
					<li>
						<a href="#" data-role="sys-admin" class="nav-link active">시스템 관리자</a>
					</li>
				</ul>
			</aside>
			<section class="col-8 col-sm-9 col-md-9 col-xxl-10 bg-white p-3 ml-2" data-section="sys-master">
				<div class="d-flex">
					<span class="fs-4">최고 관리자</span>
				</div>
				<hr>
				<article></article>
			</section>
			<section class="col-8 col-sm-9 col-md-9 col-xxl-10 bg-white p-3 ml-2" data-section="sys-admin">
				<div class="d-flex">
					<span class="fs-4">시스템 관리자</span>
					<div data-node="add" class="btn-add h-auto ml-2" title="시스템 관리자 추가"></div>
				</div>
				<hr>
				<article></article>
			</section>
		</div>
	</div>
</body>
<script type="module" src="/resources/front-end-assets/js/lime/page/manage/admin/authority_manage.js"></script>
</html>