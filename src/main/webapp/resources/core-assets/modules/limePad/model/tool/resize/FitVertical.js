/**
 * 
 */
import {FitTool} from '../Tool.js';
import {fitMixin} from '../../mixin/tool/resizeMixin.js';
const FitVertical = new FitTool({
	name: "fitver",
	title: "세로맞춤",
	icon: "fas fa-arrows-alt-v",
	fit: "vertical"
});
Object.assign(FitVertical, fitMixin);
export default FitVertical;