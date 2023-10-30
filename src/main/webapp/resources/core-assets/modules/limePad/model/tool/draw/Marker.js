/**
 * 
 */
import Path from '../../shape/path/Path.js';
import {markerMixin} from '../../mixin/tool/pathMixin.js';
const Marker = new Path({
	strokeWidth: 25,
	strokeOpacity: 0.5,
	level: 1,
});
Object.assign(Marker, markerMixin);
Marker.setName("marker");
Marker.setTitle("마커");
export default Marker;