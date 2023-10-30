/**
 * 브라우저 IndexedDB 제어를 위한 클래스.
 * 
 * IDB 라이브러리를 사용하여 구현.
 * 타입스크립트 기반 Mutex 라이브러리를 통해 CRUD요청에 대한 동시성 제어. 처리 접근 권한을 얻은 시점과 요청이 속한 스토어가 같지 않으면 해당 요청건은 무시
 * 
 * @see https://www.npmjs.com/package/idb
 * @see https://badge.fury.io/js/async-mutex
 * @author mckim
 * @version 3.0
 * @since 3.0
 */
import { openDB, deleteDB, wrap, unwrap } from './idb/with-async-ittr.js';
import Mutex from '/resources/library/async-mutex/es6/Mutex.js';
export default class IDBConnector{
	mutex = new Mutex();
	constructor(data = {}) {
		const {
			DB_NAME = "INDEXED_DB",
			DB_VERSION = undefined,
			STORE_NAME = "MEMO",
			INDEX_LIST = ["id", "page"]
		} = data;
		this.DB_NAME = DB_NAME;
		this.INDEX_LIST = INDEX_LIST;
		this.setDB(this.DB_NAME, DB_VERSION, STORE_NAME);
		//console.log("IDBConnector", "DB_NAME", DB_NAME);
	}
	/**
	 * DB 생성 및 오픈
	 *  @param DB_NAME: 생성할 DB 이름
	 *  @param @Nullable DB_VERSION: DB의 버전 정보. 값이 없는 경우 현재 버전으로 자동 적용
	 *  @param @Nullable STORE_NAME: DB생성 및 업데이트시 생성할 STORE 이름.
	 *  @param @Nullable INDEX_LIST: 생성된 STORE에 적용할 INDEX 목록
	 */
	setDB(DB_NAME, DB_VERSION, STORE_NAME){
		return new Promise(async (resolve, reject) => {
			if(this.DB){
				//DB가 OPEN 상태인 경우 VERSION CHANGE가 트리거 되지 않음
				this.DB.close();
			}
			await this.clearExpiredDB();
			this.STORE_NAME = STORE_NAME;
			const INDEX_LIST = this.INDEX_LIST;
			//console.log("setDB", "INDEX_LIST", INDEX_LIST);
			this.DB = await openDB(DB_NAME, DB_VERSION, {
				upgrade(db) {
					const setConfigStore = () => {
						const objectStoreNames = db.objectStoreNames;
						if(objectStoreNames.contains("CONFIG")){
							return;
						}
						const store = db.createObjectStore("CONFIG", {
				    		autoIncrement: true,
				      	});
						store.createIndex("key", "key");
					}
					const setBookmarkStore = () => {
						const objectStoreNames = db.objectStoreNames;
						if(objectStoreNames.contains("BOOKMARK")){
							return;
						}
						const store = db.createObjectStore("BOOKMARK", {
				    		autoIncrement: true,
				      	});
						store.createIndex("key", "key");
					}
					const setStore = (STORE_NAME) => {
						//console.log("upgrade", "STORE_NAME", STORE_NAME);
						const store = db.createObjectStore(STORE_NAME, {
				    		autoIncrement: true,
				      	});
						if(INDEX_LIST){
							for(const INDEX_NAME of INDEX_LIST){
								store.createIndex(INDEX_NAME, INDEX_NAME);
							}
						}
					}
					setConfigStore();
					setBookmarkStore();
					if(STORE_NAME){
						setStore(STORE_NAME)
					}
					
			    },
			});
			//console.log("setDB", "DB", this.DB);
			this.DB_VERSION = this.DB.version;
			await this.setExpire();
			resolve();
		});
	}
	
	unsetDB(DB_NAME) {
		return new Promise(async (resolve, reject) => {
			//console.log("unsetDB", "DB", this.DB);
			if(this.DB){
				this.DB.close();
			}
			await deleteDB(DB_NAME);
			resolve();
		});
	}
	
	setExpire(){
		return new Promise(async (resolve, reject) => {
			const expDataList = await this.DB.getAllFromIndex("CONFIG", "key", "EXP");
			if(expDataList.length == 0){
				const nowDT = new Date();	// 현재 날짜 및 시간
				const expDT = new Date(nowDT.setDate(nowDT.getDate() + 1));
				this.DB.add("CONFIG", {key: "EXP", value: expDT.getTime()});
			}
			resolve();
		});
	}
	
	clearExpiredDB(){
		return new Promise(async (resolve, reject) => {
			const dbInfoList = await indexedDB.databases();
			for(const dbInfo of dbInfoList){
				const nowD = Date.now();
				const db = await openDB(dbInfo.name);
				try{
					const dataList = await db.getAllFromIndex("CONFIG", "key", "EXP");
					const expD = dataList[0].value;
					db.close();
					if(nowD > expD){
						await deleteDB(dbInfo.name);
					}
				}catch(err){
					//console.log("catch", err);
					//console.log("DB NAME", dbInfo.name);
					db.close();
					await deleteDB(dbInfo.name);
				}
			}
			resolve();
		});
	}
	async setBookmark(fileKey, pageList){
		await this.DB.put("BOOKMARK", pageList, fileKey);
	}
	async getBookmark(fileKey){
		const bookmark = await this.DB.get("BOOKMARK", fileKey);
		return (bookmark)?bookmark:[];
	}
	/**
	 * STORE 생성 및 현재 사용할 STORE로 설정. 현재 OPEN한 DB에 해당 스토어가 있는 경우 버전이 업데이트 되지 않으므로 STORE를 다시 생성하지 않고 사용할 STORE로만 설정
	 * @param STORE_NAME: STORE 이름
	 */
	setStore(STORE_NAME){
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				//console.log("SET STORE", STORE_NAME);
				try{
					const storeCollection = this.DB.objectStoreNames;
					//console.log("SET STORE", "STORE_LIST", storeCollection);
					let NEW_DB_VERSION = this.DB_VERSION + 1;
					for(const key in storeCollection){
						const value = storeCollection[key];
						if(value == STORE_NAME){
							NEW_DB_VERSION = this.DB_VERSION;
							break;
						}
					}
					//console.log("SET STORE", "GO SET DB", this.DB_NAME, NEW_DB_VERSION, STORE_NAME);
					await this.setDB(this.DB_NAME, NEW_DB_VERSION, STORE_NAME);
					resolve();
				}catch(err){
					await this.unsetDB(this.DB_NAME);
					await this.setDB(this.DB_NAME, undefined, STORE_NAME);
					return this.setStore(STORE_NAME);
				}
			});
		});
	}
	
	/**
	 * STORE에 데이터 추가
	 * @param data: 추가할 데이터 객체
	 */
	insert(STORE_NAME, INSERT_DATA) {
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				//console.log("insert", "NOW_STORE", this.STORE_NAME, "REQUEST_STORE", STORE_NAME);
				if(this.STORE_NAME == STORE_NAME){
					await this.DB.add(this.STORE_NAME, INSERT_DATA);
				}
				resolve();
			});
		});
	}
	
	/**
	 * 해당 INDEX의 모든 값을 기준값 REF_INDEX_VALUE보다 큰 경우 1씩 감소시킨다.
	 * 해당 기준값을 가진 모든 데이터가 삭제된 경우 나머지 데이터의 INDEX값을 맞춰줘야 할 때 사용
	 * @param INDEX_NAME: INDEX 이름
	 * @param REF_INDEX_VALUE: 기준값
	 */
	shiftLeftIndex(STORE_NAME, INDEX_NAME, REF_INDEX_VALUE){
		return new Promise(async (resolve, reject) => {
		    this.mutex.runExclusive(async () => {
				if(this.STORE_NAME == STORE_NAME){
					const tx = this.DB.transaction(this.STORE_NAME, 'readwrite');
				    const index = tx.store.index(INDEX_NAME);
				    for await (const cursor of index.iterate()) {
				        const context = { ...cursor.value };
				        const INDEX_VALUE = context[INDEX_NAME];
				        if(INDEX_VALUE > REF_INDEX_VALUE){
				        	context[INDEX_NAME] = INDEX_VALUE - 1;
				        	cursor.update(context);
				        }
				    }
				}
				resolve();
			});
		});
	}
	
	/**
	 * 해당 INDEX의 모든 값을 기준값 REF_INDEX_VALUE보다 크거나 같은 경우 1씩 증가시킨다.
	 * 해당 STORE에 추가할 데이터가 이미 사용중인 INDEX 값을 필요로 하는 경우 기존 데이터를 위로 올려 자리를 만들어줄 때 사용
	 * @param INDEX_NAME: INDEX 이름
	 * @param REF_INDEX_VALUE: 기준값
	 */
	shiftRightIndex(STORE_NAME, INDEX_NAME, REF_INDEX_VALUE){
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				if(this.STORE_NAME == STORE_NAME){
					const tx = this.DB.transaction(this.STORE_NAME, 'readwrite');
				    for await (const cursor of tx.store) {
				        const context = { ...cursor.value };
				        const INDEX_VALUE = context[INDEX_NAME];
				        if(INDEX_VALUE >= REF_INDEX_VALUE){
				        	context[INDEX_NAME] = INDEX_VALUE + 1;
				        	cursor.update(context);
				        }
				    }
				}
				resolve();
			});
		});
	}
	
	/**
	 * STORE의 가장 마지막 데이터를 삭제
	 */
	deleteLast(STORE_NAME) {
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				if(this.STORE_NAME == STORE_NAME){
					let cursor = await this.DB.transaction(this.STORE_NAME).store.openCursor();
					let lastKey;
					while (cursor) {
						const next = await cursor.continue();
						if(!next){
							lastKey = cursor.key;
						}
						cursor = next;
					}
					if(lastKey){
						await this.DB.delete(this.STORE_NAME, lastKey);
					}
				}
				resolve();
			});
		});
	}
	
	/**
	 * STORE에서 해당 INDEX에서 특정 값을 가진 모든 데이터를 삭제
	 * @param INDEX_NAME: INDEX 이름
	 * @param INDEX_VALUE: 삭제할 INDEX의 값 
	 */
	deleteDataFromIndex(STORE_NAME, INDEX_NAME, INDEX_VALUE){
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				//console.log("deleteDataFromIndex", "NOW_STORE", this.STORE_NAME, "REQUEST_STORE", STORE_NAME, "INDEX_NAME", INDEX_NAME, "INDEX_VALUE", INDEX_VALUE);
				if(this.STORE_NAME == STORE_NAME){
					const allKeys = await this.DB.getAllKeysFromIndex(this.STORE_NAME, INDEX_NAME, INDEX_VALUE);
					for(const key of allKeys){
						//console.log("deleteDataFromIndex", "DELETE KEY", key, "OF", allKeys);
						await this.DB.delete(this.STORE_NAME, key);
					}
				}
				resolve();
			});
		});
	}
	
	clearStore(STORE_NAME){
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				//console.log("clearStore", "NOW_DB", this.DB, "NOW_STORE", this.STORE_NAME, "REQUEST_STORE", STORE_NAME);
				if(this.STORE_NAME == STORE_NAME){
					await this.DB.clear(this.STORE_NAME);
					resolve();
				}else{
					resolve();
				}
			});
		});
	}
	
	/**
	 * STORE에 저장된 모든 데이터 조회
	 */
	getAllData(STORE_NAME) {
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				//console.log("getAllData", "NOW_DB", this.DB, "NOW_STORE", this.STORE_NAME, "REQUEST_STORE", STORE_NAME);
				if(this.STORE_NAME == STORE_NAME){
					const allData = await this.DB.getAll(this.STORE_NAME);
					//console.log("getAllData", "allData", allData);
					resolve(allData);
				}else{
					resolve();
				}
			});
		});
	}
	
	/**
	 * STORE에 저장된 모든 데이터를 해당 데이터의 key값과 함께 통합하여 조회
	 */
	getAllDataWithKeys() {
		return new Promise(async (resolve, reject) => {
			const allKeys = await this.DB.getAllKeys(this.STORE_NAME);
			const allData = [];
			for(const key of allKeys){
				const data = await this.DB.get(this.STORE_NAME, key);
				data.key = key;
				allData.push(data);
			}
			resolve(allData);
		});
	}
	
	/**
	 * STORE에서 해당 INDEX에서 특정 값을 가진 모든 데이터 조회
	 * @param INDEX_NAME: INDEX 이름
	 * @param INDEX_VALUE: 조회할 INDEX의 값 
	 */
	getDataFromIndex(STORE_NAME, INDEX_NAME, INDEX_VALUE) {
		return new Promise(async (resolve, reject) => {
			this.mutex.runExclusive(async () => {
				if(this.STORE_NAME == STORE_NAME){
					const allData = await this.DB.getAllFromIndex(this.STORE_NAME, INDEX_NAME, INDEX_VALUE);
					resolve(allData);
				}else{
					resolve();
				}
			});
		});
	}
}

