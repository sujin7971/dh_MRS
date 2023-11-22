<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	style-src-elem 'self' 'unsafe-inline' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>L-MRS</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">    
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingtime.css">
    <!-- script -->
	<script type="text/javascript" src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script type="text/javascript" src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
    <script type="text/javascript" src="/resources/meetingtime/lime/js/chart.js"></script>
    <script type="text/javascript" src="/resources/meetingtime/lime/js/chartjs-plugin-datalabels.js"></script>
    
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
</head>

<body class="mm3">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<!-- <div id="gnb"></div>include -->
<div class="wrapper">
	<div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">통 계</div>
	</div>
	<div id="searchBox" class="scdSrchDiv">
		<div class="row">
               <div class="radioDiv2 d-none">
                   <label for="targetC">
                       <input type="radio" id="targetC" name="target" value="company" checked>
                       <span>회사전체</span>
                   </label>
                   <label for="targetP">
                       <input type="radio" id="targetP" name="target" value="personal">
                       <span>개 인</span>
                   </label>
               </div>
		</div>
		<div class="row ml-auto">
			<div class="answer d-none">
				<div class="selectDiv">
					<label for="periodPresetSelect" class="ellipsis p-1" id="periodPresetLabel">기간선택</label>
				   	<select id="periodPresetSelect" title="선택 구분" style="width:fit-content;">
					    <option value="-1" class="d-none">기간선택</option>
					    <option value="1q">1분기</option>
					    <option value="2q">2분기</option>
					    <option value="3q">3분기</option>
					    <option value="4q">4분기</option>
					    <option value="fhalf">상반기</option>
					    <option value="shalf">하반기</option>
					    <option value="yearly">연간</option>
					</select>
				</div>
			</div>
    	</div>
    	<div class="row">
    		<div class="item"><span>기 간</span></div>
    		<div class="answer date">
				<div data-input="startDate" id="startDateDiv">
					<input type="text" id="startDateInput" class="width100p input-md" style="background-color:#fff; cursor:pointer;" readonly>
				</div>
				<span class="period">~</span>
		   		<div data-input="endDate" id="endDateDiv">
		    		<input type="text" id="endDateInput" class="width100p input-md" style="background-color:#fff; cursor:pointer;" readonly>
		    	</div>
			</div>
    	</div>
    	<div class="srchBtnDiv">
            <!--초기화 버튼은 2개. 모바일용, pc용-->
            <button class="btn btn-md btn-blue srch" id="searchBtn">검 색</button>
        </div>
	</div>
	<div class="bodyDiv display-flex flex-direction-column flex1">
        <div class="statisticsTitDiv">
            <div>
            	<span id="statTitle"></span>
            </div>
        </div>
        <div class="statisticsBodyDiv">
            <!-- <div class="box_wordCloud flex-direction-column">
                <div class="commonTit">회의에 가장 많이 사용된 단어</div>
                <div class="container"><img src="/meetingtime/img/wordcloud.png"/></div>
            </div> -->
            <div class="box">
                <div class="box">
                    <div class="box">
                        <div class="box_paper" style="display: none;">
                            <div class="commonTit">Paperless 회의 성과</div>
                            <div class="container display-flex flex-direction-column" style="font-size:14px">
                                <div class="tit" id="totalpage"></div>
                                <div class="detail">
                                    <div>
                                        <div class="item">용지비용 절약</div>
                                        <div class="answer" data-stat-paperless="costReduction">0 원</div>
                                    </div>
                                    <div>
                                        <div class="item">온실가스 감소</div>
                                        <div class="answer" data-stat-paperless="gasReduction">0 g</div>
                                    </div>
                                    <div>
                                        <div class="item">물 절약</div>
                                        <div class="answer" data-stat-paperless="waterReduction">0 L</div>
                                    </div>
                                </div>
                                <div class="ment">
                                    A4용지 1장을 만들기 위해서는 10L의 물이 필요하며, 2.88g의 탄소가 발생<br>
                                    A4용지 네 박스(1만장)의 종이생산에 필요한 나무는 30년생 원목 1그루<br>
                                    A4용지 1장당 평균 가격 20원(2022년 기준)
                                </div>
                            </div>
                        </div>
                        <div class="box_total d-flex" data-stat-summary="total">
                            <div class="commonTit" >총 회의 건 수 / 평균 회의시간</div>
                            <div class="container" data-stat-display>
                                0회 / 0시간 0분
                            </div>
                        </div>
                        <div class="d-flex" data-stat-summary="hosting">
                            <div class="commonTit">내가 진행한 회의 / 총 회의시간</div>
                            <div class="container" data-stat-display>
                                0회 / 0시간 0분
                            </div>
                        </div>                        
                        <div class="d-none" data-stat-summary="attendance">
                            <div class="commonTit">참여 회의 건 수 / 총 회의시간</div>
                            <div class="container" data-stat-display>
                                0회 / 0시간 0분
                            </div>
                        </div>
                    </div>
                    <div class="box_top5">
                        <div class="commonTit">회의 개최가 많았던 부서 TOP5</div>
                        <div class="container">
                            <canvas id="top5Chart" class=""></canvas>
                        </div>
                    </div>
                </div>
                <div class="box_barGraph">
                    <div class="commonTit">전년대비 월 회의 건 수 추이</div>
                    <div class="container maxw-100">
                        <canvas id="monthlyTrendChart" class="chartjs-render-monitor"></canvas>
                    </div>
                </div>
            </div>
		</div>
	</div>
</div>
<div id="datePickerModal" class="modal">
	<div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody flex-direction-column align-items-center">
               <div class="calendarDiv" id="datepicker"></div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</button>
                <button type="button" class="btn btn-md btn-blue" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<script type="module" src="/resources/front-end-assets/js/lime/page/meeting/meeting_stat.js"></script>
</body>
</html>