import {Api} from '/resources/core-assets/essential_index.js';
const postUserOne = (formData) => {
	return Api.Post(`/api/lime/admin/system/user`, {
	    data: formData,
	    headers: {
	    	"Content-Type": "multipart/form-data",
	    },
	});
}
const putUserOne = (formData) => {
	return Api.Put(`/api/lime/admin/system/user`, {
	    data: formData,
	    headers: {
	    	"Content-Type": "multipart/form-data",
	    },
	});
}
const deleteUserOne = (userId) => {
	return Api.Delete(`/api/lime/admin/system/user/${userId}`);
}
const postSwitchUserAuthenticationToken = (data = {}) => {
	const {
		userId,
		loginType,
	} = data;
	return Api.Post(`/api/lime/dev/switch-authentication/user/${userId}/loginType/${loginType}`);
}
//직원 검색
const getUserList = (data = {}) => {
	const {
		officeCode,
		deptId,
		deptName,
		userId,
		userName,
	} = data;
	return Api.Get(`/api/lime/user/list`, {
		officeCode: officeCode,
		deptId: deptId,
		deptName: deptName,
		userName: userName,
	});
}
const getApprovalManagerPublicList = (data = {}) => {
	const {
		officeCode,
		roomType,
	} = data;
	return Api.Get(`/api/lime/public/roster/approval-manager/list`, {
		officeCode: officeCode,
		roomType: roomType
	});
}
const GetCall = {
	userList: getUserList,
	approvalManagerPublicList: getApprovalManagerPublicList,
}

const PostCall = {
	switchUserAuthenticationToken: postSwitchUserAuthenticationToken,
	userOne: postUserOne,
}

const PutCall = {
	userOne: putUserOne,
}

const DeleteCall = {
	userOne: deleteUserOne,
}

const UserCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default UserCall;