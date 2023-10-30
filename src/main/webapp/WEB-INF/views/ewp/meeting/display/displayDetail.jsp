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
	<title>회의실 정보 - 한국동서발전</title>
	<meta name="description" content="Metting Rome Monitoring">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">    
	<!-- Favicon -->
	<!-- <link rel="shortcut icon" href="/img/.ico">     -->
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery.mobile-1.4.5.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingDisplay.css">
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery-2.2.4.min.js"></script>
	<script type="text/javascript" src="/resources/meetingtime/ewp/js/jquery.mobile-1.4.5.min.js"></script>
    <script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
    <style>
	.btnSync {
	    position: absolute;
	    top: 30px;
	    left: 30px;
	    background-color: transparent;
	    border: none;
	    opacity: 30%;
	}
	.btnSync::after {
	    content: '\f2f1';
	    font-family: 'fa';
	    font-weight: 500;
	}
	</style>
</head>
<c:set var = "officeCode" value = "${officeCode}"/>
<c:set var = "roomType" value = "${roomType}"/>
<c:set var = "roomKey" value = "${roomKey}"/>
<fmt:parseDate value="${ nowDT }" pattern="yyyy-MM-dd'T'HH:mm" var="nowDateTime" type="both" />
<fmt:formatDate value="${ nowDateTime }" var="now" pattern="yyyy/MM/dd HH:mm" />
<body class="ui-mobile-viewport ui-overlay-a">
<div data-role="page" tabindex="0" class="ui-page ui-page-theme-a ui-page-active">
	<div class="btnSync" onclick="reset()" title="새로고침"></div>
	<div class="btnExpand" title="전체화면" onclick="expandScreen(this)"></div>
	<div class="btnContract" title="화면축소" onclick="expandScreen(this)" style="display:none"></div>
	<!-- 현재시간과 예약시간을 비교해서==>   현재 사용중일때 class=useY  / 예약이 있고 현재 비어있을때 class="useN"  /  예약시간이 null일때 class="useNull" -->
	<div class="meetingroom">
		<table id="meetingTable" class="box useNull">
			<tbody>
				<tr class="db1">
					<td colspan="2" class="roomTit border-bottom"><span id="meetingTitle">
					
					</span></td>
				</tr>		
				<tr class="db1">
					<td colspan="2" class="roomTime border-bottom">
						<table>
							<tbody>
								<tr>
									<td><div class="status"></div></td>
									<td class="time" id="meetingTime"></td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr class="db1">
					<td class="roomUserPart border-right" id="meetingHost"></td>
					<td class="roomUserName" id="meetingAuthor"></td>
				</tr>

				<tr class="db0">
					<td>
						<img class="logo" src="/resources/meetingtime/ewp/img/ci_bg_red.png"><br>
						<div class="roomTit">${roomName}</div>
						<div class="status"></div>
					</td>
				</tr>		
			</tbody>
		</table>	
		<a href="#listPage" class="btnNext ui-link ui-btn ui-shadow ui-corner-all" data-transition="flip" data-role="button" role="button"></a>
	</div>
</div>
	<div class="ui-loader ui-corner-all ui-body-a ui-loader-default">
		<span class="ui-icon-loading"></span>
		<h1>loading</h1>
	</div>
<div data-role="page" id="listPage" tabindex="0" class="ui-page ui-page-theme-a" >
	<div class="roomschedule">
		<table class="header">
			<colgroup>
				<col width="50%"><col width="">
			</colgroup>
			<tbody><tr>
				<td class="tit"><img src="/resources/meetingtime/ewp/img/ewp_logomark.png"><span>${roomName}</span></td>
				<td class="time"><i class="fas fa-calendar-alt"></i><span id="nowTime">${now}</span></td>
				<!-- 
				<td>
					<div class="btn btnExpand" title="전체화면"></div>
					<div class="btn btnContract" title="화면축소" style="display:none"></div>
				</td>
				 -->
			</tr>
		</tbody></table>
	
		<div class="grid" id="listSection">
	        <!-- row 에 다음의 status class를 add  :  사용완료 finish / 진행중 ongoing -->
		</div>
	    <a href="javascript:window.history.back();" class="btnPrev ui-link ui-btn ui-shadow ui-corner-all" data-transition="flip" data-role="button" role="button"></a>
	</div>
</div>
<div class="infoPop" id="initNotice" style="display:flex;">
	<div class="pop">
		<img src="/resources/meetingtime/ewp/img/ewp_logomark.png">
		<p>
			현황판을 준비중입니다.<br>
			잠시만 기다려 주세요.
		</p>
		<br>
	</div>
</div>
<div class="infoPop" id="networkCheckNotice" style="display:none;">
	<div class="pop">
		<img src="/resources/meetingtime/ewp/img/ewp_logomark.png">
		<p>
			네트워크 상태를 확인중입니다.
		</p>
	</div>
