/**
 * 
 */
import Polyline from '../../shape/path/Polyline.js';
import {fancyMixin} from '../../mixin/tool/pathMixin.js';
const Fancy = new Polyline();
Object.assign(Fancy, fancyMixin);
Fancy.setName("fancy");
Fancy.setTitle("펜시");
export default Fancy;