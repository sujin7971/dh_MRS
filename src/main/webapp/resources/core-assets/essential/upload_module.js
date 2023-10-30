/**
 * 
 */

import {eventMixin, Util} from '/resources/core-assets/essential_index.js';

const createFileElem = (data = {}) => {
	const {
		multiple = true,
		accept = [".pdf", ".hwp", ".hwpx", ".pptx", ".ppt", ".doc", ".docx", ".xls", ".xlsx", ".jpg", ".jpeg", ".gif", ".png", ".bmp"],
	} = data;
	const $fileInput = Util.createElement("input");
	$fileInput.setAttribute("type", "file");
	$fileInput.setAttribute("accept", accept.toString());
	$fileInput.multiple = multiple;
	$fileInput.style.display = "none";
	return $fileInput;
}

const getTotalSize = (list) => {
	return list.reduce((sum, file) => sum += (file.size)?file.size:file.fileSize, 0);
}

const getAddedFiles = ($input) => {
	let files = Object.values($input.files);
	files = files.filter(file => {
		const filename = file.name;
		let last_dot = filename.lastIndexOf('.')
		let ext = filename.slice(last_dot + 1)
		return $input.accept.includes("."+ext);
	});
	files.forEach(file => {
		file.isNew = true;
		file["fileKey"] = Util.getUUID(5);
		file["originalName"] = file.name;
	});
	//input 초기화
	$input.value = "";
	return files;
}

const uploader = {
	__proto__: eventMixin,
	update(data = {}){
		const {
			//sizeLimit = 314572800,
			//uploadLimit = 10,
			multiple = true,
		} = data;
		//this.sizeLimit = sizeLimit;
		//this.uploadLimit = uploadLimit;
		this.$fileInput.multiple = multiple;
	},
	fileInput(){
		const event = new MouseEvent("click", {
			  bubbles: false,
			  cancelable: false,
		});
		this.$fileInput.dispatchEvent(event);
	},
	getFileSize(fileList){
		if(fileList){
			return getTotalSize(fileList);
		}else{
			return getTotalSize(this.addedList);
		}
	},
};
(() => {
	uploader.addedList = [];
	uploader.sizeLimit = 314572800;
	uploader.uploadLimit = 10;
	
	uploader.$fileInput = createFileElem({
		multiple: true,
	});
	uploader.$fileInput.onchange = (evt) => {
		const fileList = getAddedFiles(uploader.$fileInput);
		if(Util.isEmpty(fileList)){
			return;
		}
		uploader.trigger("added", {
			files: fileList,
			size: getTotalSize(fileList),
			count: fileList.length,
		});
	}
	Util.getElement("body").appendChild(uploader.$fileInput);
})();
export default uploader;