</div>
<div class="infoPop" id="networkErrorNotice" style="display:none;">
	<div class="pop">
		<img src="/resources/meetingtime/ewp/img/ewp_logomark.png">
		<p>
			네트워크 상태가 불안정하여 네트워크 정상화 후 재시도합니다.
		</p>
	</div>
</div>
<script type="module">
import {Util} from '/resources/core-assets/essential_index.js';
import {AssignCall as $AS} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
window.Util = Util;
window.$AS = $AS;
</script>
<script>
const $officeCode = "${officeCode}";
const $roomType = "${roomType}";
const $roomKey = "${roomKey}";

//안내모달
const $initNotice = (() => {
	const $modal = document.querySelector("#initNotice");
	$modal.show = () => {$modal.style.display = "flex"};
	$modal.hide = () => {$modal.style.display = "none"};
	return $modal;
})();

//네트워크 확인모달
const $networkCheckNotice = (() => {
	const $modal = document.querySelector("#networkCheckNotice");
	$modal.show = () => {$modal.style.display = "flex"};
	$modal.hide = () => {$modal.style.display = "none"};
	return $modal;
})();

//네트워크 에러모달
const $networkErrorNotice = (() => {
	const $modal = document.querySelector("#networkErrorNotice");
	$modal.show = () => {$modal.style.display = "flex"};
	$modal.hide = () => {$modal.style.display = "none"};
	return $modal;
})();

async function isNetworkOnline(){
	try{
		const isServerOnline = await promiseWithTimeout(Util.isServerOnline(), 3000);
		const isClientOnline = Util.isClientOnline();
		return isServerOnline && isClientOnline;
	}catch(err){
		return false;
	}
}

