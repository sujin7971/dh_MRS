import {Api} from '/resources/core-assets/essential_index.js';
const getNextRequestOne = (scheduleId) => {
	return Api.Get(`/api/lime/meeting/schedule/${scheduleId}/next`, {
		scheduleId: scheduleId
	});
}
const putMeetingScheduleExtend = (data = {}) => {
	const {
		scheduleId,
		minutes,
	} = data;
	return Api.Put(`/api/lime/meeting/schedule/${scheduleId}/extend/minutes/${minutes}`, {
		queryParams: {
			scheduleId: scheduleId,
			minutes: minutes
		}
	});
}
const putMeetingScheduleFinish = (data = {}) => {
	const {
		scheduleId,
	} = data;
	return Api.Put(`/api/lime/meeting/schedule/${scheduleId}/finish`);
}

const GetCall = {
	nextScheduleOne: getNextRequestOne,
}

const PostCall = {
}

const PutCall = {
	scheduleExtend: putMeetingScheduleExtend,
	scheduleFinish: putMeetingScheduleFinish,
}

const DeleteCall = {
}

const MeetingScheduleCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingScheduleCall;