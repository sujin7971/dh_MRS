/**
 * 
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';
import {StompConnector} from '/resources/core-assets/module_index.js';

export default {
	__proto__: eventMixin,
	init(data = {}){
		const {
			instanceProvider,
			destinationURL,
			debug = false,
		} = data;
		this.debug = debug;
		this.instanceProvider = instanceProvider;
		this.destinationURL = destinationURL;
	},
	update(data = {}){
		const {
			serviceProvider = this.serviceProvider,
			destinationURL = this.destinationURL,
		} = data;
		this.serviceProvider = serviceProvider;
		this.destinationURL = destinationURL;
	},
	connect(executor){
		StompConnector.init({
			debug: this.debug,
		});
		StompConnector.connect();
		StompConnector.on("connected", () => {
			this.connectedExe?.();
		});
		StompConnector.on("disconnected", () => {
			this.disconnectedExe?.();
		});
		return this;
	},
	disconnect(){
		StompConnector.off("connected");
		StompConnector.off("disconnected");
		StompConnector.disconnect();
		this.disconnectedExe?.();
	},
	isConnected(){
		return StompConnector.connected;
	},
	connected(executor){
		this.connectedExe = executor;
		return this;
	},
	disconnected(executor){
		this.disconnectedExe = executor;
		return this;
	},
	async subscribe(data = {}){
		const {
			url,
			receiver,
		} = data;
		StompConnector.subscribe({
			url: url,
			receiver: receiver
		});
		StompConnector.on("receive", receiver);
		//this.sendJoinMsg();
	},
	sendJoinMsg(){
		StompConnector.send({
			url: "/join/"+this.instanceProvider.meetingId,
		});
	},
	sendUpdateMsg(msg = {}){
		const {
			url = this.destinationURL,
			resourceType,
			data,
		} = msg;
		StompConnector.send({
			url: url,
			msg: {
				messageType: "UPDATE",
				resourceType: resourceType,
				data: data,
			},
		});
	},
	sendSyncMsg(msg = {}){
		const {
			url = this.destinationURL,
			resourceType,
		} = msg;
		StompConnector.send({
			url: url,
			msg: {
				messageType: "SYNC",
				resourceType: resourceType,
			},
		});
	},
	sendNoticeMsg(msg = {}){
		const {
			url = this.destinationURL,
			actionType,
			data,
		} = msg;
		StompConnector.send({
			url: url,
			msg: {
				messageType: "NOTICE",
				actionType: actionType,
				data: data,
			},
		});
	},
}