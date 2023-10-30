import {Api} from '/resources/core-assets/essential_index.js';
const insertDeptOne = (formData) => {
	return Api.Post(`/api/lime/admin/system/dept`, {
	    data: formData,
	    headers: {
	    	"Content-Type": "multipart/form-data",
	    },
	});
}
const deleteDeptOne = (deptId) => {
	return Api.Delete(`/api/lime/admin/system/dept/${deptId}`);
}
//부서 조회
const selectDeptOne = (deptId) => {
	return Api.Get(`/api/lime/dept/${deptId}`)
}
//하위 부서 조회
const selectSubDeptList = (parentId) => {
	return Api.Get(`/api/lime/dept/${parentId}/sub`)
}
const selectRecursiveSubDeptInfoList = (parentId) => {
	return Api.Get(`/api/lime/dept/${parentId}/sub/recursive`)
}
const selectAllDeptInfoList = () => {
	return Api.Get(`/api/lime/dept/all`)
}
const GetCall = {
	deptOne: selectDeptOne,
	selectSubDeptList: selectSubDeptList,
	selectRecursiveSubDeptInfoList: selectRecursiveSubDeptInfoList,
	selectAllDeptInfoList: selectAllDeptInfoList,
}

const PostCall = {
	insertDeptOne: insertDeptOne,
}

const PutCall = {
}

const DeleteCall = {
	deleteDeptOne: deleteDeptOne
}

const DeptCall = {
	Get: GetCall,
	Post: PostCall,
	Put: PutCall,
	Delete: DeleteCall,
}

export default DeptCall;