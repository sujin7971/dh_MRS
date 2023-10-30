import {Api} from '/resources/core-assets/essential_index.js';

const putReport = (data = {}) => {
	const {
		meetingKey,
		reportContents,
		reportStatus,
		pdf = null,
	} = data;
	const formData = new FormData();
	if(reportContents !== undefined || reportContents !== null){
		formData.append("reportContents",reportContents);
	}
	if(reportStatus){
		formData.append("reportStatus",reportStatus);
	}
	if(pdf){
		formData.append("pdf",pdf);
	}
	return Api.Post(`/api/ewp/meeting/${meetingKey}/report`, {
		data: formData,
		headers: {
			"Content-Type": "multipart/form-data",
		},
	});
}
const postOpn = (data = {}) => {
	const {
		meetingKey,
		comment,
	} = data;
	return Api.Post(`/api/ewp/meeting/${meetingKey}/report/opinion`, {
		data: comment,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
const deleteOpn = (data = {}) => {
	const {
		meetingKey,
		opnId,
	} = data;
	return Api.Delete(`/api/ewp/meeting/${meetingKey}/report/opinion/${opnId}`);
}
const getReportOne = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/report`);
}
const getReportOpinionList = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/report/opinion`);
}
const GetCall = {
	reportOne: getReportOne,
	opinionList: getReportOpinionList,
}

const PostCall = {
	opinion: postOpn,	
}

const PutCall = {
	report: putReport,
}

const DeleteCall = {
	opinion: deleteOpn
}

const ReportCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default ReportCall;