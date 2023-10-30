import {Api} from '/resources/core-assets/essential_index.js';
const selectUserInfoOne = (userId) => {
	return Api.Get(`/api/lime/admin/system/user/${userId}`);
}
const postSystemAdmin = (userId) => {
	return Api.Post(`/api/lime/admin/system/roster/system-admin/${userId}`);
}
const deleteSystemAdmin = (userId) => {
	return Api.Delete(`/api/lime/admin/system/roster/system-admin/${userId}`);
}
const getMasterDomainList = () => {
	return Api.Get(`/api/lime/admin/system/roster/master-admin/list`);
}
const getSystemDomainList = () => {
	return Api.Get(`/api/lime/admin/system/roster/system-admin/list`);
}
const GetCall = {
	userOne: selectUserInfoOne,
	masterDomainList: getMasterDomainList,
	systemDomainList: getSystemDomainList,
}

const PostCall = {
	systemDomain: postSystemAdmin,
}

const PutCall = {
}

const DeleteCall = {
	systemDomain: deleteSystemAdmin,
}

const AdminRosterCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default AdminRosterCall;