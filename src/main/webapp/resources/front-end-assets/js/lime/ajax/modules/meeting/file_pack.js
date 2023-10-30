import {Api} from '/resources/core-assets/essential_index.js';
//회의 파일 일괄 등록
const postMeetingFileList = (data = {}) => {
	const {
		meetingId, 
		fileList,
	} = data;
	return Api.Post(`/api/lime/meeting/${meetingId}/file/material/list`, {
	    data: fileList,
	    headers: {
    		"Content-Type": "multipart/form-data",
    	},
	});
}

const deleteMeetingFileList = (data = {}) => {
	const {
		meetingId, 
		fileList,
	} = data;
	return Api.Delete(`/api/lime/meeting/${meetingId}/file/material/list`, {
	    data: fileList,
	});
}

//회의파일 다운로드
const downloadMeetingFileOne = (fileId) => {
	window.location = `/api/lime/meeting/file/${fileId}/download`;
}

//회의파일 리스트 다운로드
const downloadMeetingFileList = (list) => {
	let i = 0;
	for(const fileId of list){
		setTimeout(() => {
			downloadMeetingFileOne(fileId);
		}, 500 * i++)
	}
}

// 파일 미리보기
const viewMeetingFileOne = (param = {}) => {
	const {
		fileCategory,
		meetingId,
		fileId,
	} = param;
	if(fileCategory == "IMG" || fileCategory == "AUDIO"){
		window.open(`/api/lime/meeting/file/${fileId}/view`);
	}else{
		window.open(`/viewer?file=/api/lime/meeting/file/${fileId}/view`);
	}
}

const getMeetingFileOne = (fileKey) => {
	return Api.Get(`/api/lime/meeting/file/${fileKey}`);
}
const getMeetingFileList = (param) => {
	const {
		meetingId
	} = param;
	return Api.Get(`/api/lime/meeting/${meetingId}/file/list`, param);
}
const getMeetingMaterialFileList = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/file/material/list`);
}
const getMeetingSharedFileList = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/file/shared/list`);
}
const getMeetingPrivateFileList = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/file/private/list`);
}
const getMeetingAllFileList = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/file/all/list`);
}

const getUserArchiveList = (data = {}) => {
	const {
		scheduleHost,
		title,
		fileLabel,
		relationType,
		startDate,
		endDate,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/lime/meeting/archive/manage/user`,{
		scheduleHost: scheduleHost,
		title: title,
		fileLabel: fileLabel,
		relationType: relationType,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}
const getDeptArchiveList = (data = {}) => {
	const {
		scheduleHost,
		title,
		fileLabel,
		startDate,
		endDate,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/lime/meeting/archive/manage/dept`,{
		scheduleHost: scheduleHost,
		title: title,
		fileLabel: fileLabel,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}
const getAdminArchiveList = (data = {}) => {
	const {
		scheduleHost,
		title,
		fileName,
		startDate,
		endDate,
		pageNo,
		pageCnt,
	} = data;
	return Api.Get(`/api/lime/admin/master/meeting/archive/manage`,{
		scheduleHost: scheduleHost,
		title: title,
		fileLabel: fileLabel,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo,
		pageCnt: pageCnt,
	});
}

const GetCall = {
	meetingFileOne: getMeetingFileOne,
	meetingMaterialFileList: getMeetingMaterialFileList,
	meetingSharedFileList: getMeetingSharedFileList,
	meetingPrivateFileList: getMeetingPrivateFileList,
	meetingAllFileList: getMeetingAllFileList,
	viewFile: viewMeetingFileOne,
	downloadFileOne: downloadMeetingFileOne,
	downloadFileList: downloadMeetingFileList,
	
	userArchiveList: getUserArchiveList,
	deptArchiveList: getDeptArchiveList,
	adminArchiveList: getAdminArchiveList,
}

const PostCall = {
	meetingFileList: postMeetingFileList,
}

const PutCall = {
}

const DeleteCall = {
	meetingFileList: deleteMeetingFileList,
}

const MeetingFileCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingFileCall;