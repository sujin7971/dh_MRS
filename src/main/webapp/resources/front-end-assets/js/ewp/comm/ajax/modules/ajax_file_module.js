import {Api} from '/resources/core-assets/essential_index.js';
//회의 파일 일괄 등록
const postMeetingFileList = (data = {}) => {
	const {
		meetingKey, 
		fileType,
		fileList,
	} = data;
	return Api.Post(`/api/ewp/meeting/${meetingKey}/file/${fileType}/list`, {
	    data: fileList,
	    headers: {
    		"Content-Type": "multipart/form-data",
    	},
	});
}

const postFileList = (data = {}) => {
	const {
		fileList,
	} = data;
	return Api.Post(`/api/file/list`, {
	    data: fileList,
	    headers: {
    		"Content-Type": "multipart/form-data",
    	},
	});
}

const deleteMeetingFileList = (data = {}) => {
	const {
		meetingKey, 
		fileList,
	} = data;
	return Api.Delete(`/api/ewp/meeting/${meetingKey}/file/list`, {
	    data: fileList,
	});
}

//파일 다운로드
const downloadFileOne = (link) => {
	window.location ='/api/file/'+link+'/download';
}

//파일 리스트 다운로드
const downloadFileList = (downList) => {
	for(let i =0; i < downList.length; i++){
		let link = downList[i];
		setTimeout(function(){
			downloadFileOne(link);
		},500 * i)
	}
}

//파일 미리보기
const viewFileOne = (param = {}) => {
	const {
		fileCategory,
		fileId,
	} = param;
	if(fileCategory == "IMG"){
		window.open('/api/file/'+fileId+'/view');
	}else{
		window.open('/viewer?file=/api/file/'+fileId+'/view');
	}
}

//회의파일 다운로드
const downloadMeetingFileOne = (link) => {
	window.location ='/meeting/file/'+link+'/download';
}

//회의파일 리스트 다운로드
const downloadMeetingFileList = (downList) => {
	for(let i =0; i < downList.length; i++){
		let link = downList[i];
		setTimeout(function(){
			downloadMeetingFileOne(link);
		},500 * i)
	}
}

// 파일 미리보기
const viewMeetingFileOne = (param = {}) => {
	const {
		mime,
		link,
	} = param;
	if(mime == "IMG" || mime == "AUDIO"){
		window.open('/meeting/file/'+link+'/view');
	}else{
		window.open('/viewer?file=/meeting/file/'+link+'/view');
	}
}

const getMeetingFileOne = (fileKey) => {
	return Api.Get(`/api/ewp/meeting/file/${fileKey}`);
}
const getMeetingFileList = (param) => {
	const {
		meetingKey
	} = param;
	return Api.Get(`/api/ewp/meeting/${meetingKey}/file/list`, param);
}
const getMeetingMaterialFileList = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/file/material/list`);
}
const getMeetingSharedFileList = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/file/shared/list`);
}
const getMeetingPrivateFileList = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/file/private/list`);
}
const getMeetingAllFileList = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/file/all/list`);
}

const GetCall = {
	meetingFileOne: getMeetingFileOne,
	meetingMaterialFileList: getMeetingMaterialFileList,
	meetingSharedFileList: getMeetingSharedFileList,
	meetingPrivateFileList: getMeetingPrivateFileList,
	meetingAllFileList: getMeetingAllFileList,
	viewMeetingFileOne: viewMeetingFileOne,
	downloadOne: downloadFileOne,
	downloadList: downloadFileList,
	downloadMeetingFileOne: downloadMeetingFileOne,
	downloadMeetingFileList: downloadMeetingFileList,
	viewFileOne: viewFileOne
}

const PostCall = {
	fileList: postFileList,
	meetingFileList: postMeetingFileList,
}

const PutCall = {
}

const DeleteCall = {
	meetingFileList: deleteMeetingFileList,
}

const FileCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default FileCall;