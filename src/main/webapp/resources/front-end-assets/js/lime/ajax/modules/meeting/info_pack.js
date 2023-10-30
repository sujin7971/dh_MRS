import {Api} from '/resources/core-assets/essential_index.js';

const getUserAuthorityForMeeting = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/authority/private`)
}
const getAdminAuthorityForMeeting = (meetingId) => {
	return Api.Get(`/api/lime/admin/system/meeting/${meetingId}/authority/private`)
}

const GetCall = {
	userAuthorityForMeeting: getUserAuthorityForMeeting,
	adminAuthorityForMeeting: getAdminAuthorityForMeeting,
}

const PostCall = {
}

const PutCall = {
}

const DeleteCall = {
}

const MeetingInfoCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingInfoCall;