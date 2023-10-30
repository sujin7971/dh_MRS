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
	<title>스마트 회의시스템</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />   
	<%-- Favicon --%>
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<%-- CSS --%>
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
	<%-- script --%>
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	<%-- 공통 스크립트 패키지 --%>
	<script src="/resources/front-end-assets/js/ewp/comm/ajax/ajax-package.js"></script>
	<script src="/resources/front-end-assets/js/main/comm/modal-module.js"></script>
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
	
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
			<!-- 알림섹션의 표출 조건
				0. 모든 알림을 표출한다. (스크롤시 로딩)
				1. 화면 width 940px 이하일때
					1.1 안읽은 알림만 보여준다.
					1.2 모두 읽었을때는 .alarmSection은 addClass 'display-none'				
			 -->
            <div class="alarmSection margin-top-20">
                <div class="titDiv">
                    <div class="pageTit width100p">
						<span>알 림</span>
						<span>
							<a onclick="readAllAlarm()" style="text-decoration-line:underline;">모두읽음</a>
						</span>
					</div>
                </div>
                <div class="container" id="alarmList">
					<!--사용자가 확인(클릭)한 알림 row에는 add class done-->
                </div>
            </div>
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
				<li>종료된 기밀회의는 상세내용 확인불가</li>
			</ul>
			<div class="modalBtnDiv">
				<div class="btn btn-md btn-blue" data-modal-btn="CLOSE">확 인</div>
			</div>
        </div>
    </div>
</div>	
</body>
<script>
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
$.ajaxSetup({
	beforeSend: function(xhr, settings) {
		if (!/^(GET|HEAD|OPTIONS|TRACE)$/i.test(settings.type) && !this.crossDomain) {
			xhr.setRequestHeader(header, token);
        }
	}
});
const officeCode = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.officeCode}";

let alarmdata = {
	"pageNo": 1,
	"pageCnt": 6
}

var pagecnt = 1;

//알람 리스트 생성 함수
function creatediv(list){
	let alarmlist ='';
	let alarmdiv='';
	let today = moment().format('YYYY.MM.DD')
	
	for(let i=0;i<list.length;i++){
		let readYN='';
		let row = list[i];
		let alNo = row.alarmNo.replace("T", " ")
		if(row.readYn == 'Y'){
			readYN = "done";
		} else{
			readYN = '';
		}
		
		if(today == moment(row.regDateTime).format('YYYY.MM.DD')){
			showDate = moment(row.regDateTime).format('HH:mm')
		} else{
			showDate = moment(row.regDateTime).format('YYYY.MM.DD HH:mm')
		}
		//url 연결 기능 삭제
// 		alarmdiv = '<div class="row checkRead'+ readYN +'" onclick="alarmRead('+ '\''+alNo + '\'' +',\''+row.mailLinkUrl+'\')"'
		alarmdiv = '<div class="row checkRead '+ readYN +'" onclick="alarmRead('+ '\''+alNo + '\'' + '); goUrl('+ '\''+row.mailLinkUrl + '\'' + ')"'
				 + ' data-alarmno='+ '\''+alNo + '\'' +'>'
        		 + '<span class="message">'
				 + '<div>'
// 				 + '<span class="meetingDate w-20">'+ showDate +'</span>' 
				 + '<span class="w-50">  '+ row.alarmBody +'</span>'
				 + '</div>'							
				 + '<span class="regDate w-10">'+ moment(row.alarmNo).format('YYYY.MM.DD HH:mm') +'</span>'
				 + '</span>'
    			 + '</div>';
		alarmlist += alarmdiv;
// 		if(list.length-1 == i){
// 			alarmlist += '<div class="row more loadmore" onclick="loadmorealarm()">더보기</div>'
// 		}
	}
	
	return alarmlist
}

//알람 리스트 조회
function getAlarmList(){
	$.ajax({
		url: '/api/ewp/alarm/popup/list',
		data: alarmdata,
		success: function(result){
			let alist = creatediv(result)
			$('#alarmList').append(alist)
			let alarmlength = $('#alarmList').children().length;
			//알림 없을 경우 알람섹션 hide처리
			if(alarmlength < 1){
				$('.alarmSection').hide()
			}
			$('.checkRead').on('click',function(){
			 	$(this).addClass('done')
			})
		},
		beforeSend: function(){
			$('.alarmSection').find('.loadmore').remove();			
		}
	})
}

getAlarmList();

//알람 읽음 처리, 해당 알림 링크 open
function alarmRead(alarmNo){
	$.ajax({
		url: '/api/ewp/alarm/'+alarmNo+'/read',
		type: 'put',
		success: function(){
// 			console.log('성공')
		}
	});
	//단일 알림 읽음 처리 시 새 탭에서 링크 열리도록 함(url이동 기능 삭제)
// 	if(url){
// 		window.open(url,"_blank")
// 	}
}

// 알림 관련 링크 이동 기능 추가
function goUrl(url) {
// 	location.href = url;
}

var addition_const = 0;

$('#alarmList').on('scroll', scrollEvent)

function scrollEvent(){
	let innerHeight = $(this).innerHeight();
	let scroll = $(this).scrollTop() + innerHeight;
	let height = $(this)[0].scrollHeight;
	
	if(scroll + 1 > height && addition_const < scroll){
// 		$('.alarmSection').find('.loadmore').remove();
		pagecnt++;
		alarmdata.pageNo = pagecnt;
		getAlarmList();
		addition_const = scroll
	}
}

function loadmorealarm(){
	$('.alarmSection').find('.loadmore').remove();
	pagecnt++;
	alarmdata.pageNo = pagecnt;
	getAlarmList();
}

function readAllAlarm(){
	$('.checkRead').addClass('done')
	$('.checkRead').each(function(){
		let alarmno = $(this).data('alarmno')
		alarmRead(alarmno)
	})
}
</script>

</html>