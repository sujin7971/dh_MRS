/**
 * 
 */
import {eventMixin} from '/resources/core-assets/essential_index.js';
const StompConnector = {
	__proto__: eventMixin,
	init(data = {}){
		const {
			endPoint = "/ws",
			broker = "/subscribe",
			destinationPrefixes = "/topic",
			debug = false,
		} = data;
		this.endPoint = endPoint;
		this.broker = broker;
		this.destinationPrefixes = destinationPrefixes;
		this.debug = debug;
		this.connected = false;
	},
	connect(){
		this.disconnect();
		this.socket = new SockJS(this.endPoint);
		this.stompClient = Stomp.over(this.socket);
		if(this.debug == false){
			this.stompClient.debug = () => {}; 
		}
		this.stompClient.connect({}, 
		() => {
			//console.log("stomp connected");
			this.connected = true;
			this.trigger("connected");
		},
		(err) => {
			//console.log("stomp disconnected");
			this.connected = false;
			this.trigger("disconnected");
		});
	},
	disconnect(){
		this.stompClient?.disconnect();
	},
	subscribe(data = {}){
		const {
			url = null,
			receiver = null,
		} = data;
		this.stompClient.subscribe(this.broker+url, (e) => {
			this.trigger("receive", e);
		});
		return this;
	},
	send(data = {}){
		const {
			url = this.destinationUrl,
			msg = null,
		} = data;
		this.destinationUrl = url;
		this.stompClient.send(this.destinationPrefixes+this.destinationUrl, {}, JSON.stringify(msg));
	},
}
export default StompConnector;
