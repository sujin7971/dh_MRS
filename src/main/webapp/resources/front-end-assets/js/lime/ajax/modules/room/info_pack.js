import {Api} from '/resources/core-assets/essential_index.js';

const selectRentableRoomList = (data = {}) => {
	const {
		roomType,
	} = data;
	  return Api.Get(`/api/lime/room/rentable/list`,{
		  roomType: roomType,
	  });
};

const selectRoomPublicListForDisplay = (data = {}) => {
	const {
		roomType,
	} = data;
	  return Api.Get(`/api/lime/public/room/display/list`,{
		  roomType: roomType,
	  });
};
const selectRoomOne = (data = {}) => {
	const {
		roomId
	} = data;
	return Api.Get(`/api/lime/room/${roomId}`);
}

const GetCall = {
	roomOne: selectRoomOne,
	rentableList: selectRentableRoomList,
	publicListForDisplay: selectRoomPublicListForDisplay,
}

const PostCall = {
}

const PutCall = {
}

const DeleteCall = {
}

const RoomInfoCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default RoomInfoCall;