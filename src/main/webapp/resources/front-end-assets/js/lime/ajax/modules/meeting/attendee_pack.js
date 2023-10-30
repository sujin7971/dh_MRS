import {Api} from '/resources/core-assets/essential_index.js';
//참석자 일괄변경(등록/수정/삭제)
const insertAttendeeList = (data = {}) => {
	const {
		meetingId,
		list,
	} = data;
	return Api.Post(`/api/lime/meeting/${meetingId}/attendee`, {
		data: list,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
const updateAttendeeList = (data = {}) => {
	const {
		meetingId,
		list,
	} = data;
	return Api.Put(`/api/lime/meeting/${meetingId}/attendee`, {
		data: list,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
const deleteAttendeeList = (data = {}) => {
	const {
		meetingId,
		list,
	} = data;
	return Api.Delete(`/api/lime/meeting/${meetingId}/attendee`, {
		data: list,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
//참석자 출석체크
const putAttendeeAttendance = (data = {}) => {
	const {
		meetingId, 
		attendId, 
		attendYN
	} = data;
	return Api.Put(`/api/lime/meeting/${meetingId}/attendee/${attendId}/attendance/${attendYN}`);
}
//참석자 사인
const putAttendeeSign = (data = {}) => {
	const {
		meetingId, 
		attendId, 
		signYN, 
		signSrc
	} = data;
	return Api.Put(`/api/lime/meeting/${meetingId}/attendee/${attendId}/sign/${signYN}`, {
		data: signSrc,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
//보조 진행자 지정
const putAttendeeAsAssistant = (data = {}) => {
	const {
		meetingId, 
		attendId, 
		assistantYN
	} = data;
	return Api.Put(`/api/lime/meeting/${meetingId}/attendee/${attendId}/assistant/${assistantYN}`);
}
const getAttendeeOne = (data = {}) => {
	const {
		meetingId,
		attendId,
	} = data;
	return Api.Get(`/api/lime/meeting/${meetingId}/attendee/${attendId}`);
}

const getMeetingAttendeeListByMeeting = (data = {}) => {
	const {
		meetingId,
		attendRole,
		assistantYN,
		attendYN,
		exitYN,
		signYN,
	} = data;
	return Api.Get(`/api/lime/meeting/${meetingId}/attendee/list`, {
		attendRole: attendRole,
		assistantYN: assistantYN,
		attendYN: attendYN,
		signYN: signYN,
	});
}

const getMeetingAttendeeSimpleListByMeeting = (data = {}) => {
	const {
		meetingId,
	} = data;
	return Api.Get(`/api/lime/meeting/${meetingId}/attendee/list/simple`);
}

const GetCall = {
	attendeeOne: getAttendeeOne,
	attendeeListByMeeting: getMeetingAttendeeListByMeeting,
	attendeeSimpleListByMeeting: getMeetingAttendeeSimpleListByMeeting,
}

const PostCall = {
	attendeeList: insertAttendeeList,
}

const PutCall = {
	attendeeList: updateAttendeeList,
	assistant: putAttendeeAsAssistant,
	check: putAttendeeAttendance,
	sign: putAttendeeSign,
}

const DeleteCall = {
	attendeeList: deleteAttendeeList,
}

const MeetingAttendeeCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingAttendeeCall;