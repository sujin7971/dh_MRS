<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="kr" class="ui-mobile">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<title>회의실 상황 - 한국동서발전</title>
	<meta name="description" content="Metting Rome Monitoring">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery.mobile-1.4.5.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingDisplay.css">
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery-2.2.4.min.js"></script>
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery.mobile-1.4.5.min.js"></script>
    <script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
</head>
<c:set var = "officeCode" value = "${officeCode}"/>
<fmt:parseDate value="${ nowDT }" pattern="yyyy-MM-dd'T'HH:mm:SS" var="nowDateTime" type="both" />
<fmt:formatDate value="${ nowDateTime }" var="now" pattern="yyyy/MM/dd HH:mm:SS" />
<body class="ui-mobile-viewport ui-overlay-a">
	<div data-role="page" tabindex="0" class="ui-page ui-page-theme-a ui-page-active">
		<div class="monitoring">
			<table class="header">
				<colgroup>
					<col width="50%"><col width="">
				</colgroup>
				<tbody>
					<tr>
						<td class="tit"><img src="/resources/meetingtime/ewp/img/ewp_logomark.png"><span>한국동서발전 회의실예약현황</span></td>
						<td class="time"><i class="fas fa-calendar-alt"></i><span id="nowTime">${now}</span></td>
					</tr>
				</tbody>
			</table>

			<table class="grid">
				<thead>
					<tr>
						<td>회의실 <span>|</span></td>
						<td>회의시간 <span>|</span></td>
						<td>회의주제 <span>|</span></td>
						<td>신청자 <span>|</span></td>
						<td>주관자</td>
					</tr>
				</thead>
				<tbody>
					<%-- <c:forEach items="${meetingList}" var="meeting">
						<fmt:parseDate value="${ meeting.beginDateTime }" pattern="yyyy-MM-dd'T'HH:mm" var="beginDateTime" type="both" />
						<fmt:formatDate value="${ beginDateTime }" var="beginTime" pattern="HH:mm" />
						<fmt:parseDate value="${ meeting.finishDateTime }" pattern="yyyy-MM-dd'T'HH:mm" var="finishDateTime" type="both" />
						<fmt:formatDate value="${ finishDateTime }" var="finishTime" pattern="HH:mm" />
						<tr>
							<td><span>${meeting.roomType} ${meeting.roomName}</span></td>
							<td>
								${beginTime} ~ ${finishTime} 
							</td>
							<td>${meeting.title}</td>
							<td>${meeting.deptName}</td>
							<td>${meeting.skdHost} </td>
						</tr>
					</c:forEach> --%>
					<!-- 
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>국정망 외부 인터페이스 관련</td>
						<td>기술지원팀</td>
						<td>김우수</td>
					</tr>
					<tr>
						<td><span>한국동서발전 회의실 0001</span></td>
						<td>08:00 ~ 10:00</td>
						<td>날으는 자동차 현실화 눈 앞. 클라인 비전 'AirCar', 최초로 도시간 비행</td>
						<td>사무자동화 발전기획부</td>
						<td>김우수사원</td>
					</tr>
					 -->
				</tbody>		
			</table>

			<table class="paging">
				<tbody>
					<tr>
						<td>
							<span></span>
							<span class="focusOn"></span>
							<span></span>
							<span></span>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="ui-loader ui-corner-all ui-body-a ui-loader-default">
		<span class="ui-icon-loading"></span>
		<h1>loading</h1>
	</div>
	<div class="infoPop" id="errNotice" style="display:none;">
		<div class="pop">
			<img src="/resources/meetingtime/ewp/img/ewp_logomark.png">
			<p>
				네트워크 장애로 정보수신이 지연중입니다.<br>
				잠시만 기다려 주세요.
			</p>
		</div>
	</div>

