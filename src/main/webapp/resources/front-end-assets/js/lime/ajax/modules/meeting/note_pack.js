import {Api} from '/resources/core-assets/essential_index.js';

const getNotePageLength = (data = {}) => {
	const {
		meetingId,
		fileId,
	} = data;
	  return Api.Get(`/api/lime/meeting/${meetingId}/note/${fileId}/length`);
};
const getNotePageSourceImage = (data = {}) => {
	const {
		meetingId,
		fileId,
		pageNo,
	} = data;
	return `/api/lime/meeting/${meetingId}/note/${fileId}/page/${pageNo}/image/source`;
};
const getNotePageDrawImage = (data = {}) => {
	const {
		meetingId,
		fileId,
		pageNo,
		salt,
	} = data;
	return `/api/lime/meeting/${meetingId}/note/${fileId}/page/${pageNo}/image/draw?salt=${salt}`;
};
const putNotePage = (data = {}) => {
	const {
		meetingId,
		fileId,
		pageNo,
		image,
	} = data;
	return Api.Put(`/api/lime/meeting/${meetingId}/note/${fileId}/page/${pageNo}/image/draw`, {
		data: image,
		headers: {
			"Content-Type": "application/json; charset=utf-8",
		}
	});
};

const getMemoPageLength = (meetingId) => {
	return Api.Get(`/api/lime/meeting/${meetingId}/memo/length`);
};
const getMemoSourceImage = (data = {}) => {
	const {
		meetingId,
	} = data;
	return `/api/lime/meeting/${meetingId}/memo/page/image/source`;
};
const getMemoPageDrawImage = (data = {}) => {
	const {
		meetingId,
		pageNo,
		salt,
	} = data;
	return `/api/lime/meeting/${meetingId}/memo/page/${pageNo}/image/draw?salt=${salt}`;
};
const postMemoPage = (data = {}) => {
	const {
		meetingId,
		pageNo,
	} = data;
	return Api.Post(`/api/lime/meeting/${meetingId}/memo/page/${pageNo}`);
};
const putMemoPage = (data = {}) => {
	const {
		meetingId,
		pageNo,
		image,
	} = data;
	return Api.Put(`/api/lime/meeting/${meetingId}/memo/page/${pageNo}/image/draw`, {
		data: image,
		headers: {
			"Content-Type": "application/json; charset=utf-8",
		}
	});
};
const deleteMemoPage = (data = {}) => {
	const {
		meetingId,
		pageNo,
		image,
	} = data;
	return Api.Delete(`/api/lime/meeting/${meetingId}/memo/page/${pageNo}`);
};
const GetCall = {
	notePageLength: getNotePageLength,
	notePageSourceImage: getNotePageSourceImage,
	notePageDrawImage: getNotePageDrawImage,
	
	memoPageLength: getMemoPageLength,
	memoSourceImage: getMemoSourceImage,
	memoPageDrawImage: getMemoPageDrawImage,
}

const PostCall = {
	memoPage: postMemoPage,
}

const PutCall = {
	notePage: putNotePage,	
		
	memoPage: putMemoPage,
}

const DeleteCall = {
	memoPage: deleteMemoPage,
}

const MeetingNoteCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default MeetingNoteCall;