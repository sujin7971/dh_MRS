import UserCall from './organization/user_pack.js';
import DeptCall from './organization/dept_pack.js';
import AdminRosterCall from './organization/admin_roster_pack.js';

const OrgCall = {
  Get: {
	  ...UserCall.Get,
	  ...DeptCall.Get,
	  ...AdminRosterCall.Get,
  },
  Post: {
	  ...UserCall.Post,
	  ...DeptCall.Post,
	  ...AdminRosterCall.Post,
  },
  Put: {
	  ...UserCall.Put,
	  ...DeptCall.Put,
	  ...AdminRosterCall.Put,
  },
  Delete: {
	  ...UserCall.Delete,
	  ...DeptCall.Delete,
	  ...AdminRosterCall.Delete,
  },
};

export default OrgCall;