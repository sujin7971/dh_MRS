/**
 * 화면에 표시된 SVG Graphic의 스냅샷 데이터를 보관 및 처리하는 클래스
 */
export default class GraphicHistory{
	constructor(service) {
	    this.empty();
	    this._service = service;
	}
	/* 스택 비우기 */
	empty(){
		this._undoStack = [];
	    this._redoStack = [];
	}
	/* 현재 그래픽의 스냅샷을 임시 저장 */
	dump(){
		this._dump = snapshotGraphic(this._service._display);
	}
	/* 임시 저장된 스냅샷이 있는 경우 스택에 보관. 없는 경우 현재 그래픽의 스냅샷을 생성하여 보관 */
	take() {
		let snapshot = this._dump;
		if(!snapshot){
			snapshot = snapshotGraphic(this._service._display);
		}
	    this._undoStack.push(snapshot);
	    this._redoStack = [];
	    this._dump = undefined;
	}
	/* 되돌리기 */
	undo() {
	    if (this._undoStack.length > 0) {
	    	const dump = this._undoStack.pop();
	      	const snapshot = snapshotGraphic(this._service._display);
	      	this._redoStack.push(snapshot);
	      	
	      	this._service.restore(dump);
	    }
	}
	/* 다시하기 */
	redo() {
		if (this._redoStack.length > 0) {
	    	const dump = this._redoStack.pop();
	    	const snapshot = snapshotGraphic(this._service._display);
	    	this._undoStack.push(snapshot);
	    	
	    	this._service.restore(dump);
	    }
	}
	/* 다시하기할 데이터가 있는지 확인 */
	hasRedo(){
		return (this._redoStack.length > 0)?true:false;
	}
}
function snapshotGraphic(collection){
	const contextList = collection.map(g => g.toContext());
	return JSON.stringify(contextList);
}

