import {Api} from '/resources/core-assets/essential_index.js';
const getNextRequestOne = (skdKey) => {
	return Api.Get(`/api/ewp/meeting/schedule/${skdKey}/next`, {
		skdKey: skdKey
	});
}
const putMeetingScheduleExtend = (data = {}) => {
	const {
		skdKey,
		minutes,
	} = data;
	return Api.Put(`/api/ewp/meeting/schedule/${skdKey}/extend/minutes/${minutes}`, {
		queryParams: {
			skdKey: skdKey,
			minutes: minutes
		}
	});
}
const putMeetingScheduleFinish = (data = {}) => {
	const {
		skdKey,
	} = data;
	return Api.Put(`/api/ewp/meeting/schedule/${skdKey}/finish`);
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

const ScheduleCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default ScheduleCall;