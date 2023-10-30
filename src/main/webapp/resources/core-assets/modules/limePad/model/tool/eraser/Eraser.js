/**
 * 
 */
import {EraserTool} from '../Tool.js';
import {eraserMixin} from '../../mixin/tool/eraserMixin.js';
const Eraser = new EraserTool({
	name: "eraser",
	title: "지우개",
	icon: "fas fa-eraser",
});
Object.assign(Eraser, eraserMixin);
export default Eraser;