/**
 * 
 */
import {Util} from '/resources/core-assets/essential_index.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

window.onload = async () => {
	setPersonalStat();
};


const getPeriod = () => {
	const nowDateM = moment();
	const startDate = nowDateM.clone().add(-1, "y").format("YYYY-MM-DD");
	const endDate = nowDateM.clone().add(-1, "d").format("YYYY-MM-DD");
	return {
		startDate: startDate,
		endDate: endDate,
	}
}

const getValueByStat = (paperless, stat) => {
	  // stat 값에 따라 적절한 값을 반환하는 로직 작성
	  switch (paperless) {
	    case 'pageReduction':
	    	return stat + "장";
	    case 'costReduction':
	    	return 20 * stat + "원";
	    case 'gasReduction':
	    	return (2.88 * stat).toFixed(2) + "g";
	    case 'waterReduction':
	    	return 10 * stat + "L";
	    default:
	    	return '';
	  }
}

const setPersonalStat = async () => {
	const hostingStat = await $MEETING.Get.meetingCountAndTotalDurationStatForHosting(getPeriod());
	const attendanceStat = await $MEETING.Get.meetingCountAndTotalDurationStatForAttendance(getPeriod());
	showMeetingSummaryStatForPersonal({
		hostingCount: hostingStat.statValue1,
		hostingDuration: hostingStat.statValue2,
		attendanceCount: attendanceStat.statValue1,
		attendanceDuration: attendanceStat.statValue2,
	});
}

const showMeetingSummaryStatForPersonal = (stat) => {
	const statContainers = Util.getElementAll("[data-stat-summary]");
	const {
		hostingCount = 0,
		hostingDuration = 0,
		attendanceCount = 0,
		attendanceDuration = 0,
	} = stat;
	statContainers.forEach($container => {
	    const summary = $container.getAttribute('data-stat-summary');
	    if(summary == "hosting"){
	    	$container.style.display = "flex";
	    	const hours = Math.floor(hostingDuration / 60); // 시간 계산
			const remainingMinutes = hostingDuration % 60; // 남은 분 계산
	    	const $display = $container.querySelector('[data-stat-display]')
	    	$display.textContent = `${hostingCount}회 / ${hours}시간 ${remainingMinutes}분`;
	    }else if(summary == "attendance"){
	    	$container.style.display = "flex";
	    	const hours = Math.floor(attendanceDuration / 60); // 시간 계산
			const remainingMinutes = attendanceDuration % 60; // 남은 분 계산
	    	const $display = $container.querySelector('[data-stat-display]')
	    	$display.textContent = `${attendanceCount}회 / ${hours}시간 ${remainingMinutes}분`;
	    }else{
	    	$container.style.display = "none";
	    }
	});
}