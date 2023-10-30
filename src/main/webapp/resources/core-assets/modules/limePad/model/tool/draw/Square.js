/**
 * 
 */
import Rectangle from '../../shape/basic/Rectangle.js';
import {rectangleMixin} from '../../mixin/tool/basicMixin.js';
const Sqaure = new Rectangle();
Object.assign(Sqaure, rectangleMixin);
Sqaure.setName("sqaure");
Sqaure.setTitle("사각형");
export default Sqaure;