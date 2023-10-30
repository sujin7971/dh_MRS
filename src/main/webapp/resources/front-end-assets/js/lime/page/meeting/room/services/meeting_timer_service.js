/**
 * 
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';

export default {
	__proto__: eventMixin,
	init(data = {}){
		const {
			instanceProvider
		} = data;
		this.instanceProvider = instanceProvider;
	}
}