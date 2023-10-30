import {Api} from '/resources/core-assets/essential_index.js';

const insertRoomOne = (data = {}) => {
	return Api.Post(`/api/lime/admin/system/room`, {
		data: data,
		headers: {
	        "Content-Type": "multipart/form-data",
		},
	});
};
const updateRoomOne = (data = {}) => {
	return Api.Put(`/api/lime/admin/system/room`, {
		data: data,
		headers: {
	        "Content-Type": "multipart/form-data",
		},
	});
};
//회의실 삭제
const deleteRoomOne = (data = {}) => {
	const {
		roomId,
	} = data;
	return Api.Delete(`/api/lime/admin/system/room/${roomId}`);
};
// 모든 room 리스트 요청
const selectRoomList = (data = {}) => {
	const {
		roomType,
		disableYN,
		delYN,
	} = data;
	  return Api.Get(`/api/lime/admin/system/room/list`,{
		  roomType: roomType,
		  disableYN: disableYN,
		  delYN: delYN
	  });
};

const GetCall = {
	roomList: selectRoomList,
}

const PostCall = {
	roomOne: insertRoomOne,
}

const PutCall = {
	roomOne: updateRoomOne,
}

const DeleteCall = {
	roomOne: deleteRoomOne,
}

const AdminRoomInfoCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default AdminRoomInfoCall;