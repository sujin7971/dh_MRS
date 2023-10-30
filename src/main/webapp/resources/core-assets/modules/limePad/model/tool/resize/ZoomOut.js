/**
 * 
 */
import {ZoomTool} from '../Tool.js';
import {zoomMixin} from '../../mixin/tool/resizeMixin.js';
const ZoomOut = new ZoomTool({
	name: "zoomout",
	title: "축소",
	icon: "fas fa-search-minus",
	zoom: "out"
});
Object.assign(ZoomOut, zoomMixin);
export default ZoomOut;