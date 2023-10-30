import {Api} from '/resources/core-assets/essential_index.js';
const selectUserInfoOne = (userId) => {
	return Api.Get(`/api/ewp/admin/system/user/${userId}`);
}
const postSystemAdmin = (userId) => {
	return Api.Post(`/api/ewp/admin/system/roster/system-admin/${userId}`);
}
const deleteSystemAdmin = (userId) => {
	return Api.Delete(`/api/ewp/admin/system/roster/system-admin/${userId}`);
}
const postRequestManager = (userId) => {
	return Api.Post(`/api/ewp/admin/system/roster/request-manager/${userId}`);
}
const deleteRequestManager = (userId) => {
	return Api.Delete(`/api/ewp/admin/system/roster/request-manager/${userId}`);
}
const postRoomManager = (param = {}) => {
	const {
		officeCode,
		roomType,
		userId,
	} = param;
	return Api.Post(`/api/ewp/admin/system/roster/room-manager/${userId}`, {
		queryParams: {
			officeCode: officeCode,
			roomType: roomType,
		}
	});
}
const deleteRoomManager = (param = {}) => {
	const {
		officeCode,
		roomType,
		userId,
	} = param;
	return Api.Delete(`/api/ewp/admin/system/roster/room-manager/${userId}`, {
		queryParams: {
			officeCode: officeCode,
			roomType: roomType,
		}
	});
}
const putOfficeAutoApproval = (param = {}) => {
	const {
		officeCode,
		autoYN,
	} = param;
	return Api.Put(`/api/ewp/manager/approval/office/${officeCode}/policy/${autoYN}`);
}
const getOfficeApprovalPolicy = (officeCode) => {
	return Api.Get(`/api/ewp/manager/approval/office/${officeCode}/policy`);
}
const getMasterDomainList = () => {
	return Api.Get(`/api/ewp/admin/system/roster/master-admin/list`);
}
const getSystemDomainList = () => {
	return Api.Get(`/api/ewp/admin/system/roster/system-admin/list`);
}
const getRequestManagerList = (data = {}) => {
	const {
		officeCode,
		roomType,
	} = data;
	return Api.Get(`/api/ewp/admin/system/roster/request-manager/list`, {
		officeCode: officeCode
	});
}
const getRoomManagerList = (data = {}) => {
	const {
		officeCode,
		roomType,
	} = data;
	return Api.Get(`/api/ewp/admin/system/roster/room-manager/list`, {
		officeCode: officeCode,
		roomType: roomType
	});
}
const postAssignApproval = (data = {}) => {
	const {
		skdKey,
		status,
		comment,
	} = data;
	return Api.Post(`/api/ewp/manager/approval/meeting/assign/${skdKey}/approval/${status}`, {
		queryParams: {
			comment: comment
		}
	});
}
const putMeetingAssignTitleAndHost = (data = {}) => {
	const {
		skdKey,
		title,
		skdHost,
	} = data;
	return Api.Put(`/api/ewp/manager/approval/assign/${skdKey}`, {
	    queryParams: {
			title: title,
			skdHost: skdHost,
		},
  	});
};
const getAssignOneForApproval = (skdKey) => {
	return Api.Get(`/api/ewp/manager/approval/meeting/assign/${skdKey}`, {
		skdKey: skdKey
	});
}
const getAssignListForApproval = (data = {}) => {
	  const {
	    officeCode,
	    approvalStatus,
	    roomType,
	    roomKey,
		title,
	    host,
		attendeeName,
		elecYN,
		secretYN,
	    startDate,
	    endDate,
	    pageNo,
	    pageCnt,
	  } = data;
	  return Api.Get(`/api/ewp/manager/approval/meeting/assign/list`,{
	    officeCode: officeCode,
	    approvalStatus: approvalStatus,
	    roomType: roomType,
	    roomKey: roomKey,
		title: title,
	    host: host,
		attendeeName: attendeeName,
		elecYN: elecYN,
		secretYN: secretYN,
	    startDate: startDate,
	    endDate: endDate,
	    pageNo: pageNo,
	    pageCnt: pageCnt,
	  });
};
const GetCall = {
	userOne: selectUserInfoOne,
	officeApprovalPolicy: getOfficeApprovalPolicy,
	masterDomainList: getMasterDomainList,
	systemDomainList: getSystemDomainList,
	roomManagerList: getRoomManagerList, 
	requestManagerList: getRequestManagerList,
	assignOneForApproval: getAssignOneForApproval,
	assignListForApproval: getAssignListForApproval,
}

const PostCall = {
	systemDomain: postSystemAdmin,
	requestManager: postRequestManager,
	roomManager: postRoomManager,
	assignApproval: postAssignApproval,
}

const PutCall = {
	officeAutoApproval: putOfficeAutoApproval,
	meetingAssignTitleAndHost: putMeetingAssignTitleAndHost,
}

const DeleteCall = {
	systemDomain: deleteSystemAdmin,
	requestManager: deleteRequestManager,
	roomManager: deleteRoomManager,
}

const AdminCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default AdminCall;