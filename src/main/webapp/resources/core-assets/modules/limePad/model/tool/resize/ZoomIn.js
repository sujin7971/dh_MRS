/**
 * 
 */
import {ZoomTool} from '../Tool.js';
import {zoomMixin} from '../../mixin/tool/resizeMixin.js';
const ZoomIn = new ZoomTool({
	name: "zoomin",
	title: "확대",
	icon: "fas fa-search-plus",
	zoom: "in"
});
Object.assign(ZoomIn, zoomMixin);
export default ZoomIn;