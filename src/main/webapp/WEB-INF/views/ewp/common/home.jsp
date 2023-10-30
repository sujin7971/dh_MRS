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
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
	
</head>
<body class="mm1">
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<div class="wrapper home">
	<div class="bodyDiv home">
		<div class="homeCardListDiv">
        	<jsp:include page="/WEB-INF/partials/ewp/fragment/home/home_schedule.jsp"></jsp:include>
        </div>
        <div class="homeRightDiv">
        	<jsp:include page="/WEB-INF/partials/ewp/fragment/home/home_notice.jsp"></jsp:include>
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
            <jsp:include page="/WEB-INF/partials/ewp/fragment/home/home_stat.jsp"></jsp:include>
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