<script type="text/javascript">
var xhr = new XMLHttpRequest();
xhr.onreadystatechange = function(){
	if(xhr.readyState == xhr.DONE){
		if(xhr.status === 200 || xhr.status === 201){
			if( xhr.responseText.startsWith('[') ){
				$("div.monitoring table.grid tbody").html('');
				$("div.monitoring table.paging td").html('');
				let res = JSON.parse(xhr.responseText);
				document.querySelector("#errNotice").style.display='none';
				
				/* console.log(res);
				res.sort( (a,b) => {
					return parseFloat(a.roomKey) - parseFloat(b.roomKey);
				}); */
				if(res.length > 0){
					for(let i=0 ; i < Math.ceil(res.length / rowNum) ; i++){
						$("div.monitoring table.paging td").append("<span></span>");
					}
					timer(res);
					interval = setInterval( () => {
						if( document.querySelector("#errNotice").style.display != 'flex'){
							timer(res);	
						};
					}, 5000 );
				} else{
					//// 화면 표시 리스트 갯수 8개, 예약 리스트 8개 이하일 경우 빈 칸 생성
					for(let i = res.length; i< rowNum ; i++){
						$("div.monitoring table.grid tbody").append(
							"<tr>"
						  + "    <td> \u00a0" + "</td>"
						  + "    <td> \u00a0" + "</td>"  
						  + "    <td> \u00a0" + "</td>"
						  + "    <td> \u00a0" + "</td>"
						  + "    <td> \u00a0" + "</td>"
						  + "</tr>"
						)
					};
					setTimeout( () => list() , 5000);
				}
				if( $("#errNotice").css('display') != 'none' ){
					$("#errNotice").css('display','none');
				}
			} else{
				document.querySelector("#errNotice").style.display='flex';
				list();
			}
		} else {
			document.querySelector("#errNotice").style.display='flex';
			list();
			//console.error(xhr.responseText);
		}
	}
}
xhr.timeout = 5000;
xhr.onload = () => {
	document.querySelector("#errNotice").style.display='none';
}
xhr.ontimeout = (e) => {
	document.querySelector("#errNotice").style.display='flex';
}
//xhr.onerror = function(){
//	document.querySelector("#errNotice").style.display='flex';
//	list();
//}
const $officeCode = '${officeCode}';
const rowNum = 8; 
const list = () => {
	const officeCode = $officeCode;
	xhr.open("GET",`/api/ewp/public/meeting/assign/display/list?officeCode=${officeCode}`, true);
	xhr.send();
}
var interval;
var currIdx = 0;
var pageNum = 0;
const $monitoringList = $("div.monitoring table.grid tbody");
const timer = (meetingList) => {
	let dataLength = meetingList.length;
	if(currIdx == dataLength){
		clearInterval(interval);
		currIdx = pageNum = 0;
		list();
		return;
	}
	//tbody 클리어
	$monitoringList.html('');
	meetingList.sort((a, b) => {
		  if (a.room.roomFloor === null && b.room.roomFloor === null) {
		    return a.room.roomName.localeCompare(b.room.roomName);
		  } else if (a.room.roomFloor === null) {
		    return 1;
		  } else if (b.room.roomFloor === null) {
		    return -1;
		  } else if (a.room.roomFloor === b.room.roomFloor) {
		    return a.room.roomName.localeCompare(b.room.roomName);
		  } else {
		    return a.room.roomFloor - b.room.roomFloor;
		  }
		});
	//8줄씩 표현
	for(let i = currIdx ; i < dataLength ; i++){
		let meeting = meetingList[i];
		$monitoringList.append(
				"<tr>"
			  + "    <td style='max-width:480px;'><span>" + meeting.room.roomName + "</span></td>"
			  + "    <td style='width:400px;'>" + moment(meeting.beginDateTime).format("HH:mm")  + ' ~ ' + moment(meeting.finishDateTime).format("HH:mm") + "</td>"  
			  + "    <td>" + $.trim(meeting.title) + "</td>"
			  //+ "    <td>" + $.trim(meeting.deptName) + "</td>"
			  + "    <td style='width:300px;'>" + $.trim(meeting.writer.userName) + "</td>"
			  + "    <td style='min-width:350px;'>" + $.trim(meeting.skdHost) + "</td>"
			  + "</tr>"
			);
		currIdx++;
		if( currIdx % rowNum == 0){
			break;
		}
	}
	//8줄이 되지 않은 남은 데이터 보정처리
	if( $monitoringList.find('tr').length < rowNum ){
		for(let i =  $monitoringList.find('tr').length ; i<8 ; i++){
			$monitoringList.append(
				"<tr>"
			  + "    <td> \u00a0" + "</td>"
			  + "    <td> \u00a0" + "</td>"  
			  + "    <td> \u00a0" + "</td>"
			  + "    <td> \u00a0" + "</td>"
			  + "    <td> \u00a0" + "</td>"
			  + "</tr>"
			)
		}
	}
	
	//paging span 태그 포커싱
	$('div.monitoring table.paging td span').removeClass("focusOn");
	$('div.monitoring table.paging td span:eq('+pageNum+')').addClass("focusOn");
	pageNum++;
}

function getTime(){
	$("#nowTime").text( moment().format('yyyy/MM/DD HH:mm:ss') );
}
// 전체화면 전환 이벤트
var docV = document.documentElement;
$("img").dblclick(function(){
	if(document.fullscreen){
	    if (document.exitFullscreen)
		    document.exitFullscreen();
		else if (document.webkitExitFullscreen) // Chrome, Safari (webkit)
		    document.webkitExitFullscreen();
		else if (document.mozCancelFullScreen) // Firefox
		    document.mozCancelFullScreen();
		else if (document.msExitFullscreen) // IE or Edge
		    document.msExitFullscreen();
	} else{
	    if (docV.requestFullscreen)
	        docV.requestFullscreen();
	    else if (docV.webkitRequestFullscreen) // Chrome, Safari (webkit)
	        docV.webkitRequestFullscreen();
	    else if (docV.mozRequestFullScreen) // Firefox
	        docV.mozRequestFullScreen();
	    else if (docV.msRequestFullscreen) // IE or Edge
	        docV.msRequestFullscreen();
	}
});
$(document).ready(function(){
	list();
    setInterval(getTime, 1000);
})
</script>
</body>
</html>