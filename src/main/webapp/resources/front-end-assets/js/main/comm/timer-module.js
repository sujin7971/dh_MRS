/**
 * 
 */
const getServerTime = () => {
	let xmlHttp;
    try {
        //FF, Opera, Safari, Chrome
        xmlHttp = new XMLHttpRequest();
    }
    catch (err1) {
        //IE
        try {
            xmlHttp = new ActiveXObject('Msxml2.XMLHTTP');
        }
        catch (err2) {
            try {
                xmlHttp = new ActiveXObject('Microsoft.XMLHTTP');
            }
            catch (eerr3) {
                //AJAX not supported, use CPU time.
                alert("AJAX not supported");
            }
        }
    }
    xmlHttp.open('HEAD', window.location.href.toString(), false);
    xmlHttp.setRequestHeader("Content-Type", "text/html");
    xmlHttp.send('');
    return xmlHttp.getResponseHeader("Date");
}
const TimerMng = function(options){
	let instance;
	var setting = $.extend({}, defaults, options);
	var defaults = {
		fixedEndTime : null
		, fixedCall : null
		, fixedEndCall : null
		, intervalEndTime : null
		, intervalCall : null
		, intervalEndCall : null
		, timerFormat : 'ss'
	}
	const serverTime = getServerTime();
	const srvLocGap = (new Date(serverTime)).getTime() - (new Date()).getTime();
	
	let fixedTimeoutStack = [];
	let intervalEndTimeout;
	let intervalTimer;
	
	let fixedEndMoment;
	let intervalEndMoment;
	
	initTimer();
	return instance = TimerMng.fn = TimerMng.prototype = {
		update: function(options){
			console.log("timer update", options)
			setting = $.extend({}, setting, options);
			initTimer();
		}
	}
	
	function initTimer(){
		clearTimer();
		if(setting.fixedEndTime){
			setFixedTimer();
		}
		if(setting.intervalEndTime){
			setIntervalTimer();
		}
	}
	
	function clearTimer(){
		while(fixedTimeoutStack.length > 0){
			let timeout = fixedTimeoutStack.pop();
			clearTimeout(timeout);
		}
		
		clearTimeout(intervalEndTimeout);
		clearInterval(intervalTimer);
	}
	
	/* 회의종료까지 남은 시간별 알림 메시지 설정 */
	function setFixedTimer(){
		fixedEndMoment = moment(setting.fixedEndTime);
		let finishTimeDiff = getTimeDiff(fixedEndMoment);
		let timeDiffAsMinutes = moment.duration(finishTimeDiff, 'milliseconds').asMinutes();
		let timerCount = (timeDiffAsMinutes <= 10)?timeDiffAsMinutes:10;
		timerCount = (timerCount < 0)?0:timerCount;
		for(let i = 0; i <= timerCount; i++){
			let finishTimeout = setTimeout(function(){
				if(i == 0){
					setting.fixedEndCall();
				}else{
					setting.fixedCall(i);
				}
			},finishTimeDiff - (i * 60000));
			fixedTimeoutStack.push(finishTimeout);
		}
	}
	
	var intervalTimeDiff;
	
	/* 회의퇴장까지 남은 시간별 알림 메시지 설정 */
	function setIntervalTimer(){
		intervalEndMoment = moment(setting.intervalEndTime);
		intervalTimer = setInterval(timer, 1000);
	}
	
	/* 가예약 등록 취소까지 남은 시간 표시 */
	function timer(){
		intervalTimeDiff = getTimeDiff(intervalEndMoment);//남은 등록 제한시간(밀리초)
		if(intervalTimeDiff >= 0){
			let timeDiffFormatted = moment(intervalTimeDiff).format(setting.timerFormat);
			if(setting.intervalCall){
				setting.intervalCall(intervalTimeDiff, timeDiffFormatted);
			}
		}else{
			clearInterval(intervalTimer);
			if(setting.intervalEndCall){
				setting.intervalEndCall();
			}
		}
	}

	function getTimeDiff(timeMoment){
		let nowMoment = moment().clone().add(srvLocGap, 'millisecond');
		const diff = timeMoment.diff(nowMoment);
		return diff;
	}
}