<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%--
	 시스템 안내 페이지
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
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
    <!-- script -->
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery-ui.js"></script>
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
	<script src="/resources/front-end-assets/js/common/util-package.js"></script>
	<script src="/resources/front-end-assets/js/common/modal-module.js"></script>
</head>
<body class="mm5">
	<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
	<div class="wrapper">
	    <div class="titDiv">
			<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
	       <div class="pageTit">시스템 안내</div>
	        <!-- <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div> -->
	        <!-- <div class="comment"></div> -->
		</div>
		<div class="bodyDiv helpBody">
	        <section>
	            <h3>B-PLMS 는</h3>
	            <p>
	                오프라인 회의 지원을 위한 웹 기반의 구축형 솔루션입니다. <br>
	                기존의 오프라인 회의에서 인쇄물로 공유하던 문서들을 전자문서로 전환하여 종이의 사용을 줄이고자 함이 목적이며<br>
	                사내 회의실의 예약기능과 예약스케줄 관리, 
	                공유 전자문서의 개인별 판서 기능, 회의록 작성, 개인별 파일함 제공, 사용통계 등의 기능을 지원합니다.
	            </p>
	        </section>
	        <hr>
	        <section>
	            <h3>시스템 버전</h3>
	            <p>
	                본 B-PLMS는 v2.0 입니다.
	            </p>
	        </section>
	        <hr>
	        <section>
	            <h3>사용자 매뉴얼</h3>
	            <p>
	                <a class="btn btn-md btn-white disabled" href="B-PLMS v1.0_usermanual.pdf" download><i class="fas fa-arrow-alt-to-bottom margin-right-8"></i> 사용자 매뉴얼 v2.0 - PDF DOWNLOAD</a>
	            </p>
	        </section>
	        <hr>        
	        <section>
	            <h3>저작권 및 사용권</h3>
	            <p>
	                공급된 소프트웨어의 저작권은 공급사에 있으며 구매기관은 해당 소프트웨어에 대한 사용권만을 가집니다.
	            </p>
	        </section>
		</div>
	</div>
</body>
</html>