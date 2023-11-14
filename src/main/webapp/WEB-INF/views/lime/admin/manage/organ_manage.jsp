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
	<title>B-PLMS 조직관리</title>
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
		href="/resources/front-end-assets/css/lime/lime-version-styles.css">
		
		<%-- Fancytree --%>
	<link rel="stylesheet" href="/resources/library/fancytree/skin-awesome/ui.fancytree.css">
	<script src="/resources/library/fancytree/jquery.fancytree-all.min.js"></script>
	
	<style>
		.btn-edit:focus{box-shadow: none !important;}
		#invalidDeptLink{cursor: default !important;}
		#invalidDeptLink:hover{color: #000;}
		ul.fancytree-container {border:none !important;}
		ul.fancytree-container * {outline:none !important;}
		#deptTree > ul > li > span span, 
		#deptTree > ul > li > ul li > span span {padding: 5px 5px; border-radius: 5px;}
		#deptTree > ul > li > span span.fancytree-title, 
		#deptTree > ul > li > ul li > span span.fancytree-title {font-family: 'Pretendard', 'malgun-gothic', sans-serif !important;}
		ul.fancytree-container ul {padding-top:0;}
		.fancytree-treefocus span.fancytree-active span.fancytree-title{background-color:#2667a0 !important;}
	</style>
</head>
<body class="mm8">
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
				<div class="d-flex">
					<span class="fs-4">조직도</span>
					<button type="button" class="btn btn-white px-2 ml-auto" id="addDeptBtn"><i class="fas fa-plus"></i></button>
					<button type="button" class="btn btn-white px-2 ml-2" id="deleteDeptBtn" disabled><i class="fas fa-minus"></i></button>
				</div>
				<hr>
				<ul class="nav nav-pills flex-column">
					<li id="deptTree" class="border-0">
					</li>
					<li>
						<a href="#" id="invalidDeptLink" class="nav-link"><i class="fas fa-users-slash mr-2"></i>미지정</a>
					</li>
				</ul>
				<div ></div>
			</aside>
			<section class="col-8 col-sm-9 col-md-9 col-xxl-10 bg-white p-3 ml-2" data-section="dept-member">
				<div class="d-flex">
					<span id="memberTitle" class="fs-4">직원</span>
					<div id="addMemberBtn" class="btn-add h-auto ml-2" title="부서원 추가"></div>
				</div>
				<hr>
				<article id="memberCardSection"></article>
			</section>
		</div>
	</div>
<div id="addMemberModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
        	<div class="modalTitle">
        		사원 추가
        	</div>
            <div class="modalBody">
                <div class="modalFormDiv">
                <form id="memberForm">
                    <div class="row">
                        <div class="item"><span>부 서</span></div>
                        <div class="answer">
                        <select class="w-100 pl-1 py-2" name="deptId">
                        </select>
                        	<!-- <input type="hidden" name="deptId">
                            <input type="text" name="deptName" class="input-lg width100p" disabled> -->
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>직 급</span></div>
                        <div class="answer">
                            <select class="w-100 pl-1 py-2" name="titleName">
	                            <option value="사원">사원</option>
	                            <option value="주임">주임</option>
	                            <option value="대리">대리</option>
	                            <option value="과장">과장</option>
	                            <option value="차장">차장</option>
	                            <option value="부장">부장</option>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>이 름</span></div>
                        <div class="answer">
                            <input type="text" name="userName" class="input-lg width100p" >
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>사 번</span></div>
                        <div class="answer">
                            <input type="number" name="userId" class="input-lg width100p" >
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>이메일</span></div>
                        <div class="answer">
                            <input type="text" name="email" class="input-lg width100p" >
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>개인연락처</span></div>
                        <div class="answer">
                            <input type="number" name="personalCellPhone" class="input-lg width100p" >
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>사내연락처</span></div>
                        <div class="answer">
                            <input type="number" name="officeDeskPhone" class="input-lg width100p" >
                        </div>
                    </div>
                </form>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</button>
				<!-- <button type="button" class="btn btn-md btn-red" data-btn="delete">삭 제</button> -->
				<button type="button" class="btn btn-md btn-blue" data-modal-btn="OK">저 장</button>
            </div>
        </div>
    </div>
</div>
<div id="addDeptModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
        	<div class="modalTitle">
        		부서 추가
        	</div>
            <div class="modalBody">
                <div class="modalFormDiv">
                <form id="deptForm">
                	<div class="row">
                        <div class="item"><span>상위부서</span></div>
                        <div class="answer">
                        <select class="w-100 pl-1 py-2" name="parentId">
                        </select>
                        	<!-- <input type="hidden" name="deptId">
                            <input type="text" name="deptName" class="input-lg width100p" disabled> -->
                        </div>
                    </div>
                    <div class="row">
	                    <input type="hidden" name="deptId">
                        <div class="item"><span>부서명</span></div>
                        <div class="answer">
                            <input type="text" name="deptName" class="input-lg width100p" >
                        </div>
                    </div>
                </form>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</button>
				<!-- <button type="button" class="btn btn-md btn-red" data-btn="delete">삭 제</button> -->
				<button type="button" class="btn btn-md btn-blue" data-modal-btn="OK">저 장</button>
            </div>
        </div>
    </div>
</div>
</body>
<script type="module" src="/resources/front-end-assets/js/lime/page/manage/admin/organ_manage.js"></script>
</html>