import {Api} from '/resources/core-assets/essential_index.js';

const postNoticeOne = (data = {}) => {
	const {
		formData,
	} = data;
	return Api.Post(`/api/lime/admin/notice`, {
	    data: formData,
	    headers: {
	        "Content-Type": "multipart/form-data",
	      },
	  });
}

const postNoticeFileList = (data = {}) => {
	const {
		noticeId,
		fileIdList
	} = data;
	return Api.Post(`/api/lime/admin/notice/${noticeId}/file/list`, {
	    data: fileIdList,
	  });
}

const putNoticeOne = (data = {}) => {
	const {
		noticeId,
		formData,
	} = data;
	return Api.Put(`/api/lime/admin/notice/${noticeId}`, {
	    data: formData,
	    headers: {
	        "Content-Type": "multipart/form-data",
	      },
	  });
};

const deleteNoticeOne = (noticeId) => {
	return Api.Delete(`/api/lime/admin/notice/${noticeId}`);
};

const deleteNoticeFileList = (data = {}) => {
	const {
		noticeId,
		fileIdList
	} = data;
	return Api.Delete(`/api/lime/admin/notice/${noticeId}/file/list`, {
	    data: fileIdList,
	  });
}

const getNoticeOne = (noticeId) => {
	return Api.Get(`/api/lime/notice/${noticeId}`);
}
const getNoticeList = (data = {}) => {
	  const {
	    officeCode,
	    title,
	    fixYN,
	    startDate,
	    eDate,
	    pageNo,
	    pageCnt,
	  } = data;
	  return Api.Get(`/api/lime/notice/list`,{
	    officeCode: officeCode,
	    title: title,
	    fixYN: fixYN,
	    startDate: startDate,
	    eDate: eDate,
	    pageNo: pageNo,
	    pageCnt: pageCnt,
	  });
};
const getNoticeListCnt = (data = {}) => {
	  const {
	    officeCode,
	    title,
	    fixYN,
	    startDate,
	    eDate,
	    pageNo,
	    pageCnt,
	  } = data;
	  return Api.Get(`/api/lime/notice/list/cnt`,{
	    officeCode: officeCode,
	    title: title,
	    fixYN: fixYN,
	    startDate: startDate,
	    eDate: eDate,
	    pageNo: pageNo,
	    pageCnt: pageCnt,
	  });
};
const GetCall = {
	noticeOne: getNoticeOne,
	noticeList: getNoticeList,
	noticeListCnt: getNoticeListCnt,
}

const PostCall = {
	noticeOne: postNoticeOne,
	fileList: postNoticeFileList,
}

const PutCall = {
	noticeOne: putNoticeOne,
}

const DeleteCall = {
	noticeOne: deleteNoticeOne,
	fileList: deleteNoticeFileList
}

const NoticeCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default NoticeCall;