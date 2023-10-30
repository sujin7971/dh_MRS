/**
 * 
 */
import HighlightTool from '../HighlightTool.js';
import {highlightMixin} from '../../../mixin/tool/controlMixin.js';
const Laser = new HighlightTool({
	name: "laser",
	title: "레이저포인터",
	icon: "fas fa-bullseye-pointer",
	color: "#e9000066",
	wradius: 40,
	hradius: 40,
});
Laser.setType("highlight");
Object.assign(Laser, highlightMixin);
export default Laser;