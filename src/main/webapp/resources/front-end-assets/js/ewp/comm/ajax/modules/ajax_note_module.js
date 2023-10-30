import {Api} from '/resources/core-assets/essential_index.js';

const getNotePageLength = (data = {}) => {
	const {
		meetingKey,
		fileKey,
	} = data;
	  return Api.Get(`/api/ewp/meeting/${meetingKey}/note/${fileKey}/length`);
};
const getNotePageSourceImage = (data = {}) => {
	const {
		meetingKey,
		fileKey,
		pageNo,
	} = data;
	return `/api/ewp/meeting/${meetingKey}/note/${fileKey}/page/${pageNo}/image/source`;
};
const getNotePageDrawImage = (data = {}) => {
	const {
		meetingKey,
		fileKey,
		pageNo,
		salt,
	} = data;
	return `/api/ewp/meeting/${meetingKey}/note/${fileKey}/page/${pageNo}/image/draw?salt=${salt}`;
};
const putNotePage = (data = {}) => {
	const {
		meetingKey,
		fileKey,
		pageNo,
		image,
	} = data;
	return Api.Put(`/api/ewp/meeting/${meetingKey}/note/${fileKey}/page/${pageNo}/image/draw`, {
		data: image,
		headers: {
			"Content-Type": "application/json; charset=utf-8",
		}
	});
};

const getMemoPageLength = (meetingKey) => {
	return Api.Get(`/api/ewp/meeting/${meetingKey}/memo/length`);
};
const getMemoSourceImage = (data = {}) => {
	const {
		meetingKey,
	} = data;
	return `/api/ewp/meeting/${meetingKey}/memo/page/image/source`;
};
const getMemoPageDrawImage = (data = {}) => {
	const {
		meetingKey,
		pageNo,
		salt,
	} = data;
	return `/api/ewp/meeting/${meetingKey}/memo/page/${pageNo}/image/draw?salt=${salt}`;
};
const postMemoPage = (data = {}) => {
	const {
		meetingKey,
		pageNo,
	} = data;
	return Api.Post(`/api/ewp/meeting/${meetingKey}/memo/page/${pageNo}`);
};
const putMemoPage = (data = {}) => {
	const {
		meetingKey,
		pageNo,
		image,
	} = data;
	return Api.Put(`/api/ewp/meeting/${meetingKey}/memo/page/${pageNo}/image/draw`, {
		data: image,
		headers: {
			"Content-Type": "application/json; charset=utf-8",
		}
	});
};
const deleteMemoPage = (data = {}) => {
	const {
		meetingKey,
		pageNo,
		image,
	} = data;
	return Api.Delete(`/api/ewp/meeting/${meetingKey}/memo/page/${pageNo}`);
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

const FileCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default FileCall;