let watcher;
function checkServerConnection(){
	$networkCheckNotice.show();
	const interval = setInterval(async () => {
		const networkOnline = await isNetworkOnline();
		$networkCheckNotice.hide();
		if(networkOnline){
			$networkErrorNotice.hide();
			window.reset();
			clearInterval(interval);
		}else{
			$networkErrorNotice.show();
		}
	}, 5000)
}
function setFactoryWatcher(){
	clearTimeout(watcher);
	watcher = setTimeout(async () => {
		window.factory?.stop();
		checkServerConnection();
	}, 60000);
}
function promiseWithTimeout(promise, timeout){
	  let timeoutId;
	  const timeoutPromise = new Promise((_, reject) => {
	    timeoutId = setTimeout(() => {
	      reject(new Error('Request timed out'));
	    }, timeout);
	  });

	  return Promise.race([promise, timeoutPromise]).finally(() => {
	    clearTimeout(timeoutId);
	  });
};
</script>
<script>
const docV = document.documentElement;
window.expandScreen = ($element) => {
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
	$element.style.display = "none";
}
window.reset = () => {
	location.reload();
}
$(document).on("pagechange",(() => {
	let flipTimer;
	const clear = () => {
		clearTimeout(flipTimer);
	}
	const flip = () => {
		clear();
		if($("#listPage").hasClass('ui-page-active')){
			$('.grid').scrollTop(0);
			if( $(".ongoing").length > 0){
				if( $(".grid .row").index( $(".ongoing") ) > 0 ){
					if( $('.ongoing').offset().top < 0){
						$('.grid').animate({ scrollTop: ( $('.ongoing').offset().top ) }, 'slow');
					} else{
						$('.grid').animate({ scrollTop: ( $('.ongoing').offset().top - $(".header").height() ) }, 'slow');
					}
				} else{
					$('.grid').animate({ scrollTop: ( $('.ongoing').offset().top ) }, 'slow');
				}
			}
			flipTimer = setTimeout( () => {history.back();} ,10000);
		}
	}
	return flip;
})());
</script>
<script>
setFactoryWatcher();
window.onload = () => {
	window.factory = ((global) => {
		//회의내용 표시
		const displayMeeting = (() => {
			const $table = Util.getElement("#meetingTable");
			const $listSection = Util.getElement("#listSection");
			
			const $title = Util.getElement("#meetingTitle");
			const $time = Util.getElement("#meetingTime");
			const $dept = Util.getElement("#meetingHost");
			const $host = Util.getElement("#meetingAuthor");
			
			const initMeetingTable = () => {
				$title.innerText = "";
				$time.innerText = "";
				$dept.innerText = "";
				$host.innerText = "";
				Util.removeClass($table, "useY", "useN", "useNull");
				Util.addClass($table, "useNull");
			}
			const setMeetingTable = (meeting) => {
				$title.innerText = Util.unescape(meeting.title);
				$time.innerText = moment(meeting.beginDateTime).format("HH:mm") + ' ~ ' + moment(meeting.finishDateTime).format("HH:mm");
				$dept.innerText = meeting.writer.deptName;
				$host.innerText = Util.unescape(meeting.skdHost);
			}
			const initDisplay = () => {
				initMeetingTable();
				$listSection.innerHTML = '';
			}
			//진행중 또는 예정인 회의 표시
			const showLatestMeeting = (list = []) => {
				const nowTimeM = moment();
				Util.removeClass($table, "useY", "useN", "useNull");
				((meeting) => {
					if(!meeting){
						return false;
					}
					setMeetingTable(meeting);
					Util.addClass($table, "useY");
					return true;
				})(list.find(obj => moment().isBetween( moment(obj.beginDateTime), moment(obj.finishDateTime))))
				||
				((meeting) => {
					if(Util.isEmpty(meeting)){
						return false;
					}
					setMeetingTable(meeting[0]);
					Util.addClass($table, "useN");
					return true;
				})(list.filter(obj => moment(obj.beginDateTime).isAfter(nowTimeM)))
				||
				(() => {
					initMeetingTable();
					Util.addClass($table, "useNull");
				})();
			}
			//예약된 회의 목록 표시
			const showAllMeetingList = (list = []) => {
				const nowTimeM = moment();
				$listSection.innerHTML = '';
				list.forEach( (meeting, idx) => {
					const title = meeting.title;
					const skdHost = meeting.skdHost;
					const deptName = meeting.writer.deptName;
					const beginTimeM = moment(meeting.beginDateTime);
					const finishTimeM = moment(meeting.finishDateTime);
					const beginTime = beginTimeM.format("HH:mm");
					const finishTime = finishTimeM.format("HH:mm");
					
					const $row = Util.createElement("div", "row");
					if(nowTimeM.isAfter(finishTimeM)){
						Util.addClass($row, "finish");
					}else if(nowTimeM.isBetween( beginTimeM, finishTimeM )){
						Util.addClass($row, "ongoing");
					}
					const $timeDiv = Util.createElement("div", "time");
					$row.appendChild($timeDiv);
					
					const $beginTimeSpan = Util.createElement("span");
					$beginTimeSpan.innerText = beginTime;
					const $finishTimeSpan = Util.createElement("span");
					$finishTimeSpan.innerText = '~'+finishTime;
					$timeDiv.appendChild($beginTimeSpan);
					$timeDiv.appendChild($finishTimeSpan);
					
					
					const $contBoxDiv = Util.createElement("div", "contBox");
					$row.appendChild($contBoxDiv);
					
					const $floor2Div = Util.createElement("div", "floor2");
					const $deptSpan = Util.createElement("span", "userPart");
					$deptSpan.innerText = deptName;
					const $hostSpan = Util.createElement("span", "userName");
					$hostSpan.innerText = Util.unescape(skdHost);
					$floor2Div.appendChild($deptSpan);
					$floor2Div.appendChild($hostSpan);
					$contBoxDiv.appendChild($floor2Div);
					
					const $floor1Div = Util.createElement("div", "floor1");
					$floor1Div.innerText = Util.unescape(title);
					$contBoxDiv.appendChild($floor1Div);
					
					$listSection.appendChild($row);
				});
			}
			return {
				init(){
					initDisplay();
				},
				renderer: ((list) => {
					try{
						showLatestMeeting(list);
						showAllMeetingList(list);
					}catch(err){
						initDisplay();
					}
				}),
			}
		})();
		//회의 목록 조회
		const requestInterval = (() => {
			let requestInterval;
			const stop = () => {
				displayMeeting.init();
				if(requestInterval){
					clearInterval(requestInterval);
				}
			}
			const start = () => {
				stop();
				requestInterval = setInterval(async () => {
					try{
						const api = $AS.Get.assignPublicListForDisplay({
							officeCode: $officeCode, 
							roomType: $roomType, 
							roomKey: $roomKey,
						});
						const list = await promiseWithTimeout(api, 5000);
						displayMeeting.renderer(list);
						setFactoryWatcher();
					}catch(err){
						console.log("데이터 요청 실패");
					}finally{
						$initNotice.hide();
					}
				}, 5000);
			}
			return {
				stop: stop,
				start: start,
			}
		})();
		const clock = (() => {
			const $clock = Util.getElement("#nowTime");
			let timer;
			return {
				stop: () => {
					if(timer){
						clearInterval(timer);
					}
				},
				start: () => {
					stop();
					timer = setInterval(() => {
						const nowTime = moment().format('yyyy/MM/DD HH:mm:ss');
						$clock.innerText = nowTime;
					}, 1000);
				},
			}
		})();
		return {
			run: () => {
				$initNotice.show();
				requestInterval.start();
				clock.start();
				setFactoryWatcher();
			},
			stop: () => {
				requestInterval.stop();
				clock.stop();
			},
		}
	})(this);
	window.reset = () => {
		factory.run();
	}
	factory.run();
}
</script>
</body>
</html>