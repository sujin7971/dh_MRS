<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 홈 대시보드 --%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8"/>
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
	<title>L-MRS</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />   
	<%-- Favicon --%>
	<link rel="shortcut icon" href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<%-- CSS --%>
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingtime.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
	<%-- script --%>
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	<style>
		.homeRightDiv > .statSection .container {
			background-image: none;
			background-color: #e6e6e6;
		}
		
		.homeRightDiv > .statSection > .container {
		    border: 1px solid #ccc;
		}
		
		.homeRightDiv > .statSection {
			flex: 0.5;
		}
		
		body > div.wrapper.home > div > div.homeRightDiv > div.statSection.margin-top-20 > div.container > div > div > div {
			align-items: center;
			justify-content: center;
			font-size: 22px;
			font-weight: 600;
			color: #396281;
			padding: 20px 0;
		}
		
		
	</style>
	
</head>
<body class="mm1">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<div class="wrapper home">
	<div class="bodyDiv home">
		<div class="homeCardListDiv">
        	<jsp:include page="./module/home_schedule.jsp"></jsp:include>
        </div>
        <div class="homeRightDiv">
        	<jsp:include page="./module/home_notice.jsp"></jsp:include>
            <jsp:include page="./module/home_stat.jsp"></jsp:include>
        </div>
	</div>
</div>


<!-- modal  스케줄회의, 즉시회의 선택 -->
<div id="scheduleHelpModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">오늘의 스케줄</div>
            <!-- <div class="modalBody flex-direction-column"></div> -->
			<ul class="modalInfo margin-top-16">
				<li>오늘의 스케줄은 내가 등록한 사용신청 중 오늘 날짜에 해당하는 스케줄 입니다.</li>
				<li>다른 사용자가 등록한 전자회의에 내가 참석자로 등록되었다면 해당 스케줄도 표출됩니다.</li>
			</ul>
			<div class="modalBtnDiv">
				<div class="btn btn-md btn-blue" data-modal-btn="CLOSE">확 인</div>
			</div>
        </div>
    </div>
</div>

<div id="noticeViewModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
			
            <div class="modalTitle" id="office">공지사항</div>
            <div class="modalBody">
                <div class="modalFormDiv">
					<h2 class="mb-3" id="title"></h2>
					<div class="fs-6 fw-normal text-preline lh-base overflow-scroll border p-1" id="contents" style="min-height:20vh; max-height:40vh; min-width:20vw">
					</div>
					<div id="fileViewBox" class="flex-column my-2">
						<div class="bg-light border-bottom p-2">
							<i class="fas fa-paperclip"></i> 첨부파일 <span id="attachedCount" class="fw-bold">2개</span>
						</div>
						<ul class="list-group list-group-flush">
						</ul>
					</div>
					<div id="writerViewBox" class="fw-normal text-right mt-3 fs-6">
					</div>
                </div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver btn-cancle" data-modal-btn="CLOSE">닫 기</div>
            </div>
        </div>
    </div>
</div>
<div id="statHelpModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">전자회의 성과</div>
            <!-- <div class="modalBody flex-direction-column"></div> -->
			<ul class="modalInfo margin-top-16">
				<li>전자회의는 문서를 인쇄하여 공유하는 형태를 대신하여, 시스템에 접속해서 전자문서로 공유하고 판서함으로서 인쇄에 들어가는 시간, 자원, 비용등을 절감하며 데이터의 영구보존 관리를 수월하게 합니다.</li>
				<li>A4용지 절약 : 본인이 참석자로 등록된 회의에 첨부된 파일들의 인쇄 매수</li>
				<li>용지비용 : 2022년 기준, A4용지 한 장당 가격 20원 기준으로 산출</li>
				<li>탄소 감축 : A4용지 한 장의 생산에서 배출되는 탄소의 량은 2.88g</li>
				<li>물 절약 : A4용지 한 장 생산기준 10L의 물이 필요</li>
				<li>A4용지 4박스(1만장)의 종이 생산에 필요한 나무는 30년생 원목 1그루</li>
			</ul>
			<div class="modalBtnDiv">
				<div class="btn btn-md btn-blue" data-modal-btn="CLOSE">확 인</div>
			</div>
        </div>
    </div>
</div>
<div id="authorityInfoModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">권한 없음</div>
            <div class="modalBody flex-column">
                <div class="commonMent">
                    <p class="colorRed">상세내용을 확인하실 수 없습니다.</p>
                </div>
            </div>
			<ul class="modalInfo margin-top-16">
				<li>사용신청에 등록된 사용자가 아니거나, 보안설정에 따라 공개대상이 아닌경우에는 상세내용 확인불가</li>
				<!-- 
				<li>종료된 기밀회의는 상세내용 확인불가</li>
				 -->
			</ul>
			<div class="modalBtnDiv">
				<div class="btn btn-md btn-blue" data-modal-btn="CLOSE">확 인</div>
			</div>
        </div>
    </div>
</div>	
</body>
</html>