/**
 * 
 */
import {ControlTool} from '../../Tool.js';
import {controlMixin} from '../../../mixin/tool/controlMixin.js';
const Pointer = new ControlTool({
	name: "pointer",
	title: "포커스",
	icon: "fas fa-hand-paper"
});
Object.assign(Pointer, controlMixin);
export default Pointer;