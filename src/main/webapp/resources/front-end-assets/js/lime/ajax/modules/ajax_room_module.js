import RoomInfoCall from './room/info_pack.js';
import AdminRoomInfoCall from './room/admin_info_pack.js';

const RoomCall = {
  Get: {
	  ...RoomInfoCall.Get,
	  ...AdminRoomInfoCall.Get,
  },
  Post: {
	  ...RoomInfoCall.Post,
	  ...AdminRoomInfoCall.Post,
  },
  Put: {
	  ...RoomInfoCall.Put,
	  ...AdminRoomInfoCall.Put,
  },
  Delete: {
	  ...RoomInfoCall.Delete,
	  ...AdminRoomInfoCall.Delete,
  },
};

export default RoomCall;