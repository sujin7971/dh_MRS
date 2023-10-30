/**
 * 
 */
import {Util, Modal} from '/resources/core-assets/essential_index.js';
/**
 * 서버 응답의 예외처리 범위를 설정
 * success-only: 서버 자체 응답을 정상적으로 수신했더라도 응답 코드 200이 아니라면 예외처리  
 * http-errors-only: http 프로토콜 에러만 예외처리
 * no-exceptions: 모두 정상응답으로 취급
 */
const exceptionHandlePolicy = ['success-only', 'http-errors-only', 'no-exceptions'];
/**
 * Ajax를 처리할 인스턴스를 생성하는 클래스. 사용자는 AjaxBuilder함수를 통해 인스턴스를 생성하여 사용할 수 있다.
 */
class AjaxRequest{
	constructor(executor = {}){
		const {
			request,
			param,
			loading = true,
			debug = true,
			exception = exceptionHandlePolicy[1],
			exceptionModal = true,
		} = executor;
		this.request = request;
		this.param = param;
		this.loading = loading;
		this.debug = debug;
		this.exception = exception;
	}
	success(executor){
		this.successExe = executor;
		return this;
	}
	error(executor){
		this.errorExe = executor;
		return this;
	}
	finally(executor){
		this.finallyExe = executor;
		return this;
	}
	async exe(){
		try{
			if(this.loading){
				Modal.startLoading();
			}
			const res = await this.request(this.param);
			if(this.loading){
				Modal.endLoading();
			}
			if(this.exception == exceptionHandlePolicy[0]){
				const status = res.status;
				if(status != 200){
					throw res;
				}
			}
			this.successExe?.(res);
			return res;
		}catch(err){
			if(this.loading){
				Modal.endLoading();
			}
			if(this.debug){
				console.log(err);
			}
			if(this.exceptionModal){
				Modal.error({response: err});
			}
			this.errorExe?.(err);
			return null;
		}finally{
			this.finallyExe?.();
		}
	}
}
/**
 * 사용자의 AJAX 요청양식을 처리할 AjaxRequest클래스의 인스턴스를 생성하여 넘겨줄 함수
 */
const AjaxBuilder = (executor = {}) => {
	return new AjaxRequest(executor);
}
export default AjaxBuilder;
