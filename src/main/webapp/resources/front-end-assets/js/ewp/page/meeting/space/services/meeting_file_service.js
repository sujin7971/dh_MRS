/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {FileCall as $FILE} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';



export default {
	__proto__: eventMixin,
	async init(data = {}){
		const {
			instanceProvider,
			deletable = false,
		} = data;
		FileManager.init({
			addConfirm: true,
			delConfirm: true,
		});
		FileManager.on("added", async (data) => {
			const {
				files
			} = data;
			Modal.startLoading({
				text: "파일을 업로드 중입니다...",
				animation: "foldingCube",
			});
			const formData = new FormData;
			for(const file of files){
				formData.append("list",file)
			}
			await AjaxBuilder({
				request: $FILE.Post.meetingFileList,
				param: {
					meetingKey: instanceProvider.meetingKey, 
					fileType: "MATERIAL",
					fileList: formData
				},
				loading: false,
				exception: 'success-only',
			}).success(res => {
				const uploadErrors = res.error;
				const errorList = uploadErrors?.map(error => "업로드 실패: "+error.data.originalName);
				Modal.info({
					msg: "회의 첨부파일을 업로드했습니다.",
					detail: errorList,
				});
				this.trigger("change");
			}).error(res => {
				Modal.error({
					response: res,
				});
			}).finally(() => {
				Modal.endLoading();
			}).exe();
		});
		FileManager.on("deleted", async (data) => {
			const {
				files
			} = data;
			await AjaxBuilder({
				request: $FILE.Delete.meetingFileList,
				param: {
					meetingKey: instanceProvider.meetingKey,
					fileList: files,
				},
				exception: 'success-only',
			}).success(res => {
				const delErrors = res.error;
				const errorList = delErrors?.map(error => "삭제 실패: "+error.data.originalName);
				Modal.info({
					msg: "회의 첨부파일을 삭제했습니다.",
					detail: errorList,
				});
				this.trigger("change");
			}).error(res => {
				Modal.error({
					response: res,
				});
			}).finally(() => {
				Modal.endLoading();
			}).exe();
		});
		this.instanceProvider = instanceProvider;
		const meetingKey = this.instanceProvider.meetingKey;
		const fileList = await $FILE.Get.meetingMaterialFileList(meetingKey);
		this.setFileList(fileList);
	},
	setFileList(fileList){
		if(this.fileMap){
			this.fileMap.clear();
		}else{
			this.fileMap = new Map();
		}
		fileList.forEach(file => this.fileMap.set(file.fileKey, file));
		this.showFileList();
	},
	getFileList(){
		return Array.from(this.fileMap.values());
	},
	getFile(fileKey){
		return this.fileMap.get(fileKey);
	},
	updateFile(file){
		this.fileMap.set(file.fileKey, file);
		this.showFileList();
		FileManager.setFiles(this.getFileList());
	},
	showFileList(){
		const fileList = this.getFileList();
		const typeCollection = Final.fileOptionTemplate;
		const generateFileRow = (file) => {
			const $li = Util.createElement("li", "align-center");
			const $span = Util.createElement("span");
			$span.innerHTML = file.originalName;
			const [
				deletable,
				downloadable,
				viewable,
			] = typeCollection[file.roleType].state[file.state + 2];
			if(viewable == true){
				Util.addClass($span, "cursor-pointer", "href-link");
				$span.onclick = () =>{
					this.trigger("select", {
						file: file,
						pageno: 1,
					});
				}
			}else{
				const $iconDiv = Util.createElement("span", "my-auto", "mr-2");
				const $icon = Util.createElement("i", ...typeCollection[file.roleType].icon[file.state + 2]);
				$iconDiv.appendChild($icon);
				$li.appendChild($iconDiv);
				Util.addClass($span, "text-muted");
			}
			
			$li.appendChild($span);
			return $li;
		}
		const fileProgressCheck = (file) => {
			const checkInterval = setInterval(async () => {
				const updatedFile = await $FILE.Get.meetingFileOne(file.fileKey);
				if(updatedFile.state == -1 || updatedFile.state == 2){
					clearInterval(checkInterval);
					this.updateFile(updatedFile);
				}else{
					clearInterval(checkInterval);
				}
			},2000);
		}
		const $listBox = Util.getElement("#fileListBox");
		$listBox.innerHTML = "";
		for(const file of fileList){
			if(file.state == -1){
				continue;
			}else if(file.state != 2){
				//fileProgressCheck(file);
			}
			const $row = generateFileRow(file);
			$listBox.appendChild($row);
		}
	},
	async showFileMng(){
		const attendRole = this.instanceProvider.attendRole;
		FileManager.update({
			deletable: (attendRole == "HOST" || attendRole == "ASSISTANT")?true:false,
		});
		FileManager.setFiles(this.getFileList());
		const res = await Modal.show("fileModal");
	},
	enableSelect(){
		Util.removeClass(".docuLeftSection", "disabled");
		Util.removeClass(".fileSection", "disabled");
		Util.removeClass(".docuPagingDiv", "disabled");
	},
	disableSelect(){
		Util.addClass(".docuLeftSection", "disabled");
		Util.addClass(".fileSection", "disabled");
		Util.addClass(".docuPagingDiv", "disabled");
	},
	enableUpload(){
		const $fileMngBtn = Util.getElement("#fileMngBtn");
		$fileMngBtn.style.display = "inline-flex";
		this.off("change");
		this.on("change", async () => {
			this.instanceProvider.getSocketService().sendSyncMsg({
				resourceType: "FILE"
			});
		});
		$fileMngBtn.onclick = async () => {
			$fileMngBtn.disabled = true;
			this.showFileMng();
			$fileMngBtn.disabled = false;
		}
	},
	disableUpload(){
		const $fileMngBtn = Util.getElement("#fileMngBtn");
		$fileMngBtn.onclick = null;
		$fileMngBtn.style.display = "none";
		this.off("change");
	},
}
