import {Api} from '/resources/core-assets/essential_index.js';
//참석자 일괄변경(등록/수정/삭제)
const changeAttendeeList = (data = {}) => {
	const {
		meetingKey,
		list,
	} = data;
	return Api.Post(`/api/ewp/meeting/${meetingKey}/attendee`, {
		data: list,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
//참석자 출석체크
const putAttendeeAttendance = (data = {}) => {
	const {
		meetingKey, 
		attendKey, 
		attendYN
	} = data;
	return Api.Put(`/api/ewp/meeting/${meetingKey}/attendee/${attendKey}/attendance/${attendYN}`);
}
//참석자 사인
const putAttendeeSign = (data = {}) => {
	const {
		meetingKey, 
		attendKey, 
		signYN, 
		signSrc
	} = data;
	return Api.Put(`/api/ewp/meeting/${meetingKey}/attendee/${attendKey}/sign/${signYN}`, {
		data: signSrc,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
//보조 진행자 지정
const putAttendeeAsAssistant = (data = {}) => {
	const {
		meetingKey, 
		attendKey, 
		assistantYN
	} = data;
	return Api.Put(`/api/ewp/meeting/${meetingKey}/attendee/${attendKey}/assistant/${assistantYN}`);
}
const getAttendeeOne = (data = {}) => {
	const {
		meetingKey,
		attendKey,
	} = data;
	return Api.Get(`/api/ewp/meeting/${meetingKey}/attendee/${attendKey}`);
}

const getMeetingAttendeeListByMeeting = (data = {}) => {
	const {
		meetingKey,
		attendRole,
		assistantYN,
		attendYN,
		exitYN,
		signYN,
	} = data;
	return Api.Get(`/api/ewp/meeting/${meetingKey}/attendee/list`, {
		attendRole: attendRole,
		assistantYN: assistantYN,
		attendYN: attendYN,
		signYN: signYN,
	});
}

const getMeetingAttendeeSimpleListByMeeting = (data = {}) => {
	const {
		meetingKey,
	} = data;
	return Api.Get(`/api/ewp/meeting/${meetingKey}/attendee/list/simple`);
}
const insertSecurityAgreementOne = (data = {}) => {
	const {
		meetingKey, 
		signSrc
	} = data;
	return Api.Post(`/api/ewp/meeting/${meetingKey}/attendee/security-agreement`, {
		data: signSrc,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
const selectSecurityAgreementOne = (data = {}) => {
	const {
		meetingKey,
		attendKey,
	} = data;
	return Api.Get(`/api/ewp/meeting/${meetingKey}/attendee/${attendKey}/security-agreement`);
}
const selectSecurityAgreementAll = (data = {}) => {
	const {
		meetingKey,
	} = data;
	return Api.Get(`/api/ewp/meeting/${meetingKey}/attendee/security-agreement`);
}
const GetCall = {
	attendeeOne: getAttendeeOne,
	attendeeListByMeeting: getMeetingAttendeeListByMeeting,
	attendeeSimpleListByMeeting: getMeetingAttendeeSimpleListByMeeting,
	
	selectSecurityAgreementOne: selectSecurityAgreementOne,
	selectSecurityAgreementAll: selectSecurityAgreementAll,
}

const PostCall = {
	attendeeList: changeAttendeeList,
	
	insertSecurityAgreementOne: insertSecurityAgreementOne,
}

const PutCall = {
	assistant: putAttendeeAsAssistant,
	check: putAttendeeAttendance,
	sign: putAttendeeSign,
}

const DeleteCall = {
}

const AttendeeCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default AttendeeCall;