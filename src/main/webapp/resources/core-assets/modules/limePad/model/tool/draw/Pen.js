/**
 * 
 */
import Path from '../../shape/path/Path.js';
import {penMixin} from '../../mixin/tool/pathMixin.js';
const Pen = new Path({
	name: "pen",
	title: "펜",
	level: 3,
});
Object.assign(Pen, penMixin);
Pen.setName("pen");
Pen.setTitle("펜");
export default Pen;