/**
 * 
 */
import {Util, Modal} from '/resources/core-assets/essential_index.js';
import reportService from './modules/report_module.js';

window.onload = async () => {
	Modal.startLoading();
	reportService.init({scheduleId: scheduleId, meetingId: meetingId});
	await reportService.setReport();
	await reportService.setAttendee();
	Modal.endLoading();
}

