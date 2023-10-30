/**
 * 
 */
import {FitTool} from '../Tool.js';
import {fitMixin} from '../../mixin/tool/resizeMixin.js';
const FitHorizontal = new FitTool({
	name: "fithor",
	title: "가로맞춤",
	icon: "fas fa-arrows-alt-h",
	fit: "horizontal"
});
Object.assign(FitHorizontal, fitMixin);
export default FitHorizontal;