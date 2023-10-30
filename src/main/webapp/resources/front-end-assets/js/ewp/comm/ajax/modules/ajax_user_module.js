import {Api} from '/resources/core-assets/essential_index.js';
const postSwitchUserAuthenticationToken = (data = {}) => {
	const {
		userId,
		loginType,
	} = data;
	return Api.Post(`/api/ewp/dev/switch-authentication/user/${userId}/loginType/${loginType}`);
}
//부서 조회
const getDeptOne = (deptId) => {
	return Api.Get(`/api/ewp/dept/${deptId}`)
}
//하위 부서 조회
const getSubDeptList = (parentCode) => {
	return Api.Get(`/api/ewp/dept/${parentCode}/sub`)
}
//직원 검색
const getUserList = (data = {}) => {
	const {
		officeCode,
		deptId,
		deptName,
		userKey,
		userName,
	} = data;
	return Api.Get(`/api/ewp/user/list`, {
		officeCode: officeCode,
		deptId: deptId,
		deptName: deptName,
		userName: userName,
	});
}
const getUserAuthorityForMeeting = (meetingId) => {
	return Api.Get(`/api/ewp/user/authority/meeting/${meetingId}`)
}
const getAdminAuthorityForMeeting = (meetingId) => {
	return Api.Get(`/api/ewp/admin/authority/meeting/${meetingId}`)
}
const getApprovalManagerPublicList = (data = {}) => {
	const {
		officeCode,
		roomType,
	} = data;
	return Api.Get(`/api/ewp/public/roster/approval-manager/list`, {
		officeCode: officeCode,
		roomType: roomType
	});
}
const GetCall = {
	deptOne: getDeptOne,
	subDeptList: getSubDeptList,
	userList: getUserList,
	subDeptList: getSubDeptList,
	userAuthorityForMeeting: getUserAuthorityForMeeting,
	adminAuthorityForMeeting: getAdminAuthorityForMeeting,
	approvalManagerPublicList: getApprovalManagerPublicList,
	
}

const PostCall = {
	switchUserAuthenticationToken: postSwitchUserAuthenticationToken,
}

const PutCall = {
}

const DeleteCall = {
}

const UserCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default UserCall;