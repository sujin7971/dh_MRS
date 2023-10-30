import {Api} from '/resources/core-assets/essential_index.js';

const insertRoomOne = (data = {}) => {
	return Api.Post(`/api/ewp/admin/system/room`, {
		data: data,
		headers: {
	        "Content-Type": "application/x-www-form-urlencoded",
		},
	});
};
const updateRoomOne = (data = {}) => {
	return Api.Put(`/api/ewp/admin/system/room`, {
		data: data,
		headers: {
	        "Content-Type": "application/x-www-form-urlencoded",
		},
	});
};
//회의실 삭제
const deleteRoomOne = (data = {}) => {
	const {
		roomType,
		roomKey,
	} = data;
	return Api.Delete(`/api/ewp/admin/system/room/${roomType}/${roomKey}`);
};
const insertRoomPermissionList = (data) => {
	return Api.Post(`/api/ewp/admin/system/room/permission/list`, {
		data: data,
		headers: {
	        "Content-Type": "application/json; charset=utf-8",
		},
	});
}
const deleteRoomPermissionAll = (data = {}) => {
	const {
		roomType,
		roomKey,
	} = data;
	console.log("data", data)
	return Api.Delete(`/api/ewp/admin/system/room/${roomType}/${roomKey}/permission/all`, {
		data: data
	});
}
// 모든 room 리스트 요청
const selectRoomList = (data = {}) => {
	const {
		officeCode,
		roomType = "ALL_ROOM",
		rentYN,
		delYN,
	} = data;
	  return Api.Get(`/api/ewp/admin/system/room/${roomType}/list`,{
		  officeCode: officeCode,
		  rentYN: rentYN,
		  delYN: delYN
	  });
};

const selectRentableRoomList = (data = {}) => {
	const {
		officeCode,
		roomType,
	} = data;
	  return Api.Get(`/api/ewp/room/${roomType}/rentable/list`,{
		  officeCode: officeCode,
	  });
};

const selectRoomPublicListForDisplay = (data = {}) => {
	const {
		officeCode,
		roomType,
	} = data;
	  return Api.Get(`/api/ewp/public/room/${roomType}/display/list`,{
		  officeCode: officeCode,
	  });
};
const selectRoomPermissionList = (param) => {
	const {
		roomType,
		roomKey
	} = param;
	return Api.Get(`/api/ewp/admin/system/room/${roomType}/${roomKey}/permission/list`);
}
const selectRoomOne = (data = {}) => {
	const {
		roomType,
		roomKey
	} = data;
	return Api.Get(`/api/ewp/room/${roomType}/${roomKey}`);
}

const GetCall = {
	roomOne: selectRoomOne,
	roomList: selectRoomList,
	rentableList: selectRentableRoomList,
	publicListForDisplay: selectRoomPublicListForDisplay,
	
	roomPermissionList: selectRoomPermissionList,
}

const PostCall = {
	roomOne: insertRoomOne,
	roomPermissionList: insertRoomPermissionList
}

const PutCall = {
	roomOne: updateRoomOne,
}

const DeleteCall = {
	roomOne: deleteRoomOne,
	roomPermissionAll: deleteRoomPermissionAll,
}

const RoomCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default RoomCall;