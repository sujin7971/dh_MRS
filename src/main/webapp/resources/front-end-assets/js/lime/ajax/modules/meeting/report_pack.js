import {Api} from '/resources/core-assets/essential_index.js';

const putReport = (data = {}) => {
	const {
		meetingId,
		reportContents,
		reportStatus,
		reportFile = null,
	} = data;
	const formData = new FormData();
	if(reportContents !== undefined || reportContents !== null){
		formData.append("reportContents",reportContents);
	}
	if(reportStatus){
		formData.append("reportStatus",reportStatus);
	}
	if(reportFile){
		formData.append("reportFile",reportFile);
	}
	return Api.Post(`/api/lime/meeting/${meetingId}/report`, {
		data: formData,
		headers: {
			"Content-Type": "multipart/form-data",
		},
	});
}
const getReportOne = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/report`);
}
const GetCall = {
	reportOne: getReportOne,
}

const PostCall = {
}

const PutCall = {
	report: putReport,
}

const DeleteCall = {
}

const MeetingReportCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingReportCall;