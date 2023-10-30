import {Api} from '/resources/core-assets/essential_index.js';
const postMeetingAssign = (data = {}) => {
	const {
		roomType,
		formData,
	} = data;
	return Api.Post(`/api/ewp/meeting/assign/post`, {
	    data: formData,
	    headers: {
	        "Content-Type": "application/x-www-form-urlencoded",
	    },
	});
}

const putMeetingAssign = (data = {}) => {
	const {
		skdKey,
		formData,
	} = data;
	return Api.Put(`/api/ewp/meeting/assign/${skdKey}`, {
	    data: formData,
	    headers: {
	        "Content-Type": "application/x-www-form-urlencoded",
	      },
	  });
};
const postAssignApproval = (data = {}) => {
	const {
		skdKey,
		status,
		comment,
	} = data;
	return Api.Post(`/api/ewp/meeting/assign/${skdKey}/approval/${status}`, {
		queryParams: {
			comment: comment
		}
	});
}
const getAssignOne = (skdKey) => {
	return Api.Get(`/api/ewp/meeting/assign/${skdKey}`, {
		skdKey: skdKey
	});
}
const getAssignList = (data = {}) => {
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
	    orderColumn,
	    orderDirection = "ASC",
	  } = data;
	  return Api.Get(`/api/ewp/meeting/assign/list`,{
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
	    orderColumn: orderColumn,
	    orderDirection: orderDirection,
	  });
};
const getAssignListCnt = (data = {}) => {
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
	return Api.Get(`/api/ewp/meeting/assign/list/cnt`,{
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
}
const getAssignListForDisplay = (data = {}) => {
	  const {
	    officeCode,
	    roomType,
	    roomKey,
	    startDate,
	    endDate,
	  } = data;
	  return Api.Get(`/api/ewp/meeting/assign/display/list`,{
	    officeCode: officeCode,
	    roomType: roomType,
	    roomKey: roomKey,
	    startDate: startDate,
	    endDate: endDate,
	  });
};
const getAssignPublicListForDisplay = (data = {}) => {
	  const {
	    officeCode,
	    roomType,
	    roomKey,
	    startDate,
	    endDate,
	  } = data;
	  return Api.Get(`/api/ewp/public/meeting/assign/display/list`,{
	    officeCode: officeCode,
	    roomType: roomType,
	    roomKey: roomKey,
	    startDate: startDate,
	    endDate: endDate,
	  });
};
const getAssignListForPlanned = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	  return Api.Get(`/api/ewp/meeting/assign/planned/list`,{
		  startDate: startDate,
		  endDate: endDate,
	  });
};
const getAssignStatForPlanned = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	  return Api.Get(`/api/ewp/meeting/assign/planned/stat`,{
		  startDate: startDate,
		  endDate: endDate,
	  });
};
const getAssignListForRegister = (data = {}) => {
	const {
		pageNo,
		pageCnt,
	} = data;
	  return Api.Get(`/api/ewp/meeting/assign/register/list`,{
		  pageNo: pageNo,
		  pageCnt: pageCnt,
	  });
};
const getUserArchiveList = (data = {}) => {
	const {
		skdHost,
		title,
		originalName,
		roleType,
		startDate,
		endDate,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/ewp/meeting/archive/manage/user`,{
		skdHost: skdHost,
		title: title,
		originalName: originalName,
		roleType: roleType,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}
const getDeptArchiveList = (data = {}) => {
	const {
		skdHost,
		title,
		originalName,
		startDate,
		endDate,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/ewp/meeting/archive/manage/dept`,{
		skdHost: skdHost,
		title: title,
		originalName: originalName,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}
const getAdminArchiveList = (data = {}) => {
	const {
		skdHost,
		title,
		originalName,
		startDate,
		endDate,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/ewp/admin/master/meeting/archive/manage`,{
		skdHost: skdHost,
		title: title,
		originalName: originalName,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}
const getUserAssignList = (data = {}) => {
	const {
		approvalStatus,
		meetingStatus,
		roomType,
		title,
		host,
		writerKey,
		attendeeName,
		elecYN,
		secretYN,
		startDate,
		endDate,
		pageNo,
		pageCnt,
		orderColumn,
		orderDirection = "DESC",
	} = data;
	return Api.Get(`/api/ewp/meeting/assign/manage/user`,{
		approvalStatus: approvalStatus,
		meetingStatus: meetingStatus,
		roomType: roomType,
		title: title,
		host: host,
		writerKey: writerKey,
		attendeeName: attendeeName,
		elecYN: elecYN,
		secretYN: secretYN,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
		orderColumn: orderColumn,
		orderDirection: orderDirection,
	});
}
const getDeptAssignList = (data = {}) => {
	const {
		approvalStatus,
		meetingStatus,
		roomType,
		title,
		host,
		attendeeName,
		elecYN,
		secretYN,
		startDate,
		endDate,
		pageNo,
		pageCnt,
		orderColumn,
		orderDirection = "DESC",
	} = data;
	return Api.Get(`/api/ewp/meeting/assign/manage/dept`,{
		approvalStatus: approvalStatus,
		meetingStatus: meetingStatus,
		roomType: roomType,
		title: title,
		host: host,
		attendeeName: attendeeName,
		elecYN: elecYN,
		secretYN: secretYN,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
		orderColumn: orderColumn,
		orderDirection: orderDirection,
	});
}
const GetCall = {
	assignOne: getAssignOne,
	assignList: getAssignList,
	assignListCnt: getAssignListCnt,
	assignListForPlanned: getAssignListForPlanned,
	assignStatForPlanned: getAssignStatForPlanned,
	assignListForRegister: getAssignListForRegister,
	assignListForDisplay: getAssignListForDisplay,
	assignPublicListForDisplay: getAssignPublicListForDisplay,
	
	
	userArchiveList: getUserArchiveList,
	deptArchiveList: getDeptArchiveList,
	adminArchiveList: getAdminArchiveList,
	userAssignList: getUserAssignList,
	deptAssignList: getDeptAssignList,
}

const PostCall = {
	assignApproval: postAssignApproval,
	assignOne: postMeetingAssign,
}

const PutCall = {
	assignOne: putMeetingAssign,
}

const DeleteCall = {
}

const AssignCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default AssignCall;