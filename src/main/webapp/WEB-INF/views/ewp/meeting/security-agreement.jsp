<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
	 EWP 로그인 페이지
--%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<meta http-equiv="Content-Security-Policy" 
	content="default-src 'self';
	img-src 'self' localhost:* data: blob:; 
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>스마트 회의시스템</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/> 
	<%-- Favicon --%>
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<%-- CSS --%>
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
    <%-- script --%>
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
</head>
<c:url var="loginProcessingUrl" value="/login" />
<body class="index">
    <article class="d-flex flex-column h-90 align-items-center">
        <header class="sys_tit w-20 h-10 d-flex flex-column align-items-center">
            <div class="ciEWP"><img src="/resources/meetingtime/ewp/img/ci_ewp.png" alt="한국동서발전 로고"></div>
            <div class="ciTit">스마트 회의시스템</div>
        </header>
        <article class="bg-white border border-1 h-80 d-flex flex-column justify-content-between px-5 py-3">
        	<header class="text-center py-5">
	        	<h1 class="fs-2">보 안<span class="mx-3"></span>서 약 서</h1>
	        </header>
            <section id="oathSection" class="overflow-scroll">
                <article id="docArticle">
	                <p class="fs-5 py-2 lh-base h-10"><span id="phrase1">본인은 OOOO년 OO월 OO일 한국동서발전(주) OOO 회의를 함에 있어</span> <span class="text-danger bg-yellow text-decoration-underline">보안준수사항을 교육받았으며 다음 사항을 준수할 것을 엄숙히 서약합니다.</span></p>
	                <ol class="fs-5 p-4 h-70">
	                <li class="py-2 lh-base">
	                본인은 해당 회의(평가) 내용이 국가안전보장 및 한국동서발전(주)의 기밀자료임을 인정한다.
	                </li>
	                <li class="py-2 lh-base">
	                본인은 본 회의(평가)간에 습득한 기밀사항을 누설하는 것이 국가와 한국동서발전(주)의 이익에 위해를 끼칠 수 있음을 자각하고 보안 관련 제반 법령과 규정을 성실히 이행한다.
	                </li>
	                <li class="py-2 lh-base">
	                본인은 회의와 관련되어 지급받은 일체의 현황과 자료를 반납하며, 지득한 제반사항을 私益을 위해 외부로 누설 및 유출하지 않는다.
	                </li>
	                <li class="py-2 lh-base">
	                위 서약을 어길시에는 어떠한 경우라도 관련 법령에 따라 엄중한 처벌을 받을 것을 서약한다.
	                </li>
	                </ol>
                </article>
        	</section>
			<footer class="d-flex flex-column">
				<p class="text-center fs-5 py-5" id="phrase2">OOOO년 OO월 OO일</p>
				<div class="d-flex flex-row">
					<div class="col-3 fs-5">
						<span>서 약 자</span>
					</div>
					<div class="col-6 fs-5 d-flex flex-column">
						<div>
							<span id="phrase3">소 속: <c:out value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.deptName}"/></span>
						</div>
						<div class="py-2">
							<span id="phrase4">직 위: <c:out value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.positionName}"/></span>
						</div>
						<div>
							<span id="phrase5">성 명: <c:out value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userName}"/></span>
						</div>
					</div>
					<div class="col-3 fs-5 d-flex flex-row align-items-center justify-content-center">
						<span id="signBtn" class="href-link">(서명)</span>
						<div id="signLoc"></div>
					</div>
				</div>
			</footer>
		</article>
        <footer class="py-2 h-10 d-flex flex-column justify-content-center">
           	<button class="btn btn-lg btn-blue" id="agreementBtn" disabled>동의하고 서명 완료</button>
       	</footer>
    </article>
</body>
<script>
const loginKey = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userKey}";
const skdKey = "${skdKey}";
const meetingKey = "${meetingKey}";
const attendKey = "${attendKey}";
</script>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/page/meeting/dist/meeting_security-agreement.bundle.js')}"></script>
</html>