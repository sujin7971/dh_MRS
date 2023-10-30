import {Api} from '/resources/core-assets/essential_index.js';
const postMeetingAssign = (data = {}) => {
	const {
		formData,
	} = data;
	return Api.Post(`/api/lime/meeting/assign/post`, {
	    data: formData,
	    headers: {
	        "Content-Type": "multipart/form-data",
	    },
	});
}

const putMeetingAssign = (data = {}) => {
	const {
		scheduleId,
		formData,
	} = data;
	return Api.Put(`/api/lime/meeting/assign/${scheduleId}`, {
	    data: formData,
	    headers: {
	        "Content-Type": "multipart/form-data",
	      },
	  });
};
const postAssignApproval = (data = {}) => {
	const {
		scheduleId,
		status,
		comment,
	} = data;
	return Api.Post(`/api/lime/meeting/assign/${scheduleId}/approval/${status}`, {
		queryParams: {
			comment: comment
		}
	});
}
const getAssignOne = (scheduleId) => {
	return Api.Get(`/api/lime/meeting/assign/${scheduleId}`, {
		scheduleId: scheduleId
	});
}
const getAssignList = (data = {}) => {
	  const {
	    officeCode,
	    approvalStatus,
	    roomType,
	    roomId,
	    startDate,
	    endDate,
	    scheduleHost,
	    pageNo,
	    pageCnt,
	    orderColumn,
	    orderDirection = "ASC",
	  } = data;
	  return Api.Get(`/api/lime/meeting/assign/list`,{
	    officeCode: officeCode,
	    approvalStatus: approvalStatus,
	    roomType: roomType,
	    roomId: roomId,
	    startDate: startDate,
	    endDate: endDate,
	    scheduleHost: scheduleHost,
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
		roomId,
		startDate,
		endDate,
		scheduleHost,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/lime/meeting/assign/list/cnt`,{
		officeCode: officeCode,
		approvalStatus: approvalStatus,
		roomType: roomType,
		roomId: roomId,
		startDate: startDate,
		endDate: endDate,
		scheduleHost: scheduleHost,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}
const getAssignListForDisplay = (data = {}) => {
	  const {
	    officeCode,
	    roomType,
	    roomId,
	    startDate,
	    endDate,
	  } = data;
	  return Api.Get(`/api/lime/meeting/assign/display/list`,{
	    officeCode: officeCode,
	    roomType: roomType,
	    roomId: roomId,
	    startDate: startDate,
	    endDate: endDate,
	  });
};
const getAssignPublicListForDisplay = (data = {}) => {
	  const {
	    officeCode,
	    roomType,
	    roomId,
	    startDate,
	    endDate,
	  } = data;
	  return Api.Get(`/api/lime/public/meeting/assign/display/list`,{
	    officeCode: officeCode,
	    roomType: roomType,
	    roomId: roomId,
	    startDate: startDate,
	    endDate: endDate,
	  });
};
const getAssignListForPlanned = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	  return Api.Get(`/api/lime/meeting/assign/planned/list`,{
		  startDate: startDate,
		  endDate: endDate,
	  });
};
const getAssignStatForPlanned = (data = {}) => {
	const {
		startDate,
		endDate,
	} = data;
	  return Api.Get(`/api/lime/meeting/assign/planned/stat`,{
		  startDate: startDate,
		  endDate: endDate,
	  });
};
const getAssignListForRegister = (data = {}) => {
	const {
		pageNo,
		pageCnt,
	} = data;
	  return Api.Get(`/api/lime/meeting/assign/register/list`,{
		  pageNo: pageNo,
		  pageCnt: pageCnt,
	  });
};
const getUserAssignList = (data = {}) => {
	const {
		approvalStatus,
		meetingStatus,
		roomType,
		title,
		scheduleHost,
		writerId,
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
	return Api.Get(`/api/lime/meeting/assign/manage/user`,{
		approvalStatus: approvalStatus,
		meetingStatus: meetingStatus,
		roomType: roomType,
		title: title,
		scheduleHost: scheduleHost,
		writerId: writerId,
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
		scheduleHost,
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
	return Api.Get(`/api/lime/meeting/assign/manage/dept`,{
		approvalStatus: approvalStatus,
		meetingStatus: meetingStatus,
		roomType: roomType,
		title: title,
		scheduleHost: scheduleHost,
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

const MeetingAssignCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingAssignCall;