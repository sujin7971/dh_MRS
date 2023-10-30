/**
 * 
 */
import {Util} from '/resources/core-assets/essential_index.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

window.onload = async () => {
	setPersonalStat();
};

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
	const stat = await $MEETING.Get.paperlessStatForPersonal();
	const statElements = Util.getElementAll("[data-stat]");
	statElements.forEach(element => {
	    const paperless = element.getAttribute('data-stat');
	    const value = getValueByStat(paperless, stat); // stat에 따라 값을 가져오는 함수 호출
	    element.textContent = value; // 값을 해당 element에 할당
	});
}