/**
 * 
 */
import {Util, Modal} from '/resources/core-assets/essential_index.js';
import {MeetingCall as $MEET, AttendeeCall as $ATT} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
import reportService from './modules/report_module.js';
import opinionService from './modules/opinion_module.js';

$(async function(){
	Modal.startLoading();
	reportService.init({skdKey: skdKey, meetingKey: meetingKey});
	await reportService.setReport();
	await reportService.setAttendee();
	await opinionService.init({
		meetingKey: meetingKey, 
		attendeeList: reportService.getAttendeeList(),
	});
	Modal.endLoading();
});

