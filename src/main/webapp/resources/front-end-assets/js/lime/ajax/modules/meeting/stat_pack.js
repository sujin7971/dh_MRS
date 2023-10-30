import {Api} from '/resources/core-assets/essential_index.js';

const getPaperlessStatForCompany = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/paperless/company`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getMeetingCountAndAverageDurationStatForCompany = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/count-and-avgduration/company`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getTop5DepartmentWithMeetingForCompany = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/top5-department/company`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getMeetingMonthlyTrendForCompany = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	console.log("getMeetingMonthlyTrendForCompany", "startDate", startDate, "endDate", endDate)
	return Api.Get(`/api/lime/meeting/stat/monthly-trend/company`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getPaperlessStatForPersonal = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/paperless/personal`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getMeetingCountAndTotalDurationStatForHosting = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/count-and-totalduration/hosting`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getMeetingCountAndTotalDurationStatForAttendance = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/count-and-totalduration/attendance`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getTop5DepartmentWithMeetingForOffice = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/top5-department/office`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const getMeetingMonthlyTrendForPersonal = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	return Api.Get(`/api/lime/meeting/stat/monthly-trend/personal`, {
		startDate: startDate,
		endDate: endDate,
	});
}

const GetCall = {
	paperlessStatForCompany: getPaperlessStatForCompany,
	meetingCountAndAverageDurationStatForCompany: getMeetingCountAndAverageDurationStatForCompany,
	top5DepartmentWithMeetingForCompany: getTop5DepartmentWithMeetingForCompany,
	meetingMonthlyTrendForCompany: getMeetingMonthlyTrendForCompany,
	
	paperlessStatForPersonal: getPaperlessStatForPersonal,	
	meetingCountAndTotalDurationStatForHosting: getMeetingCountAndTotalDurationStatForHosting,
	meetingCountAndTotalDurationStatForAttendance: getMeetingCountAndTotalDurationStatForAttendance,
	top5DepartmentWithMeetingForOffice: getTop5DepartmentWithMeetingForOffice,
	meetingMonthlyTrendForPersonal: getMeetingMonthlyTrendForPersonal,
}

const MeetingStatCall = {
	Get: GetCall,
}

export default MeetingStatCall;